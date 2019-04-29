package payload;

import burp.BurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IIntruderPayloadGenerator;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author tmendo
 */
public class IntruderPayloadGenerator implements IIntruderPayloadGenerator {

    int payloadIndex, indexFiles, indexContent;
    ArrayList<String> files, processingFiles;
    IBurpExtenderCallbacks callbacks = BurpExtender.getBurpCallbacks();
    
    // if true returns the filenames instead of the payload
    boolean useFilename;
    PrintWriter stdout, stderr;

    public IntruderPayloadGenerator(File folder, boolean useFilename) {
        if (folder == null) {
            callbacks.issueAlert("Please choose the payload folder first");
            files = new ArrayList<>();
        }
        else {
            files = FileUtils.listFilesForFolder(folder.getAbsolutePath());
        }
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        this.useFilename = useFilename;
    }

    @Override
    public boolean hasMorePayloads() {
        if(useFilename)
            return indexFiles < files.size() ;
        else{
            return processingFiles == null || indexFiles <= files.size() || indexContent < processingFiles.size();
        }
    }

    @Override
    public byte[] getNextPayload(byte[] baseValue) {
         
        if(processingFiles == null || processingFiles.size() <= indexContent)
        {
            String filename = files.get(payloadIndex);
            this.stdout.println("Based on file:" + filename);
            if(useFilename){
                baseValue = callbacks.getHelpers().stringToBytes(Paths.get(filename).getFileName().toString());
            }else{
                 try{
                    processingFiles = (ArrayList)Files.readAllLines(Paths.get(filename));
                } catch (IOException ex) {
                    this.stderr.println("Could not read \"" + filename + "\" - " + ex.getLocalizedMessage());
                }
            }
            indexFiles++;
            indexContent = 0;
        }
        if(!useFilename)
        {
            baseValue = callbacks.getHelpers().stringToBytes(processingFiles.get(indexContent));
            indexContent++;
        }
        payloadIndex++;
        this.stdout.println("generated=>" + callbacks.getHelpers().bytesToString(baseValue));
        return baseValue;
    }

    @Override
    public void reset() {
        payloadIndex = 0;
    }
}
