//
//  Corner.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 11 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.ui;

import java.awt.*;
import javax.swing.*;

public class Corner extends JComponent {
    
    public void paintComponent(Graphics g)
    {
        // Fill with black
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
