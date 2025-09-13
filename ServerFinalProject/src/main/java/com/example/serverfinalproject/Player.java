package com.example.serverfinalproject;

import java.util.ArrayList;

public class Player {
    private String name;
    private String playerImageN;
    private String color;
    private boolean eliminated;
    private int intelPoints;
    private int energy;
    private ArrayList<Integer> controlledPositions = new ArrayList<>();
    private Position currentPosition;
    private int currentPIMNum;
    private int previousLocation;
    private int playerTime;
    private int winsNum;
    public Player(String n, String im){
        name = n;
        playerImageN = "src/main/resources/images/player"+im+".png";
        energy = 2;
        intelPoints = 0;
        color = im;
        eliminated = false;
        winsNum = 0;
        playerTime = 20;
    }
    public boolean getEliminated(){
        return eliminated;
    }
    public void setEliminated(boolean b){
        eliminated = b;
    }
    public int getWinsNum(){
        return winsNum;
    }
    public void setWinsNum(int i){
        winsNum = i;
    }
    public void setCurrentPosition(int n){
        previousLocation = currentPIMNum;
        currentPIMNum = n;
    }
    public int getCurrentPIMNum(){
        return currentPIMNum;
    }
    public void setPlayerTime(int i){
        playerTime = i;
    }
    public int getPlayerTime(){
        return playerTime;
    }
    public String getPlayerImageN(){
        return playerImageN;
    }
    public String getName(){
        return name;
    }
    public String getColor(){
        return color;
    }
    public void setColor(String c){
        color = c;
    }
    public int getIntelPoints(){
        return intelPoints;
    }
    public int getEnergy(){
        return energy;
    }
    public ArrayList<Integer> getControlledPositions(){
        return controlledPositions;
    }
    public void setControlledPositions(int p){
        controlledPositions.add(p);
    }
    public void setIntelPoints(int i){
        intelPoints = i;
    }
    public void setEnergy(int e){
        energy = e;
    }
    public void setCurrentPosition(Position p){
        currentPosition = p;
    }
    public Position getCurrentPosition(){
        return currentPosition;
    }
    public int getPreviousLocation(){
        return previousLocation;
    }
    public boolean checkIfControlled(int place){
        for(int i = 0; i< controlledPositions.size(); i++){
            if(controlledPositions.get(i)==place){
                return true;
            }
        }
        return false;
    }
}
