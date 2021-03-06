package com.lsmsdb.task3.utils;

import com.lsmsdb.task3.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class Utils {
    
    public static byte[] readFromDisk(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
    
    /*public static byte[] readFromClasspath(String filePath) throws URISyntaxException, IOException {
        URL url = Utils.class.getClassLoader().getResource(filePath);
        return Files.readAllBytes(Paths.get(url.toURI()));
    }*/
    
    public static String readAsStringFromClasspath(String filePath) {
        InputStream in = Utils.class.getResourceAsStream(filePath); 
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
    }
    
    public static void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ((Stage)(alert.getDialogPane().getScene().getWindow())).getIcons().add(Main.getProgramIcon());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ((Stage)(alert.getDialogPane().getScene().getWindow())).getIcons().add(Main.getProgramIcon());
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
