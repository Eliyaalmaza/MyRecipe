package com.example.myrecipe;

public class Ingredient {
    private String name;
    private String tag;
    private String description;
    private boolean foundOrNot;
    private boolean favorite;

    public Ingredient(String name, String tag, String description, boolean foundOrNot, boolean favorite) {
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.foundOrNot = foundOrNot;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFoundOrNot() {
        return foundOrNot;
    }

    public void setFoundOrNot(boolean foundOrNot) {
        this.foundOrNot = foundOrNot;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
