package View;

import Controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private View view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("mainWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Search Engine");
        Scene scene = new Scene(root, 650, 500);
        primaryStage.setScene(scene);
/*        view = fxmlLoader.getController();
        view.setStage(primaryStage);*/
        //view.setMainStage(primaryStage);
/*        Controller con = new Controller(view);
        view.setController(con);*/
        primaryStage.show();
    }

    public static void main (String [] args) throws IOException {

        launch(args);
/*        String corpusPath = "d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus";
        ReadFile rf = new ReadFile(false, "d:\\documents\\users\\shaharar\\Downloads\\test", corpusPath);
        try {
            long startTime 	= System.nanoTime();
            rf.getFilesFromDir(corpusPath);
            long finishTime 	= System.nanoTime();
            long totalTime = (long)((finishTime - startTime)/1000000.0);
            System.out.println("Total time:  " + totalTime/60000.0 + " min");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
