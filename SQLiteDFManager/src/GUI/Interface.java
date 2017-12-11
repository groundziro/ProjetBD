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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private ArrayList<DF> deleted = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) {
        Button Browse = new Button("Browse");
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
                while(result==null){
                    
                    result = choose.showOpenDialog(primaryStage);
                }
                BorderPane p = new BorderPane();
                try{
                    dfs = new DFManager(result.getAbsolutePath());
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                }
                List<Button> btns = new ArrayList<>();
                List<Button> dfBtns = new ArrayList<>();
                try{
                    for(ArrayList<DF> table : DFManager.orderDFList(dfs.getDFs())){
                        for(DF df: table){
                            dfBtns.add(new Button(df.toString()));
                        }
                    }
                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
                try{
                    for(String table : dfs.getTabNames()){
                        if(!"FuncDep".equals(table))
                        btns.add(new Button(table));
                    }
                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
                Alert continu = new Alert(AlertType.CONFIRMATION,"Continue?");
                Button Add = new Button("Add DF");
                Button Exit = new Button("Exit");
                Button Check = new Button("Check...");
                Button Modify = new Button("Modify...");
                Button Delete = new Button("Delete...");
                Button Return = new Button("Return");
                p.setBottom(new HBox(Add,Exit,Check,Modify,Delete));
                try{
                    p.setCenter(current(dfs));
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                }
                Scene Tables = new Scene(p);
                Return.setOnAction(back->{
                    BorderPane newP = new BorderPane();
                    try{
                        newP.setCenter(current(dfs));
                    }catch(SQLException e){
                        System.out.println(e.getMessage());
                    }
                    newP.setBottom(new HBox(Add,Exit,Check,Modify,Delete));
                    Scene newTables = new Scene(newP);
                    primaryStage.setScene(newTables);
                 });
                Add.setOnAction(add->{
                    BorderPane choice = new BorderPane();
                    VBox v = new VBox();
                    HBox h = new HBox();
                    TextField table = new TextField();
                    TextField lhs = new TextField();
                    TextField rhs = new TextField();
                    rhs.setPromptText("RightHandSide");
                    table.setPromptText("Table");
                    lhs.setPromptText("LeftHandSide");
                    h.getChildren().addAll(lhs,rhs);
                    v.getChildren().addAll(table,h);
                    Button confirm = new Button("Confirm");
                    confirm.setOnAction(confirmed->{
                            DF df = new DF(table.getText(),lhs.getText(),rhs.getText());
                            Alert alert = new Alert(AlertType.CONFIRMATION,"Do you want to add to "+table.getText()+": "+df.toString());
                            alert.showAndWait().ifPresent(response->{
                                if(response == ButtonType.OK){
                                    try{
                                        add(df);
                                        continu.showAndWait().ifPresent(flux->{
                                            if(flux!=ButtonType.OK)
                                                Return.fire();
                                        });                                        
                                    } catch (SQLException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                    
                                }
                            });
                    });
                    choice.setCenter(v);
                    choice.setBottom(confirm);                            
                    primaryStage.setScene(new Scene(choice));
                });
                Modify.setOnAction(mod->{
                    BorderPane choice = new BorderPane();
                    VBox v = new VBox();
                    HBox h = new HBox();
                    TextField lhs = new TextField("LeftHandSide");
                    TextField rhs = new TextField("RightHandSide");
                    h.getChildren().addAll(lhs,rhs);
                    for(Button b : dfBtns){
                        b.setOnAction(mod1->{
                            try {
                                modify(b.getText(),lhs.getText(),rhs.getText());
                                continu.showAndWait().ifPresent(flux->{
                                        if(flux!=ButtonType.OK)
                                                Return.fire();
                                        });  
                            } catch (SQLException ex) {
                                System.out.println(ex.getMessage());
                            }
                        });
                        v.getChildren().add(b);
                    }
                    choice.setCenter(v);
                    choice.setBottom(h);
                    primaryStage.setScene(new Scene(choice));
                });
                Exit.setOnAction(quit->{
                    primaryStage.setScene(scene);
                });
                Delete.setOnAction((ActionEvent del)->{
                    BorderPane choice = new BorderPane();
                    VBox v = new VBox();
                    for(Button b : dfBtns){
                        b.setOnAction((ActionEvent del1)->{
                            Alert alert = new Alert(AlertType.CONFIRMATION,"Do you want to delete :"+b.getText()+"?");
                            alert.showAndWait().ifPresent(response->{
                                if(response == ButtonType.OK){
                                    try {
                                        delete(b.getText());
                                        b.setVisible(false);
                                        continu.showAndWait().ifPresent(flux->{
                                            if(flux != ButtonType.OK){
                                                Return.fire();
                                            }
                                        });
                                    }catch (SQLException ex){
                                        System.out.println(ex.getMessage());
                                    }
                                }
                            });                            
                        });
                        v.getChildren().add(b);
                    }
                    choice.setCenter(v);
                    choice.setBottom(Return);
                    primaryStage.setScene(new Scene(choice));
                });
                Check.setOnAction(check->{
                    BorderPane choice = new BorderPane();
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
                if(!deleted.contains(func))
                    str+="\t"+func.toString()+"\n";
            }
        }
        txt.setText(str);
        return txt;
    }
    private DF getDF(String df){
        for(DF func : dfs.getDFs()){
            if(func.toString().equals(df))
                return func;
        }
    }
    private void add(DF df)throws SQLException{
        if(!dfs.getTabNames().contains(df.getTableName()))
            return;
        if(!dfs.getDFs().contains(df))
            dfs.getDFs().add(df);
    }
    private void modify(String df,String lhs,String rhs)throws SQLException{
        DF value = getDF(df);
        DF newFunc = new DF(value.getTableName(),lhs,rhs);
        dfs.getDFs().add(dfs.getDFs().indexOf(value), newFunc);
        delete(df);
    }
    private void delete(String df)throws SQLException{
        dfs.getDB().deleteDF("lhs rhs", );
    }
    private boolean check3NF(String table){
        //return DF.check3NF(table);
        return true;
    } 
    private boolean checkBCNF(String table){
        //return DF.checkBCNF(table);
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
       // System.out.println("ok");
    }
    
}
