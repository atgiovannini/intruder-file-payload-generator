package payload;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author tmendo
 */
public class FileUtils {
        
    static String[] excludedFiles = new String[]{"readme", ".", "licen"};
    static String[] excludedFolders = new String[]{".git", ".vscode", ".settings"};
    public static ArrayList<String> listFilesForFolder(String folder) {
        ArrayList<String> files = new ArrayList<>();
               
        File inputFolder = new File(folder);
        for (final File fileEntry : inputFolder.listFiles()) {
            String name = fileEntry.getName();
            boolean con = false;

            if (fileEntry.isDirectory()) {
                // Needs to improve ignore patterns
                for (String exc : FileUtils.excludedFolders) {
                    if (name.toLowerCase().startsWith(exc)) {
                        con = true;
                        break;
                    }
                }
                if (con) continue;
                files.addAll(listFilesForFolder(fileEntry.getAbsolutePath()));
            } else {
                // Needs to improve ignore patterns
                for (String exc : FileUtils.excludedFiles) {
                    if (name.toLowerCase().startsWith(exc)) {
                        con = true;
                        break;
                    }
                }
                if (con) continue;
                files.add(fileEntry.getAbsolutePath());
            }

        }
        
        return files;
    }
}
