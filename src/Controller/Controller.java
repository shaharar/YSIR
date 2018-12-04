package Controller;

import Model.Model;
import View.View;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.HashSet;


public class Controller {

    View view;
    Model model;

    public Controller() {
        model = new Model();
    }

    public void run(String corpusPath, String savePath) {
        model.run(corpusPath,savePath);
    }

    public void stemming(boolean selected) {
        model.stemming(selected);
    }

    public boolean reset(String savePath) {
        return model.reset(savePath);
    }

    public void loadDictionary(String savePath, File newDic) {
        model.loadDictionary (savePath, newDic);
    }

/*    public HashSet<String> getLanguages() {
        return model.getLanguages();
    }*/
}
