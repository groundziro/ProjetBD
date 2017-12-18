/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//import static main.SQLiteConnector.createNewDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Alfatta
 */
public class DFManager {
    DBManager dbm;

    public DFManager(DBManager dbc) throws SQLException {
        this.dbm = dbc;
        checkFuncDep();
    }   
   
    /**
     * Given a table, return all the keys of the table
     * @param table
     * @return
     * @throws SQLException
     */
    public ArrayList<Key> getKeys(String table) throws SQLException{
        List<String> atribNames = dbm.getColNames(table);
        List<DF> dfs = getDFs();
        
        boolean[] tt=new boolean[atribNames.size()];  //Is is possible to reach attribute i with a DF?
        for(int i=0;i<tt.length;i++){
            tt[i]=false;
        }
        for(int i=0;i<atribNames.size();i++){
            for(DF curDf:dfs){
                if(curDf.getRhs().equals(atribNames.get(i))){
                    tt[i]=true;
                }
            }
        }
        ArrayList<String> alrInKey=new ArrayList<>();
        for(int i=0;i<tt.length;i++){
            if(!tt[i])        //If this attribute is reached by 0 DF, then it is in all the keys
                alrInKey.add(atribNames.get(i));
        }
        return recursGetKeys(new ArrayList<Key>(), atribNames, alrInKey, new ArrayList<String>(), dfs);
    }
    
    public static ArrayList<Key> recursGetKeys(ArrayList<Key> bag, List<String> attributes, ArrayList<String> alrInKey, ArrayList<String> alrHenced, List<DF> dfs){
        ArrayList<String> remaining=new ArrayList<>();
        ArrayList<Key> result=new ArrayList<>();
        for(int i=0;i<attributes.size();i++){
            if(!(alrInKey.contains(attributes.get(i)) || alrHenced.contains(attributes.get(i)))){
                remaining.add(attributes.get(i));
            }
        }
        if(remaining.isEmpty()){
            result.add(new Key(alrInKey));
            return result;
        }
        else{
            ArrayList<String> newAlrInKey;
            ArrayList<String> newAlrHenced;
            ArrayList<String> editedNewAlrHenced;
            ArrayList<Key> subbag;
            for(String rem:remaining){
                newAlrInKey=new ArrayList<>();
                newAlrInKey.addAll(alrInKey);
                newAlrInKey.add(rem);
                boolean newz;
                newAlrHenced=findConsc(newAlrInKey,dfs);
                //THIS PART SHOULD ACTUALLY GO TRROUGH ONCE SINCE findConsc ALREADY RETURN THE CONSC OF THE CONSC
                editedNewAlrHenced=findConsc(newAlrHenced,dfs);
                do{
                    newz=false;
                    for(String str:editedNewAlrHenced){
                        if(! newAlrHenced.contains(str)){
                            newAlrHenced.add(str);
                            newz=true;
                        }                            
                    }
                    newAlrHenced.addAll(editedNewAlrHenced);
                    editedNewAlrHenced=findConsc(newAlrHenced,dfs);
                //}while(! newAlrHenced.containsAll(editedNewAlrHenced)); 
                }while(newz);     //while we got more henced with the new henced
                //END OF 'THIS PART'
                subbag=recursGetKeys(bag,attributes,newAlrInKey,newAlrHenced, dfs);
                for(Key k:subbag){
                    if(! bag.contains(k))
                        bag.add(k);
                }
            }
            return bag;
        }
    }
    public boolean isBCNF(String table) throws SQLException{
        List<DF> dfs = getDFs();
        boolean bcnf = true;
        for(DF df : dfs){
            List<DF> func = new ArrayList<>();
            func.add(df);
            ArrayList<String> attr = new ArrayList<String>();
            for(String s : decomposeLhs(df))
                attr.add(s);
            bcnf&=findConsc(attr,func).containsAll(getColNames(table));           
        }
        return bcnf;
    }
    /**
     * If we got the attributes in "whatWeGot",then, with the DFs "dfs", we also got the returned attributes
     * @param whatWeGot
     * @param dfs
     * @return
     */
    public static ArrayList<String> findConsc(ArrayList<String> whatWeGot, List<DF> dfs){
        String[] cut;
        boolean good;
        ArrayList<String> consc=new ArrayList<>();
        boolean added=false;
        do{
            added=false;
            for(DF df:dfs){
                if(! consc.contains(df.getRhs())){
                    good=true;
                    cut=df.getLhs().split(" ");
                    for(String part:cut){
                        if(! whatWeGot.contains(part)){
                            good=false;
                            break;
                        }
                    }
                    if(good){        
                        consc.add(df.getRhs());
                        whatWeGot.add(df.getRhs());
                        added=true;
                    }
                }
            }
        }while(added);
        for(String str:whatWeGot){
            if(! consc.contains(str))
                consc.add(str);
        }
        return consc;
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
    
    /**
     * Discover all the -DF related mistakes
     * @return an ArrayList of DFConflict
     * @throws SQLException
     */
    public ArrayList<DFConflict> checkConflict() throws SQLException{
        ArrayList<DFConflict> conflicts = new ArrayList<>();
        List<DF> dfs = getDFs();
        List<String> cols;
        ResultSet tableData;
        int[] rhcol;
        int lhcol;
        boolean ok;
        String line;
        Map<String, String> map;
        List<String> tabName = getTabNames();
        for(DF curDF: dfs){
            ok=false;
            for (String name : tabName) {
                if(name.equals(curDF.getTableName())){
                    ok=true;
                }
            }
            if(!ok){
                conflicts.add(new DFConflict(curDF,3,"No such table: '"+curDF.getTableName()+"'"));
            }
            else{
                tableData=getTableDatas(curDF.getTableName());
                map = new HashMap<>();
                cols = getColNames(curDF.getTableName()); //les colonnes de la table
                String[] lh=decomposeLhs(curDF);   //les nom des attribus concernés
                rhcol= new int[lh.length];   //garde les id des colonnes concernées par rhs
                for(int i=0;i<rhcol.length;i++){
                    rhcol[i]=-1;
                }
                lhcol=-1;
                for(int i=0;i<lh.length;i++){
                    for(int j=0;j<cols.size();j++){
                        if(lh[i].equals(cols.get(j))){
                            rhcol[i]=j;
                        }
                    }       
                }
                for(int i=0;i<cols.size();i++){
                    if(cols.get(i).equals(curDF.getRhs()))
                        lhcol=i;
                }
                if(lhcol==-1){                  //Check attribu lh dans la table
                    conflicts.add(new DFConflict(curDF,2,"Attribute '"+curDF.getRhs()+"' not in table "+curDF.getTableName()));
                    ok=false;
                }
                if(ok){
                    for(int i=0;i<rhcol.length;i++){        
                        if(rhcol[i]==-1){           //Check tout attribu rh dans la table
                             conflicts.add(new DFConflict(curDF,2,"Attribute '"+lh[i]+"' not in table "+curDF.getTableName()));
                             ok=false;
                        } 
                    }}
                if(ok){
                    curDF.giveRef(rhcol, lhcol);
                    while(tableData.next()){
                        line="";
                        for(int k=0;k<rhcol.length;k++){
                           line=line+String.valueOf(tableData.getObject(rhcol[k]+1))+",";
                        }
                        line=line.substring(0,line.length()-1);
                        if(!map.containsKey(line)){
                            map.put(line,String.valueOf(tableData.getObject(lhcol+1)));
                        }
                        else{
                            if(! map.get(line).equals(String.valueOf(tableData.getObject(lhcol+1)))){
                                if(conflicts.size() != 0 && conflicts.get(conflicts.size()-1).getDf().equals(curDF)){
                                    conflicts.get(conflicts.size()-1).addConflictedLhs(String.valueOf(tableData.getObject(lhcol+1)));
                                }
                                else{  
                                conflicts.add(new DFConflict(curDF,line,String.valueOf(tableData.getObject(lhcol+1)),map.get(line)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return conflicts;
    }
    
    public static String[] decomposeLhs(DF df){
        return df.getLhs().split(" ");
    }
    
    public List<String> getColNames(String table) throws SQLException{
        return dbm.getColNames(table);
    }
            
    public List<String> getTabNames() throws SQLException{
        return dbm.getTabNames();
    }    
    
    public ResultSet getTableDatas(String table) throws SQLException{
        return dbm.getTableDatas(table);
    }
    
    public List<DF> getDFs() throws SQLException{
        return dbm.getDFs();
    }
    
    public DFManager(String path) throws SQLException {
        dbm = new DBManager(path);
        checkFuncDep();
    }

    public final boolean checkFuncDep() throws SQLException{
        dbm.createNewTable("FuncDep","tableName text","lhs text","rhs integer","PRIMARY KEY (tableName,lhs,rhs)");
        return dbm.isEmpty("FuncDep"); 
    }
    
    public void deleteData(String table, String attributes, Object[] values){
        dbm.deleteData(table, attributes, values);
    }
    
    /**
     * Delete ONE conflicted tuple with DF from the data base. 
     * Note that the DF could still show conflict if more than 2 data where in conflict.
     * @param intru the DFConflict 
     */
    public void deleteOneConflictedData(DFConflict intru){
        String[] cut=intru.getLhconfl().split(",");
        Object[] values=new Object[cut.length+1];
        for(int g=0;g<cut.length;g++){
            values[g]=cut[g];
        }
        values[values.length-1]=intru.getRhconfl().get(0);
        String attributes="";
        String lhhs = intru.getDf().getLhs();
        String p[]= lhhs.split(" ");
        for(String pp : p){
            attributes=attributes+pp+",";
        }
        attributes=attributes+intru.getDf().getRhs();
        deleteData(intru.getDf().getTableName(),attributes,values);
        intru.removeLh(intru.getRhconfl().get(0));
    }
   
    
    //BROUILLON
    public void deleteOneConflictedData(DFConflict intru, int id){
        String[] cut=intru.getLhconfl().split(",");
        Object[] values=new Object[cut.length+1];
        for(int g=0;g<cut.length;g++){
            values[g]=cut[g];
        }
        values[values.length-1]=intru.getRhconfl().get(id);
        String attributes="";
        String lhhs = intru.getDf().getLhs();
        String p[]= lhhs.split(" ");
        for(String pp : p){
            attributes=attributes+pp+",";
        }
        attributes=attributes+intru.getDf().getRhs();
        deleteData(intru.getDf().getTableName(),attributes,values);
        intru.removeLh(intru.getRhconfl().get(id));
    }
    
    public DBManager getDB(){
        return dbm;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        DFManager dfm = new DFManager("test.db");
        //dfm.dbc.printTable("bananes");
        System.out.println("");
        ArrayList<DFConflict> johlebanjo= dfm.checkConflict();
        for(int i=0;i<johlebanjo.size();i++){
            System.out.println(johlebanjo.get(i).message);/*
            if(johlebanjo.get(i).type==1){
                DFConflict intru=johlebanjo.get(i);
                String[] values=new String[intru.getLhs().size()+1];
                for(int p=0;p<intru.getLhs().size();p++){
                    values[p]=intru.getLhs().get(p);
                }
                String[] cut=intru.getLhconfl().split(",");
                Object[] values=new Object[cut.length+1];
                for(int g=0;g<cut.length;g++){
                    values[g]=cut[g];
                }
                values[values.length-1]=intru.getRhconfl().get(0);
                String attributes="";
                String lhhs = intru.getDf().getLhs();
                String p[]= lhhs.split(" ");
                for(String pp : p){
                    attributes=attributes+pp+",";
                }
                attributes=attributes+intru.getDf().getRhs();
                dfm.deleteData(intru.getDf().getTableName(),attributes,values);
                
            }*/
            System.out.println("");
        }
        /*
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
        */
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
