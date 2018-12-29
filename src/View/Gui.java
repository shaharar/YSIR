package View;

import Model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Gui {

    Model model;
    File corpusDirSelected = null;
    File saveDirSelected = null;
    File queriesFileSelected = null;
    File saveResultsDirSelected = null;
    /*    public Button btn_corpusPath;
    public Button btn_savePath;
    public Button btn_run;
    public Button btn_reset;
    public Button btn_showDic;
    public Button btn_loadDic;*/
    public TextField txt_corpusChooser;
    public TextField txt_savePathChooser;
    public TextField txt_saveResultsChooser;
    public CheckBox chbx_stemming;
    public CheckBox chbx_semantic;
    public ChoiceBox chobx_language;
    public CheckComboBox<String> chobx_cities;
    public TextField txt_query;
    public Button btn_browseQueries;
    public TextField txt_queriesPathChooser;
    // public ListView <String> lv_dic;
    String corpusPath;
    String savePath;
    String saveResultsPath;
    public File queriesFile;
/*    public Button btn_showResults;
    public Button btn_saveResults;*/
    ArrayList<String> chosenCities;
    boolean isLoaded;
    boolean isRunIndex;

    public Gui() {
        model = new Model();
        isLoaded = false;
        isRunIndex = false;
    }

    //load the corpus path the user chose
    public void loadCorpusPath() {
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        corpusDirSelected = dc.showDialog(stage);
        if (corpusDirSelected != null) {
            corpusPath = corpusDirSelected.getAbsolutePath();
            txt_corpusChooser.setText(corpusPath);
        }
    }

    //load the path of saving files the user chose
    public void saveFilesPath() {
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        saveDirSelected = dc.showDialog(stage);
        if (saveDirSelected != null) {
            savePath = saveDirSelected.getAbsolutePath();
            txt_savePathChooser.setText(savePath);
        }
    }

    //build the inverted index
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
                showAlert("Running index started, please wait for the end of the process");
                Thread.sleep(3000);
                model.run(corpusPath,savePath);
                String message = model.endOfRun ();
                showAlert("Running successful!\n\n" + message);
                setCities();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HashSet<String> languages = model.getLanguages();
            chobx_language.setItems(FXCollections.observableArrayList(languages));
            chobx_language.setDisable(false);
            isRunIndex = true;
        }
    }

    //load dictionary file from the relevant directory
    public void loadDictionary () {
        if (savePath == null){
            showAlert("Please insert save files path");
            return;
        }
        File newDicFile, newCitiesDicFile, entitiesFile;

        //load regular dictionary
        if (chbx_stemming.isSelected()){
            newDicFile = new File(savePath + "\\indexResults\\dictionary_stemming.txt");
        }
        else{
            newDicFile = new File(savePath + "\\indexResults\\dictionary.txt");
        }

        //load cities dictionary
        newCitiesDicFile = new File(savePath + "\\cityIndexResults\\citiesDictionary.txt");

        //load entities file
        if (chbx_stemming.isSelected()){
            entitiesFile = new File(savePath + "\\entitiesInformation_stemming.txt");
        }
        else{
            entitiesFile = new File(savePath + "\\entitiesInformation.txt");
        }

        if (!newDicFile.exists() && !newCitiesDicFile.exists() && !entitiesFile.exists()){
            showAlert("Files weren't found. Please load terms dictionary, cities dictionary and entities file");
        }
        else {
            model.loadDictionary (savePath, newDicFile,newCitiesDicFile,entitiesFile);
            showAlert("Loading successful");
            setCities();
            isLoaded = true;
        }
    }

    public void stemming(){
        model.stemming (chbx_stemming.isSelected());
    }

    //clear all directories and memory
    public void reset(){
        if (savePath == null){
            showAlert("Please insert save files path");
            return;
        }
        if (! model.reset(savePath)){
            showAlert("The files and memory have been cleared");
        }
        else {
            showAlert("Reset is done");
        }
    }

    //show the dictionary file existing in the directory
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

    public void runQuery() {
        if (savePath == null) {
            showAlert("Please choose save files path");
            return;
        }
        if (!isRunIndex && !isLoaded) {
            showAlert("You should load your dictionaries before run query");
            return;
        }
        if(txt_query.getText().equals("")){
            showAlert("Please enter a query");
            return;
        }
        ArrayList<String> chosenCities = getChosenCities();
        model.runQuery(txt_query.getText(),chosenCities,chobx_cities.getItems(),savePath);
        showAlert("Run query done!");
    }

    public void runQueriesFile() {
        if (savePath == null) {
            showAlert("Please choose save files path");
            return;
        }
        if (!isRunIndex && !isLoaded) {
            showAlert("You should load your dictionaries before run queries file");
            return;
        }
        if(queriesFile == null){
            showAlert("Please choose a queries file");
            return;
        }
        ArrayList<String> chosenCities = getChosenCities();
        model.runQueriesFile(queriesFile,chosenCities,chobx_cities.getItems(),savePath);
        showAlert("Run queries file done!");
    }

    public void browseQueriesFile() {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        queriesFileSelected = fc.showOpenDialog(stage);
        if (queriesFileSelected != null) {
            queriesFile = queriesFileSelected.getAbsoluteFile();
            txt_queriesPathChooser.setText(queriesFile.getAbsolutePath());
        }
        else{
            showAlert("None file was chosen");
        }
    }

    //show results of query
    public void showResults() {

    }

    public void setCities(){
        ObservableList<String> cities = FXCollections.observableArrayList();
        HashMap<String,Integer> citiesDic = model.getCityIndexer().getCitiesDictionary();
        for (String city: citiesDic.keySet()) {
            cities.add(city);
        }
/*        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(savePath + "\\cityIndexResults\\citiesDictionary.txt")));
            String line = "";
            while ((line = (br.readLine())) != null) {
                cities.add(line.substring(0,line.indexOf(":") - 1) + System.lineSeparator());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        chobx_cities.getItems().addAll(cities);
        chobx_cities.setDisable(false);
    }

    public ArrayList<String> getChosenCities(){
       ArrayList<String> chosenCities = new ArrayList<>();
        for (String city: chobx_cities.getCheckModel().getCheckedItems()) {
            chosenCities.add(city);
        }
        return chosenCities;
    }

    public void semantics(){
        model.semantics (chbx_semantic.isSelected());
    }

    public void saveResults() {
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        saveResultsDirSelected = dc.showDialog(stage);
        if (saveResultsDirSelected != null) {
            saveResultsPath = saveResultsDirSelected.getAbsolutePath();
         //   txt_saveResultsChooser.setText(saveResultsPath);
        }
        model.saveResults(saveResultsPath);
        showAlert("Results have been saved");
    }
}
