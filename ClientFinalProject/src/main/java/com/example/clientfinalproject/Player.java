package com.example.clientfinalproject;

import java.util.ArrayList;

public class Player {
    private String name;
    private String playerImageN;
    private int intelPoints;
    private int energy;
    private int currentPIMNum;
    private long playerTime;
    private int previousLocation;
    private String color;
    private int winsNum;
    public Player(){

    }
    public int getWinsNum(){
        return winsNum;
    }
    public void setWinsNum(int i){
        winsNum = i;
    }
    public String getColor(){
        return color;
    }
    public void setColor(String c){
        color = c;
    }
    public void setPlayerTime(){
        playerTime = System.nanoTime();
    }
    public long getPlayerTime(){
        return playerTime;
    }
    public String getPlayerImageN(){
        return playerImageN;
    }
    public void setPlayerImageN(String ImNam){
        playerImageN = ImNam;
    }
    public void setName(String n){
        name = n;
    }
    public String getName(){
        return name;
    }
    public int getIntelPoints(){
        return intelPoints;
    }
    public int getEnergy(){
        return energy;
    }
    public void setIntelPoints(int i){
        intelPoints = i;
    }
    public void setEnergy(int e){
        energy = e;
    }
    public void setCurrentPosition(int n){
        previousLocation = currentPIMNum;
        currentPIMNum = n;
    }
    public int getCurrentPIMNum(){
        return currentPIMNum;
    }
    public void setPreviousLocation(int n){
        previousLocation = n;
    }
    public int getPreviousLocation(){
        return previousLocation;
    }
}
