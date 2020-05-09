package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.Main;
import static com.lsmsdb.task3.Main.getProgramIcon;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class for the user sign in panel
 */
public class SignInUiController implements Initializable {

    /*
     * Private static final data members
    */
    private static final String BUTTON_CHANGE_INTERNAL_FRAME_TEXT_1 = "Where do my info go?";
    private static final String BUTTON_CHANGE_INTERNAL_FRAME_TEXT_2 = "Go back";
    private static final String PRIVACY_POLICY_TXT_PATH = "/otherResources/privacyPolicy.txt";
    private static final Logger LOGGER = Logger.getLogger(SignInUiController.class.getName());
    
    
    /*
     * FXML objects
    */
    @FXML
    private AnchorPane anchorPaneInternalForm;
    @FXML
    private TextField textFieldLoginUserId;
    @FXML
    private Button buttonLogIn;
    @FXML
    private TextField textFieldSignUpName;
    @FXML
    private TextField textFieldSignUpSurname;
    @FXML
    private TextField textFieldSignUpUserId;
    @FXML
    private Button buttonSignUpAndLogin;
    @FXML
    private Button buttonChangeInternalFrame;
    @FXML
    private AnchorPane anchorPaneInternalInfo;
    @FXML
    private Label labelPrivacyPolicy;

    
    /*
     * Other private data memebers
    */
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonChangeInternalFrame.setOnAction((event) -> {
            if(anchorPaneInternalForm.isVisible()) {
                anchorPaneInternalForm.setVisible(false);
                anchorPaneInternalInfo.setVisible(true);
                buttonChangeInternalFrame.setText(BUTTON_CHANGE_INTERNAL_FRAME_TEXT_2);
            } else {
                anchorPaneInternalForm.setVisible(true);
                anchorPaneInternalInfo.setVisible(false);
                buttonChangeInternalFrame.setText(BUTTON_CHANGE_INTERNAL_FRAME_TEXT_1);
            }
        });
        
        try {
            labelPrivacyPolicy.setText(
                    Utils.readAsStringFromClasspath(PRIVACY_POLICY_TXT_PATH)
            );
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            labelPrivacyPolicy.setText("Error loading privacy policy");
        }
        
        buttonLogIn.setOnAction((event) -> {
            signIn(textFieldLoginUserId.getText());
        });
        
        textFieldLoginUserId.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                signIn(textFieldLoginUserId.getText());
                event.consume();
            }
        });
        
        buttonSignUpAndLogin.setOnAction((event) -> {
            signUpAndLogin(textFieldSignUpName.getText(), textFieldSignUpSurname.getText(), textFieldSignUpUserId.getText());
        });
    }
    
    private Stage getStage() {
        return (Stage)buttonLogIn.getScene().getWindow();
    }
    
    private void signIn(String userId) {
        if(userId == null || userId.isEmpty()) {
            Utils.showErrorAlert(
                    "Error: can not procede",
                    "To sign in, please, insert your userId"
            );
            return;
        }
        Person person = Neo4JManager.getIstance().login(userId);
        if(person == null) {
            Utils.showErrorAlert(
                    "Error: failed to log in",
                    "Please check the user id you have inserted"
            );
            return;
        }
        showUserWorkspace(person);
    }
    
    private void signUpAndLogin(String name, String surname, String userId) {
        if(userId == null || name == null || surname == null ||
           userId.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            Utils.showErrorAlert(
                    "Error: can not procede",
                    "To sign up, please, fill the form with your data.\n" + 
                    "All the field are mandatory.\n" +
                    "To get more information about how the application will use your data, plase click on the left bottom purle button."
            );
            return;
        }
        if(Neo4JManager.getIstance().login(userId) != null) {
            Utils.showErrorAlert(
                    "Error: can not sign up",
                    "This user ID is already in use!"
            );
            return;
        }
        
        Person person = new Person(userId, name, surname);
        
        Place house = null;
        final String OPTION_0 = "Add a new house";
        final String OPTION_1 = "Select an existing house";
        ChoiceDialog choiseDialog = new ChoiceDialog(OPTION_0, OPTION_0, OPTION_1);
        //((Stage)choiseDialog.getGraphic().getScene().getWindow()).getIcons().add(Main.getProgramIcon());
        choiseDialog.setHeaderText("Please select an option");
        choiseDialog.showAndWait();
        if(choiseDialog.getResult() == null) {
            return;
        }
        String selected = (String)choiseDialog.getSelectedItem();
        switch(selected) {
            case OPTION_0:
                try {
                    ResourceBundle resourceBundle = new ResourceBundle() {
                        @Override
                        protected Object handleGetObject(String key) {
                            if(key.equals("person"))
                                return person;
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
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setResources(resourceBundle);
                    Parent mapRoot = fxmlLoader.load(SignInUiController.class.getResource("/fxml/UserUiAddHouse.fxml").openStream());
                    UserUiAddHouseController controller = (UserUiAddHouseController)fxmlLoader.getController();
                    Scene scene = new Scene(mapRoot);
                    Stage stage = new Stage();
                    stage.setTitle("Add an house");
                    stage.setScene(scene);
                    stage.getIcons().add(Main.getProgramIcon());
                    stage.setResizable(false);
                    stage.showAndWait();
                    house = controller.getHouse();
                    if(house == null)
                        return;
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
                break;
            case OPTION_1:
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    Parent mapRoot = fxmlLoader.load(SignInUiController.class.getResource("/fxml/UserUiSelectExistingHouse.fxml").openStream());
                    UserUiSelectExistingHouseController controller = (UserUiSelectExistingHouseController)fxmlLoader.getController();
                    Scene scene = new Scene(mapRoot);
                    Stage stage = new Stage();
                    stage.setTitle("Select an house");
                    stage.setScene(scene);
                    stage.getIcons().add(Main.getProgramIcon());
                    stage.setResizable(false);
                    stage.showAndWait();
                    house = controller.getHouse();
                    if(house == null)
                        return;
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
                break;
            default:
                break;
        }
        
        if(Neo4JManager.getIstance().registerPersonAndCreateItsHouse(person, house, System.currentTimeMillis())) {
            Utils.showInfoAlert(
                    "Success",
                    "You request has been correcly executed!"
            );
            showUserWorkspace(person);
        } else {
            Utils.showErrorAlert(
                    "Error can not procede",
                    "There was an error trying to connect to the database or registring your profile or house.\n" +
                    "Please, check your internet connection and the information you have inserted."
            );
        }
    }
    
    private void showUserWorkspace(Person person) {
        try {
            ResourceBundle resourceBundle = new ResourceBundle() {
                @Override
                protected Object handleGetObject(String key) {
                    if(key.equals("person"))
                        return person;
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
            Parent root = FXMLLoader.load(SignInUiController.class.getResource("/fxml/UserUiControlPanel.fxml"),resourceBundle);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("User panel");
            stage.setScene(scene);
            stage.getIcons().add(getProgramIcon());
            stage.setResizable(false);
            stage.setOnCloseRequest((event) -> {
                Platform.exit();
            });
            stage.show();
            getStage().close();
        }
        catch(IOException ex) {
            LOGGER.log(Level.SEVERE,null,ex);
            System.exit(1);
        }
    }
    
}
