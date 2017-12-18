/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;

/**
 *
 * @author Alfa
 */
public class Key {
    ArrayList<String> attributes;

    public Key() {
        attributes=new ArrayList();
    }

    public Key(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public void addAttributes(String newAtr){
        attributes.add(newAtr);
    }
    @Override
    public String toString(){
        String str= "[";
        for(String s : attributes){
            str+=s+" ";
        }
        str=str.substring(0,str.length()-1);
        str+="]";
        return str;
    }
}
