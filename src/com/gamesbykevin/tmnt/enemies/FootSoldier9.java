package com.gamesbykevin.tmnt.enemies;

import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.tmnt.player.Player;
import com.gamesbykevin.tmnt.player.PlayerRules;

public class FootSoldier9 extends Enemy implements PlayerRules
{
    private static final int SPRITE_WIDTH  = 85;
    private static final int SPRITE_HEIGHT = 85;
    
    private static final int VELOCITY_WALK = 1;
    
    /**
     * In this constructor we want to setup all of the animations for this Player
     */
    public FootSoldier9()
    {
        //all default settings for this player
        setupDefaults();
    }
    
    @Override
    public void setupDefaults()
    {
        //setup dimensions
        super.setDimensions(SPRITE_WIDTH, SPRITE_HEIGHT);
        
        //setup all animations for this player
        setupAnimations();
        
        //setup all velocity for this player
        setupVelocity();
    }
    
    @Override
    public void setupVelocity()
    {
        this.setVelocityWalk(VELOCITY_WALK);
    }
    
    @Override
    public void setupAnimations()
    {
        SpriteSheetAnimation animation;
        
        //idle animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,1), getNanoSeconds(250));
        animation.setLoop(true);
        getSpriteSheet().add(animation, Player.State.IDLE);
        
        //throwing projectile animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,2), getNanoSeconds(200));
        animation.add(getSpriteRectangle(0,2), getNanoSeconds(100));
        animation.add(getSpriteRectangle(0,0), getNanoSeconds(100));
        animation.add(getSpriteRectangle(2,2), getNanoSeconds(100));
        animation.add(getSpriteRectangle(3,2), getNanoSeconds(100));
        getSpriteSheet().add(animation, Player.State.THROW_PROJECTILE);

        //projectile animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,0), getNanoSeconds(75));
        getSpriteSheet().add(animation, Player.State.PROJECTILE1);
        
        //walk horizontal animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(3,1), getNanoSeconds(125));
        animation.add(getSpriteRectangle(2,1), getNanoSeconds(125));
        animation.add(getSpriteRectangle(4,1), getNanoSeconds(125));
        animation.setLoop(true);
        getSpriteSheet().add(animation, Player.State.WALK_HORIZONTAL);
        
        //walk vertical animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(3,1), getNanoSeconds(125));
        animation.add(getSpriteRectangle(2,1), getNanoSeconds(125));
        animation.add(getSpriteRectangle(4,1), getNanoSeconds(125));
        animation.setLoop(true);
        getSpriteSheet().add(animation, Player.State.WALK_VERTICAL);
        
        //hurt animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(2,0), getNanoSeconds(50));
        animation.add(getSpriteRectangle(3,0), getNanoSeconds(200));
        getSpriteSheet().add(animation, Player.State.HURT);
        
        //dead animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(4,0), getNanoSeconds(50));
        animation.add(getSpriteRectangle(0,1), getNanoSeconds(200));
        getSpriteSheet().add(animation, Player.State.DEAD);
    }
}