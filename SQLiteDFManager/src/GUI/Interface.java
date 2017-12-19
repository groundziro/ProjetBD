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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import main.DF;
import main.DFConflict;
import main.DFManager;
import main.Key;

/**
 *
 * @author Thomas
 */
public class Interface extends Application {
    private DFManager dfs = null;
    @Override
    public void start(Stage primaryStage) {
        Button Browse = new Button("Browse");
        BorderPane root = new BorderPane();
        root.setPrefSize(300, 300);
        root.setCenter(Browse);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        Browse.setOnAction((ActionEvent actionEvent) -> {
            FileChooser choose = new FileChooser();
            choose.getExtensionFilters().add(new ExtensionFilter("DataBases","*.db"));
            File result = choose.showOpenDialog(primaryStage);
            while(result==null){                
                result = choose.showOpenDialog(primaryStage);
            }
            BorderPane p = new BorderPane();
            p.setPrefSize(300, 300);
            try{
                dfs = new DFManager(result.getAbsolutePath());
                if(!dfs.getTabNames().contains("FuncDep")){
                    Alert alert = new Alert(AlertType.INFORMATION,"This table doesn't have any FDs.\nDo you want to add the FuncDep relation.");
                    alert.showAndWait().ifPresent(cnsmr->{
                        if(cnsmr==ButtonType.OK)
                            try {
                                dfs.checkFuncDep();
                        } catch (SQLException ex) {
                            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }                    
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
            try {
                if(!dfs.checkConflict().isEmpty()){
                    Alert alert = new Alert(AlertType.INFORMATION, "There's conflicts in your DB.\nLet's reolve them.");
                    alert.showAndWait().ifPresent(cnsmr->{
                        try {
                            conflicts(primaryStage);
                        } catch (SQLException ex) {
                            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    
                }else{
                    init(primaryStage,p,scene);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
            }            
            primaryStage.setOnShown(value->{
                init(primaryStage,p,scene);
            });
        });
        primaryStage.show();
    }
    protected void init(Stage primaryStage, BorderPane p,Scene scene){
         List<Button> btns = new ArrayList<>();
            ArrayList<Button> conflictBtns = null;
            try{
                conflictBtns = getConflicts();
                for(String table : dfs.getTabNames()){
                    if(!"FuncDep".equals(table))
                        btns.add(new Button(table));
                }
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        primaryStage.setTitle("");
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
                    if(!dfs.checkConflict().isEmpty()){
                        conflicts(primaryStage);
                    }
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
                    Alert alert = new Alert(AlertType.CONFIRMATION,"Do you want to add to "+df.getTableName()+": "+df.toString());
                    alert.showAndWait().ifPresent(response->{
                        if(response == ButtonType.OK){
                            try{
                                if(!dfs.getTabNames().contains(df.getTableName())){
                                    throw new SQLException("This table doesn't exist");
                                }
                                add(df);
                                continu.showAndWait().ifPresent(flux->{
                                    if(flux!=ButtonType.OK)
                                        Return.fire();
                                });
                            } catch (SQLException ex) {
                                Alert warning = new Alert(AlertType.WARNING,ex.getMessage());
                                warning.showAndWait();
                            }
                        }
                    });
                });
                choice.setCenter(v);
                choice.setBottom(confirm);
                primaryStage.setScene(new Scene(choice));
            });
            Modify.setOnAction(mod->{
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
                BorderPane choice = new BorderPane();
                VBox v = new VBox();
                HBox h = new HBox();
                TextField lhs = new TextField();
                TextField rhs = new TextField();
                lhs.setPromptText("LeftHandSide");
                rhs.setPromptText("RightHandSide");
                h.getChildren().addAll(lhs,rhs);
                for(Button b : dfBtns){
                    b.setOnAction(mod1->{
                        Alert alert = new Alert(AlertType.CONFIRMATION,"Would you like to modify "+b.getText()+"into "+lhs.getText()+"->"+rhs.getText(),ButtonType.APPLY,ButtonType.CANCEL);
                        alert.showAndWait().ifPresent(cnsmr->{
                            try{
                                if(cnsmr==ButtonType.APPLY&&dfs.getDB().getColNames(getDF(b.getText()).getTableName()).contains(lhs.getText())&&dfs.getDB().getColNames(getDF(b.getText()).getTableName()).contains(rhs.getText())){
                                    modify(b.getText(),lhs.getText(),rhs.getText());
                                    continu.showAndWait().ifPresent(flux->{
                                        if(flux!=ButtonType.OK)
                                            Return.fire();
                                    });
                                }
                                else{
                                    System.out.println(lhs.getText()+"\n"+getDF(b.getText()).getTableName()+"\n"+dfs.getDB().getColNames(getDF(b.getText()).getTableName()));
                                    Alert warning = new Alert(AlertType.WARNING,"This FD isn't good");
                                    warning.showAndWait();
                                }
                            } catch (SQLException ex) {  
                                System.out.println(ex.getMessage());
                            }
                        });
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
            Exit.setCancelButton(true);
            Delete.setOnAction((ActionEvent del)->{
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
                BorderPane choice = new BorderPane();
                VBox v = new VBox();
                for(Button b : dfBtns){
                    b.setOnAction((ActionEvent del1)->{
                        Alert alert = new Alert(AlertType.CONFIRMATION,"Do you want to delete : "+b.getText()+"?");
                        alert.showAndWait().ifPresent(response->{
                            if(response == ButtonType.OK){
                                try {
                                    delete(b.getText());
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
                BorderPane newChoice = new BorderPane();
                        Button BCNF = new Button("BCNF");
                        Button NF = new Button("3NF");
                        BCNF.setOnAction(bcnf->{
                            Alert alert = new Alert(AlertType.CONFIRMATION,"Do you wanna check if this database is BCNF?");
                            alert.showAndWait().ifPresent(cnsmr->{
                                if(cnsmr == ButtonType.OK)
                                   try {
                                       checkBCNFDB();
                                       Return.fire();
                                } catch (SQLException ex) {
                                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        });
                        NF.setOnAction(nf->{
                            Alert alert = new Alert(AlertType.CONFIRMATION,"Do you wanna check if this database is in 3NF?");
                            alert.showAndWait().ifPresent(cnsmr->{
                                if(cnsmr == ButtonType.OK)
                                   try {
                                       check3NFDB();
                                       Return.fire();
                                } catch (SQLException ex) {
                                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        });
                        newChoice.setCenter(new HBox(BCNF,NF));
                        primaryStage.setScene(new Scene(newChoice));
            });
            Check.setDefaultButton(true);
            primaryStage.setScene(Tables);
    }
    /**
     *
     * @param primaryStage
     * @throws SQLException
     */
    protected void conflicts(Stage primaryStage) throws SQLException{
        Text instructions = new Text("Click on a button to resolve the conflict induced with the functional dependency");
        primaryStage.hide();
        ArrayList<Button> conflictBtns = getConflicts();
        Stage conflictStage = new Stage();
        BorderPane conflict = new BorderPane();
        VBox v = new VBox();
        for(Button btn : conflictBtns){
        if(getType(btn.getText())>=2){
            btn.setOnAction(conflicted -> {
                Alert alert = null;
                try {
                    alert = new Alert(AlertType.CONFIRMATION,"This DF needs to be deleted, \nbecause : "+getConflict(btn.getText()).toString()+"\nDo you want it ?");
                    alert.showAndWait().ifPresent(cnsmr->{
                    if(cnsmr==ButtonType.OK){
                        try{
                            delete(btn.getText());
                            btn.setVisible(false);
                            if(dfs.checkConflict().isEmpty()){
                                conflictStage.close();
                                primaryStage.show();
                            }
                        }catch(SQLException e){
                            System.out.println(e.getMessage());
                        }
                    }
                });
                } catch (SQLException ex) {
                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            });
        }else{
            btn.setOnAction(conflicted->{
                try {
                    Alert alert = new Alert(AlertType.INFORMATION,getConflict(btn.getText()).toString()+"\nYou'll have to delete some values.\nMake your choice.",ButtonType.OK);
                    alert.showAndWait().ifPresent(cnsmr->{
                        try {
                            if(cnsmr==ButtonType.OK){
                                BorderPane tuples = new BorderPane();
                                VBox v1 = new VBox();
                                int i=0;
                                DFConflict df = getConflict(btn.getText());
                                for(String rh:df.getRhconfl()){
                                    HBox h = new HBox();
                                    Text t = new Text();
                                    t.setText(df.getLhconfl()+" "+rh);
                                    Button b = new Button(String.valueOf(i));
                                    b.setId(String.valueOf(i));
                                    b.setOnAction(deleted->{
                                        Alert confirm = new Alert(AlertType.CONFIRMATION,"Do you want to delete this tuple:"+df.getLhconfl()+" "+rh+"?");
                                        boolean confirmed = false;
                                        confirm.showAndWait().ifPresent(delete->{
                                            if(delete==ButtonType.OK && df.getRhconfl().size()>1){
                                                dfs.deleteOneConflictedData(df, getId(rh,df));
                                                h.setVisible(false);
                                                try {
                                                    if(df.getRhconfl().size()==1){
                                                        BorderPane newConflict = new BorderPane();
                                                        newConflict.setCenter(v);
                                                        conflictStage.setScene(new Scene(newConflict));
                                                    }
                                                    if(dfs.checkConflict().isEmpty()){
                                                        conflictStage.close();
                                                        primaryStage.show();
                                                    }
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }
                                        });
                                    });
                                    i++;
                                    h.getChildren().addAll(t,b);
                                    v1.getChildren().add(h);
                                }
                                tuples.setCenter(v1);
                                conflictStage.setScene(new Scene(tuples));
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    btn.setVisible(false);
                } catch (SQLException ex) {
                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        v.getChildren().add(btn);
    }
    conflict.setTop(instructions);
    conflict.setCenter(v);
    conflictStage.setScene(new Scene(conflict));
    conflictStage.setTitle("Conflicts");
    conflictStage.show();
    }
    protected void addBtns(VBox v, ArrayList<Button> b){
        v.getChildren().addAll(b);
    }
    protected ArrayList<Button> getConflicts()throws SQLException{
        ArrayList<Button> conflictsBtns = new ArrayList<>();
        for(DFConflict conflict : dfs.checkConflict()){
            Button b = new Button(conflict.getDf().toString());
            conflictsBtns.add(b);
        }
        return conflictsBtns;
    }
    
    protected int getType(String df) throws SQLException{
        for(DFConflict conflict:dfs.checkConflict()){
            if(conflict.getDf().toString().equals(df))
                return conflict.getType();
        }
        return 0;
    }
    private DFConflict getConflict(String df) throws SQLException{
        for(DFConflict conflict:dfs.checkConflict()){
            if(conflict.getDf().toString().equals(df))
                return conflict;
        }
        return null;
    }
    private Text current(DFManager df) throws SQLException{
        if(df.getDFs().isEmpty())
            return new Text("The \"FuncDep\" table is empty, please fill it.");
        Text txt = new Text();
        String str="";
        ArrayList<ArrayList<DF>> array = DFManager.orderDFList(df.getDFs());
        for(ArrayList<DF> table : array){
            str+= table.get(0).getTableName()+":\n";
            str+="Keys :\n";
            for(Key k : df.getKeys(table.get(0).getTableName())){
                str+="\t"+k.toString()+"\n";
            }
            str+="DFs :\n";
            for(DF func : table){
                    str+="\t"+func.toString()+"\n";
            }
            str+="-----------------------------\n";
        }
        txt.setText(str);
        return txt;
    }
    private int getId(String rhs,DFConflict df){
        int i = 0;
        for(String rh : df.getRhconfl()){
            if(!rhs.equals(rh))
                i++;
            else{
                break;
            }
        }
        return i;
    }
    private ArrayList<String> getLhs(String lhs){
        ArrayList<String> res = new ArrayList<>();
        String copy = lhs;
        res.add(copy.substring(0, copy.indexOf(' ')));
        while(!copy.equals(copy+"")&&!copy.equals(copy+" ")){
            System.out.println(copy);
            copy = copy.substring(copy.indexOf(' '));
            res.add(copy.substring(0,copy.indexOf(' ')));
        }
        return res;
    }
    private DF getDF(String df)throws SQLException{
        for(DF func : dfs.getDFs()){
            if(func.toString().equals(df))
                return func;
        }
        return null;
    }
    private void add(DF df)throws SQLException{
        if(!dfs.getTabNames().contains(df.getTableName())){
            return;
        }
        if(dfs.getDB().getColNames(df.getTableName()).contains(df.getRhs())){
            for(String lhs: getLhs(df.getLhs())){
                if(!dfs.getDB().getColNames(df.getTableName()).contains(lhs)){
                    System.out.println("SHEISSE");
                    return;
                }
            }
            dfs.getDB().insertDF(df.getTableName(),df.getLhs(),df.getRhs());
        }
    }
    private void modify(String df,String lhs,String rhs)throws SQLException{
        dfs.getDB().insertData("FuncDep", dfs.getColNames("FuncDep").get(0)+"lhs,rhs", getDF(df).getTableName(),lhs,rhs);
        delete(df);
    }
    private void delete(String df)throws SQLException{
        dfs.getDB().deleteDF("lhs,rhs", df.substring(0, df.indexOf(" -")),df.substring(df.indexOf(">")+2));
    }
    private boolean check3NF(String table) throws SQLException{
       return dfs.is3NF(table);           
    } 
    private boolean checkBCNF(String table) throws SQLException{
        return dfs.isBCNF(table);
        
    }
    private void checkBCNFDB() throws SQLException{
        boolean nf = true;
        for(String table : dfs.getTabNames()){
            if(!checkBCNF(table))
                nf = false;
        }
        if(nf){
            Alert alert = new Alert(AlertType.INFORMATION,"This database is in BCNF.");
            alert.showAndWait();
       }else{
           Alert alert = new Alert(AlertType.CONFIRMATION,"This database isn't in BCNF. \nDo you want to check if it's in 3NF?");
           alert.showAndWait().ifPresent(cnsmr->{
               if(cnsmr==ButtonType.OK)
                   try {
                       check3NFDB();
               } catch (SQLException ex) {
                   Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
               }
           });
       }
    }
    private void check3NFDB() throws SQLException{
        boolean nf = true;
        for(String table : dfs.getTabNames()){
            if(!check3NF(table))
                nf = false;
        }
        if(nf){
            Alert alert = new Alert(AlertType.INFORMATION,"This database is in 3NF.");
            alert.showAndWait();
       }else{
           Alert alert = new Alert(AlertType.CONFIRMATION,"This database isn't in 3NF. Do you want a decomposition?");
           alert.showAndWait().ifPresent(cnsmr->{
               if(cnsmr==ButtonType.OK)
                   try {
                       dfs.decompose3NF();
               } catch (Exception ex) {
                   Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
               }
           });
       }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
       // System.out.println("ok");
    }
    
}
