package t2;

import java.security.SecureRandom;
import java.security.Security;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider; // Inclui;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import t2.Utils4;

public class RSAUtilsServer {
	 
    public int addProvider; 
    Cipher	         cipher;
    SecureRandom     random = Utils4.createFixedRandom();
    
    KeyPairGenerator generator;
    Key serverPubKey, serverPrivKey, clientPubKey;
    
	public RSAUtilsServer () throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
		generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1048);
		KeyPair pair = generator.generateKeyPair();
		serverPubKey = pair.getPublic();
		System.out.println("Pub server: "+Hex.encodeHexString(serverPubKey.getEncoded()));
		serverPrivKey = pair.getPrivate();
		addProvider = Security.addProvider(new BouncyCastleProvider());
	}
	
	public byte [] cifraRSAServerWithPubClientKey(byte [] input) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
		byte[] cipherText = cipher.doFinal(input);
		return cipherText;
	}
	
	public String decifraRSAServerWithPrivServerKey(byte [] cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, serverPrivKey);
		byte[] plainText = cipher.doFinal(cipherText);
        String plano = new String (plainText);
        return plano;
	}
	
	public Key getServerPubKey() {
		return serverPubKey;
	}

	public Key getServerPrivKey() {
		return serverPrivKey;
	}

	public void setClientPubKey(Key clientPubKey) {
		this.clientPubKey = clientPubKey;
	}

	public Key getClientPubKey() {
		// TODO Auto-generated method stub
		return this.clientPubKey;
	}


}
