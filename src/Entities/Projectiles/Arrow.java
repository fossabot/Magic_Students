package Entities.Projectiles;

import Renderer.ProjectileRenderer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Arrow extends Projectile {

    private static final int SIZE = 1;

    public Arrow(Vector2f position, Vector2f direction) {
        super(position.getX(), position.getY(), Snowball.MAX_SPEED*4, Snowball.ACCELERATION_RATE*4, SIZE, direction);

        this.updateSpeed(direction.normalise().scale(this.getAccelerationRate()));

        try {
            this.renderer = new ProjectileRenderer(this, new Image("img/arrow_32x32.png", false, Image.FILTER_NEAREST).getScaledCopy(2), new Vector2f(64, 64), 1000);
        } catch (SlickException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void fadeOut() {

    }
}