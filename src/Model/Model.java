package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
            File f = new File(savePath + "\\indexResults");
            f.deleteOnExit();
            rf.reset();
            return true;
        }
        else{
            return false;
        }
    }

    public void loadDictionary(String savePath, File newDic) {
        index = new Indexer(savePath);
        index.loadDictionary(newDic);
    }
}
