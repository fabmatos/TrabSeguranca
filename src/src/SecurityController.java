package src;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class SecurityController {

	private PBKDF2Util pbkdf2 = new PBKDF2Util();
	private FileUtils gravador = new FileUtils();
	private Key chave = null;

	public IvParameterSpec recuperarIV() {
		IvParameterSpec ivSpec = null;
		String gcmIV = "";

		try {
			gcmIV = this.gravador.readFile("arquivos/gcm_iv.txt").replace("\n", "").replace("\r", "");
			if (gcmIV.isEmpty()) {
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				random.nextBytes(ivBytes);

				ivSpec = new IvParameterSpec(ivBytes);
				this.gravador.escreverArquivo(Hex.encodeHexString(ivSpec.getIV()), "arquivos/gcm_iv.txt", 0);
			} else {
				ivSpec = new IvParameterSpec(Hex.decodeHex(gcmIV));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ivSpec;
	}

	public Key recuperarHMACKey() {

		String chave = "";
		Key hMacKey = null;
		try {
			chave = this.gravador.readFile("arquivos/hmac_key.txt").replace("\n", "").replace("\r", "");
			if (chave.isEmpty()) {
				SecureRandom random = new SecureRandom();
				Key key = Utils.createKeyForAES(128, random);
				hMacKey = new SecretKeySpec(key.getEncoded(), "HMacSHA256");
				this.gravador.escreverArquivo(Hex.encodeHexString(hMacKey.getEncoded()), "arquivos/hmac_key.txt", 0);
			} else {
				byte[] K = org.apache.commons.codec.binary.Hex.decodeHex(chave.toCharArray());
				hMacKey = new SecretKeySpec(K, "AES");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hMacKey;
	}

	public String calculaHMAC(String nomeArquivo) {
		String hmacNomeArquivo = "";
		Key hMKey = this.recuperarHMACKey();
		try {
			Mac hMac = Mac.getInstance("HMacSHA256", "BC");
			hMac.init(hMKey);
			hMac.update(nomeArquivo.getBytes());
			hmacNomeArquivo = Hex.encodeHexString(hMac.doFinal());

		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmacNomeArquivo;
	}

	public String cifraChaveWithGcm(String chave) {

		String textoCifrado = "";
		IvParameterSpec iv = this.recuperarIV();
		try {
			String masterKey = this.gravador.readFile("arquivos/master_key.txt").replace("\n", "").replace("\r", "");
			String salt = this.gravador.readFile("arquivos/salt_mk.txt").replace("\n", "").replace("\r", "");
			String chavePBKDF2 = this.pbkdf2.generateDerivedKey(masterKey, salt, 100000);

			byte[] K = Hex.decodeHex(chavePBKDF2.toCharArray());
			Key keyDerivada = new SecretKeySpec(K, "AES");

			Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifrador.init(Cipher.ENCRYPT_MODE, keyDerivada, iv);

			byte[] chaveCifrada = cifrador.doFinal(chave.getBytes());
			textoCifrado = Hex.encodeHexString(chaveCifrada);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return textoCifrado;
	}

	public byte[] decifraChaveWithGcm(String chaveCifrada) {

		byte[] chave = null;
		IvParameterSpec iv = this.recuperarIV();
		try {
			String masterKey = this.gravador.readFile("arquivos/master_key.txt").replace("\n", "").replace("\r", "");
			String salt = this.gravador.readFile("arquivos/salt_mk.txt").replace("\n", "").replace("\r", "");
			String chavePBKDF2 = this.pbkdf2.generateDerivedKey(masterKey, salt, 100000);

			byte[] K = Hex.decodeHex(chavePBKDF2.toCharArray());
			Key keyDerivada = new SecretKeySpec(K, "AES");

			Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifrador.init(Cipher.DECRYPT_MODE, keyDerivada, iv);

			byte[] chaveCifradaBytes = Hex.decodeHex(chaveCifrada.toCharArray());
			chave = cifrador.doFinal(chaveCifradaBytes);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chave;
	}

	public String derivarMasterKeyPBKDF2(String senha) throws NoSuchAlgorithmException, IOException {
		String chavePBKDF2 = "";
		String salt = this.pbkdf2.getSalt();
		chavePBKDF2 = this.pbkdf2.generateDerivedKey(senha, salt, 100000);
		this.gravador.escreverArquivo(salt, "arquivos/salt_mk.txt", 0);
		this.gravador.escreverArquivo(chavePBKDF2, "arquivos/master_key.txt", 0);
		// System.out.println("Sal gerado = " + salt);
		return chavePBKDF2;
	}

	public boolean verificaMasterKey(String senha) throws IOException {

		String masterKey = this.gravador.readFile("arquivos/master_key.txt").replace("\n", "").replace("\r", "");
		String salt = this.gravador.readFile("arquivos/salt_mk.txt").replace("\n", "").replace("\r", "");
		String chavePBKDF2 = this.pbkdf2.generateDerivedKey(senha, salt, 100000);

		return chavePBKDF2.equals(masterKey);
	}

	public void cifraArquivo(String arquivo) {
		IvParameterSpec iv = this.recuperarIV();

		try {
			this.chave = Utils.createKeyForAES(256, SecureRandom.getInstance("SHA1PRNG"));
			String textoPlano = this.gravador.readFile(arquivo);
			Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifrador.init(Cipher.ENCRYPT_MODE, this.chave, iv);

			byte[] arquivoCifrado = cifrador.doFinal(textoPlano.getBytes());
			String textoCifrado = Hex.encodeHexString(arquivoCifrado);

			this.gravador.escreverArquivo(textoCifrado, arquivo, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void decifraArquivo(String arquivo) {
		Cipher cifra;
		IvParameterSpec iv = this.recuperarIV();

		try {
			String arquivoLido = this.gravador.readFile(arquivo).replace("\n", "").replace("\r", "");
			cifra = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cifra.init(Cipher.DECRYPT_MODE, this.chave, iv);

			byte[] transforma_bytes = Hex.decodeHex(arquivoLido.toCharArray());

			String arquivoDecifrado = new String(cifra.doFinal(transforma_bytes));
			this.gravador.escreverArquivo(arquivoDecifrado, arquivo, 2);
		} catch (Exception e) {
			Logger.getLogger(SecurityController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void inserirNoChaveiro(String nomeArquivo, String chaveArquivo) {

		String hmacNomeArquivo = this.calculaHMAC(nomeArquivo);
		String gcmChave = this.cifraChaveWithGcm(chaveArquivo);

	}

}
