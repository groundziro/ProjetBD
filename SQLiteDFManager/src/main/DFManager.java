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
import java.util.Arrays;
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
    
    
    ArrayList<Key> getSuperKeys(String table) throws SQLException{
        ArrayList<Key> toReturn = new ArrayList<>();
        toReturn.addAll(getKeys(table));
        List<String> attributes = getColNames(table);
        ArrayList<String> atrbNotInKey;
        ArrayList<String> atrbInKey;
        ArrayList<Key> bag;
        Key tmpk;
        boolean added;
        boolean inThere; 
        boolean isThisOne;
        
        
        do{
            bag=new ArrayList<>();
            added = false;
            for(Key k : toReturn){
                atrbInKey = new ArrayList<>();
                atrbNotInKey = new ArrayList<>();
                
                for(String ls : k.getAttributes()){
                    atrbInKey.add(ls);
                }
                
                for(String str:attributes){
                    if(! atrbInKey.contains(str)){
                        atrbNotInKey.add(str);
                    }
                }
                
                
                for(String l:atrbNotInKey){
                    tmpk=new Key(false);
                    for(String at:atrbInKey){
                        tmpk.addAttributes(at);
                    }
                    tmpk.addAttributes(l);
                    bag.add(tmpk);
                }
                                               
            }
            
            for(Key kbag:bag){
                inThere=false;
                for(Key ky:toReturn){
                    isThisOne=true;
                    for(String atrb:kbag.getAttributes()){
                        if(! ky.getAttributes().contains(atrb)){
                           isThisOne=false;
                           break;

                        }
                    }
                    if(isThisOne){
                        inThere=true;
                    }
                }
                
                if(! inThere){
                    toReturn.add(kbag);
                    added=true;
                }
            }
        }while(added);
        return toReturn;
    }
    
    /**
     * Given a table, return all the keys of the table
     * @param table
     * @return
     * @throws SQLException
     */
    public ArrayList<Key> getKeys(String table) throws SQLException{
        List<String> atribNames = getColNames(table);
        List<DF> dfss = getDFs();
        ArrayList<DF> dfs = new ArrayList<>();
        for(DF d:dfss){
            if(d.getTableName().equals(table))
                dfs.add(d);
        }
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
        ArrayList<String> whatWeGot=new ArrayList<>();
  
        whatWeGot.addAll(alrInKey);
        for(String ty:alrHenced){
            if(! whatWeGot.contains(ty)){
                whatWeGot.add(ty);
            }
        }
        ArrayList<String> newHenced=findConsc(whatWeGot,dfs);

        
        for(int i=0;i<attributes.size();i++){
            if(!(alrInKey.contains(attributes.get(i)) || newHenced.contains(attributes.get(i)))){
                remaining.add(attributes.get(i));
            }
        }

        if(remaining.isEmpty()){
            Key k=new Key(alrInKey);
            //result.add(new Key(alrInKey));
            result.add(k);
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

                newAlrHenced=findConsc(newAlrInKey,dfs);
                          
                subbag=recursGetKeys(bag,attributes,newAlrInKey,alrHenced, dfs);
                for(Key k:subbag){
                    if(! bag.contains(k))
                        bag.add(k);
                }
            }
            return bag;
        }
    }
    
    /**
     * Given a table, return true if the table (and her DFs) are 3NF. False otherwise
     * @param table
     * @return true if 3NF, false otherwise
     * @throws SQLException
     */
    public boolean is3NF(String table) throws SQLException{
        List<DF> dfss = getDFs();
        ArrayList<DF> dfs = new ArrayList<>();
        for(DF d:dfss){
            if(d.getTableName().equals(table))
                dfs.add(d);
        }
        
        List<Key> keys=getKeys(table);
        
        String[] lhs;
        String rh;
        
        for(DF df : dfs){
            lhs=df.getLhs().split(" ");
            rh=df.getRhs();
            if(!isOneOfTheKey(lhs,keys)){
                if(! isPrm(rh,keys)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Given a table, return true if the table (and her DFs) are BCNF. False otherwise
     * @param table
     * @return true if BCNF, false otheriwse
     * @throws SQLException
     */
    public boolean isBCNF(String table) throws SQLException{
        List<DF> dfss = getDFs();
        ArrayList<DF> dfs = new ArrayList<>();
        for(DF d:dfss){
            if(d.getTableName().equals(table))
                dfs.add(d);
        }
        
        List<Key> keys=getKeys(table);
        
        String[] lhs;
        
        for(DF df : dfs){
            lhs=df.getLhs().split(" ");
            if(!isOneOfTheKey(lhs,keys)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if one attribute is part of one key
     * @param atr the attribute to check
     * @param keys the list of the concerned keys
     * @return
     */
    public static boolean isPrm(String atr, List<Key> keys){
        for(Key k:keys){
            if(k.getAttributes().contains(atr))
                return true;
        }
        return false;
    }
    
    /**
     * Check if one tab of attribute is one key or one super key
     * @param atrb the tab of attributes
     * @param keys a list of the concerned keys
     * @return
     */
    public static boolean isOneOfTheKey(String[] atrb, List<Key> keys){
        boolean isThisOne;
        ArrayList<String> atrbArray=new ArrayList<>();
        for(String a:atrb){
            atrbArray.add(a);
        }
        for(Key k:keys){
            isThisOne=true;
            for(String atr:k.getAttributes()){
                if(! atrbArray.contains(atr) ){
                    isThisOne=false;
                    break;
                }
            }
            if(isThisOne){
                return true;
            }
        }
        return false;
    }
    
    /**
     * If we got the attributes in "whatWeGot",then, with the DFs "dfs", we also got the returned attributes
     * @param whatWeGot
     * @param dfs
     * @return
     */
    public static ArrayList<String> findConsc(ArrayList<String> whatWeGot, List<DF> dfs){
        ArrayList<String> whatWeGotGot=new ArrayList<>();
        whatWeGotGot.addAll(whatWeGot);
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
                        if(! whatWeGotGot.contains(part)){
                            good=false;
                            break;
                        }
                    }
                    if(good){        
                        consc.add(df.getRhs());
                        whatWeGotGot.add(df.getRhs());
                        added=true;
                    }
                }
            }
        }while(added);
        for(String str:whatWeGotGot){
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
    
    /**
     * Pre-conditoins: DFs are respected
     * @return
     * @throws SQLException
     */
    public String decompose3NF() throws SQLException, Exception{
        List<String> not3NFTables = new ArrayList<>();
        List<String> okTables = new ArrayList<>();
        for(String tabName:getTabNames()){
            if(!is3NF(tabName)){
                not3NFTables.add(tabName);
            }
            else{
                okTables.add(tabName);
            }
        }
        if(not3NFTables.size()==0){
            return "nothing to do here";
        }
        
        DFManager newDfm=new DFManager(""+dbm.getName().substring(0,dbm.getName().length()-3)+"_3NF.db");
        
        transferDFs(newDfm);
        
        List<DF> dfss;
        ArrayList<DF> dfs;
                
        for(String badTable:not3NFTables){
            dfss = getDFs();
            dfs = new ArrayList<>();
            for(DF d:dfss){
                if(d.getTableName().equals(badTable))
                    dfs.add(d);
            }
            createTables(newDfm,badTable,dfs);
        }
        
        return "solut";
    }
    
    public void transferDFs(DFManager newDFM) throws SQLException{
        List<DF> dfs = getDFs();
        for(DF df:dfs){
        newDFM.dbm.insertDF(df.getTableName(),df.getLhs(),df.getRhs());
        }
    }
    
    /**
     * Create a tables for the dfManager newDfm for the  of each DF + one for a key if this is necessery (based on one other table, for 3NF decomposition purpose)
     * Note that the type of all the attributes will be text (SQLite doesn't really care about the type of his attributes anyway...)
     * @param table
     * @param dfs
     * @return
     */
    public String createTables(DFManager newDFM, String table, List<DF> dfs) throws Exception{
        String[] a1;
        String[] a;
        String[] b;
        Integer[] c;
        ArrayList<Integer> idConcerned;
        Key key;
        ResultSet tuples;
        ArrayList<String[]> tablesCreated=new ArrayList<>();
        ArrayList<String> vals;
        ArrayList<Object> arObjs;
        String tablename;
        for(int q=0;q<dfs.size();q++){
            a1=dfs.get(q).getLhs().split(" ");
            a=new String[a1.length+1];
            for(int s=0;s<a1.length;s++){
                a[s]=a1[s];
            }
            a[a.length-1]=dfs.get(q).getRhs();
            b=new String[a.length];
            for(int i=0;i<a.length;i++){
                b[i]="text";
            }
            c=new Integer[(a.length)-1];
            for(int k=0;k<c.length;k++){
                c[k]=k;
            }
            tablename=table+String.valueOf(q);
            newDFM.dbm.createNewTable(tablename,a,b,c); 
            tablesCreated.add(a);
            
            //ID concerned
            vals = new ArrayList(Arrays.asList(a));
            idConcerned=dbm.getIdConcerned(table,vals); 
            
            String attributes="";
            for(int bv=0;bv<a.length;bv++){
                attributes=attributes+a[bv]+",";
            }
            attributes=attributes.substring(0, attributes.length()-1);
            
            tuples = dbm.getTableDatas(table); 
            while(tuples.next()){
                arObjs=new ArrayList<>();
                for(Integer in:idConcerned){
                    arObjs.add(tuples.getObject(in+1));
                }
                Object[] tabObjs = new Object[arObjs.size()];
                for(int w=0;w<arObjs.size();w++)
                    tabObjs[w]=arObjs.get(w);
                newDFM.dbm.insertData(tablename, attributes , tabObjs); 
            }
        }   
        
        //Create a table based on a key if this is necessery 
        key=getKeys(table).get(0); 
        if(isIncluded(key.getAttributes(),tablesCreated) == -1){
            tablename=table+String.valueOf(dfs.size());
            a=new String[key.getAttributes().size()];
            for(int x=0;x<key.getAttributes().size();x++){
                a[x]=key.getAttributes().get(x);
            }
            b=new String[a.length];
            for(int x=0;x<a.length;x++){
                b[x]="text";
            }
            c=new Integer[a.length];
            for(int x=0;x<a.length;x++){
                c[x]=x;
            }
            newDFM.dbm.createNewTable(tablename,a,b,c); 
            
            //And now adding the tuples. A bit of copy paste from the frist part still.. 
            //Code could be more beautifull, with some more methods
            vals = new ArrayList(Arrays.asList(a));
            idConcerned=dbm.getIdConcerned(table,vals); 
            
            String attributes="";
            for(int bv=0;bv<a.length;bv++){
                attributes=attributes+a[bv]+",";
            }
            attributes=attributes.substring(0, attributes.length()-1);
            
            tuples = dbm.getTableDatas(table); 
            while(tuples.next()){
                arObjs=new ArrayList<>();
                for(Integer in:idConcerned){
                    arObjs.add(tuples.getObject(in+1));
                }
                Object[] tabObjs = new Object[arObjs.size()];
                for(int w=0;w<arObjs.size();w++)
                    tabObjs[w]=arObjs.get(w);
                newDFM.dbm.insertData(tablename, attributes , tabObjs); 
            }
            
        }
            
        
        return "tables "+table+"[0-"+String.valueOf(dfs.size())+"] created";
    }
    
    /**
     * Return the id of one includers if the included is indeed included in it.
     * Return -1 if no includers include the included
     * @param included
     * @param includers
     * @return
     */
    public static int isIncluded(List<String> included, ArrayList<String[]> includers){
        ArrayList<String> includer;
        boolean ok;
        for(int i=0;i<includers.size();i++){
            ok=true;
            includer=new ArrayList(Arrays.asList(includers.get(i)));
            for(String str:included){
                if(!includer.contains(str)){
                    ok=false;
                    break;
                }
            }
            if(ok)
                return i;
        }
        return -1;
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
    public static void main(String[] args) throws SQLException, Exception {
        DFManager dfm = new DFManager("test.db");
        //dfm.dbc.printTable("bananes");
        System.out.println("");
          
        //dfm.decompose3NF("warehouse");
            //dfm.decompose3NF();
        DFManager dfm2 = new DFManager("test_3NF.db");
        dfm.dbm.transferTable(dfm2.dbm);
        
        /*
        ArrayList<String> ad=new ArrayList<>();
        ad.add("name");
        ad.add("capacity");
        ad.add("id");
        System.out.println(dfm.dbm.getIdConcerned("warehouse", ad));
        */
        
        /*
        ArrayList<Key> kk= dfm.getKeys("alpha");
        System.out.println("-------------------");
        for(Key k:kk){
            System.out.println(k);
        }
        */
         
        System.out.println();
    }
    
}
