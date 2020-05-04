package com.lsmsdb.task3.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    private TableView<?> tablePlaces;
    @FXML
    private Button buttonAdd;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
