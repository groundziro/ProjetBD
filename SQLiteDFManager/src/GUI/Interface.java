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
import javafx.scene.control.TextField;
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
    private DFManager dfs = null;
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
                try{
                    dfs = new DFManager(result.getAbsolutePath());
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                }
                List<Button> btns = new ArrayList<>();
                List<Button> dfBtns = new ArrayList<>();
                try{
                    for(DF df : dfs.getDFs()){
                        dfBtns.add(new Button(df.toString()));
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
                Add.setOnAction(add->{
                    HBox h = new HBox();
                    TextField table = new TextField("Table");
                    TextField lhs = new TextField("LeftHandSide");
                    TextField rhs = new TextField("RightHandSide");
                    h.getChildren().addAll(table,lhs,rhs);
                    Button confirm = new Button("Confirm");
                    confirm.setOnAction(confirmed->{
                        try {
                            add(table.getText(),lhs.getText(),rhs.getText());
                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }
                    });
                    choice.setCenter(table);
                    choice.setBottom(confirm);
                });
                Modify.setOnAction(mod->{
                    VBox v = new VBox();
                    HBox h = new HBox();
                    TextField lhs = new TextField("LeftHandSide");
                    TextField rhs = new TextField("RightHandSide");
                    h.getChildren().addAll(lhs,rhs);
                    for(Button b : dfBtns){
                        b.setOnAction(mod1->{
                            try {
                                modify(b.getText(),lhs.getText(),rhs.getText());
                            } catch (SQLException ex) {
                                System.out.println(ex.getMessage());
                            }
                        });
                        v.getChildren().add(b);
                    }
                   choice.setTop(h);
                   choice.setCenter(v);
                   primaryStage.setScene(new Scene(choice));
                });
                Exit.setOnAction(quit->{
                    primaryStage.setScene(scene);
                });
                Delete.setOnAction((ActionEvent del)->{
                    VBox v = new VBox();
                    for(Button b : dfBtns){
                        b.setOnAction((ActionEvent del1)->{
                            try {
                                delete(b.getText());
                            } catch (SQLException ex) {
                                System.out.println(ex.getMessage());
                            }
                        });
                        v.getChildren().add(b);
                    }
                   choice.setCenter(v);
                   choice.setBottom(Exit);
                   primaryStage.setScene(new Scene(choice));
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
    private void add(String table,String lhs,String rhs)throws SQLException{
        if(!dfs.getTabNames().contains(table))
            return;
        DF newFunc = new DF(table,lhs,rhs);
        dfs.getDFs().add(newFunc);
    }
    private void modify(String df,String lhs,String rhs)throws SQLException{
        for(DF func : dfs.getDFs()){
            if(func.toString().equals(df)){
                DF newFunc = new DF(func.getTableName(),lhs,rhs);
                dfs.getDFs().add(dfs.getDFs().indexOf(func), newFunc);
                dfs.getDFs().remove(func);
            }
        } 
    }
    private void delete(String df)throws SQLException{
        for(DF func : dfs.getDFs()){
            if(func.toString().equals(df)){
                dfs.getDFs().remove(func);
            }
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
