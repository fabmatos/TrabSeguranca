package src;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

public class SecurityController {
	
	private PBKDF2Util pbkdf2 = new PBKDF2Util();
	private FileUtils gravador = new FileUtils();
	
	public SecurityController(){
		this.crateGCMIV();
	}
	
	public void crateGCMIV(){
		String gcmIV="";
		
		try {
			gcmIV = gravador.readFile("arquivos/gcm_iv.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(gcmIV.isEmpty()){
			SecureRandom	random = new SecureRandom();
			IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
			try {
				gravador.writeFile(Hex.encodeHexString(ivSpec.getIV()), "arquivos/gcm_iv.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public String derivarMasterKeyPBKDF2(String senha) throws NoSuchAlgorithmException, IOException {
		String chavePBKDF2 = "";
		String salt = pbkdf2.getSalt();
		chavePBKDF2 = pbkdf2.generateDerivedKey(senha, salt, 100000);
		gravador.writeFile(salt, "arquivos/salt_mk.txt");
		gravador.writeFile(chavePBKDF2, "arquivos/master_key.txt");
        System.out.println("Sal gerado = " + salt);
		return chavePBKDF2;
	}
	
	public boolean verificaMasterKey(String senha) throws IOException{
		
		String masterKey = gravador.readFile("arquivos/master_key.txt");
		String salt = gravador.readFile("arquivos/salt_mk.txt");
		String chavePBKDF2 = pbkdf2.generateDerivedKey(senha, salt, 100000);
		
		return chavePBKDF2.equals(masterKey);
		
	}
	
	

}
