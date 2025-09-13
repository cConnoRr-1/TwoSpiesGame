package com.example.serverfinalproject;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Position {
    private String positionName;
    private ArrayList<Player> playersHere = new ArrayList<>();
    private ArrayList<Position> placesCanGo = new ArrayList<>();
    public Position(String placeName) {
        positionName = placeName;
    }
    public ArrayList<Player> getPlayersHere() {
        return playersHere;
    }
    public void setPlayersHere(Player p){
        playersHere.add(p);
    }
    public void playerLeftPosition(Player p){
        playersHere.remove(p);
    }
    public String getPositionName(){
        return positionName;
    }
    public void setPlacesCanGo(Position p){
        placesCanGo.add(p);
    }
    public ArrayList<Position> getPlacesCanGo(){
        return placesCanGo;
    }
}
