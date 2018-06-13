package t2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Server {

	private static Socket socket;

	public static void main(String[] args)
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		// Adicionando o provider BC
		Security.addProvider(new BouncyCastleProvider());
		//Classe para gerar o par de chaves do servidor
		RSAUtilsServer rsaServer = new RSAUtilsServer();
		
		//Instancia cipher para cifrar com RSA
		Cipher cipher = Cipher.getInstance("RSA", "BC");
		SecureRandom random = Utils4.createFixedRandom();
		
		//Geracao das chaves publica e privada do servidor
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair          pair = generator.generateKeyPair();
        Key              serverPubKey = pair.getPublic();
        Key              serverPrivKey = pair.getPrivate();

		try {

			int port = 25000;
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port 25000");

			// Server is running always. This is done using this while(true) loop
			while (true) {
				
				// Reading the message from the client
				socket = serverSocket.accept();
			
				//Recebimento do IV do cliente
				InputStream is2 = socket.getInputStream();
				InputStreamReader isr2 = new InputStreamReader(is2);
				BufferedReader br2 = new BufferedReader(isr2);
				String ivClientString = br2.readLine();
				byte[] ivClientBytes = Hex.decodeHex(ivClientString.toCharArray());
				IvParameterSpec ivClient = new IvParameterSpec(ivClientBytes);
				//System.out.println(Hex.encodeHexString(ivClient.getIV()));
				
				//Recebimento da SessionKey do cliente
				InputStream is3 = socket.getInputStream();
				InputStreamReader isr3 = new InputStreamReader(is3);
				BufferedReader br3 = new BufferedReader(isr3);
				String sessionKeyClient = br3.readLine();
		        Key clientKey = null;
		        byte[] K = Hex.decodeHex(sessionKeyClient);
		        clientKey = new SecretKeySpec(K, "AES");
		        //System.out.println(Hex.encodeHexString(clientKey.getEncoded()));
			    
			    Scanner entrada = new Scanner(socket.getInputStream());
				
				 while (entrada.hasNextLine()) {
			        	String cifrada = entrada.nextLine();
			        	System.out.println("Mensagem cifrada : "+ cifrada);
			        	String decifrada = decifraMensagem(cifrada, ivClient, clientKey);
			        	System.out.println("Mensagem decifrada : "+ decifrada);

			        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	public static String decifraMensagem(String msg, IvParameterSpec iv, Key key) {

		byte[] emBytes = null;

		try {

			Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifrador.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] decifradaBytes = Hex.decodeHex(msg.toCharArray());
			emBytes = cifrador.doFinal(decifradaBytes);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new String(emBytes);
	}

	public static IvParameterSpec gerarIV() {

		IvParameterSpec ivSpec = null;
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			byte[] ivBytes = new byte[16];
			random.nextBytes(ivBytes);
			ivSpec = new IvParameterSpec(ivBytes);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ivSpec;
	}
}