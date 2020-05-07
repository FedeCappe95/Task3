package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
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
            if(textFieldCity.getText().isEmpty()) {
                Utils.showErrorAlert("Error, can not procede", "Please, do not leave the city field empty");
                return;
            }
            
            house = new Place("house" + person.getSurname(), textFieldCity.getText(), area);
            house.setLatitude(latitude);
            house.setLongitude(longitude);
            ((Stage)buttonAdd.getScene().getWindow()).close();
        });
        
    }
    
    public Place getHouse() {
        return house;
    }
    
}
