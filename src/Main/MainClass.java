package Main;

import Entities.*;
import HUD.FadeToBlack;
import HUD.HealthBar;
import HUD.PauseMenu;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Entities.Projectile.*;
import static java.lang.Math.round;

public class MainClass extends BasicGame {
    private Player player;
    private ArrayList<Monster> enemies = new ArrayList<>();

    public static final int MAX_FPS = 60;
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private static TimeScale inGameTimeScale = new TimeScale(1f);
    private static TimeScale guiTimeScale = new TimeScale(1f);

    private static GameContainer instanceGameContainer;
    private static MainClass instance = null;
    private boolean portalSet = false;
    private boolean portalEngaged = false;

    private HealthBar healthBar;

    private PauseMenu menu;
    private FadeToBlack fadeToBlack;

    public static boolean isGamePaused() {
        return instance.menu.isActive();
    }

    private void generateEnemies(Image skin, Vector2f tileSize, int[] viewFrames) {
        Random random = new Random();
        int randomX;
        int randomY;
        for(int i = 0; i< 2; i++){
            randomX = random.nextInt(Math.round(WIDTH-2*tileSize.getX())) + (int) tileSize.getX();
            randomY = random.nextInt(Math.round(HEIGHT-2*tileSize.getY())) + (int) tileSize.getY();
            switch(random.nextInt(2)){
                case 0 :
                    Bowman tmpb = new Bowman(randomX, randomY, (int) tileSize.getX(), (int) tileSize.getY(), 250/MAX_FPS, 60/MAX_FPS, 100,2,5,(int) Math.round(0.4*tileSize.getY()));
                    tmpb.setShowDebugRect(true);
                    this.enemies.add(tmpb);
                    break;
                case 1 :
                    Rusher tmpr = new Rusher(randomX, randomY, (int) tileSize.getX(), (int) tileSize.getY(), 250/MAX_FPS, 60/MAX_FPS, 100,2,5,(int) Math.round(0.4*tileSize.getY()));
                    tmpr.setShowDebugRect(true);
                    this.enemies.add(tmpr);
                    break;
                default: break;
            }
        }
    }

    private void generateRoom(GameContainer gc) throws SlickException {
        Ranged.allyProjectiles = new ArrayList<>();
        Ranged.enemyProjectiles = new ArrayList<>();
        generateEnemies(new Image("img/24x24.png", false, Image.FILTER_NEAREST).getScaledCopy(2).getSubImage(48, 0, 384, 48), new Vector2f(48,48), new int[] {2, 2, 2, 2});
    }

    public static void setGamePaused(boolean gamePaused) {
        getInGameTimeScale().setTimeScale((gamePaused) ? 0f : 1f);
        instance.menu.setActive(gamePaused);
    }

    private static void triggerGamePaused() {
        getInGameTimeScale().setTimeScale((isGamePaused()) ? 1f : 0f);
        instance.menu.setActive(!isGamePaused());
    }

    public static TimeScale getInGameTimeScale() {
        return inGameTimeScale;
    }

    public static MainClass getInstance() {
        return instance;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Monster> getEnemies() {
        return enemies;
    }

    public static TimeScale getGuiTimeScale() {
        return guiTimeScale;
    }

    public static Input getInput() {
        return instanceGameContainer.getInput();
    }

    public MainClass(String name) { super(name); }

    @Override
    public void init(GameContainer gc) throws SlickException {
        instanceGameContainer = gc;
        instance = this;
        menu = new PauseMenu(gc);
        this.fadeToBlack = new FadeToBlack(gc);

        this.player = new Player(gc,100,100);
        this.player.setShowDebugRect(true);
        this.healthBar = new HealthBar(this.player);

        SceneRenderer.generateBackground("img/ground.png", gc);

        generateRoom(gc);

        int[][] possible_positions = {{WIDTH / 2 - 20, 40}, {WIDTH / 2 - 20, HEIGHT - 40 - 40},
                {40, HEIGHT / 2 - 20}, {WIDTH - 40 - 40, HEIGHT / 2 - 20}};
        Portal portal;

        for (int p = 0; p < 4; p++) {
            portal = new Portal(possible_positions[p][0], possible_positions[p][1],
                    40, 40, 20);
            portal.setShowDebugRect(true);
            Portal.portals.add(portal);
        }
        this.portalSet = false;
        this.portalEngaged = false;

        System.out.println(Configuration.getConfigurationFile().getJSONObject("glossary").getString("title"));
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        inGameTimeScale.setDeltaTime(i);

        this.player.update();
        this.player.checkCollision();
        updateEnemyProjectile(player);
        updateAllyProjectiles();

        for(Monster enemy : this.enemies){
            enemy.update(this.player);
            enemy.checkCollision();
            if (this.player.isDead()){
                setGamePaused(true);
            }
        }

        for (int j=0; j<enemies.size(); j++) {
            if (enemies.get(j).isDead()) {
                System.out.println("You killed an enemy");
                LivingBeing.livingBeings.remove(this.enemies.get(j));
                this.enemies.remove(this.enemies.get(j));
            }
        }

        if (this.enemies.size() == 0) {
            if (!portalSet) {
                Random random = new Random();
                int nbVisiblePortal = 0;

                for (Portal portal : Portal.portals) {
                    if (random.nextInt(4) == 0) {
                        portal.setVisible(true);
                        nbVisiblePortal++;
                    }
                }
                if (nbVisiblePortal == 0) {
                    Portal.portals.get(random.nextInt(4)).setVisible(true);
                }
                portalSet = true;
            }

            if (this.portalEngaged) {
                for (Portal portal : Portal.portals) {
                    if (portal.isVisible() && player.collidesWith(portal)) {
                        getInGameTimeScale().setTimeScale(0f);
                        fadeToBlack.setActive(true);
                        this.portalEngaged = false;
                    }
                }
            }
        }

        if (fadeToBlack.isActive()) {
            fadeToBlack.update(gc);

            if (fadeToBlack.getCurrentCount() == fadeToBlack.getDuration() / 2) {
                generateRoom(gc);

                for (Portal portal_bis : Portal.portals) {
                    portal_bis.setVisible(false);
                    portalSet = false;
                }
            }
            else if (fadeToBlack.getCurrentCount() == fadeToBlack.getDuration()) {
                getInGameTimeScale().setTimeScale(1f);
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_ESCAPE) {
            triggerGamePaused();
        }
        if (key == Input.KEY_SPACE) {
            this.portalEngaged = true;
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        this.player.keyReleased(key, c);

        if (key == Input.KEY_SPACE) {
            this.portalEngaged = false;
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        SceneRenderer.renderBackground(g, 0, 0);

        LivingBeing.sortAndRenderLivingBeings(g);

        this.healthBar.render(g);
        for (Monster enemy: enemies){
            enemy.setHealthBar(new HealthBar(enemy ,(int) enemy.getPosition().x, (int) enemy.getPosition().y + (int) round(enemy.getRadius()*2.5)));
            enemy.getHealthBar().render(g);
        }
        for (Portal portal: Portal.portals) {
            if (portal.isVisible()) {
                portal.render(g);
            }
        }
        this.menu.render(g);
        this.fadeToBlack.render(g);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new MainClass("Magic Students"));
            appgc.setDisplayMode(WIDTH, HEIGHT, false);
            appgc.setTargetFrameRate(MAX_FPS);
            appgc.start();
        }
        catch (SlickException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
