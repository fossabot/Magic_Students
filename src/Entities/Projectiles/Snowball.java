package Entities.Projectiles;

import Main.MainClass;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Snowball extends Projectile {
    public static final float MAX_SPEED = 250/ MainClass.MAX_FPS;
    public static final float ACCELERATION_RATE = 135/ MainClass.MAX_FPS;
    private static final String IMAGE_PATH = "img/snowball.png";
    private static final int RADIUS = 10;

    /**
     * In game rendering
     * @param g the grapgics to draw on
     */
    public void render(Graphics g) {
        super.render(g);
        if (showDebugRect) {
            g.draw(this.getBounds());
        }
    }

    /**
     * Constructor made super simple
     * @param position the initial vector position
     * @param direction the direction vector
     */
    public Snowball(Vector2f position, Vector2f direction) {
        super(position.getX(), position.getY(), MAX_SPEED, ACCELERATION_RATE, direction, IMAGE_PATH, RADIUS);

        this.updateSpeed(direction.normalise().scale(this.getAccelerationRate()));
    }

    /**
     * Constructor made simple
     * @param x the initial x position of the snowball
     * @param y the initial x position of the snowball
     * @param direction the direction of the snowball
     */
    public Snowball(float x, float y, Vector2f direction) {
        super(x, y, MAX_SPEED, ACCELERATION_RATE, direction, IMAGE_PATH, RADIUS);

        this.updateSpeed(direction.normalise().scale(this.getAccelerationRate()));
    }

    /**
     * Complex constructor
     * @param x initial x position of the entity
     * @param y initial y position of the entity
     * @param maxSpeed maximum speed of the entity
     * @param accelerationRate acceleration factor of the entity
     * @param imagePath the image ref to the the snowball image
     * @param direction the direction vector
     */
    public Snowball(float x, float y, float maxSpeed, float accelerationRate, String imagePath, Vector2f direction) {
        super(x, y, maxSpeed, accelerationRate, direction, imagePath, RADIUS);

        this.updateSpeed(direction.normalise().scale(this.getAccelerationRate()));
    }

    /**
     * Fade out operation for the snowball
     */
    @Override
    public void fadeOut() {
        this.image.setAlpha(opacity);
    }

    public static int getSnowballRadius(){return RADIUS;}
}