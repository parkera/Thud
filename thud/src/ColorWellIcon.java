//
//  ColorWellIcon.java
//  Thud
//
//  Created by Anthony Parker on Mon Apr 08 2002.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//

// This class is for drawing the buttons with colored icons inside of them, indicating a currently selected color

import java.awt.*;
import javax.swing.*;

public class ColorWellIcon implements Icon {

    int			w;
    int			h;
    Color		c;
    
    public ColorWellIcon()
    {
        w = 20;
        h = 10;
        c = Color.black;
    }

    public ColorWellIcon(Color c, int w, int h)
    {
        this.c = c;
        this.w = w;
        this.h = h;
    }

    public ColorWellIcon(Color c)
    {
        this.c = c;
        this.w = 20;
        this.h = 10;
    }

    // -------------

    public int getIconHeight()
    {
        return h;
    }

    public int getIconWidth()
    {
        return w;
    }

    public void paintIcon(Component com, Graphics g, int x, int y)
    {
        Color		oldColor = g.getColor();
        g.setColor(c);
        g.fillRect(x, y, w, h);
        g.setColor(Color.black);
        g.drawRect(x, y, w, h);
        g.setColor(oldColor);
    }

}
