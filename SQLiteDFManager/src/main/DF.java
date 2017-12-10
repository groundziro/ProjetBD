/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Alfatta
 */
public class DF {
    String tableName;
    String lhs;
    String rhs;

    public DF(String tableName, String lhs, String rhs) {
        this.tableName = tableName;
        this.lhs = lhs;
        this.rhs = rhs;
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

    @Override
    public String toString() {
        return ""+lhs+" -> "+ rhs;
    }
    
    
}
