//
//  HexShape.java
//  Thud 
//
//  Created by Anthony Parker on Wed Mar 27 2002.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

public class HexShape implements Shape {

    float		x[] = {0f, 0f, 0f, 0f, 0f, 0f};
    float		y[] = {0f, 0f, 0f, 0f, 0f, 0f};

    static final float		tan60 = (float) Math.tan(MUXMapComponent.toRadians(60.0f));
    static final float		sin60 = (float) Math.sin(MUXMapComponent.toRadians(60.0f));

    float					h = 40;
    float					w;
    float					l;
    
    GeneralPath	gp;

    public HexShape(float h)
    {
        this.h = h;

        w = -h / (2 * sin60);
        l = h / (2 * tan60);

        gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, 6);

        // Figure out the proper coordinates
        // top left
        x[0] = l;
        y[0] = 0f;
        
        // leftmost side
        x[1] = 0f;
        y[1] = h / 2f;

        // bottom left
        x[2] = l;
        y[2] = h;

        // bottom right
        x[3] = w + l;
        y[3] = h;

        // rightmost side
        x[4] = w + (2f * l);
        y[4] = h / 2f;

        // top right
        x[5] = w + l;
        y[5] = 0f;

        gp.moveTo(x[0], y[0]);
        for (int i = 1; i < 6; i++)
            gp.lineTo(x[i], y[i]);
        gp.lineTo(x[0], y[0]);
    }

    public double getX(int i)
    {
        if (i < 0 || i > 5)
            return 0;
        else
            return x[i];
    }

    public double getY(int i)
    {
        if (i < 0 || i > 5)
            return 0;
        else
            return y[i];
    }
    
    // ------------------

    /*
     Definition of insideness: A point is considered to lie inside a Shape if and only if:

     ¥  it lies completely inside theShape boundary or
     ¥  it lies exactly on the Shape boundary and the space immediately adjacent to the point in the increasing X direction is entirely inside the boundary or
     ¥  it lies exactly on a horizontal boundary segment and the space immediately adjacent to the point in the increasing Y direction is inside the boundary.


     */
    
    public boolean contains(double x, double y)
    {
        return (gp.contains(x, y));
    }

    public boolean contains(double x, double y, double w, double h)
    {
        return (gp.contains(x, y, w, h));
    }

    public boolean contains(Rectangle2D r)
    {
        return (gp.contains(r));
    }

    public boolean contains(Point2D p)
    {
        return (gp.contains(p));
    }

    // ---------------
    
    public Rectangle getBounds()
    {
        return (gp.getBounds());
    }

    public Rectangle2D getBounds2D()
    {
        return (gp.getBounds2D());
    }

    /**
     * Gives us the real coordinates (appropriate for drawing in a window) of a specified hex at a certain height.
     * If the center flag is true, then it will give us the exact center of the hex. Otherwise, it returns the upper-left corner.
     *  -> __
     *    /  \   upper left corner of hex
     *    \__/
     *
     *     __
     *    /..\   .. = w (width of hex at narrowest point)
     *    \__/
     *
     *     _____
     *    /|   |\    _ = l (2*l + w = width of hex at widest point)
     *   /_|   |_\
     *   \ |   | /
     *    \|___|/
     *
     *     __
     *    /+ \	+
     *    \+_/	+ = h (height of hex at tallest point)
     *
     * @param x The x coordinate, in hexes.
     * @param y The y coordinate, in hexes.
     * @param h The height of each hex
     * @param center True if we want the center of the hex, false if we want the upper-left corner.
     */
    public Point2D hexToReal(int x, int y, boolean center)
    {
        float		xoffset, yoffset;

        xoffset = l + ((float)x * (w + l));			// initial offset of l, then add (w + l) * desired_x_coord...
        yoffset = ((float)y * h);

        if (x % 2 == 0)
            yoffset += (h / 2f);
        
        if (center)
        {
            xoffset += w / 2f;
            yoffset += h / 2f;
        }

        return (new Point2D.Float(xoffset, yoffset));
    }

    // For saving memory - use this method and pass in a Point instead of creating a new one
    public void hexToReal(int x, int y, boolean center, Point2D pt)
    {
        float		xoffset, yoffset;

        xoffset = l + ((float)x * (w + l));			// initial offset of l, then add (w + l) * desired_x_coord...
        yoffset = ((float)y * h);

        if (x % 2 == 0)
            yoffset += (h / 2f);

        if (center)
        {
            xoffset += w / 2f;
            yoffset += h / 2f;
        }

        pt.setLocation(xoffset, yoffset);
    }

    /*

     This picture represents the smallest repeatable area in the hex map.

      ________________________
     |        \              /|
     |         \    III     / |
     |          \          /IV|
     |           \________/   |
     |   I       /        \   |
     |          /   II     \  |
     |         /            \ |
     |________/______________\|
     
     Sizes:
     I (at top, bottom): w
     I (at middle): w + l
     II (at top): w
     III (at bottom): w + l + l
     III (at top): w + l + l
     III (at bottom): w
     IV (at top, bottom): 0
     IV (at middle): l

     First, we take the point the mouse hit and see what 'repeatable box' it's in.
     The whole box is 'h' high and 'w + l + w + l' wide.

     We then figure out the point within this box that the mouse hit.
     
     Next, we split the box into sections for comparison:
     
      _______1___2_______3____
     |       1\  2       3   /|
     |       1 \ 2       3  / |
     |       1  \2       3 /  |
     4444444444444444444444444|
     |       1   2       3\   |
     |       1  /2       3 \  |
     |       1 / 2       3  \ |
     |_______1/__2_______3___\|

     This allows us to make quick checks to see which general section a coordinate is in.

     Now we determine which hex they clicked in based on which section of the box they clicked.
     Note: We multiply boxX by 2 because each box contains 2 'x' columns.
     I: (boxX*2, boxY)
     II: (boxX*2 + 1, boxY + 1)
     III: (boxX*2 + 1, boxY)
     IV: (boxX*2 + 2, boxY)     
     
     */

    // Get the hex coordinate from a real coordinate
    // This code is based (loosely) on the code from mech.util.c in the 3030MUX source
    public Point realToHex(int mX, int mY)
    {
        int			boxX, boxY;
        double		x, y;
        int			section = 0;
        double		beta = (h / 2.0d) / l;

        // So we start out with the first repeatable box at the proper location
        double		rX = mX - l;
        double		rY = mY - h/2;

        // ******* Need to handle special cases: rX < l (before adjustment), rY < h/2
        
        // Figure out the x and y-coordinates of the 'repeatable box' we're in
        boxX = (int) Math.floor(rX / (double) (l*2 + w*2));
        boxY = (int) Math.floor(rY / (double) h);

        // And the offsets inside the box, from the left edge
        x = rX - (boxX * (l*2 + w*2));
        y = rY - (boxY * h);

        // Start checking the coordinates
        if (x < w)
        {
            // L1
            section = 1;
        }
        else if (x > (w + l) && x < (2*w + l))
        {
            // x > L2 && x < L3
            if (y < h/2)
                section = 3;
            else
                section = 2;
        }
        else if (x >= w && x <= (w + l))
        {
            // between the for-sure I and the for-sure II or III
            if (y < h/2)
            {
                if (y >= beta * (x - w))
                    section = 1;
                else
                    section = 3;
            }
            else
            {
                if (y - h/2 >= -beta * (x - w) + h/2)
                    section = 2;
                else
                    section = 1;
            }
        }
        else
        {
            // II, III, or IV
            if (y < h/2)
            {
                if (y >= -beta * (x - (2*w + l)) + h/2)
                    section = 4;
                else
                    section = 3;
            }
            else
            {
                if (y - h/2 >= beta * (x - (2*w + l)))
                    section = 2;
                else
                    section = 4;
            }
        }

        // Return the proper data

        if (section == 1)
            return new Point(boxX * 2, boxY);
        else if (section == 2)
            return new Point(boxX * 2 + 1, boxY + 1);
        else if (section == 3)
            return new Point(boxX * 2 + 1, boxY);
        else if (section == 4)
            return new Point(boxX * 2 + 2, boxY);
        else		// Bad hex, maybe
            return new Point(-1, -1);
    }
            
    // ----------------

    public PathIterator getPathIterator(AffineTransform at)
    {
        return (gp.getPathIterator(at));
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness)
    {
        return (gp.getPathIterator(at, flatness));
    }

    // ----------------

    public boolean intersects(double x, double y, double w, double h)
    {
        return (gp.intersects(x, y, w, h));
    }

    public boolean intersects(Rectangle2D r)
    {
        return (gp.intersects(r));
    }
}
