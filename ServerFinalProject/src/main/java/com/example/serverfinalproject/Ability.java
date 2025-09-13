package com.example.serverfinalproject;

public class Ability {
    private String abilityName;
    private int abilityCost;
    public Ability(String n, int aC){
        abilityName = n;
        abilityCost = aC;
    }
    public void setAbilityCost(int c){
        abilityCost= c;
    }
    public int getAbilityCost() {
        return abilityCost;
    }
    public String getAbilityName(){
        return abilityName;
    }
}
