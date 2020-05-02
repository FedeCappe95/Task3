package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.beans.Place;
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

/**
 * FXML Controller class for the delegated person panel
 */
public class AdminUiController implements Initializable {

    /*
     * FXML private data members
    */
    @FXML
    private TableView<?> tableViewUsers;
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
    }
    
}
