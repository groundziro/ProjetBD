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
public class DBManager {

    Connection conn;
    
    public DBManager(String path) {
        conn=connect(path);
    }
    
    /**
     * Connect the DBController with a DB.
     * Not recommanded if the current DBController instance is already connected to a DB.
     * This also create an empty DB if there is no DB found.
     * @param path should be the path to the DB. absolute or relative path are both acceptable
     * @return a SQLite DB connection
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
        tablecraft=tablecraft.substring(0,tablecraft.length()-2); 
        tablecraft=tablecraft+"\n);";
        //System.out.println(tablecraft+"\n ------");
        executeStatement(tablecraft);
    }
    
    /**
     * Create a table.
     * Example use:
     *    String[] a={"A","B","C"};
     *    String[] b={"text","text","text"};
     *    Integer[] c ={0,1};
     *    createTable("TheNewTab",a,b,c); 
     * @param name the name of the table
     * @param atrNames the name of the attributes
     * @param classes the type of the attributes
     * @param prKeyRefs the id (int) of the attributes beeing part of the primary key
     * @throws Exception
     */
    public void createNewTable(String name, String[] atrNames, String[] classes, Integer[] prKeyRefs) throws Exception{
        if(atrNames.length != classes.length){
            throw new Exception("atrNames and classes should have the same size");
        }
        String sqlStm="CREATE TABLE IF NOT EXISTS "+name+"(\n";
        for(int i=0;i<atrNames.length;i++){
            sqlStm=sqlStm+" "+atrNames[i]+" "+classes[i]+",\n";
        }
        sqlStm=sqlStm+" PRIMARY KEY(";
        for(int i=0;i<prKeyRefs.length;i++){
            sqlStm=sqlStm+atrNames[i]+",";
        }
        sqlStm=sqlStm.substring(0,sqlStm.length()-1);
        sqlStm=sqlStm+")\n";
        sqlStm=sqlStm+");";
        //System.out.println(sqlStm);
        executeStatement(sqlStm);
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
        //System.out.println(sqlStm);
        try{
            PreparedStatement pstmt=conn.prepareStatement(sqlStm);
            String[] parts = attributes.split(",");
            for(int i=0;i<parts.length;i++){
               //System.out.println(""+i+" : "+values[i]);
               pstmt.setObject(i+1,values[i]);
            }
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            //System.out.println(e.getMessage());
               //Ignored because 3NF decomposition could volontary generate data not respecting unique priv key
        }
    }
    
    
    
    public void insertDF(String attributes,Object... values){
        insertData("FuncDep",attributes,values);
    }
    
    /**
     * Delete entries from the DB. Only accept equality conditions
     * ex use:
     *   deleteData("bananes","avweight,colour,tasteval",8.6,"blue",5);
     *   deleteData("bananes","tasteval",2);
     * @param table
     * @param attributes
     * @param values
     */
    public void deleteData(String table, String attributes, Object... values){
        String sqlStm="DELETE FROM "+table+" WHERE ("+attributes+")= (";
        for(int i=0;i<values.length;i++){
            if(values[i].getClass()==String.class){
                sqlStm=sqlStm+"'"+(String)values[i]+"',";
            }
            else{
                sqlStm=sqlStm+values[i].toString()+",";
            }
        }
            //removing the last ","
        sqlStm=sqlStm.substring(0,sqlStm.length()-1);
        sqlStm=sqlStm+");";
        System.out.println(sqlStm);
        executeStatement(sqlStm);
    }
    
    /**
     * Delete DFs.
     * @param attributes
     * @param values 
     */
    public void deleteDF(String attributes, Object... values){
        deleteData("FuncDep",attributes,values);
    }
    
    public ResultSet executeQuery(String sqlStm) throws SQLException{
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sqlStm);
        return rs;
    }
    
    public List<String> getTabNames() throws SQLException{
        String sqlStm="SELECT name FROM sqlite_master WHERE type='table'";
        ResultSet rs=executeQuery(sqlStm);
        ArrayList<String> result = new ArrayList();
        while(rs.next()){
            result.add((String)rs.getObject(1));
            //System.out.println((String)rs.getObject(1));
        }
        return result;
    }
    
    public static String parsToStr(Object o){
        String result;
        try{
            result=(String) o;
        }
        catch(Exception e){  //Only Integers can't be casted to String in the SQLite supported types
            result=String.valueOf(o);
        }
        return result;
    }
    
    public List<DF> getDFs() throws SQLException{
        ResultSet rs = getTableDatas("FuncDep");
        ArrayList<DF> dfResult=new ArrayList<>();
        while(rs.next()){
            //dfResult.add(new DF((String)rs.getObject(1),(String)rs.getObject(2),(String)rs.getObject(3)));
            //dfResult.add(new DF(String.valueOf(rs.getObject(1)),String.valueOf(rs.getObject(2)),String.valueOf(rs.getObject(3))));
            dfResult.add(new DF(parsToStr(rs.getObject(1)),parsToStr(rs.getObject(2)),parsToStr(rs.getObject(3))));
            //System.out.println(""+rs.getObject(1)+":"+rs.getObject(2)+"->"+rs.getObject(3));
        }
        return dfResult;
    }

    /**
     * Get a list of all the attributes name of one table. 
     * The list is sorted by the unnamed perspective order
     * @param table
     * @return The list of the attributes names
     * @throws SQLException
     */
    public List<String> getColNames(String table) throws SQLException{
        String sqlStm="PRAGMA table_info("+table+");";
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sqlStm);
        ArrayList<String> result = new ArrayList();
        while(rs.next()){
            result.add((String)rs.getObject(2));
        }
        return result;
    }
    
    /**
     * Return a ResultSet containing all the tuples the table contains (unnamed perspective)
     * @param table
     * @return
     * @throws SQLException
     */
    public ResultSet getTableDatas(String table) throws SQLException{
        String sqlStm="SELECT * FROM "+table;
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sqlStm);
        return rs;        
    }
    
    
    public int tableSize(String table) throws SQLException{
         //We are not using getTableDatas then counting the number of values in the ResultSet
         //because it would read the entire ResultSet only to count the number of entries
        String sqlStm="SELECT COUNT(*) FROM "+table;
        
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sqlStm);
        return rs.getInt(1);
    }
    
    public boolean isEmpty(String table) throws SQLException{
        if(tableSize(table) == 0)
            return true;
        else
            return false;
    }
    
    public ArrayList<Integer> getIdConcerned(String table, ArrayList<String> atrNames) throws SQLException{
        List<String> attributes=getColNames(table);
        ArrayList<Integer> r = new ArrayList<>();
        for(String currentAtr:atrNames){
            for(int i=0;i<attributes.size();i++){
                if(currentAtr.equals(attributes.get(i))){
                    r.add(i);
                }
            }
        }
        return r;
    }
    
    public void printTable(String table){
        try{
            List cn = getColNames(table);
            for(int i=0;i<cn.size();i++){
                System.out.print(cn.get(i)+"\t");
            }
            System.out.println("\n-------------------------");
            ResultSet rs=getTableDatas(table);
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
    
    

    



   
    
}
