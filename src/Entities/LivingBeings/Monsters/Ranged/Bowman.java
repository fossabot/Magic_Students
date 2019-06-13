package Entities.LivingBeings.Monsters.Ranged;

import Entities.LivingBeings.LivingBeing;
import Entities.Projectiles.Arrow;
import Entities.Projectiles.Fireball;
import Entities.Projectiles.Snowball;
import Main.MainClass;
import Renderers.LivingBeingRenderer;
import Renderers.SpriteView;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Bowman extends Ranged {

    public static final Vector2f BOWMAN_TILESIZE = new Vector2f(48,48);
    static final int SHOT_DELAY = 120;
    int delayCounter;

    public Bowman(float x, float y, float maxSpeed, float accelerationRate, int hpCount, int armor, int damage, int radius){
        super(x, y, (int) BOWMAN_TILESIZE.getX(), (int) BOWMAN_TILESIZE.getY(), maxSpeed, accelerationRate, hpCount, armor, damage, radius);
        this.delayCounter = 0;

        this.renderer = new LivingBeingRenderer(this, BOWMAN_TILESIZE);

        final String prepath = "img/bowman/";

        final int duration = 1000/8;

        for(String vision : LivingBeingRenderer.ACCEPTED_VISION_DIRECTIONS) {
            this.renderer.addView(vision + "Move", new SpriteView(prepath + vision + ".png", BOWMAN_TILESIZE, duration));
        }
    }

    public Bowman(float x, float y, Vector2f tileSize, float maxSpeed, float accelerationRate, int hpCount, int armor, int damage, int radius){
        super(x, y, (int) tileSize.getX(), (int) tileSize.getY(), maxSpeed, accelerationRate, hpCount, armor, damage, radius);
        this.delayCounter = 0;

        this.renderer = new LivingBeingRenderer(this, tileSize);

        final String prepath = "img/bowman/";

        final int duration = 1000/8;

        for(String vision : LivingBeingRenderer.ACCEPTED_VISION_DIRECTIONS) {
            this.renderer.addView(vision + "Move", new SpriteView(prepath + vision + ".png", tileSize, duration));
        }
    }

    @Override
    public void update(LivingBeing target) {

        if(target.getPosition().distance(this.getPosition()) < 150) {
            this.updateSpeed(target.getPosition().sub(this.getPosition()).normalise().negate().scale(this.getAccelerationRate()));
            this.move();
        }
        else if(this.getSpeed().length()!=0){
            this.updateSpeed(this.getSpeed().normalise().negate().scale(getAccelerationRate()));
            this.move();
        }
        else if(this.delayCounter > SHOT_DELAY) {
            attack(target);
        }
        else{
            this.delayCounter = Math.min(this.delayCounter + 1, 121);
        }
    }

    protected void attack(LivingBeing target){

        Vector2f direction = target.getPosition().sub(this.getPosition()).normalise();
        enemyProjectiles.add(new Arrow(this.getPosition().add(direction.copy().scale(this.getRadius())), direction));
        enemyProjectiles.get(enemyProjectiles.size()-1).setShowDebugRect(true);
        this.setSpeed(new Vector2f(0,0));
        this.delayCounter = 0;
    }

    public void render(Graphics g) {
        super.render(g);
    }
}
