//
//  HexShape.java
//  Thud 
//
//  Created by Anthony Parker on Wed Mar 27 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.MUConstants;

import java.awt.*;
import java.awt.geom.*;

public class HexShape implements Shape {

    float		x[] = {0f, 0f, 0f, 0f, 0f, 0f};
    float		y[] = {0f, 0f, 0f, 0f, 0f, 0f};

    float					h = 40;
    float					w;
    float					l;
    
    GeneralPath	gp;

    public HexShape(float h)
    {
        this.h = h;

        w = h * 2f * MUConstants.ALPHA;
        l = h *      MUConstants.ALPHA;

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

     *  it lies completely inside theShape boundary or
     *  it lies exactly on the Shape boundary and the space immediately adjacent to the point in the increasing X direction is entirely inside the boundary or
     *  it lies exactly on a horizontal boundary segment and the space immediately adjacent to the point in the increasing Y direction is inside the boundary.


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
