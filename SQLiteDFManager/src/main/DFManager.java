/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//import static main.SQLiteConnector.createNewDB;

import java.sql.SQLException;


/**
 *
 * @author Alfatta
 */
public class DFManager {
    DBManager dbc;

    public DFManager(DBManager dbc) {
        dbc = dbc;
    }
    
    public DFManager(String path) {
        dbc = new DBManager(path);
    }

    public boolean checkFuncDep() throws SQLException{
        dbc.createNewTable("FuncDep","tableName text","lhs text","rhs integer","PRIMARY KEY (tableName,lhs,rhs)");
        return dbc.isEmpty("FuncDep"); 
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        DFManager dfm = new DFManager("test.db");
        try{
           System.out.println(dfm.checkFuncDep());
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        
        DBManager dbc= new DBManager("test.db");
        //dbc.createNewTable("bananes","colour text","type text","tasteval integer","avweight real","PRIMARY KEY (colour,type)");
         // dbc.check();
        //dbc.createNewTable("warehouse","id integer PRIMARY KEY","name text NOT NULL","capacity real");
        //dbc.insertData("warehouse","name,capacity","N2",(double)222.2);
        //dbc.insertData("warehouse","capacity,name","N3",(double)333.3);
        //dbc.insertData("bananes", "avweight,colour,type,tasteval",1.1,"noir","miam",8);
        
      //dbc.deleteData("bananes","avweight,colour,tasteval",8.6,"blue",5);
        //dbc.deleteData("bananes","tasteval",2);
      dbc.printTable("bananes");
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
