package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Person;
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
 * FXML Controller class that guide the user to add his/er house
 */
public class UserUiAddHouseController implements Initializable {

    /*
     * FXML private data members
    */
    @FXML
    private TextField textFieldArea;
    @FXML
    private TextField textFieldLatitude;
    @FXML
    private TextField textFieldLongitude;
    @FXML
    private TextField textFieldCity;
    @FXML
    private TextField textFieldName;
    @FXML
    private Button buttonAdd;
    
    /*
     * Other private data members
    */
    private Place house;
    private Person person;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        house = null;
        
        person = (Person)rb.getObject("person");
        
        buttonAdd.setOnAction((event) -> {
            long area;
            double latitude;
            double longitude;
            String name = textFieldName.getText();
            String city = textFieldCity.getText();
            if(name.isEmpty()) {
                Utils.showErrorAlert("Error, can not procede", "Please, do not leave the name field empty");
                return;
            }
            if(city.isEmpty()) {
                Utils.showErrorAlert("Error, can not procede", "Please, do not leave the city field empty");
                return;
            }
            try {
                area = Long.parseLong(textFieldArea.getText());
                latitude = Double.parseDouble(textFieldLatitude.getText());
                longitude = Double.parseDouble(textFieldLongitude.getText());
            }
            catch(NumberFormatException ex) {
                ex.printStackTrace();
                Utils.showErrorAlert("Error, can not procede", "Can not parse the data you inserted. Please check all text fields.");
                return;
            }
            
            if(Neo4JManager.getIstance().getPlace(name) != null) {
                Utils.showErrorAlert(
                        "Error: can not procede",
                        "A place named " + name + " already exists in the system.\n" +
                        "Plase, choose another name, maybe something with your surname or a easy to remember name."
                );
                return;
            }
            
            house = new Place(name, city, area, Place.HOUSE_TYPE_IDENTIFICATOR);
            house.setLatitude(latitude);
            house.setLongitude(longitude);
            ((Stage)buttonAdd.getScene().getWindow()).close();
        });
        
    }
    
    public Place getHouse() {
        return house;
    }
    
}
