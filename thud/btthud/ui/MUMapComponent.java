//
//  MUMapComponent.java
//  Thud
//
//  Created by Anthony Parker on Thu Dec 27 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import btthud.data.*;
import btthud.engine.*;
import btthud.util.*;

/* Notes:

This is the class that does probably the most interesting work in all of Thud. It draws the map and the components inside the map.

It tries to draw as much as possible beforehand (in the changeHeight() function) so as to do as little processing as possible when it is drawing a lot of hexes across and down. This includes filling, drawing lines, drawing terrain types, and drawing elevations. It stores all of this in an array then copies it while running. It is probably possible to do further optimizations here, and that would be a good idea.

Also needed; a better way to determine exactly what hexes to draw or not. Right now it just guestimates then adds on some on both sides. We could avoid drawing a lot of hexes if we had a more accurate algorithm.

- asp, 7/7/2002

*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.*;
import java.util.*;

public class MUMapComponent extends JComponent implements MouseListener
{
    MUData					data;
    MUPrefs					prefs;

    Font					smallFont;
    Font					hexNumberFont;
    Font					infoFont;
    Font					terrainFont;
    Font					elevFont;

    double					PI = Math.PI;

    FontRenderContext		frc;
    
    private int				numAcross = 20;
    private int				numDown = 20;

    private int				elevWidth[] = new int[10]; 	// Stores width of each elevation number glyph, 0 - 9

    static final int		UNKNOWN = 0;
    static final int		PLAIN = 1;
    static final int		WATER = 2;
    static final int		LIGHT_FOREST = 3;
    static final int		HEAVY_FOREST = 4;
    static final int		MOUNTAIN = 5;
    static final int		ROUGH = 6;
    static final int		BUILDING = 7;
    static final int		ROAD = 8;
    static final int		FIRE = 9;
    static final int		WALL = 10;
    static final int		SMOKE = 11;
    static final int		ICE = 12;
    static final int		SMOKE_OVER_WATER = 13;

    static final int		TOTAL_TERRAIN = 14;

    // There should be the same number of items in this array as TOTAL_TERRAIN
    private	char			terrainTypes[] = {'?', '.', '~', '`', '"', '^', '%', '@', '#', '&', '=', ':', '-', '+'};
    private BufferedImage	hexImages[][] = new BufferedImage[TOTAL_TERRAIN][10];			// One for each hex type and elevation

    GeneralPath				gp = new GeneralPath();
    HexShape				hexPoly;

    private int					h = 40;
    private float				w = h / 2f;
    private static final float	tan30 = (float) Math.tan(toRadians(30.0d)); //0.5773502692f;
    private float				l = h / 2f * tan30;

    private int					myLocX, myLocY;

    private int					barHeight = 15;
    private int					heatBarMaxLength = 50;

    RenderingHints				rHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Rectangle					bounds;
        
    public MUMapComponent(MUData data, MUPrefs prefs)
    {
        super();

        this.data = data;
        this.prefs = prefs;

        setupFonts();

        setDoubleBuffered(true);

        addMouseListener(this);
        
        // Do some initial setup
        changeHeight(prefs.hexHeight);
        precalculateNumbers();

        bounds = getBounds();
        
        //rHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        //rHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
    }
    
    /* ---------------------- */

    /**
     * This function is a hack, and there is a better way to detect hits on certain parts of the map.
     * Future expansion ideas: click unid ID to bring up scan, lock, other info
     */
    public void mouseClicked(MouseEvent e)
    {
        getBounds(bounds);

        // Check to see if the click is in our status bar...
        if (e.getY() > bounds.height - barHeight)
        {
            if (e.getX() > 10 && e.getX() < 20)
            {
                // '-' was pressed
                if (e.isControlDown())
                    prefs.hexHeight -= 10;	// double the zoom factor
                else
                    prefs.hexHeight -= 5;

                repaint();
            }
            else if (e.getX() > 25 && e.getX() < 35)
            {
                // '+' was pressed
                if (e.isControlDown())
                    prefs.hexHeight += 10;	// double the zoom factor
                else
                    prefs.hexHeight += 5;

                repaint();
            }
        }
    }

    // Not much to do for these
    public void mouseEntered(MouseEvent e)
    {

    }

    public void mouseExited(MouseEvent e)
    {

    }

    public void mousePressed(MouseEvent e)
    {

    }

    public void mouseReleased(MouseEvent e)
    {

    }
    /* ---------------------- */
    
    /**
      * The purpose of this function is to take some CPU-intensive stuff that mostly stays the same and move it outside
      * of the main drawing loops, for efficiency. We store the values in private class variables. If any font sizes change,
      * this function should be called again.
      */
    private void precalculateNumbers()
    {
        frc = new FontRenderContext(new AffineTransform(), true, true);			//((Graphics2D) getGraphics()).getFontRenderContext();
        
        Rectangle2D	elevRect;
        
        for (int i = 0; i < 10; i++)
        {
            elevRect = elevFont.getStringBounds(Integer.toString(i), frc);
            elevWidth[i] = (int) elevRect.getWidth();
        }
    }

    /**
      * This function is for outside callers who want to make sure that we redraw in case of preference color or font change.
      */
    public void newPreferences(MUPrefs prefs)
    {
        this.prefs = prefs;
        setupFonts();
        changeHeight(prefs.hexHeight);
    }
    /**
      * The purpose of this function is to take some CPU-intensive stuff that mostly stays the same and move it outside
      * of the main drawing loops, for efficiency. This one deals with saving the Images for each color of hex, to avoid 'fill' costs.
      * It should be called any time that the height changes.
      */
    private void changeHeight(int newHeight)
    {
        // Set some variables
        if (newHeight < 5)
            h = 5;
        else
            h = newHeight;
        
        w = h / 2f;
        l = h / 2f * tan30;

        terrainFont = new Font("Monospaced", Font.PLAIN, h/2 - 2);

        hexPoly = new HexShape(h);
        
        // Now draw our images      
        for (int i = 0; i < TOTAL_TERRAIN; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                BufferedImage	newImage = new BufferedImage(hexPoly.getBounds().width, hexPoly.getBounds().height, BufferedImage.TYPE_INT_ARGB);

                // Get the graphics context for this BufferedImage
                Graphics2D		g = (Graphics2D) newImage.getGraphics();

                //g.setColor(new Color((float) 1.0, (float) 1.0, (float) 1.0, (float) 0.0));
                //g.fill(hexPoly.getBounds());

                // Setup the color
                g.setColor(colorForElevation(colorForTerrain(terrainTypes[i]), j));

                // Fill the hex
                g.fill(hexPoly);

                // Draw the line around the hex
                g.setColor(Color.gray);
                g.draw(hexPoly);

                // Draw the elevation number (lower right corner)
                if (prefs.tacShowTerrainElev && h >= 20)
                {
                    // Draw the elevation
                    g.setColor(Color.black);
                    g.setFont(elevFont);

                    if (j != 0)			// We don't draw zero elevation numbers
                    {
                        String		hexElev = Integer.toString(j);
                        int			width;

                        if (j < 0 && Math.abs(j) <= 9)
                            width = 2 * elevWidth[Math.abs(j)];
                        else if (j > 0 && j <= 9)
                            width = elevWidth[j];
                        else
                            width = elevWidth[0];

                        g.drawString(hexElev,
                                     (float) (hexPoly.getX(0) + w - width),
                                     (float) (hexPoly.getY(0) + h - 2));
                    }
                }

                // Draw the terrain type (upper left corner)
                if (prefs.tacShowTerrainChar && h >= 20)
                {
                    if (terrainTypes[i] != '.')			// we don't draw plain types
                    {
                        g.setFont(terrainFont);
                        g.drawString(String.valueOf(terrainTypes[i]), hexPoly.getX(0), hexPoly.getY(0) + h/2);
                    }
                }
                
                hexImages[i][j] = newImage;                
            }
        }
    }

    /**
      * This function sets up the fonts, according to the sizes specified in the preferences. It should be called each time the font size
      * changes.
      */
    private void setupFonts()
    {
        smallFont = new Font("Monospaced", Font.PLAIN, prefs.smallFontSize);
        hexNumberFont = new Font("Monospaced", Font.BOLD, prefs.hexNumberFontSize);
        infoFont = new Font("Monospaced", Font.BOLD, prefs.infoFontSize);
        terrainFont = new Font("Monospaced", Font.PLAIN, 10);		// this changes dynamically anyway. based on size of hex
        elevFont = new Font("Monospaced", Font.PLAIN, prefs.elevationFontSize);

        // need to recalculate font widths
        precalculateNumbers();
    }
    
    /* ---------------------- */

    /**
     * Paints the whole map, in steps.
     */
    public void paint(Graphics gfx)
    {
        // Why bother? We're going to draw over it in a minute anyway...
        //super.paint(gfx);
        // -----------

        getBounds(bounds);
        
        //Graphics2D			g = (Graphics2D) (gfx.create(0, 0, bounds.width, bounds.height));
        Graphics2D			g = (Graphics2D) gfx;
        AffineTransform		oldTrans = g.getTransform();
        AffineTransform		baseTrans = g.getTransform();

        //g.addRenderingHints(rHints);
        
        // Now we go through and draw everything we need. 'Lower' items should be drawn first,
        // ie hexes and terrain
        
        // We use the offsets and the total width and height of each hex to find out how many can fit on our
        // window exactly

        if (prefs.hexHeight != h)
            changeHeight(prefs.hexHeight);
        
        myLocX = data.myUnit.x + (int) prefs.xOffset;
        myLocY = data.myUnit.y + (int) prefs.yOffset;
        
        // First, let's do some initial setup on our tactical map area
        g.setColor(Color.black);
        g.fill(bounds);

        // How many hexes to draw?
        numAcross = (int) ((bounds.width / (w + l)) + 3);
        numDown = (int) ((bounds.height / h) + 3);

        //AffineTransform		offsetXform = new AffineTransform(baseTrans);
        //offsetXform.translate(prefs.xOffset, prefs.yOffset);
        //g.setTransform(offsetXform);
        
        // Paint the terrain
        paintTerrain(g);
        // Paint hex numbers
        if (h >= 20)
            paintNumbers(g);
        // Paint our own unit
        paintUnit(g);
        // Paint other contacts on the map
        paintContacts(g);

        // Finally, draw our status bar at the bottom of the screen
        paintStatusBar(g);
        
        // Reset the transform
        g.setTransform(oldTrans);
    }

    /**
     * Paint our own unit on the map.
     * We should draw at the exact center. paintTerrain() should have translated the hexes underneath us to show the correct location.
     * @param g The graphics context into which we are drawing.
     */
    public void paintUnit(Graphics2D g)
    {
        Point2D	 		unitPt = new Point2D.Float();

        float			unitXOffset = prefs.xOffset * (w + l);
        float			unitYOffset = prefs.yOffset * h;
        
        if (prefs.xOffset % 2 != 0 && data.myUnit.x % 2 == 0)
            unitYOffset -= h/2;
        else if (prefs.xOffset % 2 != 0 && data.myUnit.x % 2 != 0)
            unitYOffset += h/2;

        unitPt.setLocation((float) bounds.width / 2.0f - unitXOffset, (float) bounds.height / 2.0f - unitYOffset);

        // Draw our own unit...
        drawHeading(g, unitPt, data.myUnit.heading, data.myUnit.speed, false, false);
        if (data.myUnit.heading != data.myUnit.desiredHeading)
            drawHeading(g, unitPt, data.myUnit.desiredHeading, data.myUnit.speed, true, false);
        drawIDBox(g, data.myUnit, unitPt, true, false, null);
    }
    
    /**
     * Paint other contacts on the map (not own unit)
     * @param g The graphics context into which we are drawing.
     */
    public void paintContacts(Graphics2D g)
    {
        AffineTransform			oldTrans = g.getTransform();
        AffineTransform			baseTrans; // = g.getTransform();

        TreeSet					conTree;
        
        MUUnitInfo				unit;
        Point2D					conPoint = new Point2D.Float();

        //int						myLocX = data.myUnit.x;
        //int						myLocY = data.myUnit.y;
        int						hexX = myLocX - (numAcross / 2);
        int						hexY = myLocY - (numDown / 2);

        baseTrans = setupStandardTransform(g.getTransform());

        g.setTransform(baseTrans);
    
        synchronized (data.contacts)
        {
            conTree = new TreeSet((data.contacts).values());   
        }
        
        // We could sort these by range, so closer units always stay on top... or something
        // But really, who cares
        for (Iterator it = conTree.iterator(); it.hasNext(); )
        {
            unit = (MUUnitInfo) it.next();

            int			ubtc = unit.bearingToCenter;
            double 		urtc = unit.rangeToCenter;

            int ub = ubtc + 180;
            if (ub > 360)
                ub -= 360;
            ub += 90;

            double 		ufcOffsetX = urtc * (w + 2 * l) * Math.cos(toRadians(ub));
            double 		ufcOffsetY = urtc * h * Math.sin(toRadians(ub));
            
            // Find out where the center of the hex they're in is at
            Point2D	realHex = hexPoly.hexToReal(unit.x, unit.y, true);

            conPoint.setLocation(realHex.getX() + ufcOffsetX,
                                 realHex.getY() + ufcOffsetY);
        
            drawHeading(g, conPoint, unit.heading, unit.speed, false, unit.isDestroyed());
            // Draw box for contact ID
            // last 3 bools: friend, expired, target -- should get from contact data
            drawIDBox(g, unit, conPoint, false, false, null);

        } // end of contact loop
        
        // Reset the transformation
        g.setTransform(oldTrans);
    }

    /**
     * Paint terrain for the hexes on the map. 
     * @param g The graphics context into which we are drawing.
     */
    
    public void paintTerrain(Graphics2D g)
    {
        /*
         
        We should go hex by hex from the top left, check what terrain it is, and draw
        the correct color or pattern or whatever for that hex.

        The style of the map may change based on preferences or something, but for now
        we should draw it in a fashion similar to what we get with the text-based map.
        This means terrain type on top l/r and bottom left and elevation on bottom right.

        We could just figure out the coords for the entire hex.
        Then we can create a Polygon object with base coords, translate, and fill for each hex...

        So, the first hex (top leftmost) is translated 0 in x and 0 in y, of course.
        Other hexes are translated:
        
        xtrans = x * (w + ((h/2) * tan30))

        if y even or 0:
          ytrans = y * h
        if y odd:
          ytrans = h/2 + (y * h)
           
        */
        
        // Adjust for findcenter
        
        AffineTransform			oldTrans = g.getTransform();
        AffineTransform			baseTrans; // = g.getTransform();

        //int						myLocX = data.myUnit.x + (int) prefs.xOffset;
        //int						myLocY = data.myUnit.y + (int) prefs.yOffset;
        int						hexX = myLocX - (numAcross / 2);
        int						hexY = myLocY - (numDown / 2);

        baseTrans = setupStandardTransform(g.getTransform());
        
        // --------------------
        
        AffineTransform			trans = new AffineTransform();
        MUHex					hex;
        
        // Now we go into a loop to draw the proper colors or textures for each terrain
        for (int j = 0; j < numAcross; j++)
        {
            if (hexX + j >= 0)		// Make sure we're drawing within the boundaries of the map
            {
                for (int i = 0; i < numDown; i++)
                {
                    if (data.hudRunning)
                        hex = data.getHex(hexX + j, hexY + i);
                    else
                        hex = new MUHex();
                    
                    if (hexY + i >= 0 && hex.terrain() != '?')	// Make sure we're drawing within the boundaries of the map
                    {
                        // This gives us the x,y location of this hex
                        Point2D	realHex = hexPoly.hexToReal(hexX + j, hexY + i, false);

                        // Set the transform to our previously setup one
                        trans.setTransform(baseTrans);
                        // Translate to where the hex should be located
                        // We have to compensate by -l for x because the picture in the array is shifted over by +l.
                        trans.translate(realHex.getX() - l, realHex.getY());
                        g.setTransform(trans);
                        // And draw it
                        g.drawImage(imageForTerrain(hex.terrain(), hex.absElevation()), null, null);

                        // Set our transform for the rest of the info

                        // Optional stuff -----------------------

                        if (prefs.tacShowHexNumbers)
                        {
                            AffineTransform			beforeNumberRot = g.getTransform();
                            AffineTransform			baseTrans2 = g.getTransform();
                            String					hexString = (hexX + j) + "," + (hexY + i);
                            baseTrans2.rotate(-PI / 2, hexPoly.getX(2), hexPoly.getY(2));
                            g.setColor(new Color((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.25));
                            g.setFont(hexNumberFont);
                            g.setTransform(baseTrans2);
                            g.drawString(hexString, hexPoly.getX(2) + 2,
                                         hexPoly.getY(2) + (hexNumberFont.getLineMetrics(hexString, frc)).getAscent());
                            g.setTransform(beforeNumberRot);
                        }

                        if (prefs.highlightMyHex && (hexX + j == myLocX) && (hexY + i == myLocY))
                        {
                            Stroke		saveStroke = g.getStroke();
                            g.setStroke(new BasicStroke(2.0f));
                            g.setColor(Color.black);

                            g.draw(hexPoly);

                            g.setStroke(saveStroke);
                        }
                        
                        if (prefs.tacShowCliffs)
                        {
                            Stroke		saveStroke = g.getStroke();
                            
                            g.setColor(Color.red);
                            g.setStroke(new BasicStroke(2.0f));		// Make the red line wider
                            
                            // We are at: hexX + j, hexY + i
                            if ((hexX + j) % 2 == 0)
                            {
                                // Even X
                                if (Math.abs(data.getHexElevation(hexX + j + 0, hexY + i - 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(0), (int) hexPoly.getY(0), (int) hexPoly.getX(5), (int) hexPoly.getY(5));
                                if (Math.abs(data.getHexElevation(hexX + j - 1, hexY + i + 0) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(0), (int) hexPoly.getY(0), (int) hexPoly.getX(1), (int) hexPoly.getY(1));
                                if (Math.abs(data.getHexElevation(hexX + j - 1, hexY + i + 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(1), (int) hexPoly.getY(1), (int) hexPoly.getX(2), (int) hexPoly.getY(2));
                                if (Math.abs(data.getHexElevation(hexX + j + 0, hexY + i + 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(2), (int) hexPoly.getY(2), (int) hexPoly.getX(3), (int) hexPoly.getY(3));
                                if (Math.abs(data.getHexElevation(hexX + j + 1, hexY + i + 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(3), (int) hexPoly.getY(3), (int) hexPoly.getX(4), (int) hexPoly.getY(4));
                                if (Math.abs(data.getHexElevation(hexX + j + 1, hexY + i + 0) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(4), (int) hexPoly.getY(4), (int) hexPoly.getX(5), (int) hexPoly.getY(5));
                            }
                            else
                            {
                                // Odd X
                                if (Math.abs(data.getHexElevation(hexX + j + 0, hexY + i - 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(0), (int) hexPoly.getY(0), (int) hexPoly.getX(5), (int) hexPoly.getY(5));
                                if (Math.abs(data.getHexElevation(hexX + j - 1, hexY + i - 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(0), (int) hexPoly.getY(0), (int) hexPoly.getX(1), (int) hexPoly.getY(1));
                                if (Math.abs(data.getHexElevation(hexX + j - 1, hexY + i + 0) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(1), (int) hexPoly.getY(1), (int) hexPoly.getX(2), (int) hexPoly.getY(2));
                                if (Math.abs(data.getHexElevation(hexX + j + 0, hexY + i + 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(2), (int) hexPoly.getY(2), (int) hexPoly.getX(3), (int) hexPoly.getY(3));
                                if (Math.abs(data.getHexElevation(hexX + j + 1, hexY + i + 0) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(3), (int) hexPoly.getY(3), (int) hexPoly.getX(4), (int) hexPoly.getY(4));
                                if (Math.abs(data.getHexElevation(hexX + j + 1, hexY + i - 1) - hex.elevation()) > prefs.cliffDiff)
                                    g.drawLine((int) hexPoly.getX(4), (int) hexPoly.getY(4), (int) hexPoly.getX(5), (int) hexPoly.getY(5));
                            }

                            g.setStroke(saveStroke);				// Restore the old stroke
                        }
                    }
                }                
            }
        }

        // Reset the transformation
        g.setTransform(oldTrans);
    }

    /**
      * Setup the standard transformation for contacts, terrain
      */
    public AffineTransform setupStandardTransform(AffineTransform base)
    {
        AffineTransform			newTrans = new AffineTransform(base);
        
        int						hexX = myLocX - (numAcross / 2);
        int						hexY = myLocY - (numDown / 2);

        int 					btc = data.myUnit.bearingToCenter;
        float 					rtc = data.myUnit.rangeToCenter;

        int b = btc + 180;
        if (b > 360)
            b -= 360;
        b += 90;

        double 					fcOffsetX = rtc * (w + 2 * l) * -Math.cos(toRadians(b));
        double				 	fcOffsetY = rtc * h * -Math.sin(toRadians(b));

        Point2D					unitDraw = hexPoly.hexToReal(myLocX, myLocY, true);

        // Do some translation magic so that our unit is always in the exact center of the screen
        // Basically, we translate the center of our current hex to 0,0
        // Then we translate to compensate for our distance from the center of the hex
        // Then we move the whole thing to the center of the window
        newTrans.translate(-unitDraw.getX() + fcOffsetX + bounds.width/2,
                           -unitDraw.getY() + fcOffsetY + bounds.height/2);

        return newTrans;
    }
    /**
      * Get the proper color to describe a terrain character.
      */
    public Color colorForTerrain(char terrain)
    {
        switch (terrain)
        {
            case '.':							// plain
                return prefs.cPlains;
            case '~':							// water
                return prefs.cWater;
            case '`':							// light forest
                return prefs.cLightForest;
            case '"':							// heavy forest
                return prefs.cHeavyForest;
            case '^':							// mountain
                return prefs.cMountain;
            case '%':							// rough
                return prefs.cRough;
            case '@':							// building
                return prefs.cBuilding;
            case '#':							// road or
            case '/':							// bridge
                return prefs.cRoad;
            case '&':							// fire
                return prefs.cFire;
            case '=':							// wall
                return prefs.cWall;
            case ':':							// smoke
                return prefs.cSmoke;
            case '-':							// ice
                return prefs.cIce;
            case '+':
                return prefs.cSmokeOnWater;
            case '?':
                return prefs.cUnknown;				// unknown
            default:
                return prefs.cUnknown;
      }
    }

    /**
      * Get the Image that we want to copy for a given terrain character.
      */
    
    protected BufferedImage imageForTerrain(char terrain, int elevation)
    {
        if (!prefs.tacDarkenElev)
            elevation = 0;
        
        switch (terrain)
        {
            case '.':							// plain
                return hexImages[PLAIN][elevation];
            case '~':							// water
                return hexImages[WATER][elevation];
            case '`':							// light forest
                return hexImages[LIGHT_FOREST][elevation];
            case '"':							// heavy forest
                return hexImages[HEAVY_FOREST][elevation];
            case '^':							// mountain
                return hexImages[MOUNTAIN][elevation];
            case '%':							// rough
                return hexImages[ROUGH][elevation];
            case '@':							// building
                return hexImages[BUILDING][elevation];
            case '#':							// road or
            case '/':							// bridge
                return hexImages[ROAD][elevation];
            case '&':							// fire
                return hexImages[FIRE][elevation];
            case '=':							// wall
                return hexImages[WALL][elevation];
            case ':':							// smoke
                return hexImages[SMOKE][elevation];
            case '-':							// ice
                return hexImages[ICE][elevation];
            case '+':							// smoke over water
                return hexImages[SMOKE_OVER_WATER][elevation];
            case '?':
                return hexImages[UNKNOWN][elevation];				// unknown
            default:
                return hexImages[UNKNOWN][elevation];
        }
    }

    /**
     * Paint the numbers which tell us which hexes are on the map.
     * @param g The graphics context into which we are drawing.
     * @param h The height of each hex. The width will be set to h/2.
     */
    public void paintNumbers(Graphics2D g)
    {
        AffineTransform			oldTrans = g.getTransform();
        AffineTransform			baseTrans = g.getTransform();
        
        int			startX = 0;
        int			startY = 0;

        //int						myLocX = data.myUnit.x;
        //int						myLocY = data.myUnit.y;
        int						hexX = myLocX - (numAcross / 2);
        int						hexY = myLocY - (numDown / 2);

        int 					btc = data.myUnit.bearingToCenter;
        float 					rtc = data.myUnit.rangeToCenter;

        int b = btc + 180;
        if (b > 360)
            b -= 360;
        b += 90;

        double 					fcOffsetX = rtc * (w + 2 * l) * -Math.cos(toRadians(b));
        double				 	fcOffsetY = rtc * h * -Math.sin(toRadians(b));

        Point2D					unitDraw = hexPoly.hexToReal(myLocX, myLocY, true);

        // Set the proper font
        g.setFont(hexNumberFont);
        
        // Do some translation magic so that our unit is always in the exact center of the screen
        // Basically, we translate the center of our current hex to 0,0
        // Then we translate to compensate for our distance from the center of the hex
        // Then we move the whole thing to the center of the window
        /*
        baseTrans.translate(-unitDraw.getX() + fcOffsetX + bounds.width/2,
                            -unitDraw.getY() + fcOffsetY + bounds.height/2);
         */
        baseTrans.translate(-unitDraw.getX() + fcOffsetX + bounds.width/2, 0);

        AffineTransform		trans = new AffineTransform();

        // Numbers across the top
        for (int i = 1; i < numAcross; i++)
        {
            if (hexX + i >= 0)
            {
                if ((hexX + i) % 2 == 0)
                    g.setColor(Color.gray);
                else
                    g.setColor(Color.darkGray);

                Point2D	realHex = hexPoly.hexToReal(hexX + i, hexY, false);
                trans.setTransform(baseTrans);
                // Translate to where the hex should be located
                // We have to compensate by -l for x because the picture in the array is shifted over by +l.
                // (hexNumberFont.getLineMetrics(Integer.toString(hexX + i), frc)).getHeight()
                
                trans.translate(realHex.getX(), 10);
                g.setTransform(trans);

                g.drawString(Integer.toString(hexX + i), 0, 0);
            }
        }

        // Numbers along the side
        g.setTransform(oldTrans);
        baseTrans = g.getTransform();
        baseTrans.translate(0, -unitDraw.getY() + fcOffsetY + bounds.height/2);
        
        for (int i = 1; i < numDown; i++)
        {
            if (hexY + i >= 0)
            {
                if ((hexY + i) % 2 == 0)
                    g.setColor(Color.gray);
                else
                    g.setColor(Color.darkGray);

                Point2D	realHex = hexPoly.hexToReal(hexX, hexY + i, false);
                /*
                if (hexX + i % 2 == 0)
                    realHex.setLocation(realHex.getX(), realHex.getY() + h/2);
                 */
                
                trans.setTransform(baseTrans);
                // Translate to where the hex should be located
                // We have to compensate by -l for x because the picture in the array is shifted over by +l.
                trans.translate(2, realHex.getY() + 2);
                g.setTransform(trans);

                g.drawString(Integer.toString(hexY + i), 0, 0);
            }
        }

        g.setTransform(oldTrans);
    }

    // ----------------------
    // Helper functions
    // ----------------------

    /**
     * Draw a white box for blanking out terrain or whatever. Then draw ID into it.
     * NOTE: The transformation should be corrected for the translation before calling this.
     * @param g The graphics context into which we are drawing.
     * @param unit The unit we're drawing
     * @param self If true, we're drawing our own unit
     * @param debug If true, draw some debug info specified in next arg
     * @param debugString If debug is true, draw this string in the id box
     */
    public void drawIDBox(Graphics2D g, MUUnitInfo unit, Point2D pt, boolean self, boolean debug, String debugString)
    {

        try
        {
        
            AffineTransform		oldTrans = g.getTransform();
            Stroke				oldStroke = g.getStroke();
            int					borderWidth = 4;
            int					borderHeight = 4;
            int					spacing = 5;		// Distance between icon and name

            // Setup this transform

            AffineTransform		contactMove = new AffineTransform(oldTrans);
            contactMove.translate(pt.getX() - h/4, pt.getY() - h/4);
            g.setTransform(contactMove);

            // Draw icon
            if (unit.isDestroyed())
                g.setColor(Color.lightGray);		// destroyed
            else if (unit.isFriend())
                g.setColor(Color.black);			// or green
            else
                g.setColor(Color.red);			// or yellow

            Rectangle			iconBounds = new Rectangle(0, 0, h/2, h/2);

            if (self)
                g.setStroke(new BasicStroke(2));

            g.draw(unit.icon(h / 2, false, unit.isFriend() ? Color.black : Color.red));

            if (self)
                g.setStroke(oldStroke);
            if (!self || (self && debug))		// note, turning on debug output for own unit disables weapon arcs
            {
                // Draw text box
                Rectangle			backingBox = null;
                String				textString;
                if (prefs.tacShowUnitNames)
                    textString = "[" + unit.id + "] " + unit.name;
                else
                    textString = "[" + unit.id + "]";

                if (debug)
                    textString = textString + " / " + debugString;

                Rectangle2D 		stringRect = infoFont.getStringBounds(textString, frc);
    
                backingBox = new Rectangle((int) (iconBounds.width / 2 + spacing + borderWidth / 2),
                                        (int) (-stringRect.getHeight() / 2 - borderHeight / 2),
                                        (int) (stringRect.getWidth() + borderWidth),
                                        (int) (stringRect.getHeight() + borderHeight));
                
                
                if (!unit.isDestroyed())
                {
                    if (unit.isFriend())
                        g.setColor(new Color((float) 1.0, (float) 1.0, (float) 1.0, (float) 0.5));
                    else
                        g.setColor(new Color((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.5));

                    g.fill(backingBox);
                    g.setColor(Color.black);
                    g.draw(backingBox);
                }
                else
                {
                    g.setColor(Color.lightGray);
                    g.draw(backingBox);                    
                }
                        
                // Draw contact's ID string
                if (unit.isOld() || unit.isDestroyed())
                    g.setColor(Color.darkGray);
                else if (unit.isFriend())
                    g.setColor(Color.black);
                else if (unit.isTarget())
                    g.setColor(Color.red);
                else
                    g.setColor(Color.yellow);
                
                g.setFont(infoFont);
                g.drawString(textString,
                            (float) (iconBounds.width / 2 + spacing + borderWidth),
                            (float) (stringRect.getHeight() / 2 + ((infoFont.getLineMetrics(textString, frc)).getDescent()) - 2) - borderHeight);
    
                // If it's destroyed, draw a red X through the box
                if (unit.isDestroyed())
                {
                    g.setColor(new Color(255, 0, 0, 80));
                    g.drawLine(backingBox.x, backingBox.y, backingBox.x + backingBox.width, backingBox.y + backingBox.height);
                    g.drawLine(backingBox.x, backingBox.y + backingBox.height, backingBox.x + backingBox.width, backingBox.y);
                }

            }
            else
            {
                if (prefs.tacShowArcs)
                {
                    AffineTransform arcXform = new AffineTransform(oldTrans);
                    double			headingRad;

                    // If we have a turret, we don't add in the turret heading here
                    headingRad = ((double) (data.myUnit.heading + (data.myUnit.canHaveTurret() ? 0 : data.myUnit.turretHeading)) / 180) * PI + PI;

                    arcXform.rotate(headingRad, pt.getX(), pt.getY());
                    arcXform.translate(pt.getX(), pt.getY());
                    g.setTransform(arcXform);

                    // This is our own unit. Let's draw the helper 'arcs'
                    float	r = prefs.arcIndicatorRange * h;			// r = radius

                    Arc2D	frontArc = new Arc2D.Double();
                    Arc2D	leftArc = new Arc2D.Double();
                    Arc2D	rightArc = new Arc2D.Double();
                    Arc2D	rearArc = new Arc2D.Double();

                    frontArc.setArcByCenter(0, 0, prefs.makeArcsWeaponRange ? data.myUnit.maxFrontRange() * h : r, -30.0, -120.0, Arc2D.PIE);
                    g.setColor(new Color(0, 0, 255, 30));			// faded blue
                    //g.draw(frontArc);
                    g.fill(frontArc);

                    leftArc.setArcByCenter(0, 0, prefs.makeArcsWeaponRange ? data.myUnit.maxLeftRange() * h : r, -30.0, 60.0, Arc2D.PIE);
                    rightArc.setArcByCenter(0, 0, prefs.makeArcsWeaponRange ? data.myUnit.maxRightRange() * h : r, -150, -60.0, Arc2D.PIE);
                    g.setColor(new Color(255, 255, 0, 30));			// faded yellow
                    //g.draw(leftArc);
                    //g.draw(rightArc);
                    g.fill(leftArc);
                    g.fill(rightArc);

                    rearArc.setArcByCenter(0, 0, prefs.makeArcsWeaponRange ? data.myUnit.maxRearRange() * h : r, 30.0, 120.0, Arc2D.PIE);
                    g.setColor(new Color(255, 0, 0, 30));			// faded red
                    //g.draw(rearArc);
                    g.fill(rearArc);

                    if (data.myUnit.canHaveTurret())
                    {
                        Arc2D	turretArc = new Arc2D.Double();
                        double	turretRad = ((double) (data.myUnit.heading + data.myUnit.turretHeading) / 180) * PI;

                        arcXform = new AffineTransform(oldTrans);
                        arcXform.rotate(turretRad, pt.getX(), pt.getY());
                        arcXform.translate(pt.getX(), pt.getY());
                        g.setTransform(arcXform);
                        
                        turretArc.setArcByCenter(0, 0, prefs.makeArcsWeaponRange ? data.myUnit.maxTurretRange() * h : r, -30.0, -120.0, Arc2D.PIE);
                        g.setColor(new Color(0, 255, 0, 30));			// faded green
                        g.fill(turretArc);
                    }

                }
            }
            g.setTransform(oldTrans);

        }
        catch (Exception e)
        {
            System.out.println("Error: drawIDBox: " + e);
        }
    }

    /**
     * Draw a heading indicator line.
     * NOTE: The transformation should be set before entering this function.
     * We will concatenate the transform with our own. It needs to be translated for the numbers
     * along the side and top.
     * @param g The graphics context into which we are drawing
     * @param pt The point at which we are drawing
     * @param head The heading in degrees
     * @param speed The speed in kph
     * @param h The height of the hex (for scaling purposes)
     * @param desired True if this line represents a desired heading
     * @param destroyed True if this unit is destroyed
     */
    
    public void drawHeading(Graphics2D g, Point2D pt, int heading, double speed, boolean desired, boolean destroyed)
    {
        AffineTransform		trans = new AffineTransform();
        AffineTransform		oldTrans = g.getTransform();
        double				speedDivisor = 32.25;			// 32.25 = 3 MP, 10.75 = 1 MP
        int					lineLength = (int) (h * ((speed == 0 ? speedDivisor : speed) / speedDivisor));
        double				headingRad = ((float) heading / 180) * PI + PI;
        
        // Set up our transformation
        trans.concatenate(g.getTransform());
        trans.rotate(headingRad, pt.getX(), pt.getY());
        trans.translate(pt.getX(), pt.getY());

        // Set the transform        
        g.setTransform(trans);
        
        // And finally, draw it
        if (destroyed)
            g.setColor(Color.lightGray);
        else if (desired)
            g.setColor(Color.blue);
        else
            g.setColor(Color.black);

        g.drawLine(0, 0, 0, lineLength);
        g.setColor(Color.white);
        g.drawLine(1, 0, 1, lineLength);
        g.drawLine(-1, 0, -1, lineLength);
        
        // Reset the transformation
        g.setTransform(oldTrans);
    }

    /**
     * Draws the status bar at the bottom of the tactical display window.
     * @param g The graphics context.
     */
    public void paintStatusBar(Graphics2D g)
    {
        // Need to figure out a better way to specify what's in the status bar and how to space it without relying on hardcoded magic numbers
        // Perhaps keep a counter of where the next 'item' should be placed, and add to it the length of whatever we just drew plus some spacing
        
        AffineTransform			oldTrans = g.getTransform();
        AffineTransform			baseTrans = g.getTransform();
        Rectangle				barRect = new Rectangle(0, bounds.height - barHeight, bounds.width, barHeight);
        String					tempString;
        Rectangle2D				tempRect;
        
        // We assume that barRect.x = 0 for this
        int						nextStartsAt = 10;
        int						spacingDiff = 15;

        g.setColor(Color.black);
        g.fill(barRect);
        g.setColor(Color.lightGray);
        g.drawLine(barRect.x, barRect.y, barRect.width, barRect.y);

        // Zoom buttons
        g.setFont(smallFont);
        g.setColor(Color.white);
        g.drawString("-", nextStartsAt, barRect.y + 12);
        nextStartsAt += spacingDiff;
        g.drawString("+", nextStartsAt, barRect.y + 12);
        nextStartsAt += spacingDiff;
        
        // Sep
        g.setColor(Color.darkGray);
        g.drawLine(nextStartsAt, barRect.y + 1, nextStartsAt, barRect.y + barHeight);
        g.setColor(Color.black);
        nextStartsAt += spacingDiff;
        
        // Heat indicator
        int			heatLength = data.myUnit.heat / 10;
        if (heatLength > 50)
            heatLength = 50;
        Rectangle	heatRect = new Rectangle(nextStartsAt, barRect.y + 4, heatLength, 8);
        Rectangle	totalHeatRect = new Rectangle(nextStartsAt, barRect.y + 4, heatBarMaxLength, 8);
        
        if (data.myUnit.heat - data.myUnit.heatDissipation >= 200)
            g.setColor(Color.red);
        else if (data.myUnit.heat - data.myUnit.heatDissipation >= 140)
            g.setColor(Color.yellow);
        else
            g.setColor(Color.green);
        g.fill(heatRect);

        g.setColor(Color.white);
        g.draw(totalHeatRect);
        nextStartsAt += heatBarMaxLength + spacingDiff;
        
        // Sep
        g.setColor(Color.darkGray);
        g.drawLine(nextStartsAt, barRect.y + 1, nextStartsAt, barRect.y + barHeight);
        g.setColor(Color.black);
        nextStartsAt += spacingDiff;
        
        // Unit information
        tempString = "[" + data.myUnit.id + "] " + data.myUnit.name + " (" + data.myUnit.ref + ")" + " S:" + data.myUnit.status;
        tempRect = smallFont.getStringBounds(tempString, frc);

        g.setFont(smallFont);
        g.setColor(Color.white);
        g.drawString(tempString, nextStartsAt, barRect.y + 11);
        nextStartsAt += tempRect.getWidth() + spacingDiff;

        // Sep
        g.setColor(Color.darkGray);
        g.drawLine(nextStartsAt, barRect.y + 1, nextStartsAt, barRect.y + barHeight);
        g.setColor(Color.black);
        nextStartsAt += spacingDiff;

        // Armor status
        float		armorLeft = data.myUnit.percentArmorLeft();
        g.setColor(data.myUnit.colorForPercent(armorLeft));
        tempString = armorLeft + "% / ";
        tempRect = smallFont.getStringBounds(tempString, frc);
        g.setFont(smallFont);
        g.drawString(tempString, nextStartsAt, barRect.y + 11);
        nextStartsAt += tempRect.getWidth();	// Don't put in spacing here, the space is there in the string

        // Internal status
        float		internalLeft = data.myUnit.percentInternalLeft();
        g.setColor(data.myUnit.colorForPercent(internalLeft));
        tempString = internalLeft + "%";
        tempRect = smallFont.getStringBounds(tempString, frc);
        g.setFont(smallFont);
        g.drawString(tempString, nextStartsAt, barRect.y + 11);
        nextStartsAt += tempRect.getWidth() + spacingDiff;
        
        // Reset transform
        g.setTransform(oldTrans);
    }

    static public double toRadians(double a)
    {
        return (a / 180.0d) * Math.PI + Math.PI;
    }

    public Color colorForElevation(Color ic, int e)
    {
        float[] 	comp = ic.getRGBColorComponents(null);
        float		mod = prefs.elevationColorMultiplier * e;
        float[]		newComp = {comp[0], comp[1], comp[2]};

        for (int i = 0; i < 3; i++)
        {
            newComp[i] -= mod;
            if (newComp[i] < 0.0f)
                newComp[i] = 0.0f;
        }

        return new Color(newComp[0], newComp[1], newComp[2]);   
    }
}
