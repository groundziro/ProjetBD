package GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import main.DF;
import main.DFManager;

/**
 *
 * @author Thomas
 */
public class Interface extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Button Browse = new Button("Browse");
        Browse.setOnAction(actionEvent->{
            FileChooser choose = new FileChooser();
            choose.getExtensionFilters().add(new ExtensionFilter("DataBases","*.db"));
            File result = choose.showOpenDialog(primaryStage);
            BorderPane p = new BorderPane();
            DFManager dfs = null;
            try{
                dfs = new DFManager(result.getAbsolutePath());
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
            Button Add = new Button("Add DF");
            p.setBottom(new HBox(Add));
            try{
            p.setCenter(current(dfs));
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
            Scene Tables = new Scene(p);
            primaryStage.setScene(Tables);
        });
        BorderPane root = new BorderPane();
        Region up = new Region();
        up.setPrefSize(100,50);
        root.setCenter(Browse);
        root.setTop(up);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Text current(DFManager df) throws SQLException{
        Text txt = new Text();
        String str=" ";
        for(String table: df.getTabNames()){
            str+=table+":\n";          
            for(DF func : df.getDFs()){
                str+="\t"+func.toString()+"\n";
            }            
        }
        txt.setText(str);
        return txt;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
       // System.out.println("ok");
    }
    
}
