//
//  BulkStyledDocument.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 29 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
package com.btthud.thud;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.lang.*;
import java.util.*;

// This class handles all of the ANSI-related hacks to make Thud support color and such

public class BulkStyledDocument extends DefaultStyledDocument
{

    int				mainFontSize = 10;
    
    // Inner class for handling parsed strings
    public class ParsedString
    {
        public String		l;
        public ArrayList	styles;

        public ParsedString(String l, ArrayList styles)
        {
            this.l = l;
            this.styles = styles;
        }
    }

    // ---------------
    
    public BulkStyledDocument(int fontSize)
    {
        super();

        mainFontSize = fontSize;
        initStyles();
    }

    protected void initStyles()
    {
        /*
        MUX uses these colors:

         f - flash                       i - inverse
         h - hilite                      n - normal

         x - black foreground            X - black background
         r - red foreground              R - red background
         g - green foreground            G - green background
         y - yellow foreground           Y - yellow background
         b - blue foreground             B - blue background
         m - magenta foreground          M - magenta background
         c - cyan foreground             C - cyan background
         w - white foreground            W - white background
         
         */

        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);

        StyleConstants.setFontSize(def, mainFontSize);
        StyleConstants.setForeground(def, Color.white);
        Style regular = addStyle("regular", def);

        // ---------
        // We don't support colored backgrounds yet
        
        Style s = addStyle("black", regular);
        StyleConstants.setForeground(s, Color.white);

        s = addStyle("red", regular);
        StyleConstants.setForeground(s, Color.red);

        s = addStyle("green", regular);
        StyleConstants.setForeground(s, Color.green);

        s = addStyle("yellow", regular);
        StyleConstants.setForeground(s, Color.yellow);

        s = addStyle("blue", regular);
        StyleConstants.setForeground(s, Color.blue);

        s = addStyle("magenta", regular);
        StyleConstants.setForeground(s, Color.magenta);

        s = addStyle("cyan", regular);
        StyleConstants.setForeground(s, Color.cyan);

        s = addStyle("white", regular);
        StyleConstants.setForeground(s, Color.white);

        s = addStyle("regularbold", regular);	// aka highlight
        StyleConstants.setForeground(s, Color.white);
        StyleConstants.setBold(s, true);

        s = addStyle("bold", regular);
        StyleConstants.setForeground(s, Color.white);
        StyleConstants.setBold(s, true);

        s = addStyle("blackbold", regular);
        StyleConstants.setForeground(s, Color.black);
        //StyleConstants.setForeground(s, Color.white);
        StyleConstants.setBold(s, true);

        s = addStyle("redbold", regular);
        StyleConstants.setForeground(s, Color.red);
        StyleConstants.setBold(s, true);

        s = addStyle("greenbold", regular);
        StyleConstants.setForeground(s, Color.green);
        StyleConstants.setBold(s, true);

        s = addStyle("yellowbold", regular);
        StyleConstants.setForeground(s, Color.yellow);
        StyleConstants.setBold(s, true);

        s = addStyle("bluebold", regular);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setBold(s, true);

        s = addStyle("magentabold", regular);
        StyleConstants.setForeground(s, Color.magenta);
        StyleConstants.setBold(s, true);

        s = addStyle("cyanbold", regular);
        StyleConstants.setForeground(s, Color.cyan);
        StyleConstants.setBold(s, true);

        s = addStyle("whitebold", regular);
        StyleConstants.setForeground(s, Color.white);
        StyleConstants.setBold(s, true);

        s = addStyle("invert", regular);
        StyleConstants.setBackground(s, Color.white);
        StyleConstants.setForeground(s, Color.black);

        s = addStyle("invertbold", regular);
        StyleConstants.setBackground(s, Color.white);
        StyleConstants.setForeground(s, Color.black);
        StyleConstants.setBold(s, true);
        
        // For HUD messages
        
        s = addStyle("hudmessage", regular);
        StyleConstants.setForeground(s, Color.black);
        StyleConstants.setBackground(s, Color.white);

        s = addStyle("hudcommand", regular);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setBackground(s, Color.black);
    }


    /**
    * Converts color codes into strings, then inserts them in document
     * with proper formatting. Then it returns the uncolored string for parsing.
     * @param l The raw string we want to convert
     */

    String			style = "regular";

    public ParsedString parseString(String l)
    {
        StringBuffer				thisStr = new StringBuffer();
        int							numChars = l.length();
        int							i = 0;
        int							start = 0;
        int							parsePos = 0;
        boolean						done;

        ArrayList					styles = new ArrayList();	// we could specify an initial capacity here

        try
        {
            // Debugging - don't covert color codes if we just paste it into the doc
            //doc.insertString(doc.getLength(), l + "\n", textPane.getStyle("regular"));

            while (i < numChars)
            {
                if (l.charAt(i) == 0x1B)
                {
                    // Let's add a string to our document now
                    // (it should have the characteristics of the previous string)
                    //if (thisStr.length() > 0)	// don't bother adding an empty string
                    if (i - start > 0)
                    {
                        styles.add(new StyleRunHolder(style, start, thisStr.length() - start));
                        start += thisStr.length() - start;
                    }

                    // Check to see what kind of color we have
                    // charCode1 and charCode2 are used to tell what the escape code is
                    // We're at the beginning of a new string,
                    // which should start with a sequence of escape codes to tell us what style it's in
                    // We can skip the escape code and the [ to get to the first digit of the first code

                    // some more esoteric styles:
                    // #define ANSI_INVERSE  "\033[7m"
                    // #define ANSI_BLINK    "\033[5m"
                    // #define ANSI_UNDER    "\033[4m"

                    parsePos = 2;
                    done = false;

                    while (!done)
                    {
                        int		charCode1 = Character.digit(l.charAt(i + parsePos), 10);
                        int		charCode2 = (charCode1 == 1 ? -1 : Character.digit(l.charAt(i + parsePos + 1), 10));
                        if (boldEscapeCode(charCode1))
                        {
                            if (!style.endsWith("bold"))
                                style = style + "bold";
                        }
                        else if (normalEscapeCode(charCode1))
                        {
                            style = "regular";
                        }
                        else if (style.endsWith("bold"))
                        {
                            // keep the bold
                            style = parseEscapeCode(charCode1, charCode2);
                            style = style + "bold";
                        }
                        else
                        {
                            // drop the bold
                            style = parseEscapeCode(charCode1, charCode2);
                        }

                        parsePos++;				// move on to next character... (see below)
                        if (charCode2 != -1)
                            parsePos++;			// if there are 2 digits, need to go one more character

                        if (l.charAt(i + parsePos) == 'm') // end of code character is m
                        {
                            parsePos++;
                            i += parsePos;
                            // We're done
                            done = true;
                        }
                        else
                        {
                            // Must have more codes to go through
                            parsePos++;
                        }
                    }	// end of while loop
                }
                else							// Must not be an escape character
                {
                    // Add it to our current string
                    thisStr.append(l.charAt(i));
                    i++;	// move on to next character
                }
            }

            // Put the final section of the string into our holder
            if (i - start != 0)
                styles.add(new StyleRunHolder(style, start, thisStr.length() - start));

        }
        catch (Exception e)
        {
            System.out.println("Error: parseAndReturnString: " + e);
        }

        return (new ParsedString(thisStr.toString(), styles));
    }

    /**
     * Insert a parsed string
     * @param ps The parsed string object
     */
    public void insertParsedString(ParsedString ps)
    {
        try
        {
            // Insert the string into the document
            int 		docLength = getLength();
            insertString(docLength, ps.l + "\n", getStyle("regular"));

            // Format the string we just inserted
            getWriteLock();
            for (int i = 0; i < ps.styles.size(); i++)
            {
                StyleRunHolder		srh = (StyleRunHolder) ps.styles.get(i);
                setCharacterAttributes(docLength + srh.start,
                                       srh.length,
                                       getStyle(srh.style),
                                       true);
            }
            releaseWriteLock();
        }
        catch (Exception e)
        {
            System.out.println("Error: insertParsedString: " + e);
        }
    }

    /**
     * Insert a plain string -- usually a message from the HUD.
     */
    public void insertPlainString(String l)
    {
        try
        {
            insertString(getLength(), l + "\n", getStyle("regular"));
        }
        catch (Exception e)
        {
            System.out.println("Error: insertPlainString: " + e);            
        }
    }

    public void insertMessageString(String l)
    {
        try
        {
            insertString(getLength(), l + "\n", getStyle("hudmessage"));
        }
        catch (Exception e)
        {
            System.out.println("Error: insertPlainString: " + e);
        }
    }

    public void insertCommandString(String l)
    {
        try
        {
            insertString(getLength(), l + "\n", getStyle("hudcommand"));
        }
        catch (Exception e)
        {
            System.out.println("Error: insertPlainString: " + e);
        }
    }
    
    /**
     * Check to see if an escape code refers to a bold code
     */
    public boolean boldEscapeCode(int charCode1)
    {
        if (charCode1 == 1)
            return true;
        else
            return false;
    }

    /**
        * Check to see if this is a 'normal' code
     */
    public boolean normalEscapeCode(int charCode1)
    {
        if (charCode1 == 0)
            return true;
        else
            return false;
    }

    /**
        * Parse 2 integers into a style
     */
    public String parseEscapeCode(int charCode1, int charCode2)
    {
        String style;

        if (charCode1 == 0)				// normal
        {
            style = "regular";
        }
        else if (charCode1 == 1)		// highlight
        {
            style = "bold";
        }
        else if (charCode1 == 3)
        {
            switch (charCode2)
            {
                case 0:
                    style = "black";
                    break;
                case 1:
                    style = "red";
                    break;
                case 2:
                    style = "green";
                    break;
                case 3:
                    style = "yellow";
                    break;
                case 4:
                    style = "blue";
                    break;
                case 5:
                    style = "magenta";
                    break;
                case 6:
                    style = "cyan";
                    break;
                case 7:
                    style = "white";
                    break;
                default:					// dunno what this is....
                    style = "regular";
                    break;
            }
        }
        else
        {
            style = "regular";
        } // end of charcode check

        return style;
    }
    
    /**
     * Get a write lock. Use before calling setCharacterAttributes
     */
    protected void getWriteLock()
    {
        writeLock();
    }

    /**
     * Release the write lock. Make SURE to call after all setCharacterAttributes are done
     */
    protected void releaseWriteLock()
    {
        writeUnlock();
    }

    /**
     * We disable the writeLock and writeUnlock here so that we can insert lots of things with one lock.
     * This makes things like tactical maps /much/ faster. You need to call getWriteLock and releaseWriteLock
     * on your own though. Code from the Java superclass, modified to suit. I'm not sure if this is 100% kosher, but
     * it seems to work okay.
     *
     * The rest of the code is the same as the superclass.
     * @see getWriteLock
     * @see releaseWriteLock
     */
    public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace)
    {        
        try {
            //writeLock();
            DefaultDocumentEvent changes =
                new DefaultDocumentEvent(offset, length, DocumentEvent.EventType.CHANGE);

            // split elements that need it
            buffer.change(offset, length, changes);

            AttributeSet sCopy = s.copyAttributes();

            // PENDING(prinz) - this isn't a very efficient way to iterate
            int lastEnd = Integer.MAX_VALUE;
            for (int pos = offset; pos < (offset + length); pos = lastEnd) {
                Element run = getCharacterElement(pos);
                lastEnd = run.getEndOffset();
                MutableAttributeSet attr = (MutableAttributeSet) run.getAttributes();
                changes.addEdit(new AttributeUndoableEdit(run, sCopy, replace));
                if (replace) {
                    attr.removeAttributes(attr);
                }
                attr.addAttributes(s);
            }
            changes.end();
            fireChangedUpdate(changes);
            fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
        } finally {
            //writeUnlock();
        }
    }
}
