package com.lsmsdb.task3.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


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
    
}
