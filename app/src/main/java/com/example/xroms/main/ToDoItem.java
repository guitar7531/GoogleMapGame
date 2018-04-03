package com.example.xroms.main;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by xRoms on 31.03.2018.
 */

public class ToDoItem {


    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("isHost")
    private boolean mIsHost;

    @com.google.gson.annotations.SerializedName("gamestarted")
    private boolean mGameStarted;

    @com.google.gson.annotations.SerializedName("name")
    private String mName;


    @com.google.gson.annotations.SerializedName("abase1")
    private double aBase1;

    @com.google.gson.annotations.SerializedName("bbase1")
    private double bBase1;

    @com.google.gson.annotations.SerializedName("lborder1")
    private double lBorder1;

    @com.google.gson.annotations.SerializedName("rborder1")
    private double rBorder1;

    @com.google.gson.annotations.SerializedName("position1")
    private double mPosition1;

    @com.google.gson.annotations.SerializedName("abase2")
    private double aBase2;

    @com.google.gson.annotations.SerializedName("bbase2")
    private double bBase2;

    @com.google.gson.annotations.SerializedName("lborder2")
    private double lBorder2;

    @com.google.gson.annotations.SerializedName("rborder2")
    private double rBorder2;

    @com.google.gson.annotations.SerializedName("position2")
    private double mPosition2;


    public ToDoItem() {

    }

    @Override
    public String toString() {
        return getName();
    }

    public ToDoItem(String name, String id, boolean isHost, boolean gameStarted) {
        this.setName(name);
        this.setId(id);
        this.setIsHost(isHost);
        this.setGameStarted(gameStarted);
    }


    public void setPosition1(double pos) {
        mPosition1 = pos;
    }
    public double getPosition1() {return mPosition1;}

    public void setPosition2(double pos) {
        mPosition2 = pos;
    }
    public double getPosition2() {return mPosition2;}

    public void setaBase1(double pos) {
        aBase1 = pos;
    }
    public double getaBase1() {return aBase1;}

    public void setaBase2(double pos) {
        aBase2 = pos;
    }
    public double getaBase2() {return aBase2;}

    public void setbBase1(double pos) {
        bBase1 = pos;
    }
    public double getbBase1() {return bBase1;}

    public void setbBase2(double pos) {
        bBase2 = pos;
    }
    public double getbBase2() {return bBase2;}

    public void setlBorder1(double pos) {
        lBorder1 = pos;
    }
    public double getlBorder1() {return lBorder1;}

    public void setlBorder2(double pos) {
        lBorder2 = pos;
    }
    public double getlBorder2() {return lBorder2;}

    public void setrBorder1(double pos) {
        rBorder1 = pos;
    }
    public double getrBorder1() {return rBorder1;}

    public void setrBorder2(double pos) {
        rBorder2 = pos;
    }
    public double getrBorder2() {return rBorder2;}

    public void setGameStarted(boolean gameStarted) {
        mGameStarted = gameStarted;
    }

    public boolean getGameStarted() {
        return mGameStarted;
    }

    /**
     * Returns the item text
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the item text
     *
     * @param text text to set
     */
    public final void setName(String text) {
        mName = text;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id id to set
     */
    public final void setId(String id) {
        mId = id;
    }

    /**
     * Indicates if the item is marked as completed
     */
    public boolean getIsHost() {
        return mIsHost;
    }

    public boolean isInMyRoom(String id) {
        return mId.equals(id);
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setIsHost(boolean complete) {
        mIsHost = complete;
    }

}