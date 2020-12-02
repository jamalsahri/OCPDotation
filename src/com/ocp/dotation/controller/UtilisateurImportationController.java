/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocp.dotation.controller;

import com.jfoenix.controls.JFXButton;
import com.ocp.dotation.controller.dao.Dao;
import com.ocp.dotation.model.Demandeur;
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
public class UtilisateurImportationController implements Initializable {
    @FXML
    private TableView<Demandeur> tv_Utilisateur;
    @FXML
    private TableColumn tc_matricule;
    @FXML
    private TableColumn tc_nom;
    @FXML
    private TableColumn tc_prenom;
    @FXML
    private TableColumn tc_entite;
    @FXML
    private TableColumn tc_SA;
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
    private ObservableList<Demandeur> demandeurs;
    private ArrayList<String> listOfValidDemandeur = new ArrayList<>();
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
                Iterator<Demandeur> it = demandeurs.iterator();
                Demandeur demandeur;
                Dao dao = new Dao();
                while( it.hasNext() ){
                    demandeur = it.next();
                    if( listOfValidDemandeur.contains(demandeur.getMatricule()) ){
                        System.out.println("Valid pour l'inserer : "+demandeur);
                        dao.insertDemandeur(demandeur);
                    }
                }
            }catch(Exception e){
                APPException.showErrorMessage( Arrays.toString(e.getStackTrace()), Alert.AlertType.NONE);
            }
        });
        Platform.runLater(() -> {
            try{
                ArrayList<Demandeur> list = new ArrayList<>(demandeurs);
                demandeurs.clear();
                demandeurs.addAll(list);
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
            Demandeur demandeur;
            ArrayList<Demandeur> list = new ArrayList<>(); 
            for(int i=1; i<=sheet.getLastRowNum(); i++){
                row = sheet.getRow(i);
                
                demandeur = new Demandeur(row.getCell(0).getStringCellValue(),
                                          row.getCell(1).getStringCellValue(),
                                          row.getCell(2).getStringCellValue(),
                                          row.getCell(3).getStringCellValue(),
                                          row.getCell(4).getStringCellValue());
                System.out.println(""+demandeur);
                list.add(demandeur);
                //demandeurs.add(demandeur);
            }
            demandeurs.addAll(list);
            System.out.println("Taille: "+tv_Utilisateur.getItems().size());
        } catch (FileNotFoundException ex) {
            APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
        } catch (IOException ex) {
            APPException.showErrorMessage( Arrays.toString(ex.getStackTrace()), Alert.AlertType.NONE);
        }
            
    }
    
    private void configTableView(){
        demandeurs = FXCollections.observableArrayList();
            Callback<TableColumn<Demandeur, String>, TableCell<Demandeur, String>> cellFactoryValider  =  (TableColumn<Demandeur, String> param)->{
                
                final TableCell<Demandeur, String> cell = new TableCell<Demandeur,String>(){
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
                                Demandeur mDemandeur = getTableView().getItems().get(getIndex());
                                
                                if(t>-1 && insertionProcess==false){
                                    Dao dao = new Dao();
                                    //
                                    if( !listOfValidDemandeur.contains(mDemandeur.getMatricule()) && !dao.isUniqueDemandeur(mDemandeur.getMatricule())){
                                        s++;
                                        listOfValidDemandeur.add(mDemandeur.getMatricule());
                                        setGraphic(valid);
                                    }else {
                                        f++;
                                        setGraphic(invalid);
                                    }
                                    System.out.println("s:"+s+" f:"+f);
                                    failure.setText((float)(f*100)/demandeurs.size()+"%");
                                    succes.setText((float)(s*100)/demandeurs.size()+"%");
                                    setText(null);
                                }
                                if(insertionProcess==true){
                                    if(  listOfValidDemandeur.contains(mDemandeur.getMatricule())){
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
            
            Callback<TableColumn<Demandeur, String>, TableCell<Demandeur, String>> cellFactoryInserer  =  (TableColumn<Demandeur, String> param)->{
                
                final TableCell<Demandeur, String> cell = new TableCell<Demandeur,String>(){
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
                                    Demandeur mDemandeur = getTableView().getItems().get(getIndex());
                                    if(listOfValidDemandeur.contains(mDemandeur.getMatricule())){
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
                tc_matricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
                tc_nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
                tc_prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
                tc_entite.setCellValueFactory(new PropertyValueFactory<>("entite"));
                tc_SA.setCellValueFactory(new PropertyValueFactory<>("SA"));
                
                tv_Utilisateur.setItems( demandeurs );
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
