package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private Button buttonUserUpdate;
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
            showUserInfo();
        });
        
        textFieldSelectUserUserId.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER))
                showUserInfo();
        });
        
        buttonUserUpdate.setOnAction((event) -> {
            updateUser();
        });
        
        buttonMostCriticalPlacesCompute.setOnAction((event) -> {
            showMostCriticalPlaces();
        });
    }
    
    private void showUserInfo() {
        String userId = textFieldSelectUserUserId.getText();
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
    
    private void updateUser() {
        String userId = textFieldSelectUserUserId.getText();
        if(userId.isEmpty()) {
            Utils.showErrorAlert("Error", "To procede, insert a user id");
            return;
        }
        Person person = Neo4JManager.getIstance().login(userId);
        if(person == null) {
            Utils.showErrorAlert("Person not found", userId + " does not correspond to any person in the database");
            return;
        }
        if(!radioButtonNewStatusInfected.isSelected() && !radioButtonNewStatusHealed.isSelected()) {
            Utils.showErrorAlert("Error", "To procede, chose the new status type (infected or healed)");
            return;
        }
        long timestamp;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            timestamp = parser.parse(datePickerNewStatus.getEditor().getText()).getTime();
        }
        catch(ParseException e) {
            Utils.showErrorAlert("Error", "Please, insert a valid date");
            return;
        }
        if(radioButtonNewStatusInfected.isSelected()) {
            Neo4JManager.getIstance().userUpdateStatus_infected(userId, timestamp);
            Neo4JManager.getIstance().userUpdateStatus_healed(userId, 0L);
        } else {
            if(person.getTimestampInfected() == null || person.getTimestampInfected() == 0L) {
                Utils.showErrorAlert("Error", "You are trying to set a person as healed while he/she was never been infected");
                return;
            }
            if(person.getTimestampHealed() < person.getTimestampInfected()) {
                Utils.showErrorAlert("Error", "You are trying to set a person as healed in a date before his/her infection date");
                return;
            }
            Neo4JManager.getIstance().userUpdateStatus_healed(userId, timestamp);
        }
        showUserInfo();
    }
    
    private void showMostCriticalPlaces() {
        long placeNumber;
        try {
            placeNumber = Long.parseLong(textFieldMostCriticalPlacesNumber.getText());
        }
        catch(NumberFormatException e) {
            Utils.showErrorAlert("Error", "Please, insert a valid number of place to show");
            return;
        }
        if(placeNumber == 0L) {
            tableViewMostCriticalPlaces.getItems().clear();
            return;
        }
        if(placeNumber > 200)
            Utils.showInfoAlert(
                    "Warning",
                    "You are requesting a large amount of places, this could take a while"
            );
        tableViewMostCriticalPlaces.getItems().setAll(
                Neo4JManager.getIstance().mostCriticalPlace(placeNumber)
        );
    }
    
}
