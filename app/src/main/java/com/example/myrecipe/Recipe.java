package com.example.myrecipe;

public class Recipe {
    private String name, tag, itemsJson, process;
    private boolean canBeDone, favorite;

    public Recipe(String name, String tag, String itemsJson, String process,
                  boolean canBeDone, boolean favorite) {
        this.name      = name;
        this.tag       = tag;
        this.itemsJson = itemsJson;
        this.process   = process;
        this.canBeDone = canBeDone;
        this.favorite  = favorite;
    }

    public String getName()      { return name; }
    public String getTag()       { return tag; }
    public String getItemsJson() { return itemsJson; }
    public String getProcess()   { return process; }
    public boolean isCanBeDone() { return canBeDone; }
    public boolean isFavorite()  { return favorite; }
}
