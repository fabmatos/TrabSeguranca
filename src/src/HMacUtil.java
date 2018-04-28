package src;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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
		// addProvider = Security.addProvider(new BouncyCastleFipsProvider());
		this.cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
		this.hMac = Mac.getInstance("HMacSHA256", "BCFIPS");
	}

    public byte [] hmacEncypt(String input, Key key, IvParameterSpec ivSpec)throws Exception {

        this.hMacKey = new SecretKeySpec(key.getEncoded(), "HMacSHA256");

        this.cipher.init(Cipher.ENCRYPT_MODE, this.hMacKey, ivSpec);

        byte[] cipherText = new byte[this.cipher.getOutputSize(input.length() + this.hMac.getMacLength())];

        this.ctLength = this.cipher.update(Utils.toByteArray(input), 0, input.length(), cipherText, 0);

        this.hMac.init(this.hMacKey);
        this.hMac.update(Utils.toByteArray(input));//Calcula hmac da entrada

        this.ctLength += this.cipher.doFinal(this.hMac.doFinal(), 0, this.hMac.getMacLength(), cipherText, this.ctLength);//Insere hmac na cifra

        return cipherText;
    }

    public String hmacDecrypt(byte[] cipherText, Key key, IvParameterSpec ivSpec) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException{
    	String decifrada = "";

    	this.cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] plainText = this.cipher.doFinal(cipherText, 0, this.ctLength);
        int    messageLength = plainText.length - this.hMac.getMacLength();

        this.hMac.init(key);
        this.hMac.update(plainText, 0, messageLength);

        byte[] messageMac = new byte[this.hMac.getMacLength()];
        System.arraycopy(plainText, messageLength, messageMac, 0, messageMac.length);

        decifrada = Utils.toString(plainText, messageLength);
    	return decifrada;
    }
}
