package src;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.swing.JOptionPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AutenticacaoView {

	private SecurityController gerenciador = new SecurityController();

	public static void main(String[] p) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		AutenticacaoView view = new AutenticacaoView();
		view.menuPrincipal();

	}

	public void menuPrincipal() throws IOException, NoSuchAlgorithmException {

		FileUtils gravador = new FileUtils();
		String senhaMestre = gravador.readFile("arquivos/master_key.txt");
		boolean isUserLogado = false;

		if (senhaMestre.isEmpty()) {
			String mestra = JOptionPane.showInputDialog("Digite a senha mestre a ser utilizada para o chaveiro");
			this.gerenciador.derivarMasterKeyPBKDF2(mestra);
			this.menuUsuarioAutenticado();
		} else {
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

	public void menuUsuarioAutenticado() {

		int option = 1;
		String caminho = "";

		while (option != 0) {
			option = Integer.parseInt(JOptionPane.showInputDialog("Digite 1 para cifrar arquivo \n"
					+ "Digite 2 para decifrar arquivo \n"
					+ "Digite 3 para remover arquivo \n"
					+ "Digite 0 para sair"));

			switch (option) {
			case 1:
				caminho = JOptionPane.showInputDialog("Digite o caminho do arquivo a ser cifrado:");
				this.gerenciador.cifraArquivo(caminho);
				break;
			case 2:
				caminho = JOptionPane.showInputDialog("Digite o caminho do arquivo a ser decifrado:");
				this.gerenciador.decifraArquivo(caminho);
				break;
			case 3:
				caminho = JOptionPane.showInputDialog("Digite o caminho do arquivo a ser removido:");
				// this.gerenciador.removeArquivo(caminho);
				break;
			default:
				option = 0;
			}

		}
	}

}
