//
//  MapMouseListener.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui.map;

import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.geom.Point2D;

class MapMouseListener extends MouseAdapter {

    private final MUMapComponent map;

    MapMouseListener (final MUMapComponent map) {
        this.map = map;
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
        // FIXME: This is old status bar clicking stuff we're getting rid of,
        // but retain for now.
        if (e.getY() > map.bounds.height - map.barHeight) {
            if (e.getX() > 10 && e.getX() < 20) {
                // '-' was pressed.
                if (e.isControlDown())
                    map.prefs.hexHeight -= 10; // double zoom factor
                else
                    map.prefs.hexHeight -= 5;

                map.repaint();
            } else if (e.getX() > 25 && e.getX() < 35) {
                // '+' was pressed.
                if (e.isControlDown())
                    map.prefs.hexHeight += 10; // double zoom factor
                else
                    map.prefs.hexHeight += 5;

                map.repaint();
            }
        }

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
    }
}
