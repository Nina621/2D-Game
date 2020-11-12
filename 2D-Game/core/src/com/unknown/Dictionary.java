package com.unknown;

import com.badlogic.gdx.utils.ObjectMap;


// Podrska za visejezicnost - I6 #1

public class Dictionary {

    public enum Keys{

        KeyHello,
        KeyPlay,
        KeyInterrupt
    }
    public ObjectMap<String,String>map;

    public Dictionary(){}

    public  String getValue(Keys key){
        return map.get(key.name());
    }
}
