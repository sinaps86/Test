package com.app.sinaps.tabtesting.List;

import java.io.Serializable;

/**
 * Created by Sinaps on 15.08.2017.
 */
/**
 * Класс наполнитель для элементов списка
 */
public class Country implements Serializable{
    private String name;
    private boolean isVisited;

    public Country(String name, boolean isVisited){
        this.name = name;
        this.isVisited = isVisited;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean isVisited(){
        return isVisited;
    }

    public void setVisited(boolean isVisited){
        this.isVisited = isVisited;
    }
}
