package GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
        BorderPane choice = new BorderPane();
        BorderPane root = new BorderPane();
        Region up = new Region();
        up.setPrefSize(100,50);
        root.setCenter(Browse);
        root.setTop(up);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        Browse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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
                List<Button> btns = new ArrayList<>();
                List<Button> dfBtns = new ArrayList<>();
                try{
                    for(DF df : dfs.getDFs()){
                        btns.add(new Button(df.toString()));
                    }
                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
                try{
                    for(String table : dfs.getTabNames()){
                        btns.add(new Button(table));
                    }
                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
                Button Add = new Button("Add DF");
                Button Exit = new Button("Exit");
                Button Check = new Button("Check...");
                Button Modify = new Button("Modify...");
                Button Delete = new Button("Delete...");
                Delete.setOnAction((ActionEvent del)->{
                    VBox v = new VBox();
                    for(Button b : dfBtns){
                        b.setOnAction(del1->{
                            dfs.getDFs().remove(df);
                        });
                        v.getChildren().add(b);
                    }
                   choice.setCenter(v);
                   primaryStage.setScene(new Scene(choice));
                });
                Modify.setOnAction(mod->{
                    VBox v = new VBox();
                    for(Button b : dfBtns){
                        v.getChildren().add(b);
                    }
                   choice.setCenter(v);
                });
                Exit.setOnAction(quit->{
                    primaryStage.setScene(scene);
                });
                p.setBottom(new HBox(Add,Exit,Check,Modify,Delete));
                try{
                    p.setCenter(current(dfs));
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                }
                Check.setOnAction(check->{
                    VBox v = new VBox();
                    for(Button b : btns){
                        b.setOnAction(check1->{
                            checkBCNF(b.getText());
                            check3NF(b.getText());
                        });
                        v.getChildren().add(b);
                    }
                   choice.setCenter(v);
                   primaryStage.setScene(new Scene(choice));
                });
                Scene Tables = new Scene(p);
                primaryStage.setScene(Tables);
            }
        });
        primaryStage.show();
        }
    
    /*private Text current(DFManager df) throws SQLException{
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
    }*/
    private Text current(DFManager df) throws SQLException{
        Text txt = new Text();
        String str="";
        ArrayList<ArrayList<DF>> array = DFManager.orderDFList(df.getDFs());
        for(ArrayList<DF> table : array){
            str+= table.get(0).getTableName()+":\n";
            for(DF func : table){
                str+="\t"+func.toString()+"\n";
            }
        }
        txt.setText(str);
        return txt;
    }
    private void initVBox(VBox v, DFManager dfs)throws SQLException{
        for(DF df : dfs.getDFs()){
            Button b = new Button(df.toString());
            v.getChildren().add(b);
        }
    }
    private boolean check3NF(String table){
        return DF.check3NF(table);
    } 
    private boolean checkBCNF(String table){
        return DF.checkBCNF(table);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
       // System.out.println("ok");
    }
    
}
