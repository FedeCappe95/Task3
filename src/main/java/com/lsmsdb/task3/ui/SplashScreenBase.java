package com.lsmsdb.task3.ui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * Graphic component to show a loading splash screen at the boot phase of the
 * application.
 * This is the "Base" class: it is abstract since it is only an organized
 * collection of sub-graphical components and it does not do anything. It must
 * be extended.
 */
public abstract class SplashScreenBase extends AnchorPane {

    protected final ImageView imageView;
    protected final Label label;
    protected final Label labelJob;

    public SplashScreenBase() {

        imageView = new ImageView();
        label = new Label();
        labelJob = new Label();

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(400.0);
        setPrefWidth(623.0);

        AnchorPane.setLeftAnchor(imageView, 0.0);
        AnchorPane.setTopAnchor(imageView, 0.0);
        imageView.setFitHeight(400.0);
        imageView.setFitWidth(700.0);
        imageView.setLayoutX(700.0);
        imageView.setLayoutY(400.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(getClass().getResource("/otherResources/loading.png").toExternalForm()));

        AnchorPane.setLeftAnchor(label, 14.0);
        AnchorPane.setTopAnchor(label, 14.0);
        label.setLayoutX(14.0);
        label.setLayoutY(14.0);
        label.setText("Loading, please wait...");
        label.setFont(new Font("System Bold", 27.0));

        AnchorPane.setBottomAnchor(labelJob, 14.0);
        AnchorPane.setLeftAnchor(labelJob, 14.0);
        AnchorPane.setRightAnchor(labelJob, 14.0);
        labelJob.setAlignment(javafx.geometry.Pos.CENTER);
        labelJob.setLayoutX(14.0);
        labelJob.setLayoutY(369.0);
        labelJob.setText("Starting...");

        getChildren().add(imageView);
        getChildren().add(label);
        getChildren().add(labelJob);

    }
}
