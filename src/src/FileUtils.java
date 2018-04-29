package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.model.ItemChaveiro;

public class FileUtils {


    public String readFile (String filename) throws IOException {
        String path = System.getProperty("user.dir") + "/src/";
        File file = new File(path + filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
                while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append(ls);
                }
                return stringBuilder.toString();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
                reader.close();
        }
        return null;
    }

    public void escreverArquivo (String txt, String nome_arquivo, int opcao) throws IOException{

        String diretorio = System.getProperty("user.dir") + "/src/";
        File arquivo;
        switch (opcao) {
            case 0:
                arquivo = new File(diretorio + nome_arquivo);
                break;
            case 1:
                arquivo = new File(diretorio + nome_arquivo + ".cifrado");
                break;
            default:
                nome_arquivo = nome_arquivo.replace(".cifrado", "");
                arquivo = new File(diretorio + nome_arquivo + ".decifrado");
                break;
        }
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            }
            catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FileWriter escritor = new FileWriter(arquivo.getAbsoluteFile());
        BufferedWriter escritor_final = new BufferedWriter(escritor);
        escritor_final.write(txt);
        escritor_final.close();
    }

	public List<ItemChaveiro> recuperarItensChaveiro() {
		List<ItemChaveiro> itens = new ArrayList<ItemChaveiro>();

		try {
			String conteudoChaveiro = this.readFile("arquivos/chaveiro");
			if (!conteudoChaveiro.isEmpty()) {
				JSONParser parser = new JSONParser();
				JSONArray jsonArray = (JSONArray) parser.parse(conteudoChaveiro);

				for (Object item : jsonArray) {
					JSONObject itemChaveiroJson = (JSONObject) item;
					ItemChaveiro itemChaveiro = new ItemChaveiro((String) itemChaveiroJson.get("nomeArquivo"), (String) itemChaveiroJson.get("chaveArquivo"));
					itens.add(itemChaveiro);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return itens;
	}

	public JSONArray convertListToJson(List<ItemChaveiro> itens) {
        JSONArray jsonArray = new JSONArray();

        for (ItemChaveiro item: itens){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nomeArquivo", item.getNomeArquivo());
            jsonObject.put("chaveArquivo", item.getChaveArquivo());
            jsonArray.add(jsonObject);
        }

		return jsonArray;
	}

}
