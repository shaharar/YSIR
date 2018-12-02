package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class View {

    Controller controller;
    static Stage stage;
    File corpusDirSelected = null;
    File saveDirSelected = null;
    public Button btn_corpusPath;
    public Button btn_savePath;
    public Button btn_run;
    public Button btn_reset;
    public Button btn_showDic;
    public Button btn_loadDic;
    public TextField txt_corpusChooser;
    public TextField txt_savePathChooser;
    public CheckBox chbx_stemming;
    public ChoiceBox chobx_language;
    String corpusPath;
    String savePath;
    private ObservableList<String> languages = FXCollections.observableArrayList("English", "Hebrew", "French", "German", "Japanese", "Spanish", "Italian");


    public void loadCorpusPath() {
        DirectoryChooser fc = new DirectoryChooser();
        corpusDirSelected = fc.showDialog(stage);
        corpusPath = corpusDirSelected.getPath().toString();
        setCorpusTextField();
    }

    public void saveFilesPath() {
        DirectoryChooser fc = new DirectoryChooser();
        saveDirSelected = fc.showDialog(stage);
        savePath = saveDirSelected.getPath().toString();
        setPostingTextField();
    }

    public void run () {
        try {
            controller.run(corpusPath, savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDictionary () {
        File dicFile = new File(savePath + "\\indexResults\\dictionary.txt");
        try {
            Desktop.getDesktop().open(dicFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDictionary () {
        File newDicFile = new File(savePath + "\\indexResults\\dictionary.txt");
        controller.loadDictionary (savePath, newDicFile);
    }

    public void stemming(){
        controller.stemming (chbx_stemming.isSelected());
    }

    public void setLanguages(){
        chobx_language.setItems(languages);
    }

    public void reset(){
        controller.reset(savePath);
    }

    public void setCorpusTextField (){
        txt_corpusChooser.setText(corpusPath);
    }

    public void setPostingTextField (){
        txt_savePathChooser.setText(savePath);
    }
}
