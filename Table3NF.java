/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetbd1;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author thomas
 */
public class Table3NF extends Table{
    private Table table;
    private ArrayList<FuncDep> func;
    public Table3NF(Table table, FuncDep... f){
        this.table=table;
        func = new ArrayList<>();
        func.addAll(Arrays.asList(f));
    }
    
}
