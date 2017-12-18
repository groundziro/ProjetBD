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
    boolean isMinimal;

    public Key() {
        attributes=new ArrayList();
        isMinimal=true;
    }
    
    public Key(boolean b){
        isMinimal=b;
    }

    public Key(ArrayList<String> attributes) {
        this.attributes = attributes;
        isMinimal=true;
    }

    public Key(ArrayList<String> attributes, boolean isMinimal) {
        this.attributes = attributes;
        this.isMinimal = isMinimal;
    }
    
    public void setIsMinimal(boolean b){
        this.isMinimal=b;
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

    public ArrayList<String> getAttributes() {
        return attributes;
    }
}
