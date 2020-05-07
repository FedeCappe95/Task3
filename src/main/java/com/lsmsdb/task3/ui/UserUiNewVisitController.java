package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * FXML Controller class for the panel that guides the user to add a new visit
 */
public class UserUiNewVisitController implements Initializable {

    /*
    * FXML private data members
    */
    @FXML
    private TextField textFieldSearch;
    @FXML
    private TableView<Place> tablePlaces;
    @FXML
    private Button buttonAdd;
    @FXML
    private DatePicker datePickerDate;
    @FXML
    private TextField textFieldTime;
    
    /*
     * Other private data memeber
    */
    private Person person;
    private Place house;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        person = (Person)rb.getObject("person");
        house = Neo4JManager.getIstance().getHouse(person.getFiscalCode());
        if(house == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "house is null");
        }
        
        tablePlaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(TableColumn tc : tablePlaces.getColumns()) {
            switch(tc.getText()) {
                case "Place name":
                    tc.setCellValueFactory(new PropertyValueFactory("name"));
                    break;
                case "Latitude":
                    tc.setCellValueFactory(new PropertyValueFactory("latitude"));
                    break;
                case "Longitude":
                    tc.setCellValueFactory(new PropertyValueFactory("longitude"));
                    break;
            }
        }
        
        textFieldSearch.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                String city = textFieldSearch.getText();
                tablePlaces.getItems().clear();
                if(house != null)
                    tablePlaces.getItems().add(house);
                if(!city.isEmpty()) {
                    tablePlaces.getItems().addAll(
                            Neo4JManager.getIstance().getAllPlaceByCity(city)
                    );
                }
            }
        });
        
        buttonAdd.setOnAction((event) -> {
            Place place = tablePlaces.getSelectionModel().getSelectedItem();
            if(place == null) {
                Utils.showErrorAlert("Error, can not procede", "Please, select a place");
                return;
            }
            long timestamp;
            try {
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                timestamp = parser.parse(
                        datePickerDate.getEditor().getText() + " " + textFieldTime.getText()
                ).getTime();
            }
            catch(ParseException ex) {
                ex.printStackTrace();
                Utils.showErrorAlert(
                        "Error, can not procede",
                        "There was an error parsing the data you inserted.\n" +
                        "Please, check them all and retry."
                );
                return;
            }
            Neo4JManager.getIstance().visit(person.getFiscalCode(), place.getName(), timestamp);
            getStage().close();
        });
    }
    
    
    
    /*
     * Private functions
    */
    private Stage getStage() {
        return (Stage)buttonAdd.getScene().getWindow();
    }
    
}
