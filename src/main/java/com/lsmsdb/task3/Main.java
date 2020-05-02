package com.lsmsdb.task3;

import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import com.lsmsdb.task3.ui.SplashScreen;
import com.lsmsdb.task3.ui.UserUiMapController;
import com.lsmsdb.task3.utils.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
                
                Neo4JManager.init();
                
                if(!Neo4JManager.isConnected()) {
                    Platform.runLater(() -> {
                        primaryStage.close();
                        Utils.showErrorAlert(
                            "Error: can not procede",
                            "Error initializind Neo4J connection.\n" +
                            "Please, verify if the database is reachable."
                        );
                        System.exit(1);
                    });
                    return;
                }
                
                Platform.runLater(() -> {
                    primaryStage.close();
                    if(workingMode == WorkingMode.ADMIN_MODE)
                        showAdminWorkspace();
                    else if(workingMode == WorkingMode.USER_MODE)
                        showUserSignIn();
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
    private static void showLogin() {
        String options[] = {"User","Admin (Delegated person)"}; 
        ChoiceDialog choiseDialog = new ChoiceDialog(options[0], options);
        choiseDialog.setHeaderText("Login: please select your role");
        choiseDialog.showAndWait();
        String selected = (String)choiseDialog.getSelectedItem();
        switch(selected) {
            case "User":
                showUserSignIn();
                break;
            case "Admin (Delegated person)":
                showAdminWorkspace();
                break;
            default:
                break;
        }
    }
    
    /**
     * After the spash screen and the various checks you can call this function.
     * It will show the stage for the user.
     */
    private static void showUserSignIn() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/SignInUi.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Sign in");
            stage.setScene(scene);
            stage.getIcons().add(getProgramIcon());
            stage.setResizable(false);
            stage.show();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            showErrorAlertJarCorrupted();
            System.exit(1);
        }
    }
    
    /**
     * After the spash screen and the various checks you can call this function.
     * It will show the stage for the admin.
     */
    private static void showAdminWorkspace() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/AdminUi.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Admin panel");
            stage.setScene(scene);
            stage.getIcons().add(getProgramIcon());
            stage.setResizable(false);
            stage.show();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            showErrorAlertJarCorrupted();
            System.exit(1);
        }
    }
    
    /**
     * Show an arror alert that inform the user that an eror occured during
     * the loading phase
     */
    private static void showErrorAlertJarCorrupted() {
        Utils.showErrorAlert(
            "Error: can not procede",
            "It seems that some important file are missing.\n" + 
            "Maybe you have a corrupted program (jar) or you are using an unsupported version of Java.\n" +
            "The program will now be closed..."
        );
    }

    @Override
    public void stop() throws Exception {
        Neo4JManager.explicitClose();
        System.out.println("Goodbye! :)");
        super.stop();
    }
    
}
