package main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Alfatta
 */
public class DBController {
    public static void connect(){
        Connection conn=null;
        try{
            String target="jdbc:sqlite:chinook.db";
            conn=DriverManager.getConnection(target);
            System.out.println("SQLite connection established");
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        finally{
            try{
                if(conn != null){
                    conn.close();
                }           
            }
            catch(SQLException q){
                System.out.println(q.getMessage());
            }
        }
    }

    
    public static void createNewDB(String name){
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
    
    /*
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
    }*/
    
    public static void createNewTable(){
        String target="jdbc:sqlite:bob.db";
        String tablecraft= "CREATE TABLE IF NOT EXISTS mamen (\n" +
                        " id integer PRIMARY KEY,\n" +
                        " description text NOT NULL\n" +
                        ");";
        System.out.println(tablecraft);
        /*
        try{
            Connection conn=DriverManager.getConnection(target);
            Statement stmt=conn.createStatement();
            stmt.execute(tablecraft);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }*/
        System.out.println("-->");
        createNewTable(tablecraft);
    }
    
    //  OOO
    public static void createNewTable(String tablename, String... args){
        String target="jdbc:sqlite:bob.db";
        String tablecraft="CREATE TABLE IF NOT EXISTS "+tablename+" (\n";
        for(String entry : args){
            tablecraft=tablecraft+" "+entry+",\n";
        }
            //removing ",\n" at the end
        tablecraft=tablecraft.substring(0,tablecraft.length()-3); 
        tablecraft=tablecraft+"\n);";
        //System.out.println(tablecraft);
//        createNewTable(tablecraft);
    }
    
    public static void createNewTable(String tablecraft){
        try{
            Connection conn=DriverManager.getConnection("jdbc:sqlite:bob.db");
            Statement stmt=conn.createStatement();
            stmt.execute(tablecraft);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    // OOO
    public static Connection connect(String name){
        String target="jdbc:sqlite:"+name;
        Connection conn = null;
        try{
            conn=DriverManager.getConnection(target);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public static void insertData(String table, String name, double capacity){
        String sqlcmd="INSERT INTO "+table+"(name,capacity) VALUES(?,?)";
        try{
            Connection conn=connect("bob.db");
            PreparedStatement pstmt=conn.prepareStatement(sqlcmd);
            pstmt.setString(1,name);
            pstmt.setDouble(2,capacity);
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void check(){
        
        /*String sqlcmd="SELECT \n"+
                      "id,\n"+
                      "name,\n"+
                      "capacity\n"+
                      "FROM\n"+
                      "table1;"; 
        */
        String sqlcmd="SELECT id,name,capacity FROM warehouse";
        try{
            Connection conn=connect("bob.db");
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(sqlcmd);
            while(rs.next()){
                System.out.println(rs.getInt("id")+"\t"+
                                   rs.getString("name")+"\t"+
                                   rs.getDouble("capacity"));
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void updateData(int id, String name, double capacity){
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
    
    
    public static void deleteData(int id){
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
    */
}
