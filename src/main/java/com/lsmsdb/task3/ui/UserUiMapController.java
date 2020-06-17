package com.lsmsdb.task3.ui;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Circle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import netscape.javascript.JSObject;

/**
 * FXML Controller class for the Map.
 * It is also an interface to control the map from the outside.
 */
public class UserUiMapController implements Initializable {

    /*
     * FXML Objects
    */
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ToolBar toolBar;
    @FXML
    private GoogleMapView mapView;

    /*
     * Constants
    */
    private static final double INITIAL_MAP_POSITION_LATITUDE = 43.718609D;
    private static final double INITIAL_MAP_POSITION_LONGITUDE = 10.942520D;
    private static final Logger LOGGER = Logger.getLogger(UserUiMapController.class.getName());
    
    /*
     * Other objects
    */
    private boolean mapIsInitialized;
    private Runnable onCenterChangeListener;
    private GoogleMap map;
    private MapOptions mapOptions;
    private Label lblZoom;
    private Label lblCenter;
    private Label lblRadius;
    private ComboBox<MapTypeIdEnum> mapTypeCombo;
    private Button btnZoomOut;
    private Button btnZoomIn;
    private Circle circle;
    private List<Marker> markers;

    /**
     * Initializes the controller class.
     * This is the initialization first stage.
     * In total, the initialization is composed by 3 stages
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapIsInitialized = false;
        
        onCenterChangeListener = null;
        
        markers = new ArrayList<>();
        
        btnZoomIn = new Button("Zoom In");
        btnZoomIn.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() + 1);
        });
        btnZoomIn.setDisable(true);

        btnZoomOut = new Button("Zoom Out");
        btnZoomOut.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() - 1);
        });
        btnZoomOut.setDisable(true);

        lblZoom = new Label("N/D");
        lblCenter = new Label("N/D");
        lblRadius = new Label("5 Km");
        
        mapTypeCombo = new ComboBox<>();
        mapTypeCombo.setOnAction(e -> {
           map.setMapType(mapTypeCombo.getSelectionModel().getSelectedItem());
        });
        mapTypeCombo.setDisable(true);
		
        toolBar.getItems().addAll(
                btnZoomIn,
                btnZoomOut,
                mapTypeCombo,
                new Label("Zoom: "), lblZoom,
                new Label("Center: "), lblCenter,
                new Label("Radius: "), lblRadius
	);
        
        mapInitialization();
    }
    
    /**
     * Initializes the map.
     * This is the initialization second stage
     */
    public void mapInitialization() {
        //mapView.setKey("AIzaSyBYpFGg3-OsvEG6Ic5w7HVjVckyfgDD3r4");
        MapComponentInitializedListener mapComponentInitializedListener = new MapComponentInitializedListener() {
            @Override
            public void mapInitialized() {
                //Set the initial options of the map.
                mapOptions = new MapOptions();
                mapOptions.center(new LatLong(INITIAL_MAP_POSITION_LATITUDE, INITIAL_MAP_POSITION_LONGITUDE))
                        .mapType(MapTypeIdEnum.ROADMAP)
                        .overviewMapControl(false)
                        .panControl(false)
                        .rotateControl(false)
                        .scaleControl(false)
                        .streetViewControl(false)
                        .zoomControl(false)
                        .zoom(12)
                        //.styleString("[{'featureType':'landscape','stylers':[{'saturation':-100},{'lightness':65},{'visibility':'on'}]},{'featureType':'poi','stylers':[{'saturation':-100},{'lightness':51},{'visibility':'simplified'}]},{'featureType':'road.highway','stylers':[{'saturation':-100},{'visibility':'simplified'}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]")
                ;
                map = mapView.createMap(mapOptions);
                
                //UI event handlers (map)
                map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
                    LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
                    centerMap(ll);
                });
                
                //Next initialization step
                postMapInitialization();
            }
        };
        
        /*
         * GMapsFX has a bug solved in release 2.12.2.
         * Previous release has a the method (GoogleMapView.addMapInializedListener)
         * that is clearly miss typed. This program should support every version.
         * Using only
         *   mapView.addMapInializedListener(mapComponentInitializedListener);
         * or
         *   mapView.addMapInitializedListener(mapComponentInitializedListener);
         * would not be correct
        */
        Method initMethod;
        try {
            initMethod = GoogleMapView.class.getMethod("addMapInitializedListener", MapComponentInitializedListener.class);
        }
        catch(NoSuchMethodException ex) {
            LOGGER.log(Level.INFO,"Use the old GMapsFX map initialization method");
            try {
                initMethod = GoogleMapView.class.getMethod("addMapInializedListener", MapComponentInitializedListener.class);
            }
            catch(NoSuchMethodException ex1) {
                LOGGER.log(Level.SEVERE, "Can not find GMAPFX initialization method", ex1);
                ex1.printStackTrace();
                System.exit(1);
                return;
            }
        }
        try {
            initMethod.invoke(mapView, mapComponentInitializedListener);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Initializes remaining components.
     * This is the initialization third stage
     */
    public void postMapInitialization() {
        //The labels "acquire" observable values from the map
        lblCenter.setText(map.getCenter().toString());
        map.centerProperty().addListener((ObservableValue<? extends LatLong> obs, LatLong o, LatLong n) -> {
            lblCenter.setText(n.toString());
        });

        lblZoom.setText(Integer.toString(map.getZoom()));
        map.zoomProperty().addListener((ObservableValue<? extends Number> obs, Number o, Number n) -> {
            lblZoom.setText(n.toString());
        });
        
        //mapTypeCombo has to be populated and activated
        mapTypeCombo.getItems().addAll(MapTypeIdEnum.ALL);
        mapTypeCombo.getSelectionModel().select(1);
        mapTypeCombo.setDisable(false);
        
        //Also the zoom buttons have to be enabled
        btnZoomIn.setDisable(false);
        btnZoomOut.setDisable(false);
        
        mapIsInitialized = true;
    }

    /**
     * Center the map to location
     * @param location 
     */
    protected void centerMap(LatLong location) {
        map.setCenter(location);
        circle.setCenter(location);
        if(onCenterChangeListener != null)
            onCenterChangeListener.run();
    }
    
    /**
     * Create and add a marker on the map
     * @param location
     * @return the new marker
     */
    protected Marker createAndAddMarker(LatLong location) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        Marker marker = new Marker(markerOptions);
        map.addMarker(marker);
        markers.add(marker);
        return marker;
    }
    
    
    
    
    /*
    * The following methods are used to "control" the map and to retrieve data
    * from the outside.
    * In particular, the user will interact with a GUI class which it will call
    * these methods.
    */
    
    /**
     * Create and add a marker on the map
     * @param coordinate
     * @return the new marker
     */
    public Marker createAndAddMarker(Coordinate coordinate) {
        if(!mapIsInitialized)
            return null;
        return createAndAddMarker(new LatLong(coordinate.getLatitude(), coordinate.getLongitude()));
    }
    
    /**
     * Center the map to coordinates
     * @param coordinate 
     */
    public void centerMap(Coordinate coordinate) {
        if(!mapIsInitialized)
            return;
        centerMap(new LatLong(coordinate.getLatitude(), coordinate.getLongitude()));
    }
    
    /**
     * Return the actual center of the map that it is also the center of the
     * cirle
     * @return 
     */
    public Coordinate getCenter() {
        return new Coordinate("", circle.getCenter().getLatitude(), circle.getCenter().getLongitude());
    }
    
    /**
     * Set a listener (Runnable) for when the center changes
     * @param onCenterChangeListener 
     */
    public void setOnCenterChangeListener(Runnable onCenterChangeListener) {
        this.onCenterChangeListener = onCenterChangeListener;
    }
    
    /**
     * Remove the listener (Runnable) for when the center changes
     */
    public void removeOnCenterChangeListener() {
        this.onCenterChangeListener = null;
    }
    
    /**
     * Remove all markers from the map
     */
    public void clearMarkers() {
        if(!mapIsInitialized)
            return;
        map.clearMarkers();
        markers.clear();
    }
    
    /**
     * Remove one marker from the map
     * @param marker The marker to remove from the map
     */
    public void removeMarker(Marker marker) {
        if(!mapIsInitialized)
            return;
        map.removeMarker(marker);
        markers.remove(marker);
    }
    
    /**
     * Open an InfoWindows infoWindow for the Marker marker
     * @param infoWindow
     * @param marker 
     */
    public void openInfoWindow(InfoWindow infoWindow, Marker marker) {
        if(!mapIsInitialized)
            return;
        infoWindow.open(map, marker);
    }
    
}
