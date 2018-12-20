package Model;

import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Model {

    ReadFile rf;
    Indexer index;
    Searcher searcher;
    boolean isStemSelected;
    double totalTime;

    public Model() {
        isStemSelected = false;
    }

    public void run(String corpusPath, String savePath) {
        try {
            rf = new ReadFile(isStemSelected, savePath, corpusPath);
            long startTime = System.nanoTime();
            rf.getFilesFromDir(corpusPath);
            long finishTime = System.nanoTime();
            totalTime = (double) ((finishTime - startTime)/1000000000.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stemming(boolean selected) {
        isStemSelected = selected;
    }

    public boolean reset(String savePath) {
        File dirToDelete = new File(savePath);
        deleteDirectory(dirToDelete, savePath);
        if (rf != null) {
            rf.reset();
            return true;
        }
        else{
            return false;
        }
    }

    private void deleteDirectory(File dir, String savePath) {
        File[] filesInDir = dir.listFiles();
        if (filesInDir != null) {
            for (File file : filesInDir) {
                deleteDirectory(file, savePath);
            }
        }
        if (! dir.getAbsolutePath().equals(savePath)) {
            dir.delete();
        }
    }

    public void loadDictionary(String savePath, File newDic) {
        index = new Indexer(savePath, isStemSelected);
        index.loadDictionary(newDic);
    }

    public HashSet<String> getLanguages() {
        return rf.getLanguages();
    }

    public String endOfRun() {
        String termsMsg = "Unique terms in corpus : " + rf.getDicSize() + "\n";
        String docsMsg = "Indexed documents : " + rf.endOfRun() + "\n";
        String timeMsg = "Total running time : " + totalTime + " sec";
        String message = termsMsg + docsMsg + timeMsg;
        return message;
    }

    public void runQuery(String queryText, boolean withStemming, String saveInPath) {
        searcher = new Searcher();
        searcher.search(index,queryText,withStemming,saveInPath,"","");
    }

    public void runQueriesFile(TextField txt_queriesPathChooser, boolean selected, String savePath) {
        searcher.separateFileToQueries(index,txt_queriesPathChooser.getText().toString(),selected,savePath);
    }
}
