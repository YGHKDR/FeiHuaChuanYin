package com.example.feihuachuanyindemo1;

public class ResponseObject {
    boolean status;
    int ret;
    String msg;
    Data data;

    public String getMsg(){
        return msg;
    }
    public boolean getStatus(){
        return status;
    }
    public String getAuthor(){
        return data.author;
    }
    public String getTitle(){
        return data.title;
    }
    public String getText(){
        return data.text;
    }
}

