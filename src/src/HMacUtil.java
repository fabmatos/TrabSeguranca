package src;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class HMacUtil 
{   
  
	public int addProvider;
	public Cipher cipher;
	public Mac hMac;
	public Key hMacKey;
	public int ctLength;
	
	public HMacUtil() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
		addProvider = Security.addProvider(new BouncyCastleFipsProvider());
		cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
		hMac = Mac.getInstance("HMacSHA256", "BCFIPS");
	}
	
    public byte [] hmacEncypt(String input, Key key, IvParameterSpec ivSpec)throws Exception {
    	
        hMacKey = new SecretKeySpec(key.getEncoded(), "HMacSHA256");
        
        cipher.init(Cipher.ENCRYPT_MODE, hMacKey, ivSpec);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length() + hMac.getMacLength())];

        ctLength = cipher.update(Utils.toByteArray(input), 0, input.length(), cipherText, 0);
        
        hMac.init(hMacKey);
        hMac.update(Utils.toByteArray(input));//Calcula hmac da entrada
        
        ctLength += cipher.doFinal(hMac.doFinal(), 0, hMac.getMacLength(), cipherText, ctLength);//Insere hmac na cifra
        
        return cipherText;
    }
    
    public String hmacDecrypt(byte[] cipherText, Key key, IvParameterSpec ivSpec) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException{
    	String decifrada = "";
    	
    	cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
    	
        byte[] plainText = cipher.doFinal(cipherText, 0, ctLength);
        int    messageLength = plainText.length - hMac.getMacLength();
        
        hMac.init(key);
        hMac.update(plainText, 0, messageLength);
        
        byte[] messageMac = new byte[hMac.getMacLength()];
        System.arraycopy(plainText, messageLength, messageMac, 0, messageMac.length);
        
        decifrada = Utils.toString(plainText, messageLength);
    	return decifrada;
    }
}
