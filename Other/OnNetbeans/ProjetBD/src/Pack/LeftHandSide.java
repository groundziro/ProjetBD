package Pack;

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
public class LeftHandSide {
    private final ArrayList<Attributes> Content;
    public LeftHandSide(Attributes... attributes){
        Content = new ArrayList<>();
        Content.addAll(Arrays.asList(attributes));
    }
}
