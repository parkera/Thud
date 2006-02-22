//
//  ANSIParser.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.util;

import javax.swing.text.*;
import java.awt.*;

/* This is a class full of static methods to help with ANSI parsing */

/*
 MUX uses these colors:

 f - flash                       i - inverse
 h - hilite                      n - normal
 u - underline
 
 x - black foreground            X - black background
 r - red foreground              R - red background
 g - green foreground            G - green background
 y - yellow foreground           Y - yellow background
 b - blue foreground             B - blue background
 m - magenta foreground          M - magenta background
 c - cyan foreground             C - cyan background
 w - white foreground            W - white background

 */

public class ANSIParser {
    
    // ------------------------------
    // Static Methods
    // ------------------------------
    
    /**
    * Parse 2 integers, and return an AttributeSet that holds the proper color information.
    */
    static public void parseEscapeCode(int charCode1, int charCode2, MutableAttributeSet a)
    {        
        if (charCode1 == 1)		// highlight
        {
            StyleConstants.setBold(a, true);
        }
        else if (charCode1 == 4 && charCode2 == -1)
        {
            StyleConstants.setUnderline(a, true);
        }
        else if (charCode1 == 5)
        {
            StyleConstants.setItalic(a, true);		// Use italics as substitute for annoying-as-hell blinking
        }
        else if (charCode1 == 3)
        {
            Color		c = (Color) a.getAttribute("background");
            switch (charCode2)
            {
                case 0:
                    StyleConstants.setForeground(a, Color.gray);
                    break;
                case 1:
                    StyleConstants.setForeground(a, Color.red);
                    break;
                case 2:
                    StyleConstants.setForeground(a, Color.green);
                    break;
                case 3:
                    StyleConstants.setForeground(a, Color.yellow);
                    break;
                case 4:
                    StyleConstants.setForeground(a, Color.blue);
                    break;
                case 5:
                    StyleConstants.setForeground(a, Color.magenta);
                    break;
                case 6:
                    StyleConstants.setForeground(a, Color.cyan);
                    break;
                case 7:
                    if (!a.containsAttribute("foreground", Color.white))	// For some reason, this is needed to prevent a white block of text
                        StyleConstants.setForeground(a, Color.white);
                    break;
                default:					// dunno what this is.... change nothing
                    break;
            }
        }
        else if (charCode1 == 4)
        {
            switch (charCode2)
            {
                case 0:
                    StyleConstants.setBackground(a, Color.black);
                    break;
                case 1:
                    StyleConstants.setBackground(a, Color.red);
                    break;
                case 2:
                    StyleConstants.setBackground(a, Color.green);
                    break;
                case 3:
                    StyleConstants.setBackground(a, Color.yellow);
                    break;
                case 4:
                    StyleConstants.setBackground(a, Color.blue);
                    break;
                case 5:
                    StyleConstants.setBackground(a, Color.magenta);
                    break;
                case 6:
                    StyleConstants.setBackground(a, Color.cyan);
                    break;
                case 7:
                    StyleConstants.setBackground(a, Color.white);
                    break;
                default:					// dunno what this is.... change nothing
                    break;
            }
        }
    }

    /**
      * Check to see if this is a 'normal' code
      */
    static public boolean normalEscapeCode(int charCode1)
    {
        if (charCode1 == 0)
            return true;
        else
            return false;
    }
}
