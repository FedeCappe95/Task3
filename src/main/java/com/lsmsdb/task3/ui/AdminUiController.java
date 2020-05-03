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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

/**
 * FXML Controller class for the delegated person panel
 */
public class AdminUiController implements Initializable {

    /*
     * FXML private data members
    */
    @FXML
    private Button buttonNewStatusUpdate;
    @FXML
    private TextField textFieldSelectUserUserId;
    @FXML
    private DatePicker datePickerNewStatus;
    @FXML
    private RadioButton radioButtonNewStatusInfected;
    @FXML
    private RadioButton radioButtonNewStatusHealed;
    @FXML
    private TextField textFieldMostCriticalPlacesNumber;
    @FXML
    private Button buttonMostCriticalPlacesCompute;
    @FXML
    private TableView<Place> tableViewMostCriticalPlaces;
    @FXML
    private Button buttonUserShow;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableViewMostCriticalPlaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(TableColumn tc : tableViewMostCriticalPlaces.getColumns()) {
            switch(tc.getText()) {
                case "ID":
                    tc.setCellValueFactory(new PropertyValueFactory("id"));
                    break;
                case "Name":
                    tc.setCellValueFactory(new PropertyValueFactory("name"));
                    break;
                case "Infection Risk":
                    tc.setCellValueFactory(new PropertyValueFactory("infectionRisk"));
                    break;
                case "Latitude":
                    tc.setCellValueFactory(new PropertyValueFactory("latitude"));
                    break;
                case "Longitude":
                    tc.setCellValueFactory(new PropertyValueFactory("longitude"));
                    break;
                case "Type":
                    tc.setCellValueFactory(new PropertyValueFactory("type"));
                    break;
                case "Area":
                    tc.setCellValueFactory(new PropertyValueFactory("area"));
                    break;
            }
        }
        
        buttonUserShow.setOnAction((event) -> {
            showUserInfo(textFieldSelectUserUserId.getText());
        });
        
        textFieldSelectUserUserId.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER))
                showUserInfo(textFieldSelectUserUserId.getText());
        });
    }
    
    public static void showUserInfo(String userId) {
        if(userId.isEmpty()) {
            Utils.showErrorAlert("Error", "To procede, insert a user id");
            return;
        }
        Person person = Neo4JManager.getIstance().login(userId);
        if(person == null) {
            Utils.showErrorAlert("Person not found", userId + " does not correspond to any person in the database");
            return;
        }
        Utils.showInfoAlert(
                "User info",
                "Name: " + person.getName() +
                "\nSurname: " + person.getSurname() +
                "\nInfection timestamp: " + person.getFormattedInfectionDate() +
                "\nHealed timestamp: " + person.getFormattedHealedDate()
        );
    }
    
}
