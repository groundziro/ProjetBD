/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;

/**
 * @author Alfa
 */
public class DFConflict {
    DF df;
    String message; 
    String lhconfl;
    ArrayList<String> rhconfl;   //in case of case 1, the lhs of the tuples in conflict
    int type;  //1=two+ tuple, 2=no attribute, 3=no table
    
    public String getLhconfl() {
        return lhconfl;
    }

    public ArrayList<String> getRhconfl() {
        return rhconfl;
    }

    public int getType() {
        return type;
    }
    

    public DFConflict(DF df, String rh, String lh1, String lh2) {
        this.df = df;
        this.lhconfl = rh;
        this.rhconfl=new ArrayList<>();
        rhconfl.add(lh1);
        rhconfl.add(lh2);
        this.message = "table:"+df.getTableName()+"|df:["+df.toString()+"] but "+rh+" -> "+lh1+" and "+lh2;
        this.type=1;
    }

    public DF getDf() {
        return df;
    }
    
    public void addConflictedLhs(String l){
        //Retournera une erreur si type != 1
        rhconfl.add(l);
        message=message+" and "+l;
    }
    

    public DFConflict(DF df, int type, String message) {
        this.df = df;
        this.message = message;
        this.type=type;
    }

    @Override
    public String toString() {
        return message;
    }

    void removeLh(String get) {
        rhconfl.remove(get);
    }
    
}
