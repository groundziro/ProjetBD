/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//import static main.SQLiteConnector.createNewDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Alfatta
 */
public class DFManager {
    DBManager dbc;

    public DFManager(DBManager dbc) throws SQLException {
        this.dbc = dbc;
        checkFuncDep();
    }
   
    /**
     * Take en ArrayList of DF and give an ArrayList of ArrayList of dF.
     * Each ArrayList(second level) of DF countains DF of the same table.
     * @param lis
     * @return
     */
    public static ArrayList<ArrayList<DF>> orderDFList(List<DF> lis){
        int tabl;
        ArrayList<ArrayList<DF>> result= new ArrayList<>();
        for(DF cur: lis){
            tabl=-1;
               //Check in wich ArrayList (first level) cur should go
            for(int i=0;i<result.size();i++){
                if(result.get(i).get(0).getTableName().equals(cur.getTableName())) 
                    tabl=i;
            }
            if(tabl==-1){
                result.add(new ArrayList<DF>());
                tabl=result.size()-1;
            }
            result.get(tabl).add(cur);
        }
        return result;
    }
    
    public List<String> getTabNames() throws SQLException{
        return dbc.getTabNames();
    }    
    
    public List<DF> getDFs() throws SQLException{
        return dbc.getDFs();
    }
    
    public DFManager(String path) throws SQLException {
        dbc = new DBManager(path);
        checkFuncDep();
    }

    public final boolean checkFuncDep() throws SQLException{
        dbc.createNewTable("FuncDep","tableName text","lhs text","rhs integer","PRIMARY KEY (tableName,lhs,rhs)");
        return dbc.isEmpty("FuncDep"); 
    }
    public DBManager getDB(){
        return dbc;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        DFManager dfm = new DFManager("test.db");
        List<DF> df = dfm.getDFs();
        
        System.out.println("\n*.BEFORE THE SORT.*\n");
        
        for(int i=0;i<df.size();i++){
            System.out.print(""+df.get(i).getTableName()+" : ");
            System.out.println(df.get(i).toString());
        }
        
        System.out.println("\n*.AFTER THE SORT.*\n");
        
        ArrayList<ArrayList<DF>> sorted= orderDFList(df);
        
        for(int i=0;i<sorted.size();i++){
            System.out.println("--table: "+sorted.get(i).get(0).getTableName());
            for(int j=0;j<sorted.get(i).size();j++){
                System.out.println("     "+sorted.get(i).get(j));
            }
        }
        
      /*
        System.out.println("ok");
        DBManager dbc= new DBManager("test.db");
        dbc.getTabNames();
        System.out.println("oooooooooooooooook");
        dbc.getDFs();*/
        //dbc.createNewTable("bananes","colour text","type text","tasteval integer","avweight real","PRIMARY KEY (colour,type)");
         // dbc.check();
        //dbc.createNewTable("warehouse","id integer PRIMARY KEY","name text NOT NULL","capacity real");
        //dbc.insertData("warehouse","name,capacity","N2",(double)222.2);
        //dbc.insertData("warehouse","capacity,name","N3",(double)333.3);
        //dbc.insertData("bananes", "avweight,colour,type,tasteval",1.1,"noir","miam",8);
        
      //dbc.deleteData("bananes","avweight,colour,tasteval",8.6,"blue",5);
        //dbc.deleteData("bananes","tasteval",2);
    //  dbc.printTable("bananes");
        //dbc.isEmpty("bananes");
        //dbc.kedis();
        
        //connect();
        //createNewDB("bob.db");
        //createNewTable();
        /*
        insertData("warehouse","W1",309);
        insertData("warehouse","W2",393);
        insertData("warehouse","W3",39092);
        */
        //check();
        //System.out.println("---");
        //updateData(1,"dhghghghef",11243.2);
        //deleteData(2);
        //insertData("NOUVEAU",156.5);
        //check();
        //System.out.println("---");
        //createNewTable("trully","id integer PRIMARY KEY","name text NOT NULL","capacity real");
        //createNewTable("materials","id integer PRIMARY KEY","description text NOT NULL");
        //createNewTable();
        
    }
    
}
