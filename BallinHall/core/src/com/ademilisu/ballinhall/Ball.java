package com.ademilisu.ballinhall;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Circle;

public class Ball {

    private Texture texture;
    private Circle circle;
    private BallSize size;
    private boolean isBurst;
    private ParticleEffect explode;
    private ParticleEffect ballEffect;
    private int explodeEffectCount;

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

    public BallSize getSize() {
        return size;
    }

    public void setSize(BallSize size) {
        this.size = size;
    }

    public boolean isBurst() {
        return isBurst;
    }

    public void setBurst(boolean burst) {
        isBurst = burst;
    }

    public ParticleEffect getExplode() {
        return explode;
    }

    public void setExplode(ParticleEffect explode) {
        this.explode = explode;
    }

    public int getEffectCount() {
        return explodeEffectCount;
    }

    public void setEffectCount(int effectCount) {
        this.explodeEffectCount = effectCount;
    }

    public ParticleEffect getBallEffect() {
        return ballEffect;
    }

    public void setBallEffect(ParticleEffect ballEffect) {
        this.ballEffect = ballEffect;
    }

    public int getExplodeEffectCount() {
        return explodeEffectCount;
    }

    public void setExplodeEffectCount(int explodeEffectCount) {
        this.explodeEffectCount = explodeEffectCount;
    }
}
