package src;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

public class AutenticacaoView {
	
	private SecurityController gerenciador = new SecurityController();
	
	public static void main(String [] p) throws Exception {
		AutenticacaoView view = new AutenticacaoView();
		view.menuPrincipal();
		
	}
	
	public void menuPrincipal() throws IOException, NoSuchAlgorithmException{
		
		FileUtils gravador = new FileUtils();
		String senhaMestre = gravador.readFile("arquivos/master_key.txt");
		
		if(senhaMestre.isEmpty()){
			String mestra = JOptionPane.showInputDialog("Digite a senha mestre a ser utilizada para o chaveiro");
			this.gerenciador.derivarMasterKeyPBKDF2(mestra);
			this.menuUsuarioAutenticado();
		}else{
			String mestra = JOptionPane.showInputDialog("Digite a senha mestre:");
			
			if(this.gerenciador.verificaMasterKey(mestra)){
				this.menuUsuarioAutenticado();
			}
		}
		
	}
	
	public void menuUsuarioAutenticado(){
		int option =Integer.parseInt(JOptionPane.showInputDialog("Digite 1 para cifrar arquivo: \n"
				+ "Digite 2 para decifrar arquivo"));
		switch(option){
			case 1 : 
				String caminho = JOptionPane.showInputDialog("Digite o caminho do arquivo a ser cifrado:");
				this.gerenciador.cifraArquivo(caminho);
				this.gerenciador.decifraArquivo(caminho+".cifrado");
		}
	}
	
//	public static void insereUser() throws Exception {
//		
//		String senha = JOptionPane.showInputDialog("Senha: ");
//		
//        System.out.println("Senha Original = " + senha);
//		//String pbkdf5Key = derivarChavePBKDF2(senha);
//		
//		
//		byte [] senhaBytes = Hex.decodeHex(pbkdf5Key.toCharArray());//32 bytes
//		
//		System.out.println("Chave PBKDF5 gerada: "+ Utils.toHex(senhaBytes));
//		
//		SecureRandom	random = new SecureRandom();
//        IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
//		
//		Key keySenha = new SecretKeySpec(senhaBytes, "AES");
//		HMacUtil hmac = new HMacUtil();
//		
//	}
	

}
