package Entities.LivingBeings;

import Entities.Entity;
import Entities.LivingBeings.Monsters.Monster;
import Listeners.LivingBeingHealthListener;
import Listeners.LivingBeingMoveListener;
import Main.GameStats;
import Main.MainClass;
import Main.TimeScale;
import Renderers.LivingBeingRenderer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.ceil;

public abstract class LivingBeing extends Entity implements Comparable {
    private int currentHealthPoints;
    private int maxHealthPoints;
    private int armorPoints;

    private ArrayList<LivingBeingHealthListener> livingBeingHealthListeners;
    private ArrayList<LivingBeingMoveListener> livingBeingMoveListeners;

    public static ArrayList<LivingBeing> livingBeings = new ArrayList<>();

    protected LivingBeingRenderer renderer;

    public abstract float getMaxSpeed();

    public abstract float getAccelerationRate();

    public void heal(int amountOfHealing) {
        this.currentHealthPoints = this.currentHealthPoints + amountOfHealing;
        if (this.currentHealthPoints > this.maxHealthPoints) {
            this.currentHealthPoints = this.maxHealthPoints;
        }

        // update bar on heal too
        for (LivingBeingHealthListener listener : livingBeingHealthListeners) {
            listener.onUpdate(this, amountOfHealing);
            listener.onHeal(this, amountOfHealing);
        }
    }

    public void buffHP(int buffAmount) {
        this.maxHealthPoints = this.maxHealthPoints + buffAmount;
        this.heal(buffAmount);
    }

    public void buffArmor(int buffAmount) {
        this.armorPoints = this.armorPoints + buffAmount;
    }

    /**
     * Updates speed with an acceleration
     * @param acceleration the given acceleration
     */
    protected void updateSpeed(Vector2f acceleration) {
        super.setSpeed(super.getSpeed().add(acceleration));

        if (super.getSpeed().length() > this.getMaxSpeed() * TimeScale.getInGameTimeScale().getTimeScale()) {
            super.setSpeed(super.getSpeed().normalise().scale(this.getMaxSpeed() * TimeScale.getInGameTimeScale().getTimeScale()));
        }

        if (super.getSpeed().getX() > -LivingBeingConstants.MINIMUM_SPEED  && super.getSpeed().getX() < LivingBeingConstants.MINIMUM_SPEED) {
            super.setSpeed(0, super.getSpeed().getY());
        }

        if (super.getSpeed().getY() > -LivingBeingConstants.MINIMUM_SPEED && super.getSpeed().getY() < LivingBeingConstants.MINIMUM_SPEED) {
            super.setSpeed(super.getSpeed().getX(), 0);
        }
    }

    /**
     * In game rendering of all Living beings
     * @param g the graphics to draw on
     */
    public static void sortAndRenderLivingBeings(Graphics g) {
        Collections.sort(livingBeings);

        for (LivingBeing lb : livingBeings) {
            lb.render(g);
        }
    }

    public void addHealthListener(LivingBeingHealthListener listener) {
        this.livingBeingHealthListeners.add(listener);
    }

    public void addMoveListener(LivingBeingMoveListener listener) {
        this.livingBeingMoveListeners.add(listener);
    }

    public int getArmorPoints() {
        return this.armorPoints;
    }

    /**
     * Returns the current health points
     * @return the current health points
     */
    public int getCurrentHealthPoints() {
        return this.currentHealthPoints;
    }

    /**
     * Returns the maximum health points
     * @return the maximum health points
     */
    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    /**
     * Single constructor
     * @param x x initial position of the living being
     * @param y y initial position of the living being
     * @param height the height of the living being
     * @param width the width of the living being
     * @param maxHealthPoints maximum health points of the living being
     * @param armorPoints armor points of the living being
     * @param radius the collision radius
     */
    public LivingBeing(float x, float y, int width, int height, int maxHealthPoints, int armorPoints, int radius) {
        super(x, y, width, height, radius);
        this.currentHealthPoints = maxHealthPoints;
        this.maxHealthPoints = maxHealthPoints;
        this.armorPoints = armorPoints;
        this.livingBeingHealthListeners = new ArrayList<>();
        this.livingBeingMoveListeners = new ArrayList<>();

        livingBeings.add(this);
    }

    public LivingBeing(float x, float y, int maxHealthPoints, int armorPoints, int radius) {
        super(x, y, radius);
        this.currentHealthPoints = maxHealthPoints;
        this.maxHealthPoints = maxHealthPoints;
        this.armorPoints = armorPoints;

        this.livingBeingHealthListeners = new ArrayList<>();
        this.livingBeingMoveListeners = new ArrayList<>();

        livingBeings.add(this);
    }

    /**
     * allows the living being to take damage
     * @param damage damage value inflicted
     */
    public void takeDamage(int damage) {
        int tmp = this.currentHealthPoints;
        this.currentHealthPoints = Math.max(0, this.currentHealthPoints - Math.max(damage - this.armorPoints, 0));

        // launching listeners
        for (LivingBeingHealthListener listener : this.livingBeingHealthListeners) {
            listener.onUpdate(this, tmp-currentHealthPoints);
            listener.onHurt(this, tmp-currentHealthPoints);
        }

        if(this instanceof Monster) {
            GameStats.getInstance().onAttack(tmp-currentHealthPoints);
        }

        if(this.currentHealthPoints == 0) {
            for(LivingBeingHealthListener listener : this.livingBeingHealthListeners) {
                listener.onDeath(this);
            }
        }
    }

    public boolean isDead() {
        return this.currentHealthPoints <= 0;
    }

    private void solveCollision(LivingBeing pusher, LivingBeing percuted, int level) {
        if (level <= MainClass.getInstance().getEnemies().size()) {
            percuted.collidingAction(pusher);
            if (percuted.collidesWith(MainClass.getInstance().getPlayer())) {
                solveCollision(percuted, MainClass.getInstance().getPlayer(), level + 1);
            }
            for (Monster m: MainClass.getInstance().getEnemies()) {
                if (percuted.collidesWith(m)) {
                    solveCollision(percuted, m, level + 1);
                }
            }
        }
    }

    public void checkCollision() {
        if (this.collidesWith(MainClass.getInstance().getPlayer())) {
            solveCollision(this, MainClass.getInstance().getPlayer(), 1);
        }
        for (Monster m: MainClass.getInstance().getEnemies()) {
            if (this.collidesWith(m)) {
                solveCollision(this, m, 1);
            }
        }
    }

    private void tpOutOf(LivingBeing opponent) {
        Vector2f diff = super.getCenter().sub(opponent.getCenter()).normalise().scale((float) ceil(super.getRadius() + opponent.getRadius() - opponent.getCenter().sub(super.getCenter()).length()));
        super.setCenter(super.getCenter().add(diff));
        this.tpInBounds();
    }

    public void collidingAction(LivingBeing opponent) {
        if (this.collidesWith(opponent)) {
            this.tpOutOf(opponent);
            opponent.tpOutOf(this);
        }
    }
    private void tpInBounds() {
        if (super.getCenter().x < super.getRadius()) {
            super.setCenter(new Vector2f(super.getRadius(), super.getCenter().getY()));
        }
        if (super.getCenter().x >= MainClass.WIDTH - super.getRadius()) {
            super.setCenter(new Vector2f(MainClass.WIDTH - super.getRadius(), super.getCenter().getY()));
        }
        if (super.getCenter().y < super.getRadius()) {
            super.setCenter(new Vector2f(super.getCenter().getX(), super.getRadius()));
        }
        if (super.getCenter().y >= MainClass.HEIGHT - super.getRadius()) {
            super.setCenter(new Vector2f(super.getCenter().getX(), MainClass.HEIGHT - super.getRadius()));
        }
    }

    public void move() {
        super.setCenter(super.getCenter().add(super.getSpeed().scale(TimeScale.getInGameTimeScale().getTimeScale())));
        this.tpInBounds();

        for (LivingBeingMoveListener listener : this.livingBeingMoveListeners) {
            listener.onMove(this);
        }
    }

    public void render(Graphics g, Vector2f facedDirection) {
        if (this.renderer != null) {
            this.renderer.render(g, facedDirection);
        }
        super.render(g);
    }

    @Override
    public int compareTo(Object o) {
        if (this.getCenter().getY() > ((LivingBeing) o).getCenter().getY()) {
            return 1;
        } else {
            return 0;
        }
    }
}
