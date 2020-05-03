package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.Configuration;
import com.lsmsdb.task3.Main;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML
    private Button buttonRefreshRiskOfInfection;
    @FXML
    private Button buttonFind;
    @FXML
    private SplitMenuButton splitMenuDistance;
    @FXML
    private TextField textFieldSelectedPlaceRiskOfInfection;
    @FXML
    private TableView<Place> tablePlaces;
    @FXML
    private Label labelRiskOfInfection;
    @FXML
    private Button buttonMostCriticalPlacesRefresh;
    
    
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
        tablePlaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for(TableColumn tc : tablePlaces.getColumns()) {
            switch(tc.getText()) {
                case "Place":
                    tc.setCellValueFactory(new PropertyValueFactory("name"));
                    break;
                case "Risk of infection":
                    tc.setCellValueFactory(new PropertyValueFactory("infectionRisk"));
                    break;
            }
        }
        
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
            mapStage.getIcons().add(Main.getProgramIcon());
            mapStage.show();
        } catch (IOException ex) {
            Logger.getLogger(UserUiControlPanelController.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        splitMenuDistance.getItems().clear();
        for(String menuItemText : Configuration.getDistanceLookupTable().keySet()) {
            MenuItem menuItem = new MenuItem(menuItemText);
            menuItem.setOnAction((event) -> {
                splitMenuDistance.setText(menuItemText);
            });
            splitMenuDistance.getItems().add(menuItem);
        }
        
        buttonRefreshRiskOfInfection.setOnAction((event) -> {
            labelRiskOfInfection.setText(
                Neo4JManager.getIstance().userRiskOfInfection(
                    person.getId(), Configuration.getValidityPeriod()
                ).toString()
            );
        });
        
        buttonFind.setOnAction((event) -> {
            Integer hopNumber = Configuration.getDistanceLookupTable().get(splitMenuDistance.getText());
            if(hopNumber == null) {
                Utils.showErrorAlert("Error", "Before continue, select a valid distance");
                return;
            }
            Long inagsd = Neo4JManager.getIstance().infectedInAGivenSocialDistance(
                    person.getId(),
                    hopNumber.longValue(),
                    Configuration.getValidityPeriod()
            );
            Utils.showInfoAlert("Result", "I found " + inagsd + (inagsd == 1 ? " person" : " people"));
        });
        
        buttonMostCriticalPlacesRefresh.setOnAction((event) -> {
            retriveAndShowMostCriticalPlaces();
        });
    }
    
    public void retriveAndShowMostCriticalPlaces() {
        try {
            mapController.clearMarkers();
        }
        catch(Exception e) {
            e.printStackTrace();
            tablePlaces.getItems().clear();
            return;
        }
        List<Place> places = Neo4JManager.getIstance().userMostRiskfulPlace(
                person.getId(),
                Configuration.getUserMostCriticalPlacesNumber(),
                Configuration.getValidityPeriod()
        );
        tablePlaces.getItems().setAll(places);
        for(Place place : places) {
            mapController.createAndAddMarker(
                    new Coordinate(place.getName(), place.getLatitude(), place.getLongitude())
            );
        }
    }
    
}
