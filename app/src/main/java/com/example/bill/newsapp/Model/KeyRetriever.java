package com.example.bill.newsapp.Model;

public class KeyRetriever { //TODO: hide key, possibly in lower layer, to guard against reverse engineering
    public String key;

    public KeyRetriever(){
        key = "test";
    }

    public String getKey(){
        return key;
    }
}
