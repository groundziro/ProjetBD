package Pack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thomas
 */
public class TableBCNF extends Table3NF{
    private Table table;
    private FuncDep func;
   // public TableBCNF(Table table){
     //   super(table);
   // }

    public TableBCNF(Table table, FuncDep... f) {
        super(table, f);
    }
    
    
}
