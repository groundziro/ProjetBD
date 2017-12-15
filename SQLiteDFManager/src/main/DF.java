/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;

/**
 *
 * @author Alfatta
 */
public class DF {
    String tableName;
    String lhs;    
    String rhs;
    int[] lhsids; //for unnamed 
    int rhsid;    //for unnamed

    public DF(String tableName, String lhs, String rhs) {
        this.tableName = tableName;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void giveRef(int[] l, int r){
        lhsids=l;
        rhsid=r;
    }

    public String getTableName() {
        return tableName;
    }

    public String getLhs() {
        return lhs;
    }

    public String getRhs() {
        return rhs;
    }
    /*public boolean equals(DF df){
        return this.lhs.equals(df.lhs) && this.rhs.equals(df.rhs) && this.tableName.equals(df.tableName);
    }*/
    @Override
    public String toString() {
        return ""+lhs+" -> "+ rhs;
    }
     public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public void setRhs(String rhs) {
        this.rhs = rhs;
    }
    
}
