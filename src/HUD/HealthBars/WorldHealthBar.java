package HUD.HealthBars;

import Entities.LivingBeings.LivingBeing;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import static java.lang.Math.round;

public class WorldHealthBar {
    private int x;
    private int y;
    private static final int width = 50;
    private static final int height = 10;

    private LivingBeing being;

    public WorldHealthBar(LivingBeing being) {
        this.being = being;
        this.x = 80;
        this.y = 10;
    }

    public WorldHealthBar(LivingBeing being, int x, int y){
        this.being = being;
        this.x = x;
        this.y = y;
    }

    public void render(Graphics g){
        g.setColor(new Color(255,0,0));
        g.fillRect(x, y, round(being.getCurrentHealthPoints()/(float) being.getMaxHealthPoints() * width), height);
    }
}