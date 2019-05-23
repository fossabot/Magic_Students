package Entities;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

public class Fireball extends Projectile {
    protected int radius;

    public Shape getBounds(){
        return new Circle(position.x,position.y,radius);
    }

    @Override
    protected int getWidth() { return this.radius; }
    @Override
    protected int getHeight() { return this.radius; }
}