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

    float		h;
    float		tan30 = (float) Math.tan(MUMapComponent.toRadians(30.0f));
    float		w;
    float		l;

    GeneralPath	gp;

    public HexShape(float h)
    {
        this.h = h;
        w = h / 2f;
        l = h / 2f * tan30;

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
        //return (new Rectangle(0, 0, (int) (x[4] + 1), (int) (y[3] + 1)));
    }

    public Rectangle2D getBounds2D()
    {
        return (gp.getBounds2D());
        //return (new Rectangle2D.Float(0f, 0f, x[4], y[3]));
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
