package t2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.sun.java_cup.internal.runtime.Scanner;

import t2.Utils4;

public class Client {

	private static Socket socket;

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		// Adicionando o provider BC
		Security.addProvider(new BouncyCastleProvider());
		// Classe para gerar o par de chaves do cliente e cifrar RSA
		RSAUtilsClient rsaClient = new RSAUtilsClient();
		
		//Instnacia cipher para cifrar com RSA
		Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
		SecureRandom random = Utils4.createFixedRandom();
		
		//Geracao das chaves publica e privada do cliente
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair          pair = generator.generateKeyPair();
        Key              clientPubKey = pair.getPublic();
        Key              clientPrivKey = pair.getPrivate();
        
        
        
		try {
			
			
			String host = "localhost";
			int port = 25000;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

	        //Etapa de geracao da chave de sessao
	        String senha = JOptionPane.showInputDialog("Digite a senha:");
	        SecretKey clientSessionKey = derivarMasterKeyPBKDF2(senha);
	        
	        //Geracao do iv aleatorio e chave baseada na chave de sessao
	        IvParameterSpec ivClient = gerarIV();
	        Key clientKey = null;
	        byte[] K;
			try {
				K = clientSessionKey.getEncoded();
				clientKey = new SecretKeySpec(K, "AES");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			//Envio do IV para o server ()
			OutputStream os2 = socket.getOutputStream();
			OutputStreamWriter osw2 = new OutputStreamWriter(os2);
			BufferedWriter bw2 = new BufferedWriter(osw2);
			bw2.write(Hex.encodeHex(ivClient.getIV()));
			bw2.newLine();
			bw2.flush();
//			bw2.write(Hex.encodeHex(clientKey.getEncoded()));
//			bw2.newLine();
//			bw2.flush();
			
			Thread.sleep(1000);
			//Envio da sessionKey para o server ()
			OutputStream os3 = socket.getOutputStream();
			OutputStreamWriter osw3 = new OutputStreamWriter(os3);
			BufferedWriter bw3 = new BufferedWriter(osw3);
			bw3.write(Hex.encodeHex(clientKey.getEncoded()));
			bw3.newLine();
			bw3.flush();
			Thread.sleep(1000);
			PrintStream saida = new PrintStream(socket.getOutputStream());

			int opt = 1;

			while (opt == 1) {
				opt = Integer
						.parseInt(JOptionPane.showInputDialog("Digite 1 para nova mensagem \n Outra tecla para sair"));

				if (opt == 1) {
					String paraCifrar = JOptionPane.showInputDialog("Digite a mensagem aqui");
					String cifrada = cifraMensagem(paraCifrar, ivClient, clientKey);
					saida.println(cifrada);
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static SecretKey derivarMasterKeyPBKDF2(String senha) throws NoSuchAlgorithmException, IOException {
		SecretKey chavePBKDF2 = null;
		String salt = getSalt();
		chavePBKDF2 = generateDerivedKey(senha, salt, 100000);
		return chavePBKDF2;
	}

	public static String getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return Hex.encodeHexString(salt);
	}

	public static SecretKey generateDerivedKey(String password, String salt, Integer iterations) {

		SecretKey sk = null;
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, 128);
		SecretKeyFactory pbkdf2 = null;
		try {
			pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			sk = pbkdf2.generateSecret(spec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sk;
	}

	public static String cifraMensagem(String msg, IvParameterSpec iv, Key key) {

		String msgCifrada = "";

		try {

			Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifrador.init(Cipher.ENCRYPT_MODE, key, iv);

			byte[] cifradoBytes = cifrador.doFinal(msg.getBytes());
			msgCifrada = Hex.encodeHexString(cifradoBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return msgCifrada;
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
}