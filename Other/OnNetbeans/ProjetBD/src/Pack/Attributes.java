package Pack;
//#yoloswag du 28 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author thomas
 */
public class Attributes<E> {
    private final String Name;
    private ArrayList<E> values;
    public Attributes(String Name,E... value){
        this.Name=Name;
        values = new ArrayList<>();
        values.addAll(Arrays.asList(value));
    }
//COMMENTAIRE DE CORENTIN POUR TRY FORK ET MERGE
    /**
     *
     * @return Tableau des valeurs "values" de l'attribut.
     * 
     */
    public ArrayList<E> getValues(){
        return values;
    }
    /**
     * 
     * @param object Objet de type paramétré par E.
     */
    public void add(E object){
        values.add(object);
    }
}
