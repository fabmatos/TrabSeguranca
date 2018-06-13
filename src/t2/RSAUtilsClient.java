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

public class RSAUtilsClient {
	 
    public int addProvider = Security.addProvider(new BouncyCastleProvider());
    Cipher	         cipher;
    SecureRandom     random = Utils4.createFixedRandom();
    
    KeyPairGenerator generator;
    Key clientPubKey, clientPrivKey, serverPubKey;

	public RSAUtilsClient () throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
		generator = KeyPairGenerator.getInstance("RSA");
		//generator.initialize(1024);
		KeyPair pair = generator.generateKeyPair();
		clientPubKey = pair.getPublic();
		clientPrivKey = pair.getPrivate();
	}
	
	public String cifraRSAClienteWithPubServerKey(byte [] input) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
		byte[] cipherText = cipher.doFinal(input);
		return Hex.encodeHexString(cipherText);
	}
	
	public String decifraRSAClienteWithPrivClientKey(byte [] cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, clientPrivKey);
		byte[] plainText = cipher.doFinal(cipherText);
        String plano = new String (plainText);
        return plano;
	}
	
	public Key getClientPubKey() {
		return clientPubKey;
	}

	public Key getClientPrivKey() {
		return clientPrivKey;
	}

	public void setServerPubKey(Key serverPubKey) {
		this.serverPubKey = serverPubKey;
	}

	public Key getServerPubKey() {
		// TODO Auto-generated method stub
		return this.serverPubKey;
	}

}
