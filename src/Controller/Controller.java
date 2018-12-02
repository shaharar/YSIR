package Controller;

import Model.Model;
import View.View;

import java.io.File;


public class Controller {

    View view;
    Model model;

    public Controller(View view) {
        this.view = view;
    }

    public void run(String corpusPath, String savePath) {
        model.run(corpusPath,savePath);
    }

    public void stemming(boolean selected) {
        model.stemming(selected);
    }

    public void reset(String savePath) {
        model.reset(savePath);
    }

    public void loadDictionary(String savePath, File newDic) {
        model.loadDictionary (savePath, newDic);
    }
}
