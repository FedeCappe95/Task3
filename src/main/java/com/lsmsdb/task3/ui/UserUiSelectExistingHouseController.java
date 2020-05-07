package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class that guides the user to select an existing house
 */
public class UserUiSelectExistingHouseController implements Initializable {

    /*
     * FXML private data memebers
    */
    @FXML
    private TextField textFieldName;
    @FXML
    private Button buttonSelect;
    
    /*
     * Other private data members
    */
    private Place house;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        house = null;
        buttonSelect.setOnAction((event) -> {
            String placeName = textFieldName.getText();
            if(placeName.isEmpty()) {
                Utils.showErrorAlert("Error", "Please, fill the form before pressing Select");
                return;
            }
            Place maybeAnHouse = Neo4JManager.getIstance().getPlace(placeName);
            if(house == null) {
                Utils.showErrorAlert("Error: house not found", "Can not find a place with that name");
                return;
            }
            if(!maybeAnHouse.getType().equals(Place.HOUSE_TYPE_IDENTIFICATOR)) {
                Utils.showErrorAlert("Error: house not found", "The place you selected is not an house");
                return;
            }
            house = maybeAnHouse;
            ((Stage)buttonSelect.getScene().getWindow()).close();
        });
    }    

    public Place getHouse() {
        return house;
    }
    
}
