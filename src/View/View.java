package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class View {

    Controller controller;
    static Stage stage;
    File corpusDirSelected = null;
    File postingDirSelected = null;
    public Button btn_corpusPath;
    public Button btn_postingPath;
    public Button btn_run;
    public Button btn_reset;
    public Button btn_showDic;
    public Button btn_loadDic;
    public TextField txt_corpusChooser;
    public TextField txt_postingChooser;
    public CheckBox chbx_stemming;
    public ChoiceBox chobx_language;
    String corpusPath;
    String postingPath;
    private ObservableList<String> languages = FXCollections.observableArrayList("English", "Hebrew", "French", "German", "Japanese", "Spanish", "Italian");


    public void loadCorpusPath() {
        DirectoryChooser fc = new DirectoryChooser();
        corpusDirSelected = fc.showDialog(stage);
        corpusPath = corpusDirSelected.getPath().toString();
        setCorpusTextField();
    }

    public void saveFilesPath() {
        DirectoryChooser fc = new DirectoryChooser();
        postingDirSelected = fc.showDialog(stage);
        postingPath = postingDirSelected.getPath().toString();
        setPostingTextField();
    }

    public void run(String corpusPath, String postingPath) {
        try {
            controller.run(corpusPath, postingPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stemming(){
        controller.stemming (chbx_stemming.isSelected());
    }

    public void setLanguages(){
        chobx_language.setItems(languages);
    }

    public void reset(){
        controller.reset(postingPath);
    }

    public void setCorpusTextField (){
        txt_corpusChooser.setText(corpusPath);
    }

    public void setPostingTextField (){
        txt_postingChooser.setText(postingPath);
    }
}
