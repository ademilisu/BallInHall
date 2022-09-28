package com.ademilisu.ballinhall;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;


public class Block {

    private Texture texture;
    private Rectangle rectangle;
    private int crack = 0;
    private String inWhich;
    private boolean isTrap;
    private boolean isTrapActive;
    private boolean isHole;
    private boolean isSpike;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public int getCrack() {
        return crack;
    }

    public void setCrack(int crack) {
        this.crack = crack;
    }

    public String getInWhich() {
        return inWhich;
    }

    public void setInWhich(String inWhich) {
        this.inWhich = inWhich;
    }

    public boolean isTrap() {
        return isTrap;
    }

    public void setTrap(boolean trap) {
        isTrap = trap;
    }

    public boolean isTrapActive() {
        return isTrapActive;
    }

    public void setTrapActive(boolean trapActive) {
        isTrapActive = trapActive;
    }

    public boolean isHole() {
        return isHole;
    }

    public void setHole(boolean hole) {
        isHole = hole;
    }

    public boolean isSpike() {
        return isSpike;
    }

    public void setSpike(boolean spike) {
        isSpike = spike;
    }

}
