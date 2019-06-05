package Entities;

import Renderer.ProjectileRenderer;
import Renderer.SpriteRenderer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Fireball extends Projectile {

    public static final int SIZE = 24;

    public Fireball(Vector2f position, Vector2f direction) {
        super(position.getX(), position.getY(), Snowball.MAX_SPEED, Snowball.ACCELERATION_RATE, SIZE, direction);

        this.updateSpeed(direction.normalise().scale(this.getAccelerationRate()));
    }

    @Override
    public void fadeOut() {
        if(this.renderer != null)
            this.renderer.setOpacity(0);
    }
}
