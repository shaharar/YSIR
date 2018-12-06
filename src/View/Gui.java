package View;

import Model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Gui {

    Model model;
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

    public Gui() {
        model = new Model();
    }

    public void loadCorpusPath() {
        Stage stage = new Stage();
        DirectoryChooser fc = new DirectoryChooser();
        corpusDirSelected = fc.showDialog(stage);
        if (corpusDirSelected != null) {
            corpusPath = corpusDirSelected.getAbsolutePath();
            txt_corpusChooser.setText(corpusPath);
        }
    }

    public void saveFilesPath() {
        Stage stage = new Stage();
        DirectoryChooser fc = new DirectoryChooser();
        saveDirSelected = fc.showDialog(stage);
        if (saveDirSelected != null) {
            savePath = saveDirSelected.getAbsolutePath();
            txt_savePathChooser.setText(savePath);
        }
    }

    public void run () {
        chobx_language.setDisable(true);
        if (corpusPath == null) {
            showAlert("Please insert corpus path");
            return;
        }
        if (savePath == null) {
            showAlert("Please insert save files path");
            return;
        } else {
            try {
                model.run(corpusPath,savePath);
                String message = model.endOfRun ();
                showAlert("Running successful!\n\n" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HashSet<String> languages = model.getLanguages();
            chobx_language.setItems(FXCollections.observableArrayList(languages));
            chobx_language.setDisable(false);
        }
    }


    public void showDictionary () {
        if (savePath == null){
            showAlert("Please insert save files path");
            return;
        }
        File dicFile;
        if (chbx_stemming.isSelected()){
            dicFile = new File(savePath + "\\indexResults\\dictionary_stemmingShow.txt");
        }
        else{
            dicFile = new File(savePath + "\\indexResults\\dictionaryShow.txt");
        }
        if (!dicFile.exists()){
            showAlert("There wasn't found a dictionary. Please load a new one");
        }
        else {
            try {
                Desktop.getDesktop().open(dicFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void loadDictionary () {
        if (savePath == null){
            showAlert("Please insert save files path");
            return;
        }
        File newDicFile = new File(savePath + "\\indexResults\\dictionary.txt");
        if (!newDicFile.exists()){
            showAlert("There wasn't found a dictionary. Please load a new one");
        }
        else {
            model.loadDictionary (savePath, newDicFile);
            showAlert("Loading successful");
        }
    }

    public void stemming(){
        model.stemming (chbx_stemming.isSelected());
    }

    public void reset(){
        if (! model.reset(savePath)){
            showAlert("The files and memory have been cleared");
        }
        else {
            showAlert("Reset is done");
        }
    }

    public void setCorpusPath() {
   //     corpusPath = txt_corpusChooser.getSelectedText();
    }

    public void setSavePath() {
     //   savePath = txt_savePathChooser.getSelectedText();
    }

    /*    public void setLanguage(){
        if (chobx_language.getItems() == null){
            showAlert("Languages can be displayed after running");
        }
    }*/
}
