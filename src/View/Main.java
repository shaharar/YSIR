package View;

import Model.Model;
import Model.ReadFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("../../resources/mainWindow.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 1024, 600);
    }

/*    public static void main (String [] args) throws IOException {

        String corpusPath = "d:\\documents\\users\\shaharar\\Downloads\\corpus\\corpus";
        ReadFile rf = new ReadFile(false, "d:\\documents\\users\\shaharar\\Downloads\\test", corpusPath);
        try {
            long startTime 	= System.nanoTime();
            rf.getFilesFromDir(corpusPath);
            long finishTime 	= System.nanoTime();
            long totalTime = (long)((finishTime - startTime)/1000000.0);
            System.out.println("Total time:  " + totalTime/60000.0 + " min");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
