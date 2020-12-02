/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocp.dotation.controller;

import com.jfoenix.controls.JFXButton;
import com.ocp.dotation.controller.dao.Dao;
import com.ocp.dotation.model.Materiel;
import com.ocp.dotation.util.APPException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Jamal
 */
public class MaterielImportationController implements Initializable {
    @FXML
    private TableView<Materiel> tv_Materiel;
    @FXML
    private TableColumn tc_numSerie;
    @FXML
    private TableColumn tc_libelle;
    @FXML
    private TableColumn tc_model;
    @FXML
    private TableColumn tc_lieuGeo;
    @FXML
    private TableColumn tc_valider;
    @FXML
    private TableColumn tc_inserer;
    @FXML
    private Label succes;
    @FXML
    private Label failure;
    @FXML
    private JFXButton annuler;
    @FXML
    private JFXButton Injecter;

    
    
    private File src ;
    private ObservableList<Materiel> materiels;
    private ArrayList<String> listOfValidMateriel = new ArrayList<>();
    private int f = 0; //failure pourcent "compteur"
    private int s = 0; //succes pourcent  "compteur"
    private int t = -1; //total
    private Stage stage;
    private boolean insertionProcess = false;
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        configTableView();
    }    
    
    public void setStage(final Stage stage){
        this.stage = stage;
    }
    
    public void config( final File src ){
        this.src = src;
    }
    

    @FXML
    private void Annuler(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void Injecter(ActionEvent event) {
        Injecter.disableProperty().setValue(Boolean.TRUE);
        annuler.setText("Terminer");
        annuler.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
        Platform.runLater(() -> {
            try{
                insertionProcess = true;
                Iterator<Materiel> it = materiels.iterator();
                Materiel materiel;
                Dao dao = new Dao();
                while( it.hasNext() ){
                    materiel = it.next();
                    if( listOfValidMateriel.contains(materiel.getNumeroDeSerie()) ){
                        System.out.println("Valid pour l'inserer : "+materiel);
                        dao.insertMateriel(materiel);
                    }
                }
            }catch(Exception e){
                APPException.showErrorMessage( Arrays.toString(e.getStackTrace()), Alert.AlertType.NONE);
            }
        });
        Platform.runLater(() -> {
            try{
                ArrayList<Materiel> list = new ArrayList<>(materiels);
                materiels.clear();
                materiels.addAll(list);
            }catch(Exception e){
                APPException.showErrorMessage( Arrays.toString(e.getStackTrace()), Alert.AlertType.NONE);
            }
        });
    }
    
    public void readData(){
        try {
            FileInputStream fis = new FileInputStream(src);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Row row ;
            Materiel materiel;
            ArrayList<Materiel> list = new ArrayList<>(); 
            for(int i=1; i<=sheet.getLastRowNum(); i++){
                row = sheet.getRow(i);
                
                materiel = new Materiel(row.getCell(0).getStringCellValue(),
                                        row.getCell(1).getStringCellValue(),
                                        row.getCell(2).getStringCellValue(),
                                        row.getCell(3).getStringCellValue());
                System.out.println(""+materiel);
                list.add(materiel);
            }
            materiels.addAll(list);
            System.out.println("Taille: "+tv_Materiel.getItems().size());
        } catch (FileNotFoundException ex) {
            APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
        } catch (IOException ex) {
            APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
        }
            
    }
    
    private void configTableView(){
        materiels = FXCollections.observableArrayList();
            Callback<TableColumn<Materiel, String>, TableCell<Materiel, String>> cellFactoryValider  =  (TableColumn<Materiel, String> param)->{
                
                final TableCell<Materiel, String> cell = new TableCell<Materiel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if( empty ){
                            setGraphic(null);
                            setText(null);
                        }else{
                            
                            try {
                                final FontAwesomeIconView valid = new FontAwesomeIconView(FontAwesomeIcon.UPLOAD);
                                valid.setStyle("-fx-fill:#0b7dda;");  valid.setSize("16");
                                
                                final FontAwesomeIconView invalid = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
                                invalid.setStyle("-fx-fill:red;");  invalid.setSize("16");
                                Materiel mMateriel = getTableView().getItems().get(getIndex());
                                
                                if(t>-1 && insertionProcess==false){
                                    Dao dao = new Dao();
                                    //
                                    if( !listOfValidMateriel.contains(mMateriel.getNumeroDeSerie()) && !dao.isUniqueMateriel(mMateriel.getNumeroDeSerie())){
                                        s++;
                                        listOfValidMateriel.add(mMateriel.getNumeroDeSerie());
                                        setGraphic(valid);
                                    }else {
                                        f++;
                                        setGraphic(invalid);
                                    }
                                    System.out.println("s:"+s+" f:"+f);
                                    failure.setText((float)(f*100)/materiels.size()+"%");
                                    succes.setText((float)(s*100)/materiels.size()+"%");
                                    setText(null);
                                }
                                if(insertionProcess==true){
                                    if(  listOfValidMateriel.contains(mMateriel.getNumeroDeSerie())){
                                        setGraphic(valid);
                                    }else {
                                        setGraphic(invalid);
                                    }
                                    setText(null);
                                }
                                t++;
                            } catch (Exception ex) {
                                APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
                            }
                        }
                    };
                };
                
                //return the cellule created
                return cell;
            };
            tc_valider.setCellFactory( cellFactoryValider );
            
            Callback<TableColumn<Materiel, String>, TableCell<Materiel, String>> cellFactoryInserer  =  (TableColumn<Materiel, String> param)->{
                
                final TableCell<Materiel, String> cell = new TableCell<Materiel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if( empty ){
                            setGraphic(null);
                            setText(null);
                        }else{
                            
                            try {
                                if( insertionProcess==true ){
                                    //Dao dao = new Dao();
                                    Materiel mMateriel = getTableView().getItems().get(getIndex());
                                    if(listOfValidMateriel.contains(mMateriel.getNumeroDeSerie())){
                                        final FontAwesomeIconView valid = new FontAwesomeIconView(FontAwesomeIcon.SPINNER);
                                        valid.setStyle("-fx-fill:#0b7dda;");  valid.setSize("16");
                                        rotate(valid);
                                        Label progress = new Label("0%", valid);
                                        progress.setMaxHeight(20);
                                        progress.setContentDisplay(ContentDisplay.RIGHT);
                                        setGraphic(progress);
                                        //dao.insertDotation( mDotation );
                                        new Thread(){
                                            @SuppressWarnings("SleepWhileInLoop")
                                            public void run(){

                                                for(int i=0; i<=100; i++){
                                                    try{
                                                        Thread.sleep( 50 );
                                                    }catch(Exception e){
                                                        APPException.showErrorMessage( Arrays.toString(e.getStackTrace()), Alert.AlertType.NONE);
                                                    }
                                                    int counter = i;
                                                    Platform.runLater(() -> {
                                                        progress.setText(counter+"%");
                                                        if(counter==100)
                                                            progress.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
                                                    }); 
                                                }    
                                                
                                            }
                                        }.start();
                                        
                                    }else{
                                        final FontAwesomeIconView invalid = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
                                        invalid.setStyle("-fx-fill:red;");  invalid.setSize("16");
                                        setGraphic(invalid);
                                    }
                                    
                                    //setGraphic(null);    
                                    setText(null);
                                    
                                }
                                
                            } catch (Exception ex) {
                                APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
                            }
                        }
                    };
                };
                
                //return the cellule created
                return cell;
            };
            tc_inserer.setCellFactory( cellFactoryInserer );
            
            
            try{
                tc_numSerie.setCellValueFactory(new PropertyValueFactory<>("numeroDeSerie"));
                tc_libelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
                tc_model.setCellValueFactory(new PropertyValueFactory<>("model"));
                tc_lieuGeo.setCellValueFactory(new PropertyValueFactory<>("lieuGeographique"));
                
                tv_Materiel.setItems( materiels );
            }catch(Exception e){
                APPException.showErrorMessage( Arrays.toString(e.getStackTrace()), Alert.AlertType.NONE);
            }
    }
    
    public void rotate(final Node node){
        //Creating a rotate transition    
        RotateTransition rotateTransition = new RotateTransition(); 
      
        //Setting the duration for the transition 
        rotateTransition.setDuration( Duration.millis(2000) ); 
      
        //Setting the node for the transition 
        rotateTransition.setNode(node);       
      
        //Setting the angle of the rotation 
        rotateTransition.setByAngle(360); 
      
        //Setting the cycle count for the transition 
        rotateTransition.setCycleCount( 100000 ); 
      
        //Setting auto reverse value to false 
        rotateTransition.setAutoReverse(false); 
      
        //Playing the animation 
        rotateTransition.play();
    }
}
