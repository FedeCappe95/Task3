package com.lsmsdb.task3;

import com.lsmsdb.task3.ui.SplashScreen;
import com.lsmsdb.task3.ui.UserUiMapController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main class (entry point) of the application
 */
public class Main extends Application {
    
    /*
     * Private static data members
    */
    private static enum WorkingMode {ADMIN_MODE, USER_MODE, LOGIN_MODE};
    private static WorkingMode workingMode;
    
    /**
     * Application entrypoint
     * @param args 
     */
    public static void main(String[] args) {
        System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            "[%1$tF %1$tT %1$tL] %2$s %4$s: %5$s%6$s%n"
        );
        workingMode = WorkingMode.LOGIN_MODE;
        for(String s : args) {
            String slc = s.toLowerCase();
            if(slc.equals("-admin") || slc.equals("--admin")) {
                workingMode = WorkingMode.ADMIN_MODE;
                break;
            }
            if(slc.equals("-user") || slc.equals("--user")) {
                workingMode = WorkingMode.USER_MODE;
                break;
            }
        }
        launch(args);
    }

    /**
     * JavaFX start method
     * @param primaryStage
     * @throws Exception 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        SplashScreen splashScreen = new SplashScreen();
        Scene splashScreenScene = new Scene(splashScreen, 600, 380);
        
        primaryStage.setTitle("Task3");
        primaryStage.setScene(splashScreenScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(getProgramIcon());
        primaryStage.show();
        
        
        Thread initializationThread = new Thread() {
            
            @Override
            public void run() {
                Platform.runLater(() -> {
                    splashScreen.setJob("Initializing...");
                });
                
                try {
                    Thread.sleep(600);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(false) {
                    Platform.runLater(() -> {
                        primaryStage.close();
                        //showError();
                        System.exit(1);
                    });
                    return;
                }
                
                Platform.runLater(() -> {
                    primaryStage.close();
                    if(workingMode == WorkingMode.ADMIN_MODE)
                        showAdminWorkspace();
                    else if(workingMode == WorkingMode.USER_MODE)
                        showUserWorkspace();
                    else
                        showLogin();
                });
            }
            
        };
        
        initializationThread.start();
    }
    
    /**
     * Load and return the program icon in form of a javafx.scene.image.Image
     * instance
     * @return 
     */
    public static Image getProgramIcon() {
        return new Image(Main.class.getResourceAsStream("/otherResources/appIcon.png"));
    }
    
    /**
     * Show the login prompt.
     * When a working mode is selected, the corresponding workspace is shown.
     */
    private void showLogin() {
        String options[] = {"User","Admin"}; 
        ChoiceDialog choiseDialog = new ChoiceDialog(options[0], options);
        choiseDialog.setHeaderText("Login: please select your role");
        choiseDialog.showAndWait();
        String selected = (String)choiseDialog.getSelectedItem();
        switch(selected) {
            case "User":
                showUserWorkspace();
                break;
            case "Admin":
                showAdminWorkspace();
                break;
            default:
                break;
        }
    }
    
    /**
     * After the spash screen and the various checks you can call this function.
     * It will show the stages for the user.
     */
    private void showUserWorkspace() {
        try {
            //Map stage
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent mapRoot = fxmlLoader.load(getClass().getResource("/fxml/UserUiMap.fxml").openStream());
            UserUiMapController mapController = (UserUiMapController) fxmlLoader.getController();
            Scene mapScene = new Scene(mapRoot);
            Stage mapStage = new Stage();
            mapStage.setTitle("Task 3: Map View");
            mapStage.setScene(mapScene);
            mapStage.setOnCloseRequest((event) -> {
                Platform.exit();
            });
            mapStage.getIcons().add(getProgramIcon());
            mapStage.show();

            //Controls tage
            /*Parent ctrlsRoot = FXMLLoader.load(getClass().getResource("/fxml/UserUiControls.fxml"));
            Scene ctrlsScene = new Scene(ctrlsRoot);
            Stage ctrlsStage = new Stage();
            ctrlsStage.setTitle("Task 3: Controllers");
            ctrlsStage.setScene(ctrlsScene);
            ctrlsStage.setOnCloseRequest((event) -> {
                Platform.exit();
            });
            ctrlsStage.getIcons().add(getProgramIcon());
            ctrlsStage.show();*/
        }
        catch(Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Warning: can not procede");
            alert.setContentText(
                "It seems that some important file are missing.\n" + 
                "Maybe you have a corrupted program (jar) or you are using an unsupported version of Java.\n" +
                "The program will now be closed..."
            );
            alert.showAndWait();
            System.exit(1);
        }
    }
    
    /**
     * After the spash screen and the various checks you can call this function.
     * It will show the stage for the admin.
     */
    private void showAdminWorkspace() {
        throw new UnsupportedOperationException("WIP");
    }
    
}
