package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.Main;
import static com.lsmsdb.task3.Main.getProgramIcon;
import com.lsmsdb.task3.beans.Person;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class for the user control panel
 */
public class UserUiControlPanelController implements Initializable {

    /*
     * FXML private data members
    */
    @FXML
    private Label labelUserName;
    
    
    /*
     * Other private data members
    */
    private Person person;
    private UserUiMapController mapController;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        person = (Person)rb.getObject("person");
        labelUserName.setText(person.getName() + " " + person.getSurname());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent mapRoot = fxmlLoader.load(UserUiControlPanelController.class.getResource("/fxml/UserUiMap.fxml").openStream());
            mapController = (UserUiMapController) fxmlLoader.getController();
            Scene mapScene = new Scene(mapRoot);
            Stage mapStage = new Stage();
            mapStage.setTitle("Map View");
            mapStage.setScene(mapScene);
            mapStage.setOnCloseRequest((event) -> {
                Platform.exit();
            });
            mapStage.getIcons().add(getProgramIcon());
            mapStage.show();
        } catch (IOException ex) {
            Logger.getLogger(UserUiControlPanelController.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
}
