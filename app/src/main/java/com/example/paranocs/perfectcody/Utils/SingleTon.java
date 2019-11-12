package com.example.paranocs.perfectcody.Utils;

public class SingleTon {
    private SingleTon(){}

    private static class SingleTonHolder {
        private static final SingleTon INSTANCE = new SingleTon();
    }

    public static SingleTon getInstance(){
        return SingleTonHolder.INSTANCE;
    }

    public String toString(Object o){
        return o != null ? o.toString() : "0";
    }
}
