package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Model {

    ReadFile rf;
    Indexer index;
    boolean isStemSelected;

    public void run(String corpusPath, String savePath) {
        try {
            rf = new ReadFile(isStemSelected, savePath, corpusPath);
            rf.getFilesFromDir(corpusPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stemming(boolean selected) {
        isStemSelected = selected;
    }

    public void reset(String postingPath) {
        File f = new File(postingPath + "\\indexResults");
        f.delete();
        rf.reset();
    }

    public void loadDictionary(String savePath, File newDic) {
        index = new Indexer(savePath);
        index.loadDictionary(newDic);

    }
}
