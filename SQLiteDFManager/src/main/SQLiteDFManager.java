/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//import static main.SQLiteConnector.createNewDB;

/**
 *
 * @author Alfatta
 */
public class SQLiteDFManager {
    DBManager dbc;

    public SQLiteDFManager(DBManager dbc) {
        dbc = dbc;
    }
    
    public SQLiteDFManager(String path) {
        dbc = new DBManager(path);
    }

    public boolean checkFuncDep(){
        dbc.createNewTable("FuncDep","table text","lhs text","rhs integer","PRIMARY KEY (table,lhs,rhs)");
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBManager dbc= new DBManager("MAZ.db");
        //dbc.createNewTable("bananes","colour text","type text","tasteval integer","avweight real","PRIMARY KEY (colour,type)");
         // dbc.check();
        //dbc.createNewTable("warehouse","id integer PRIMARY KEY","name text NOT NULL","capacity real");
        //dbc.insertData("warehouse","name,capacity","N2",(double)222.2);
        //dbc.insertData("warehouse","capacity,name","N3",(double)333.3);
        //dbc.insertData("bananes", "avweight,colour,type,tasteval",1.1,"orange","miam",8);
        
      //dbc.deleteData("bananes","avweight,colour,tasteval",8.6,"blue",5);
        //dbc.deleteData("bananes","tasteval",2);
        //dbc.printTable("warehouse");
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
