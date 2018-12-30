package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class ShowResultsView {
    public ListView<String> lv_results;
    public Button btn_showEntities;


    public void showEntities() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("docNoForEntities.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root, 370, 155);
        stage.setScene(scene);

    }
}
