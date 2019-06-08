package Entities.LivingBeings;

import Entities.Projectiles.MeleeAttack;
import Entities.Projectiles.Snowball;
import Entities.LivingBeings.monsters.Ranged.Ranged;
import Main.MainClass;
import Renderer.LivingBeingRenderer;
import Renderer.PlayerMarkerRenderer;
import Renderer.SpriteView;
import com.sun.tools.javac.Main;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import static Main.MainClass.MAX_FPS;
import static Main.MainClass.getInGameTimeScale;

public class Player extends LivingBeing implements KeyListener, MouseListener{

    private boolean keyUp;
    private boolean keyDown;
    private boolean keyLeft;
    private boolean keyRight;
    private boolean keySpace;

    private double angleFaced;

    private PlayerMarkerRenderer playerMarkerRenderer;

    private int framesLeftBeforeAttack=0;
    private int framesLeftAfterDash=0;
    private Vector2f attackDirection = new Vector2f(0,0);

    /**
     * Single contructor
     *
     * @param gc game container
     * @param x initial x position of the player
     * @param y initial y position of the player
     */
    public Player(GameContainer gc, float x, float y) {
        super(x, y,450 / MAX_FPS, 135 / MAX_FPS, 100, 5, (int) (0.4*45));

        this.keyUp = false;
        this.keyDown = false;
        this.keyLeft = false;
        this.keyRight = false;
        this.keySpace = false;

        gc.getInput().addKeyListener(this);
        gc.getInput().addMouseListener(this);

        String prepath = "img/wizard/";
        int duration = 50;

        Color capeColor = new Color(0x0094ff);

        this.tileSize = new Vector2f(96, 96);
        this.renderer = new LivingBeingRenderer(this, this.tileSize, capeColor);
        this.renderer.setTopIdleView(new SpriteView(prepath + "topIdle.png", this.tileSize, duration, Color.red));
        this.renderer.setBottomIdleView(new SpriteView(prepath + "bottomIdle.png", this.tileSize, duration, Color.red));
        this.renderer.setLeftIdleView(new SpriteView(prepath + "leftIdle.png", this.tileSize, duration, Color.red));
        this.renderer.setRightIdleView(new SpriteView(prepath + "rightIdle.png", this.tileSize, duration, Color.red));

        this.renderer.setTopView(new SpriteView(prepath + "top.png", this.tileSize, duration, Color.red));
        this.renderer.setBottomView(new SpriteView(prepath + "bottom.png", this.tileSize, duration, Color.red));
        this.renderer.setLeftView(new SpriteView(prepath + "left.png", this.tileSize, duration, Color.red));
        this.renderer.setRightView(new SpriteView(prepath + "right.png", this.tileSize, duration, Color.red));

        this.playerMarkerRenderer = new PlayerMarkerRenderer(this, 2);

        this.setAngleFaced(gc.getInput().getMouseX(), gc.getInput().getMouseY());
    }

    @Override
    public void setShowDebugRect(boolean showDebugRect) {
        super.setShowDebugRect(showDebugRect);
    }

    public void setShowPlayerMarkerDebugRect(boolean showPlayerMarkerDebugRect) {
        this.playerMarkerRenderer.setShowDebugRect(showPlayerMarkerDebugRect);
    }

    public void setAngleFaced(int x, int y) {
        this.angleFaced = new Vector2f(x, y).sub(this.getPosition()).getTheta() + 90.0;
    }

    /**
     * In game calculations
     */
    public void update() {
        if(isAttacking()){
            if(isAttackReady()){
                doMeleeAttack();
            }
            else{
                prepareAttack();
            }
        }
        else if(this.keySpace){
            if(!isDashing()){
                startDash(MainClass.getInput().getMouseX(), MainClass.getInput().getMouseY());
            }
        }
        else if(isDashing()){
            framesLeftAfterDash-=1;
        }
        else if (this.keyUp || this.keyDown || this.keyLeft || this.keyRight) {
            if (this.keyUp) {
                this.updateSpeed(new Vector2f(0, -1).scale(this.getAccelerationRate()));
            }
            if (this.keyDown) {
                this.updateSpeed(new Vector2f(0, 1).scale(this.getAccelerationRate()));
            }
            if (this.keyLeft) {
                this.updateSpeed(new Vector2f(-1, 0).scale(this.getAccelerationRate()));
            }
            if (this.keyRight) {
                this.updateSpeed(new Vector2f(1, 0).scale(this.getAccelerationRate()));
            }
        }
        else{
            this.updateSpeed(this.getSpeed().negate().scale(0.2f));
        }
        this.move();
    }

    private void startMeleeAttack(int mouseX, int mouseY){
        this.attackDirection = new Vector2f(mouseX,mouseY).sub(this.getCenter()).normalise().scale(this.getRadius()).add(this.getCenter());
        this.speed.set(0,0);
        this.framesLeftBeforeAttack=60;
    }

    private Boolean isAttacking(){return !this.attackDirection.equals(new Vector2f(0,0));}
    private Boolean isAttackReady(){return this.framesLeftBeforeAttack==0;}

    private void prepareAttack(){this.framesLeftBeforeAttack -= 1;}

    /**
     * do a melee attack
     */
    private void doMeleeAttack(){
        Ranged.allyProjectiles.add(new MeleeAttack(this.getCenter().add(this.getCenter().sub(this.attackDirection).normalise().scale(-this.getRadius())).add(new Vector2f(-MeleeAttack.getMeleeRadius(), -MeleeAttack.getMeleeRadius())), this.attackDirection));
        this.attackDirection.set(0,0);
    }

    /**
     * Do a ranged attack
     */
    private void doRangedAttack() {
        Vector2f direction = new Vector2f( Math.round(MainClass.getInput().getMouseX()), Math.round(MainClass.getInput().getMouseY() )).sub(this.getCenter());
        Ranged.allyProjectiles.add(new Snowball(direction.copy().normalise().scale(this.getRadius()).add(new Vector2f(this.getCenter().x - Snowball.getSnowballRadius(), this.getCenter().y - Snowball.getSnowballRadius())), direction)); //décalage car bord haut gauche
    }

    private void startDash(int mouseX, int mouseY){
        attackDirection = new Vector2f(mouseX,mouseY);
        framesLeftAfterDash = 15;
        this.speed = attackDirection.copy().sub(this.getCenter()).normalise().scale(MAX_SPEED*1.42f);
    }

    public boolean isDashing(){
        return framesLeftAfterDash!=0;
    }

    /**
     * In game rendering
     * @param g the graphics to draw on
     */
    public void render(Graphics g) {
        Vector2f facedDirection = new Vector2f(0,0);
        if(this.keyDown) {
            facedDirection.y = 1;
        } else if(this.keyUp) {
            facedDirection.y = -1;
        }
        if(this.keyRight) {
            facedDirection.x = 1;
        } else if(this.keyLeft) {
            facedDirection.x = -1;
        }

        this.playerMarkerRenderer.Render(g, angleFaced);

        super.render(g, facedDirection);
    }

    /**
     * KeyListener interface key down implementation
     * @param key integer value of the key
     * @param c char associated to the int value
     */
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_UP:
            case Input.KEY_Z:
                this.keyUp = true;
                break;
            case Input.KEY_DOWN:
            case Input.KEY_S:
                this.keyDown = true;
                break;
            case Input.KEY_LEFT:
            case Input.KEY_Q:
                this.keyLeft = true;
                break;
            case Input.KEY_RIGHT:
            case Input.KEY_D:
                this.keyRight = true;
                break;
            case Input.KEY_SPACE:
                this.keySpace=true;
                break;
        }
    }


    /**
     * KeyListener interface key up implementation
     * @param key integer value of the key
     * @param c char associated to the int value
     */
    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_UP:
            case Input.KEY_Z:
                this.keyUp = false;
                break;
            case Input.KEY_DOWN:
            case Input.KEY_S:
                this.keyDown = false;
                break;
            case Input.KEY_LEFT:
            case Input.KEY_Q:
                this.keyLeft = false;
                break;
            case Input.KEY_RIGHT:
            case Input.KEY_D:
                this.keyRight = false;
                break;
            case Input.KEY_SPACE:
                this.keySpace = false;
                break;
        }
    }

    /**
     * key listener interface implementation (empty)
     * @param input the input
     */
    @Override public void setInput(Input input) {}

    /**
     * key listener interface implementation (empty)
     * @return false
     */
    @Override public boolean isAcceptingInput() { return true; }

    /**
     * key listener interface implementation (empty)
     */
    @Override public void inputEnded() {}

    /**
     * key listener interface implementation (empty)
     */
    @Override public void inputStarted() {}


    @Override
    public void mouseWheelMoved(int change) {doRangedAttack();

    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (getInGameTimeScale().getTimeScale() != 0f) {
            //this.doAttack();
            if(!isAttacking()){
                startMeleeAttack(x,y);
            }
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        this.setAngleFaced(newx, newy);
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {

    }
}