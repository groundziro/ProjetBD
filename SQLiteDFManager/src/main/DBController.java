package main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class have everything we need for SQLiteDFManager related to accessing and editing a SQLite DB
 * A DBController is connected to one (or zero) DB at a given time
 * @author Alfatta
 */
public class DBController {

    Connection conn;
    
    public DBController(String path) {
        conn=connect(path);
    }
    
    /**
     * Connect the DBController with a DB.
     * Not recommanded if the current DBController instance is already connected to a DB.
     * This also create an empty DB if there is nothing at path
     * @param path should be the path to the DB. absolute or relative path are both acceptable
     */
    public Connection connect(String path){
        Connection conn=null;
        try{
            String target="jdbc:sqlite:"+path;
            conn=DriverManager.getConnection(target);
            System.out.println("SQLite connection established with "+path);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        /*
        finally{
            try{
                if(conn != null){
                    conn.close();
                }           
            }
            catch(SQLException q){
                System.out.println(q.getMessage());
            }
        }*/
        return conn;
    }
    

    
  

    /**
     * Create the SQL command to create a new table, then send it to executeStatement
     * Example use: createNewTable("warehouse","id integer PRIMARY KEY","name text NOT NULL","capacity real")
     * @param tablename the name of the new table
     * @param args the attributes of the table 
     */
    public void createNewTable(String tablename, String... args){
        String tablecraft="CREATE TABLE IF NOT EXISTS "+tablename+" (\n";
        for(String entry : args){
            tablecraft=tablecraft+" "+entry+",\n";
        }
            //removing ",\n" at the end
        tablecraft=tablecraft.substring(0,tablecraft.length()-3); 
        tablecraft=tablecraft+"\n);";
        //System.out.println(tablecraft);
        executeStatement(tablecraft);
    }
    
    /**
     * Execute the given SQL command
     * @param sqlStm The SQL code to be executed
     */
    public void executeStatement(String sqlStm){
        try{
            Statement stmt=conn.createStatement();
            stmt.execute(sqlStm);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Create the SQL command to insert a data in a table, then send it to executeStatement
     * Example use: insertData("warehouse","name,capacity","myname1",(double)134.8)
     * @param table name of the table the data should be inserted to
     * @param attributes order of the attributes (named perspective)
     * @param values the actual information we want to store
     */
    public void insertData(String table, String attributes, Object... values){
        String sqlStm="INSERT INTO "+table+"("+attributes+") VALUES(";
        for(int i=0;i<values.length;i++){
                sqlStm=sqlStm+"?,"; 
        }
            //removing the last ","
        sqlStm=sqlStm.substring(0,sqlStm.length()-1);
        sqlStm=sqlStm+")";
        System.out.println(sqlStm);
        try{
            PreparedStatement pstmt=conn.prepareStatement(sqlStm);
            String[] parts = attributes.split(",");
            for(int i=0;i<parts.length;i++){
               System.out.println(""+i+" : "+values[i]);
               pstmt.setObject(i+1,values[i]);
            }
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
     

    
    public List getColNames(String table) throws SQLException{
        String sqlStm="PRAGMA table_info(warehouse);";
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sqlStm);
        ArrayList<String> result = new ArrayList();
        while(rs.next()){
            result.add((String)rs.getObject(2));
        }
        return result;
    }
    
    public void printTable(String table){
        String sqlStm="SELECT * FROM "+table;
        try{
            List cn = getColNames(table);
            for(int i=0;i<cn.size();i++){
                System.out.print(cn.get(i)+"\t");
            }
            System.out.println("\n---------------------");
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(sqlStm);
            while(rs.next()){
                for(int i=0;i<cn.size();i++){
                    System.out.print(rs.getObject(i+1)+"\t");
                }
                System.out.println();
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    

    
    public void updateData(int id, String name, double capacity){
        String sqlcmd="UPDATE table1 SET name = ?,"+
                      "capacity = ?"+
                      "WHERE id = ?";
        try{
            Connection conn = connect("bob.db");
            PreparedStatement pstmt = conn.prepareStatement(sqlcmd); 
            pstmt.setString(1, name);
            pstmt.setDouble(2, capacity);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    
    public void deleteData(int id){
        String sqlcmd="DELETE FROM table1 WHERE id = ?";
        try{
            Connection conn=connect("bob.db");
            PreparedStatement pstmt=conn.prepareStatement(sqlcmd);
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    

    
    
    /*  
    public static void main(String[] args) {
        connect();
    }
    

    
    
    public static void createNewTable(){
        String target="jdbc:sqlite:bob.db";
        String tablecraft="CREATE TABLE IF NOT EXISTS table1 (\n"+
                          "id integer PRIMARY KEY,\n"+
                          "name text NOT NULL,\n"+
                          "capacity real\n"+
                          ");";
        try{
            Connection conn=DriverManager.getConnection(target);
            Statement stmt=conn.createStatement();
            stmt.execute(tablecraft);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
      public void createNewDB(String name){
        String target="jdbc:sqlite:"+name;
        try{
            Connection conn=DriverManager.getConnection(target);
            if(conn!=null){
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("Driver name: "+meta.getDriverName());
                System.out.println("db created");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
     public void insertData(String table, String name, double capacity){
        String sqlcmd="INSERT INTO "+table+"(name,capacity) VALUES(?,?)";
        try{
            PreparedStatement pstmt=conn.prepareStatement(sqlcmd);
            pstmt.setString(1,name);
            pstmt.setDouble(2,capacity);
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    
     public void printTable(String table){
        String sqlStm="SELECT * FROM "+table;
        try{
            List cn = getColNames(table);
            for(int i=0;i<cn.size();i++){
                System.out.print(cn.get(i)+"\t");
            }
            System.out.println("\n---------------------");
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(sqlStm);
            String format = "%-12s%-24s%-36s%n";
            while(rs.next()){
                System.out.printf(format,rs.getObject("id"),rs.getObject("name"),rs.getObject("capacity"));
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    
        public void check(){
        String sqlcmd="SELECT id,name,capacity FROM warehouse";
        try{
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(sqlcmd);
            while(rs.next()){
                System.out.println(rs.getObject("id")+"\t"+
                                   rs.getObject("name")+"\t"+
                                   rs.getObject("capacity"));
               
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    */
    
}