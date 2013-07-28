package com.gamesbykevin.tmnt.enemies;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;

import com.gamesbykevin.tmnt.heroes.Hero;
import com.gamesbykevin.tmnt.player.Player;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Player
{
    //this is the hero the enemy has targeted
    private int assigned = -1;
    
    //the direction the player will attempt to attack from if not east then west
    private boolean attackEast = (Math.random() > .5);
    
    private static final double PROJECTILE_SPEED_RATIO = 5;
    
    private boolean step1 = true, step2 = false, step3 = false;
    
    public Enemy()
    {
        resetSteps();
    }
    
    /**
     * Run the AI here for the enemy
     */
    public void update(final Rectangle screen, final List<Hero> heroes)
    {
        super.update();
        
        if (getProjectile() != null)
        {
            for (Hero hero : heroes)
            {
                if (hero.getRectangle().intersects(getProjectile().getRectangle()))
                    setProjectile(null);
            }
            
            //projectile is no longer within game window
            if (getProjectile() != null && !screen.contains(getProjectile().getPoint()))
                setProjectile(null);
        }
        
        //make sure enemy has a hero targeted
        checkAssignment(heroes);
        
        //get the hero the enemy is assigned to attack
        Hero hero = heroes.get(assigned);
        
        //face the direction hero is
        if (getX() < hero.getX())
            setHorizontalFlip(false);
        else
            setHorizontalFlip(true);
        
        //make sure the hero isn't hurt or dead
        if (!hero.isDead() && !hero.isHurt())
        {
            //make sure enemy can walk or if they are walking to follow the logic below
            if (canWalk() || isWalking())
            {
                //reset state and velocity
                setState(State.IDLE);
                setVelocity(Player.VELOCITY_NONE, Player.VELOCITY_NONE);
                
                //surround the enemy first
                if (step1)
                    surroundEnemy(hero);
                
                //next, line up the y-cooridnate to get ready to attack
                if (!step1 && step2)
                    lineupAttack(hero);
                
                //then, close/spread the gap
                if (!step1 && !step2 && step3)
                    closeGap(hero);
                
                //here we check for the opportunity to attack and take it if available
                checkAttackOpportunity(hero);
            }
            
            if (isAttacking())
            {
                //if attack is projectile we need to add projectile
                if (getState() == State.THROW_PROJECTILE && getSpriteSheet().hasStarted())
                {
                    Sprite projectile = new Sprite();
                    projectile.setLocation(getX(), getY() - (getHeight() / 2));
                    projectile.setDimensions(getWidth(), getHeight());
                    projectile.setImage(getImage());

                    if (hasHorizontalFlip())
                    {
                        projectile.setHorizontalFlip(true);
                        projectile.setVelocity(-super.getVelocityWalk() * PROJECTILE_SPEED_RATIO, VELOCITY_NONE);
                    }
                    else
                    {
                        projectile.setHorizontalFlip(false);
                        projectile.setVelocity(super.getVelocityWalk() * PROJECTILE_SPEED_RATIO, VELOCITY_NONE);
                    }

                    //NOTE: all enemies not including bosses have 1 projectile
                    SpriteSheetAnimation animation = getSpriteSheet().getSpriteSheetAnimation(State.PROJECTILE1);
                    projectile.getSpriteSheet().add(animation, null);

                    setProjectile(projectile);
                }
            }
        }
        else
        {
            //if the hero is dead and the enemy is not jumping set them to idle state
            if (hero.isDead() && !isJumping())
            {
                setState(State.IDLE);
                setVelocity(Player.VELOCITY_NONE, Player.VELOCITY_NONE);
            }
        }
    }
    
    /**
     * If we have a chance to harm hero take it
     * @param hero The hero we want to harm.
     */
    private void checkAttackOpportunity(final Hero hero)
    {
        final State attackState = getAttackOpportunity(hero);
        
        if (attackState != null)
        {
            //while attacking the enemy isn't moving
            setVelocityX(VELOCITY_NONE);
            setVelocityY(VELOCITY_NONE);
            
            //when the player attacks it is no longer their turn
            resetSteps();
            
            //start attack here
            setState(attackState);
        }
    }
    
    /**
     * Start back at step 1
     */
    public void resetSteps()
    {
        setStep1(true);
        setStep2(false);
        setStep3(false);
    }
    
    /**
     * This step is where the enemy surrounds the hero
     * @return boolean
     */
    public boolean hasStep1()
    {
        return step1;
    }
    
    public void setStep1(final boolean step1)
    {
        this.step1 = step1;
        
    }
    
    /**
     * This step is when the enemy lines up to attack
     * @return boolean
     */
    public boolean hasStep2()
    {
        return step2;
    }
    
    public void setStep2(final boolean step2)
    {
        this.step2 = step2;
    }
    
    /**
     * This step is where the enemy closes the gap to attack the hero
     * @return boolean
     */
    public boolean hasStep3()
    {
        return step3;
    }
    
    public void setStep3(final boolean step3)
    {
        this.step3 = step3;
    }
    
    /**
     * Here depending on whether or not the enemy can throw a projectile 
     * the enemy could move further back or close in to be able to attack
     * @param hero 
     */
    private void closeGap(final Hero hero)
    {
        //if the enemy can throw a projectile we will handle this differently
        if (hasState(State.THROW_PROJECTILE))
        {
            //move away from hero since we are preparing to throw a projectile
            if (getX() <= hero.getX())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(-getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
            
            //move away from hero since we are preparing to throw a projectile
            if (getX() > hero.getX())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
        }
        else
        {
            if (getX() <= hero.getX())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
            
            if (getX() > hero.getX())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(-getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
        }
    }
    
    /**
     * Here the enemy will correct the y coordinate to 
     * lineup with the hero, basically ready to attack.
     * @param hero The hero we are targeting.
     */
    private void lineupAttack(final Hero hero)
    {
        //if the x velocity is still active or the hero is jumping we will not move into attack position
        if (hero.isJumping())
            return;
        
        Rectangle anchor = getAnchorLocation();
        Rectangle anchorHero = hero.getAnchorLocation();
        
        if (anchor.getY() >= anchorHero.getY() && anchor.getY() <= anchorHero.getY() + anchorHero.getHeight())
        {
            setStep2(false);
            setStep3(true);
            return;
        }
        
        //now that we are on the correct side we can fix the y coordinate
        if (getY() + getHeight() < hero.getY() + hero.getHeight() - getVelocityWalk())
        {
            setState(State.WALK_VERTICAL);
            setVelocityX(VELOCITY_NONE);
            setVelocityY(getVelocityWalk());
        }

        if (getY() + getHeight() > hero.getY() + hero.getHeight() + getVelocityWalk())
        {
            setState(State.WALK_VERTICAL);
            setVelocityX(VELOCITY_NONE);
            setVelocityY(-getVelocityWalk());
        }
    }
    
    /**
     * This is the initial state when an enemy starts. 
     * They will position themselves on the appropriate side
     * @param hero The hero to surround.
     */
    private void surroundEnemy(final Hero hero)
    {
        if (attackEast)
        {
            //if attacking from the east side and we aren't on the east side yet
            if (getX() < hero.getX() + hero.getWidth())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
            else
            {
                //we are done with the current step
                setStep1(false);
            }
        }
        
        if (!attackEast)
        {
            //if attacking from the west side and we aren't on the west side yet
            if (getX() > hero.getX() - hero.getWidth())
            {
                setState(State.WALK_HORIZONTAL);
                setVelocityX(-getVelocityWalk());
                setVelocityY(VELOCITY_NONE);
            }
            else
            {
                //we are done with the current step
                setStep1(false);
            }
        }
    }
    
    /**
     * Does this player attack from the east
     * @param attackEast 
     */
    public void setAttackEast(final boolean attackEast)
    {
        this.attackEast = attackEast;
    }
    
    public boolean hasAttackEast()
    {
        return this.attackEast;
    }
    
    /**
     * If the enemy is not assigned a hero to attack do so now.
     * NOTE: WILL NEED TO ADD LOGIC HERE SO IF THERE ARE MORE THAN 1 HERO
     * THE ENEMIES ASSIGNED TO EACH HERO IS EQUALLY SPREAD OUT
     * @param heroes Heroes to choose from
     */
    private void checkAssignment(final List<Hero> heroes)
    {
        if (assigned < 0 || assigned >= heroes.size())
        {
            //now we have an assigned target
            assigned = (int)(Math.random() * heroes.size());
        }
    }
    
    /**
     * If any enemy is within striking distance of the hero
     * they will take advantage of it. If there is no 
     * attack opportunity null will be returned.
     * @param hero The hero we are attacking
     * @return State, the action that will take place
     */
    private State getAttackOpportunity(final Hero hero)
    {
        //if the enemy can't attack return false, or if the hero is jumping
        if (!canAttack() || hero.isJumping())
            return null;
        
        //enemy anchor
        Rectangle anchor = getAnchorLocation();
        
        //hero anchor
        Rectangle anchorHero = hero.getAnchorLocation();
        
        boolean canAttack = false;
        
        //if the enemy bounds contains the center of the hero we can attack
        if (getRectangle().contains(hero.getPoint()) && anchor.intersects(anchorHero))
        {
            canAttack = true;
        }
        
        //if enemy has ability to throw a projectile and if the enemy y is within the hero y the hero can be attacked
        if (canThrowProjectile() && anchor.getY() >= anchorHero.getY() && anchor.getY() <= anchorHero.getY() + anchorHero.getHeight())
        {
            canAttack = true;
        }
        
        if (canAttack)
        {
            List<State> possible = getPossibleAttacks();
            
            if (possible.size() > 0)
            {
                final int rand = (int)(Math.random() * possible.size());
                return possible.get(rand);
            }
        }
            
        
        return null;
    }
    
    /**
     * We want a list of all the possible attacks for this enemy
     * @return ArrayList of possible states
     */
    private List<State> getPossibleAttacks()
    {
        List<State> possible = new ArrayList<>();
        
        //can only throw 1 projectile at a time
        if (hasState(State.THROW_PROJECTILE) && getProjectile() == null)
            possible.add(State.THROW_PROJECTILE);
        
        if (hasState(State.ATTACK1))
            possible.add(State.ATTACK1);
        if (hasState(State.ATTACK2))
            possible.add(State.ATTACK2);
        if (hasState(State.ATTACK3))
            possible.add(State.ATTACK3);
        if (hasState(State.ATTACK4))
            possible.add(State.ATTACK4);
        if (hasState(State.ATTACK5))
            possible.add(State.ATTACK5);
        
        return possible;
    }
}