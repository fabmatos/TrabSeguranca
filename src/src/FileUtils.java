package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileUtils {
	
    public static final String PATH_FILE_MASTER_KEY = "arquivos/master_key.txt";
    public static final String PATH_FILE_SALT_MK = "arquivos/salt_mk.txt";
    
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
    
    public void writeFile (String txt, String filename) throws IOException{
        String path = System.getProperty("user.dir") + "/src/";
        File file = new File(path + filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(txt);
        bw.close();
    }
}
