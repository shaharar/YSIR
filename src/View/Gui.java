package View;

import Model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Gui {

    Model model;
    File corpusDirSelected = null;
    File saveDirSelected = null;
    File queriesFileSelected = null;
    File saveResultsDirSelected = null;
    public TextField txt_corpusChooser;
    public TextField txt_savePathChooser;
    public TextField txt_saveResultsChooser;
    public CheckBox chbx_stemming;
    public CheckBox chbx_semantic;
    public ChoiceBox chobx_language;
    public TextField txt_query;
    public Button btn_browseQueries;
    public TextField txt_queriesPathChooser;
    public ListView <String> lv_dic;
    public ListView<String> lv_results;
    String corpusPath;
    String savePath;
    String saveResultsPath;
    public File queriesFile;
    boolean isLoaded;
    boolean isRunIndex;
    boolean isRunQuery;
    public TextField txt_docNo;
    public MenuButton mBtn_menuCities;
    ObservableList<String> cities;

    public Gui() {
        model = new Model();
        isLoaded = false;
        isRunIndex = false;
        isRunQuery = false;
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
        File newDicFile, newCitiesDicFile, entitiesFile, weightsPerDoc, languagesFile;

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

        //load weights doc
        if (chbx_stemming.isSelected()){
            weightsPerDoc = new File(savePath + "\\docsWeights_stemming.txt");
        }
        else{
            weightsPerDoc = new File(savePath + "\\docsWeights.txt");
        }

        //load languages
        languagesFile = new File(savePath + "\\languages.txt");
        HashSet<String> languages = new HashSet<>();
        BufferedReader br = null;
        try {
            String term;
            br = new BufferedReader(new FileReader(languagesFile));
            String line = "";
            while ((line = (br.readLine())) != null) {
                languages.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chobx_language.setItems(FXCollections.observableArrayList(languages));

        if (!newDicFile.exists() && !newCitiesDicFile.exists() && !entitiesFile.exists() && !weightsPerDoc.exists() && !languagesFile.exists()){
            showAlert("Files weren't found. Please load all files again");
        }
        else {
            model.loadDictionary (savePath, newDicFile,newCitiesDicFile,entitiesFile, weightsPerDoc);
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
/*            try {
                Desktop.getDesktop().open(dicFile);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("showDictionary.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            Scene scene = new Scene(root, 300, 600);
            stage.setScene(scene);

            Gui gui = fxmlLoader.getController();
            gui.model = this.model;
            ArrayList<String> dicRecords = new ArrayList<>();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(dicFile));
                String line = "";
                while ((line = (br.readLine())) != null) {
                    dicRecords.add(line + System.lineSeparator());
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gui.lv_dic.setItems(FXCollections.observableArrayList(dicRecords));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
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
        model.runQuery(txt_query.getText(),chosenCities,cities,savePath);
        isRunQuery = true;
        showResults();
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
        model.runQueriesFile(queriesFile,chosenCities,cities,savePath);
        isRunQuery = true;
        showResults();
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


    public void setCities(){
        cities = FXCollections.observableArrayList();
        HashMap<String,Integer> citiesDic = model.getCityIndexer().getCitiesDictionary();
        for (String city: citiesDic.keySet()) {
            cities.add(city);
        }
         for(String c : cities){
            CheckMenuItem checkItem = new CheckMenuItem();
            checkItem.setText(c);
            mBtn_menuCities.getItems().add(checkItem);
         }
    }

    public ArrayList<String> getChosenCities(){
       ArrayList<String> chosenCities = new ArrayList<>();
        for (MenuItem menuItem : mBtn_menuCities.getItems()) {
            CheckMenuItem checkItem = (CheckMenuItem)menuItem;
            if(checkItem.isSelected())
                chosenCities.add(checkItem.getText());
        }
        return chosenCities;
    }

    public void semantics(){
        model.semantics (chbx_semantic.isSelected());
    }

    public void saveResults() {
        if(!isRunQuery){
            showAlert("Please run query or queries file");
            return;
        }
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        saveResultsDirSelected = dc.showDialog(stage);
        if (saveResultsDirSelected != null) {
            saveResultsPath = saveResultsDirSelected.getAbsolutePath();
        }
        model.saveResults(saveResultsPath);
        showAlert("Results have been saved");
    }

    //show results of query
    public void showResults() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("showResults.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);

        Gui gui = fxmlLoader.getController();
        gui.model = this.model;
        HashMap<String, ArrayList<String>> results = model.showResults();
        PriorityQueue <String> sortedQueryIDs = new PriorityQueue<>();
        for (String queryID: results.keySet()) {
            sortedQueryIDs.add(queryID);
        }
        ArrayList<String> resultsList = new ArrayList<>();
        while(sortedQueryIDs.size() > 0) {
            String queryID = sortedQueryIDs.poll();
            String docs = results.get(queryID).toString();
            String lineInListView = "Query " + queryID + ":\n" + docs;
            resultsList.add(lineInListView);
        }
        gui.lv_results.setItems(FXCollections.observableArrayList(resultsList));
        gui.lv_results.setStyle("-fx-font-style: Calibri;-fx-font-weight:bold;");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void searchEntities() {
        HashMap<String, HashMap<String, Integer>> allEntities = model.showEntities();
        if(txt_docNo == null || !allEntities.containsKey(txt_docNo.getText())){
            showAlert("Please enter docNo from results");
            return;
        }
        HashMap<String, Integer> entitiesOfDoc = allEntities.get(txt_docNo.getText());
        PriorityQueue <Pair<String, Integer>> sortedEntities = new PriorityQueue<>(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
                return ((p1.getValue().compareTo(p2.getValue())) * (-1));
            }
        });

        for (String entity: entitiesOfDoc.keySet()) {
            if (entity.charAt(0) >= 65 && entity.charAt(0) <= 90){
                sortedEntities.add(new Pair<>(entity, entitiesOfDoc.get(entity)));
            }

        }
        String fiveEntities = "";
        while(sortedEntities.size() > 0){
            Pair<String,Integer> entity = sortedEntities.poll();
            fiveEntities += entity.getKey() + " - " + entity.getValue() + "\n";
        }
        showAlert(fiveEntities);
    }
}
