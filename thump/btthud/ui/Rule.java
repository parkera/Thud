//
//  Rule.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 11 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.ui;

import java.awt.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

import javax.swing.text.*;

public class Rule extends JComponent {
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public int orientation;

    int 					h;		// Hex height
    int 					max;	// Max number we'll see
    int						size;	// size of this component

    Rectangle2D				stringRect;

    Font					hexNumberFont;
    FontRenderContext		frc;

    int						preferredHeight = 0;
    int						preferredWidth = 0;

    RenderingHints			rHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    
    // ----------------------------------------
    
    public Rule(int orientation, int h, int max, int fontSize) {
        this.orientation = orientation;
        this.h = h;
        this.max = max;

        rHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        hexNumberFont = new Font("Monospaced", Font.BOLD, fontSize);
        frc = new FontRenderContext(new AffineTransform(), false, false);
        stringRect = hexNumberFont.getStringBounds(Integer.toString(max), frc);
        size = (int) stringRect.getHeight();
    }

    public void setPreferredHeight(int ph)
    {
        setPreferredSize(new Dimension(size, ph));
        preferredHeight = ph;
    }

    public void setPreferredWidth(int pw)
    {
        setPreferredSize(new Dimension(pw, size));
        preferredWidth = pw;
    }

    public void paintComponent(Graphics gfx)
    {
        Graphics2D			g = (Graphics2D) gfx;
        
        Rectangle 			bounds = g.getClipBounds();
        int					skip;
        AffineTransform		oldTrans = g.getTransform();
        AffineTransform		trans = new AffineTransform(oldTrans);
        HexShape			hexPoly = new HexShape(h);
        Point2D				realHex = new Point2D.Double();

        g.addRenderingHints(rHints);
        
        // Clear out the junk
        g.setColor(Color.black);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Draw the line
        /*
        g.setColor(Color.lightGray);
        if (orientation == VERTICAL)
            g.drawLine(size-1, 0, size-1, preferredHeight);
        else
            g.drawLine(0, size-1, preferredWidth, size-1);
         */

        // Set the proper font
        g.setFont(hexNumberFont);

        // Figure out which numbers to draw
        if (h <= 5)
            skip = 5;
        else if (h <= 10)
            skip = 3;
        else if (h <= 20)
            skip = 2;
        else
            skip = 1;

        if (orientation == VERTICAL)
        {
            for (int i = 0; i < max; i+= skip)
            {
                if (i % 2 == 0)
                    g.setColor(Color.white);
                else
                    g.setColor(Color.lightGray);

                trans.setTransform(oldTrans);
                hexPoly.hexToReal(0, i, false, realHex);
                trans.translate(3, realHex.getY());
                trans.rotate(Math.PI / 2);
                g.setTransform(trans);
                g.drawString(Integer.toString(i), 0, 0);
            }
        }
        else
        {
            for (int i = 0; i < max; i+= skip)
            {
                trans.setTransform(oldTrans);

                if (i % 2 == 0)
                    g.setColor(Color.white);
                else
                    g.setColor(Color.lightGray);

                if (i % 2 == 0)
                    trans.translate(0, h/2);

                hexPoly.hexToReal(i, 0, false, realHex);
                trans.translate(realHex.getX(), -realHex.getY() + stringRect.getHeight() - 2);
                g.setTransform(trans);
                g.drawString(Integer.toString(i), 0, 0);
            }
        }

        g.setTransform(oldTrans);
    }
}