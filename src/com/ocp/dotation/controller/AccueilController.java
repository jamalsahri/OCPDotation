/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocp.dotation.controller;

import com.gn.GNCarousel;
import java.net.URL;
import javafx.util.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Jamal
 */
public class AccueilController implements Initializable {
    @FXML
    private StackPane root;
    @FXML
    private VBox slider;
    @FXML
    private GNCarousel carousel;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            carousel.setItems(createItems());
            carousel.autoRideProperty().setValue(Boolean.TRUE);
        } catch (Exception ex) {
            Logger.getLogger(AccueilController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    private ObservableList<Node> createItems(){

        Label lb1 = new Label("First");
        Label lb2 = new Label("Second");
        Label lb3 = new Label("Third");
        Label lb4 = new Label("Fourth");
        Label lb5 = new Label("Fifth");

        lb1.setStyle("-fx-text-fill : white;");
        lb2.setStyle("-fx-text-fill : white;");
        lb3.setStyle("-fx-text-fill : white;");
        lb4.setStyle("-fx-text-fill : white;");
        lb5.setStyle("-fx-text-fill : white;");

        VBox v1 = new VBox(lb1);
        VBox v2 = new VBox(lb2);
        VBox v3 = new VBox(lb3);
        VBox v4 = new VBox(lb4);
        VBox v5 = new VBox(lb5);

        v1.setAlignment(Pos.CENTER);
        v2.setAlignment(Pos.CENTER);
        v3.setAlignment(Pos.CENTER);
        v4.setAlignment(Pos.CENTER);
        v5.setAlignment(Pos.CENTER);

        v1.getStyleClass().add("slider1");
        v2.getStyleClass().add("slider2");
        v3.getStyleClass().add("slider3");
        v4.getStyleClass().add("slider4");
        v5.getStyleClass().add("slider5");
        //v1.setStyle("-fx-background-image:url('ocp.jpg'); -fx-background-size:cover;");
        //v2.setStyle("-fx-background-color : #512DA8;");
        //v3.setStyle("-fx-background-color : #48CFAD;");
        //v4.setStyle("-fx-background-color : #02C852;");
        //v5.setStyle("-fx-background-color : #EC407A;");

        return FXCollections.observableArrayList(v1, v2, v3, v4, v5);
    }
}
