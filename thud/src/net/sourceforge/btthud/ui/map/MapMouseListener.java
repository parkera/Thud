//
//  MapMouseListener.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui.map;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Point;
import java.awt.geom.Point2D;

import net.sourceforge.btthud.engine.commands.UserCommand;
import net.sourceforge.btthud.ui.Thud;
import net.sourceforge.btthud.data.*;

class MapMouseListener extends MouseAdapter {

    private final 		MUMapComponent map;
    private boolean		bMapIsPainting = false;
    
    MapMouseListener (final MUMapComponent map) {
        this.map = map;
    }

    public void doPaint ()
    {
        bMapIsPainting = true;
        map.repaint();
        bMapIsPainting = false;
    }
    
    /**
     * Temporary mouse listener for supporting mouse events on the tactical
     * map.  The final code will put the active tactical map area in its own
     * component area, but for now, we're going to leave the edge bar areas
     * active.
     *
     * Future expansion ideas: click unit ID to bring up scan, lock, other info
     */
    public void mouseClicked (MouseEvent e) {
    	if (bMapIsPainting || map.isXPainting ())
    		return;
    	
        // FIXME: This is old status bar clicking stuff we're getting rid of,
        // but retain for now.
        if (e.getY() > map.bounds.height - map.barHeight) {
            if (e.getX() > 10 && e.getX() < 20) {
                // '-' was pressed.
                if (e.isControlDown())
                    map.prefs.hexHeight -= 10; // double zoom factor
                else
                    map.prefs.hexHeight -= 5;

                doPaint ();
                
                return;
            } else if (e.getX() > 25 && e.getX() < 35) {
                // '+' was pressed.
                if (e.isControlDown())
                    map.prefs.hexHeight += 10; // double zoom factor
                else
                    map.prefs.hexHeight += 5;

                doPaint ();
                
                return;
            }
        }

        map.mapActions.fireMouseEvent (map.myThud, map, e);
        
        if (false)
        {
        //
        // OK, time for the real stuff.  For now, this is just a demonstration
        // of how to get the various kinds of coordinates we might be
        // interested in computing.
        //

        // Compute event's pixel coordinates relative to our unit.
        //
        // Useful for code needing to compute directions relative to our unit,
        // and distances which aren't directly connected to map distances.
        final Point offsetPt = new Point ();

        map.getScreenToOffset(offsetPt, e.getX(), e.getY());

        // Compute map coordinates (hex height normalized to 1, odd hexes start
        // at y of -0.5f).
        //
        // Useful for code needing to reason about actual game map coordinates,
        // such as code to compute map distances.
        //
        // Note that it's technically safe to pass the same source and
        // destination to this method.
        final Point2D.Float mapPt = new Point2D.Float ();

        map.getOffsetToMap(mapPt, offsetPt);

        // Compute hex from map coordinate.  We can do this directly using
        // MUPoint, but this convenience method avoids allocating new MUPoints
        // every time we need to make this calculation.  We may eventually just
        // move the computations into a static method of MUPoint.
        //
        // Useful for when you need to know what hex you clicked on.
        final Point hexPt = new Point ();

        map.getMapToHex(hexPt, mapPt);

        // OK, spew all our data for the developer's edification.
        System.out.println("MOUSE EVENT");
        System.out.println("\tComponent: (" + e.getX() + ", " + e.getY() + ")");
        System.out.println("\tCenter-offset: (" + offsetPt.x + ", " + offsetPt.y + ")");
        System.out.println("\tMap: (" + mapPt.getX() + ", " + mapPt.getY() + ")");
        System.out.println("\tHex: (" + hexPt.x + ", " + hexPt.y + ")");
        
        if (e.getButton() == MouseEvent.BUTTON1)
        {
        	// check for modifiers
        	
        	int		modifiers = e.getModifiersEx ();
        	
        	System.out.println ("MO:Checking Modifiers");
        	if ((modifiers & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK)
        		System.out.println ("MO:SHIFT IS DOWN");
        	else
        		System.out.println ("MO:SHIFT IS UP");

        	if ((modifiers & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK)
        		System.out.println ("MO:CTRL IS DOWN");
        	else
        		System.out.println ("MO:CTRL IS UP");

        	if ((modifiers & MouseEvent.ALT_DOWN_MASK) == MouseEvent.ALT_DOWN_MASK)
        		System.out.println ("MO:ALT IS DOWN");
        	else
        		System.out.println ("MO:ALT IS UP");
        	
	        double theta = Math.atan2((mapPt.getY () - (double)map.data.myUnit.position.getFY ()), (mapPt.getX () - (double)map.data.myUnit.position.getFX ()));
	        double thetaDegrees = Math.toDegrees (theta);
	        
	        // now convert the degrees to map degrees
	        
	        double mapDegrees = thetaDegrees + 90.0;
	        
	        System.out.println ("\tMyUnit X:" + new Float (map.data.myUnit.position.getCenterFX ()).toString () + ": Y:" + new Float (map.data.myUnit.position.getCenterFY ()).toString () + ": Theta :" + new Double (thetaDegrees).toString () + ":");
	        System.out.println ("\tMap Degrees from unit :" + new Double(mapDegrees).toString () + ":");
	        
			try {
				map.myThud.getConn().sendCommand(new UserCommand (".h " + new Double (mapDegrees).toString ()));
			} catch (Exception e1) {
				// TODO: Seems like it'd be more friendly to report
				// these errors in the main window, or in a modal
				// dialog.  Hiding things in the console is so like
				// 1990.
				System.err.println("Can't send: " + e1);
			}
        }
        else if (e.getButton () == MouseEvent.BUTTON2)
        {
        	System.out.println ("Review Contacts:");
        	
        	int i, iSize;
        	MUUnitInfo	uiUnit;
        	double		uX;
        	double		uY;
        	double		xX;
        	double		xY;
        	double		dX;
        	double		dY;
        	
        	double		dDist;
        	MUUnitInfo	selected = null;
        	double		selectedDist = 1000000.0;
        	
        	uX = (double)mapPt.getX ();
        	uY = (double)mapPt.getY ();;
        	
        	iSize = 0;
        	if (map.data.contacts.size () > 0)
        		iSize = map.data.contacts.size ();
        	
        	for (i = 0; i < iSize; ++i)
        	{
        		uiUnit = map.data.contacts.get(i);
        		uiUnit.target = false;
        		
        		xX = (double)uiUnit.getX ();
        		xY = (double)uiUnit.getY ();
        		
        		dX = uX - xX;
        		dY = uY - xY;
        		
        		dDist = dX * dX + dY * dY;
        		
        		if (dDist < selectedDist)
        		{
        			selectedDist = dDist;
        			selected = uiUnit;
        		}
        		
        		System.out.println ("uiUnit [" + new Integer(i).toString () + "] ID :" + uiUnit.id + ": Friend " +
        		    new Boolean (uiUnit.isFriend ()).toString () + " Target " +
        		    new Boolean (uiUnit.isTarget ()).toString ());
        		System.out.println ("U (" + new Double (uX).toString () + "," + new Double (uY).toString () + ")");
        		System.out.println ("X (" + new Double (xX).toString () + "," + new Double (xY).toString () + ")");
        		System.out.println ("Dist " + new Double (dDist).toString ());
        	}
        	
        	if (selected != null)
        	{
				try {
					map.myThud.getConn().sendCommand(new UserCommand ("lock " + selected.id));
				} catch (Exception e1) {
					// TODO: Seems like it'd be more friendly to report
					// these errors in the main window, or in a modal
					// dialog.  Hiding things in the console is so like
					// 1990.
					System.err.println("Can't send: " + e1);
				}
        	}
        	
            doPaint ();
        }
        }
    }
    
    public void mouseWheelMoved(MouseWheelEvent e)
    {
    	System.out.println ("MouseWheelMoved: 001");
    	
    	if (bMapIsPainting || map.isXPainting ())
    		return;
    	
        map.mapActions.fireMouseWheelEvent (map.myThud, map, e);

        if (false)
        {
    	if (e.getWheelRotation () < 0)
    	{
            if (e.isControlDown())
                map.prefs.hexHeight += 10; // double zoom factor
            else
                map.prefs.hexHeight += 5;

            doPaint ();
            
            return;
    	}
    	else
    	{
            if (e.isControlDown())
                map.prefs.hexHeight -= 10; // double zoom factor
            else
                map.prefs.hexHeight -= 5;

            doPaint ();
            
            return;
    	}
        }
    }
}
