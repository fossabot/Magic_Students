package Entities.Projectiles;

import org.newdawn.slick.geom.Vector2f;

public class MeleeAttack extends Projectile {

    private static final int RADIUS = 25;

    public MeleeAttack(Vector2f position) {
        super(position.getX(), position.getY(), 0, 0, RADIUS, new Vector2f(0,0));
        this.isDead=true;
    }

    public static int getMeleeRadius(){return RADIUS;} //package private

    @Override
    public void fadeOut() {
    }
}

