package com.ademilisu.ballinhall;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Bonus {

    private Texture texture;
    private Circle circle;
    private float velocity;
    private boolean isSwell = false;
    private boolean isActive = false;
    private boolean isHoleReady = false;
    private boolean isHoleSwell = false;
    private boolean isBlow = false;
    private int time = 0;
    private String type;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public boolean isSwell() {
        return isSwell;
    }

    public void setSwell(boolean swell) {
        isSwell = swell;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHoleReady() {
        return isHoleReady;
    }

    public void setHoleReady(boolean holeReady) {
        isHoleReady = holeReady;
    }

    public boolean isHoleSwell() {
        return isHoleSwell;
    }

    public void setHoleSwell(boolean holeSwell) {
        isHoleSwell = holeSwell;
    }

    public boolean isBlow() {
        return isBlow;
    }

    public void setBlow(boolean blow) {
        isBlow = blow;
    }

}
