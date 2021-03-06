package com.gamesbykevin.tmnt.grunt;

import com.gamesbykevin.tmnt.main.Resources.GamePlayers;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.tmnt.player.PlayerRules;

public final class FootSoldier2 extends Grunt implements PlayerRules
{
    private static final int SPRITE_WIDTH  = 60;
    private static final int SPRITE_HEIGHT = 60;
    
    /**
     * In this constructor we want to setup all of the animations for this Player
     */
    public FootSoldier2()
    {
        super(GamePlayers.FootSoldier2);
        
        //setup dimensions
        super.setDimensions(SPRITE_WIDTH, SPRITE_HEIGHT);
        
        //setup all animations for this player
        setupAnimations();
        
        //setup all velocity for this player
        setupVelocity();
        
        //set projectile limit
        super.setProjectileLimit(Grunt.PROJECTILE_LIMIT_DEFAULT);
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
        animation.add(getSpriteRectangle(2,1), getNanoSeconds(250));
        animation.add(getSpriteRectangle(3,1), getNanoSeconds(250));
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.IDLE);
        
        //throw projectile animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(3,2), getNanoSeconds(800));
        animation.add(getSpriteRectangle(0,0), getNanoSeconds(75));
        getSpriteSheet().add(animation, State.THROW_PROJECTILE);
        
        //projectile animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,0), getNanoSeconds(75));
        animation.add(getSpriteRectangle(2,0), getNanoSeconds(75));
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.PROJECTILE1);
        
        //walk horizontal animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(0,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(2,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(4,1), getNanoSeconds(125));
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.WALK_HORIZONTAL);
        
        //walk vertical animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(1,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(0,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(2,2), getNanoSeconds(125));
        animation.add(getSpriteRectangle(4,1), getNanoSeconds(125));
        animation.setLoop(true);
        getSpriteSheet().add(animation, State.WALK_VERTICAL);
        
        //hurt animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(3,0), getNanoSeconds(150));
        animation.add(getSpriteRectangle(4,0), getNanoSeconds(275));
        getSpriteSheet().add(animation, State.HURT);
        
        //dead animation
        animation = new SpriteSheetAnimation();
        animation.add(getSpriteRectangle(0,1), getNanoSeconds(200));
        animation.add(getSpriteRectangle(1,1), getNanoSeconds(1800));
        getSpriteSheet().add(animation, State.DEAD);
    }
}