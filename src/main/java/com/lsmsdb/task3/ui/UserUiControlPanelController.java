package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.Configuration;
import com.lsmsdb.task3.Main;
import static com.lsmsdb.task3.Main.getProgramIcon;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.Marker;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * FXML Controller class for the user control panel
 */
public class UserUiControlPanelController implements Initializable {

    /*
     * CONSTANTS
     */
    private static final Logger LOGGER = Logger.getLogger(UserUiControlPanelController.class.getName());

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
    private TableView<Place> tablePlaces;
    @FXML
    private Label labelRiskOfInfection;
    @FXML
    private Button buttonMostCriticalPlacesRefresh;
    @FXML
    private Button buttonAddANewVisit;
    @FXML
    private Button buttonShowHouseInfo;
    @FXML
    private TextField textFieldShowByCityCity;
    @FXML
    private Button buttonShowByCityGo;
    @FXML
    private Label labelMostRiskfulPlaces;

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
        labelMostRiskfulPlaces.setText(
                String.format(labelMostRiskfulPlaces.getText(), Configuration.getUserMostCriticalPlacesNumber())
        );

        tablePlaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableColumn tc : tablePlaces.getColumns()) {
            switch (tc.getText()) {
                case "Place":
                    tc.setCellValueFactory(new PropertyValueFactory("name"));
                    break;
                case "Risk of infection":
                    tc.setCellValueFactory(new PropertyValueFactory("infectionRisk"));
                    break;
            }
        }

        person = (Person) rb.getObject("person");
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
        for (String menuItemText : Configuration.getDistances()) {
            MenuItem menuItem = new MenuItem(menuItemText);
            menuItem.setOnAction((event) -> {
                splitMenuDistance.setText(menuItemText);
            });
            splitMenuDistance.getItems().add(menuItem);
        }

        buttonRefreshRiskOfInfection.setOnAction((event) -> {
            Long numberOfHops = Neo4JManager.getIstance().userRiskOfInfection(
                    person.getFiscalCode(), Configuration.getValidityPeriod(), System.currentTimeMillis()
            );
            labelRiskOfInfection.setText(Configuration.getInfectionRiskByHopCount(numberOfHops));
        });

        buttonFind.setOnAction((event) -> {
            Integer hopNumber = Configuration.getDistanceLookupTable().get(splitMenuDistance.getText());
            if (hopNumber == null) {
                Utils.showErrorAlert("Error", "Before continue, select a valid distance");
                return;
            }
            Long inagsd = Neo4JManager.getIstance().infectedInAGivenSocialDistance(
                    person.getFiscalCode(),
                    hopNumber.longValue(),
                    Configuration.getValidityPeriod(),
                    System.currentTimeMillis()
            );
            Utils.showInfoAlert("Result", "I found " + inagsd + (inagsd == 1 ? " person" : " people"));
        });

        buttonMostCriticalPlacesRefresh.setOnAction((event) -> {
            retriveAndShowMostCriticalPlaces();
        });

        tablePlaces.setOnMouseClicked((event) -> {
            Place selectedPlace = tablePlaces.getSelectionModel().getSelectedItem();
            if (selectedPlace == null) {
                return;
            }
            mapController.centerMap(
                    new Coordinate(selectedPlace.getLatitude(), selectedPlace.getLongitude())
            );
        });

        buttonAddANewVisit.setOnAction((event) -> {
            try {
                ResourceBundle resourceBundle = new ResourceBundle() {
                    @Override
                    protected Object handleGetObject(String key) {
                        if (key.equals("person")) {
                            return person;
                        }
                        return null;
                    }

                    @Override
                    protected Set<String> handleKeySet() {
                        Set<String> set = new HashSet<>();
                        set.add("person");
                        return set;
                    }

                    @Override
                    public Enumeration<String> getKeys() {
                        return Collections.enumeration(keySet());
                    }
                };
                Parent root = FXMLLoader.load(SignInUiController.class.getResource("/fxml/UserUiNewVisit.fxml"), resourceBundle);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Add a new visit");
                stage.setScene(scene);
                stage.getIcons().add(getProgramIcon());
                stage.setResizable(true);
                stage.showAndWait();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        });

        buttonShowHouseInfo.setOnAction((event) -> {
            Place house = Neo4JManager.getIstance().getHouse(person.getFiscalCode());
            if (house == null) {
                Utils.showErrorAlert(
                        "Error: house not found",
                        "Your house was not found inside the database"
                );
                return;
            }
            Utils.showInfoAlert(
                    "House info",
                    String.format(
                            "Latitude x longitude: %fx%f\nArea: %d\nName: %s",
                            house.getLatitude(), house.getLongitude(), house.getArea(), house.getName()
                    )
            );
        });

        buttonShowByCityGo.setOnAction((event) -> {
            showPlacesByCity();
        });

        textFieldShowByCityCity.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER))
                showPlacesByCity();
        });
    }

    public void retriveAndShowMostCriticalPlaces() {
        try {
            mapController.clearMarkers();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            tablePlaces.getItems().clear();
            return;
        }
        List<Place> places = Neo4JManager.getIstance().userMostRiskfulPlace(
                person.getFiscalCode(),
                Configuration.getUserMostCriticalPlacesNumber(),
                Configuration.getValidityPeriod(),
                System.currentTimeMillis()
        );
        tablePlaces.getItems().setAll(places);
        for (Place place : places) {
            mapController.createAndAddMarker(
                    new Coordinate(place.getName(), place.getLatitude(), place.getLongitude())
            )
                    .setTitle(place.getName() + " - " + place.getInfectionRisk());
        }
    }

    private void showPlacesByCity() {
        String city = textFieldShowByCityCity.getText();
        if (city.isEmpty()) {
            Utils.showErrorAlert(
                    "Error, can not precede",
                    "Please, insert a valid city name"
            );
            return;
        }
        List<Place> places = Neo4JManager.getIstance().getAllPlaceByCity(city);
        mapController.clearMarkers();
        for (Place place : places) {
            Marker marker = mapController.createAndAddMarker(
                    new Coordinate(place.getLatitude(), place.getLongitude())
            );
            marker.setTitle(place.getName() + " - " + place.getInfectionRisk());
            InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
            infoWindowOptions.content(
                    String.format(
                            "<h2>%s</h2>Risk index:%.4f",
                            place.getName(), place.getInfectionRisk()
                    )
            );
            InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
            mapController.openInfoWindow(infoWindow, marker);
        }
    }

}
