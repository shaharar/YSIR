package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Model {

    ReadFile rf;
    Indexer index;
    boolean isStemSelected;

    public Model() {
        isStemSelected = false;
    }

    public void run(String corpusPath, String savePath) {
        try {
            rf = new ReadFile(isStemSelected, savePath, corpusPath);
            long startTime = System.nanoTime();
            rf.getFilesFromDir(corpusPath);
            long finishTime = System.nanoTime();
            long totalTime = (long)((finishTime - startTime)/1000000.0);
            System.out.println("Total time:  " + totalTime/60000.0 + " min");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stemming(boolean selected) {
        isStemSelected = selected;
    }

    public boolean reset(String savePath) {
        if (rf != null) {
/*            File f = new File(savePath + "\\indexResults");
            for (File file: f.listFiles()) {
                file.delete();
            }
            f.delete();
            rf.reset();*/
            try {
                Files.deleteIfExists(Paths.get(savePath + "\\indexResults"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else{
            return false;
        }
    }

    public void loadDictionary(String savePath, File newDic) {
        index = new Indexer(savePath, isStemSelected);
        index.loadDictionary(newDic);
    }
}
