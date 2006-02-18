//
//  OptionTextField.java
//  Thud
//
//  Created by Anthony Parker on Thu Oct 17 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class OptionTextField extends JTextField {

    boolean			antiAliased;

    public OptionTextField(int l, boolean antiAliased)
    {
        super(l);
        this.antiAliased = antiAliased;        
    }

    public void paintComponent(Graphics g)
    {
        /*
         // Doesn't seem to be working properly... could be the Mac OS X JRE screwing with my head
        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            this.antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        */
        super.paintComponent(g);
    }
}
