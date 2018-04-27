package src;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.swing.JOptionPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AutenticacaoView {

	private SecurityController gerenciador = new SecurityController();

	public static void main(String [] p) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		AutenticacaoView view = new AutenticacaoView();
		view.menuPrincipal();

	}

	public void menuPrincipal() throws IOException, NoSuchAlgorithmException{

		FileUtils gravador = new FileUtils();
		String senhaMestre = gravador.readFile("arquivos/master_key.txt");
		boolean isUserLogado = false;

		if(senhaMestre.isEmpty()){
			String mestra = JOptionPane.showInputDialog("Digite a senha mestre a ser utilizada para o chaveiro");
			this.gerenciador.derivarMasterKeyPBKDF2(mestra);
			this.menuUsuarioAutenticado();
		}else{
			while (!isUserLogado) {
				String mestra = JOptionPane.showInputDialog("Digite a senha mestre:");
				if (this.gerenciador.verificaMasterKey(mestra)) {
					this.menuUsuarioAutenticado();
					isUserLogado = true;
				} else {
					JOptionPane.showMessageDialog(null, "Senha inválida!");
				}
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
