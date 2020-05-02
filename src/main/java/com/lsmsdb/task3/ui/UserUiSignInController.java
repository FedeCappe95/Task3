package com.lsmsdb.task3.ui;

import com.lsmsdb.task3.utils.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class for the user sign in panel
 */
public class UserUiSignInController implements Initializable {

    /*
     * Private static final data members
    */
    private static final String BUTTON_CHANGE_INTERNAL_FRAME_TEXT_1 = "Where do my info go?";
    private static final String BUTTON_CHANGE_INTERNAL_FRAME_TEXT_2 = "Go back";
    private static final String PRIVACY_POLICY_TXT_PATH = "/otherResources/privacyPolicy.txt";
    
    
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
            Logger.getLogger(UserUiSignInController.class.getName()).log(Level.SEVERE, null, ex);
            labelPrivacyPolicy.setText("Error loading privacy policy");
        }
        
        buttonLogIn.setOnAction((event) -> {
            signIn(textFieldLoginUserId.getText());
        });
        
        buttonSignUpAndLogin.setOnAction((event) -> {
            signUpAndLogin(textFieldSignUpName.getText(), textFieldSignUpSurname.getText(), textFieldSignUpUserId.getText());
        });
    }
    
    public static void signIn(String userId) {
        if(userId == null || userId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error: can not procede");
            alert.setContentText(
                "To sign in, please, insert your userId"
            );
            alert.showAndWait();
            return;
        }
    }
    
    public static void signUpAndLogin(String name, String surname, String userId) {
        if(userId == null || name == null || surname == null ||
           userId.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error: can not procede");
            alert.setContentText(
                "To sign up, please, fill the form with your data.\n" + 
                "All the field are mandatory.\n" +
                "To get more information about how the application will use your data, plase click on the left bottom purle button."
            );
            alert.showAndWait();
            return;
        }
    }
    
}
