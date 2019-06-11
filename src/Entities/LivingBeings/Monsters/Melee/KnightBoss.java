package Entities.LivingBeings.Monsters.Melee;

import Entities.LivingBeings.LivingBeing;
import Main.MainClass;
import Main.TimeScale;
import Managers.EnemiesManager;
import org.newdawn.slick.geom.Vector2f;

import java.sql.Time;
import java.util.Random;

public class KnightBoss extends Knight {
    private int recoverTime = 0;
    private int summonCouldown = 30*60;

    public KnightBoss(float x, float y, int width, int height, float maxSpeed, float accelerationRate, int hpCount, int armor, int damage, int radius) {
        super(x, y, width, height, maxSpeed, accelerationRate, hpCount, armor, damage, radius);
    }

    @Override
    public void update(LivingBeing target){
        updateCouldown();
        if (this.isAttacking()){
            if (this.isAttackReady()){
                attack(target);
            }
            else {
                gettingReady();
            }
        }
        else {
            if (decideToSummon()){
                summon();
            }
            if (isAbleToMove()){
                this.updateSpeed(target.getPosition().sub(this.getPosition()).normalise().scale(this.getAccelerationRate()));
                this.move();
                if (isTargetInRange(target)){
                    startAttacking(target);
                }
            }
            else {
                recover();
            }
        }
    }

    private void updateCouldown() {
        if (summonCouldown != 0){
            summonCouldown = summonCouldown - 1;
        }
    }

    private void recover() {
        recoverTime = recoverTime - 1;
    }

    private boolean isAbleToMove() {
        return recoverTime == 0;
    }

    private void summon() {
        this.setSpeed(new Vector2f(0,0));
        MainClass.getInstance().getEnemiesManager().addKnight(new Vector2f(48,48));
    }

    private boolean decideToSummon(){
        Random random = new Random();
        return (random.nextFloat()%1 < 1f/(60f*3f));
    }
}


