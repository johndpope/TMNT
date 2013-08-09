/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.tmnt.levels;

import com.gamesbykevin.framework.base.Sprite;

import java.awt.geom.Area;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All levels will 
 * @author GOD
 */
public abstract class Level extends Sprite
{
    //what is considered in bounds
    private Polygon bounds;
    
    //backgrounds
    private List<Sprite> backgrounds;
    
    //power-ups the hero can collect (will only likely be pizza)
    private List<Sprite> powerUps;
    
    //do we automatically scroll the background
    private boolean autoScrollBackground = false;
    
    private List<Integer> checkpoints;
    
    //total number of enemies to spawn for each check point
    private int enemiesPerCheckpoint;
    
    //the current number of enemies we have added so far at the current checkpoint
    private int enemiesCreatedAtCheckpoint;
    
    //total number of enemies that can be on the screen at the same time
    private int enemiesAtOnce;
    
    //how many powerups will the level have by default
    private static final int POWERUP_LIMIT_DEFAULT = 2;
    
    //how many power ups will this level have
    private int powerUpLimit = POWERUP_LIMIT_DEFAULT;
    
    /**
     * Create new Level
     * 
     * @param enemiesPerCheckpoint How many enemies to spawn per check point
     * @param enemiesAtOnce  How many enemies can be on the screen at once
     */
    public Level(final int enemiesPerCheckpoint, final int enemiesAtOnce)
    {
        this.enemiesPerCheckpoint = enemiesPerCheckpoint;
        this.enemiesAtOnce        = enemiesAtOnce;
    }
    
    /**
     * Set the power up limit for this level
     * @param powerUpLimit  Number of power ups we will have
     */
    public void setPowerUpLimit(final int powerUpLimit)
    {
        this.powerUpLimit = powerUpLimit;
    }
    
    private int getPowerUpLimit()
    {
        return this.powerUpLimit;
    }
    
    /**
     * Gets the starting point for the enemies so 
     * they don't appear out of bounds. The logic 
     * is as follows. Pick the left or right side 
     * depending on what is available. The area 
     * of the level boundary that intersects with 
     * each side is the location where the enemy
     * will spawn
     * 
     * @param screen The area of the game the user can see
     * @param playerHeight We need the height to aid picking a random location
     * @return 
     */
    public Point getStart(final Rectangle screen, final int playerWidth, final int playerHeight)
    {
        //the extra part we will check on each side
        final int extraWidth = (int)(screen.getWidth() * .2);
        
        //the edges on both sides
        Rectangle west = new Rectangle(screen.x - extraWidth, screen.y, extraWidth, screen.height);
        Rectangle east = new Rectangle(screen.x + screen.width + extraWidth, screen.y, extraWidth, screen.height);
        
        Area westSide = new Area(west);
        Area eastSide = new Area(east);
        Area levelBounds = new Area(getBoundary());
        
        //create area where left side intersects with the level boundary
        westSide.intersect(levelBounds);
        
        //create area where right side intersects with the level boundary
        eastSide.intersect(levelBounds);
        
        Rectangle tmp;
        
        //pick the random side to spawn
        if (Math.random() > .5)
            tmp = westSide.getBounds();
        else
            tmp = eastSide.getBounds();
        
        //if the west side in not a possibility then they have to spawn from the east side
        if (westSide.getBounds().getWidth() < 1 || westSide.getBounds().x - playerWidth < 0)
            tmp = eastSide.getBounds();
        
        //if the east side in not a possibility then they have to spawn from the west side
        if (eastSide.getBounds().getWidth() < 1)
            tmp = westSide.getBounds();
        
        //random coordinates inside Rectangle
        final int randomX = (int)(Math.random() * tmp.getWidth()) + tmp.x;
        
        List<Integer> possibilities = new ArrayList<>();

        for (int y = screen.y; y < screen.y + screen.height - (playerHeight / 2); y++)
        {
            if (getBoundary().contains(randomX, y))
                possibilities.add(y);
        }

        //get random y coordinate
        final int randomY = possibilities.get((int)(Math.random() * possibilities.size()));
        
        
        return new Point(randomX, randomY);
    }
    
    /**
     * the current number of enemies we have added so far at the current checkpoint
     * @return int
     */
    public int getEnemiesCreatedAtCheckpoint()
    {
        return this.enemiesCreatedAtCheckpoint;
    }
    
    /**
     * reset the count back to 0
     */
    private void resetEnemiesCreatedAtCheckpoint()
    {
        this.enemiesCreatedAtCheckpoint = 0;
    }

    /**
     * Increment the total by 1
     */
    public void addEnemiesCreatedAtCheckpoint()
    {
        this.enemiesCreatedAtCheckpoint++;
    }
    
    /**
     * total number of enemies to spawn for each check point
     * @return int
     */
    public int getEnemiesPerCheckpoint()
    {
        return this.enemiesPerCheckpoint;
    }
    
    /**
     * total number of enemies that can be on the screen at the same time
     * @return int
     */
    public int getEnemiesAtOnce()
    {
        return this.enemiesAtOnce;
    }
    
    /**
     * Each level will get x number of power ups 
     * placed in random locations throughout the level
     * 
     * @param image The image of the power up
     * @throws Exception 
     */
    public void createPowerUps(final Image image, final Rectangle screen) throws Exception
    {
        if (getWidth() <= 0)
        {
            throw new Exception("Dimensions have to be set first before calling this function");
        }
        else
        {
            if (getBoundary() == null)
            {
                throw new Exception("Level Bounds has to be set first before calling this function");
            }
            else
            {
                powerUps = new ArrayList<>();
                
                //each powerup will be placed in a different area
                final int eachSectionWidth = (int)((getWidth() / getPowerUpLimit()) * .9);
                
                //continue to loop until we have reached our limit
                while (powerUps.size() < getPowerUpLimit())
                {
                    Sprite powerUp = new Sprite();
                    
                    //set the powerup image
                    powerUp.setImage(image);
                    powerUp.setDimensions(image.getWidth(null), image.getHeight(null));
                    
                    //set auto size to true so the dimensions will be set automatically based on the image width/height
                    powerUp.setAutoSize(true);
                    
                    //now choose a random location
                    final int randomX;
                    
                    //if the first power up make adjustment so it doesn't appear on initial screen
                    if (powerUps.size() < 1)
                    {
                        randomX = (int)(Math.random() * (eachSectionWidth - screen.width)) + screen.width;
                    }
                    else
                    {
                        randomX = (int)(Math.random() * eachSectionWidth) + (powerUps.size() * eachSectionWidth);
                    }

                    List<Integer> possibilities = new ArrayList<>();
                    
                    for (int y = screen.y; y < screen.y + screen.height; y++)
                    {
                        if (getBoundary().contains(randomX, y))
                            possibilities.add(y);
                    }
                    
                    //get random y coordinate
                    final int randomY = possibilities.get((int)(Math.random() * possibilities.size()));
                    
                    //set random location
                    powerUp.setLocation(randomX, randomY);
                    
                    //add powerup to list
                    powerUps.add(powerUp);
                }
            }
        }
    }
    
    /**
     * Creates a number of check points when the hero hits the check point the enemies come
     */
    public void createCheckPoints(final int total) throws Exception
    {
        if (getWidth() <= 0)
        {
            throw new Exception("Dimensions have to be set first before calling this function");
        }
        else
        {
            checkpoints = new ArrayList<>();
            
            if (total > 0)
            {
                final int eachCheckpointLength = (int)((getWidth() / total) * .75);

                while (checkpoints.size() < total)
                {
                    //checkpoints.add(checkpoints.size() * eachCheckpointLength);
                    checkpoints.add((checkpoints.size() * eachCheckpointLength) + eachCheckpointLength);
                }
            }
        }
    }
    
    /**
     * Checks current scroll location to see if we have past a checkpoint.
     * If check point is found remove check point and return true
     * 
     * @param playerWidth extra pixels to account for
     * 
     * @return boolean
     */
    public boolean hasCheckpoint(final int playerWidth)
    {
        Rectangle r = getBoundary().getBounds();
        
        for (int i=0; i < checkpoints.size(); i++)
        {
            //if the scroll x coordinate has past a check point 
            if (r.x + r.width < checkpoints.get(i) + playerWidth)
            {
                //reset counter
                resetEnemiesCreatedAtCheckpoint();
                
                //remove the check point
                checkpoints.remove(i);
                
                //indicate we found one
                return true;
            }
        }
        
        return false;
    }
    
    public int getCheckpointCount()
    {
        return checkpoints.size();
    }
    
    /**
     * gets the list of power ups
     * @return List Sprtie
     */
    public List<Sprite> getPowerUps()
    {
        return this.powerUps;
    }
    
    /**
     * Gets the east most x-coordinate of the polygon
     * @return int x-coordinate
     */
    public int getEastBoundsX()
    {
        return (bounds.getBounds().x + bounds.getBounds().width);
    }
    
    /**
     * Set the background image and creates a specific number
     * of duplicate background depending on the background
     * width and the width of the screen that the user will see.
     * 
     * @param background Image of our repeating background
     */
    public void setBackgroundImage(final Image background, final Rectangle screen) throws Exception
    {
        if (getWidth() == 0)
            throw new Exception("Dimensions have to be set first before calling this function");
        
        //the width of the original background image
        final int backgroundWidth = background.getWidth(null);
        
        //make sure we have enough backgrounds so when one background begins to scroll off the screen we are covered
        final int numBackgrounds = (screen.width / backgroundWidth) + 2;
        
        this.backgrounds = new ArrayList<>();
        
        //continue adding backgrounds until we have reached our limit
        while (backgrounds.size() < numBackgrounds)
        {
            Sprite tmp = new Sprite();

            tmp.setImage(background);
            tmp.setDimensions(backgroundWidth, background.getHeight(null));
            tmp.setX(backgrounds.size() * backgroundWidth);
            tmp.setY(0);
            this.backgrounds.add(tmp);
        }
    }
    
    /**
     * If we set the auto scroll, then that means there is a separate background image
     * @param autoScrollBackground 
     */
    protected void setAutoScrollBackground(final boolean autoScrollBackground)
    {
        this.autoScrollBackground = autoScrollBackground;
    }
    
    private boolean hasAutoScrollBackground()
    {
        return this.autoScrollBackground;
    }
    
    protected void setBoundary(final Polygon bounds)
    {
        this.bounds = bounds;
    }
    
    /**
     * Return the boundary set for this level. This boundary
     * is used so we can determine where the players are 
     * able to walk.
     * 
     * @return Polygon the boundary set for this level 
     */
    public Polygon getBoundary()
    {
        return this.bounds;
    }
    
    public boolean hasBounds(Rectangle rectangle)
    {
        return (this.bounds.contains(rectangle));
    }
    
    /**
     * Set the auto scroll speed of the background
     * @param autoScrollSpeed Pixels per frame to scroll
     */
    public void setAutoScrollSpeed(final int autoScrollSpeed)
    {
        for (Sprite background : backgrounds)
        {
            background.setVelocityX(-autoScrollSpeed);
        }
    }
    
    public void setScrollSpeed(final int scrollSpeed)
    {
        if (backgrounds != null)
        {
            //make sure auto scroll is not enabled so we can sroll the background accordingly
            if (!hasAutoScrollBackground())
            {
                for (Sprite background : backgrounds)
                {
                    //if there is a scrolling speed it needs to be more than the level scroll
                    if (scrollSpeed != 0)
                    {
                        background.setVelocityX(-(scrollSpeed - 2));
                    }
                    else
                    {
                        background.setVelocityX(scrollSpeed);
                    }
                }
            }
        }
        
        //we also need to set the scroll speed for the power ups as well so they move with the level
        for (Sprite powerUp : powerUps)
        {
            powerUp.setVelocityX(-scrollSpeed);
        }
        
        //set the scroll speed for the level
        super.setVelocityX(-scrollSpeed);
    }
    
    /**
     * Scroll the level and background, and check if background needs to repeat.
     * 
     * @param screen Window of the game the user can see
     * @throws Exception 
     */
    public void update(final Rectangle screen) throws Exception
    {
        if (backgrounds != null)
        {
            for (Sprite background : backgrounds)
            {
                //update background position
                background.update();

                //if background is off screen repeat on the east side
                if (background.getX() + background.getWidth() < screen.x)
                    background.setX(screen.x + screen.width);
            }
        }
        
        if (bounds != null)
        {
            //update level position and bounds
            bounds.translate(super.getVelocityX(), 0);
        }
        
        //we also need to set the scroll speed for the power ups as well so they move with the level
        for (Sprite powerUp : powerUps)
        {
            powerUp.update();
        }
        
        super.update();
    }
    
    /**
     * Draw the level with the background
     * @param g Graphics object
     * @return Graphics object with level drawn to it
     */
    public Graphics render(Graphics g)
    {
        if (backgrounds != null)
        {
            for (Sprite background : backgrounds)
            {
                background.draw(g);
            }
        }
        
        //now draw level on top of background
        super.draw(g);
        
        if (bounds != null)
        {
            //draw bounds so we can see for now
            g.setColor(Color.RED);
            g.drawPolygon(bounds);
        }
        
        //return result
        return g;
    }
}