package View;

import Model.Model;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
   // public ListView <String> lv_dic = new ListView<>();
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
        if (corpusPath == null) {
            showAlert("Please choose corpus path");
            return;
        }
        if (savePath == null) {
            showAlert("Please choose save files path");
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
/*            ArrayList<String> dicRecords = new ArrayList<>();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(dicFile));
                String line = "";
                while ((line = (br.readLine())) != null) {
                    dicRecords.add(line + System.lineSeparator());
                }
                br.close();
                lv_dic.setItems(FXCollections.observableArrayList(dicRecords));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = fxmlLoader.load(getClass().getResource("showDictionary.fxml"));
                stage.setTitle("Dictionary");
                Scene scene = new Scene(root, 650, 500);
                stage.setScene(scene);
                stage.initModality((Modality.APPLICATION_MODAL));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
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
        File newDicFile;
        if (chbx_stemming.isSelected()){
            newDicFile = new File(savePath + "\\indexResults\\dictionary_stemming.txt");
        }
        else{
            newDicFile = new File(savePath + "\\indexResults\\dictionary.txt");
        }
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
}
