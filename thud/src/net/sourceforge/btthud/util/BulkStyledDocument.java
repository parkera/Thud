//
//  BulkStyledDocument.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 29 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.util;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;

import javax.swing.text.*;

import java.util.*;

public class BulkStyledDocument extends DefaultStyledDocument
{

    int						fontSize = 10;
    MutableAttributeSet		attrBase = new SimpleAttributeSet();
    MutableAttributeSet		thisAttrSet;

    MutableAttributeSet		attrPlain;
    MutableAttributeSet		attrCommand;
    MutableAttributeSet		attrHudMessage;

    HashMap<String,AttributeSet>	cachedAttributes = new HashMap<String,AttributeSet>();

    int						maxLines = 1000;

    static final int		NUM_TAB_STOPS = 20;
    
    TabStop[]				tabStops = new TabStop[NUM_TAB_STOPS];
    TabSet					tabSet;

    Font					font;
    
    // ---------------
    
    public BulkStyledDocument(int fontSize, int maxLines, Font font)
    {
        super();

        this.fontSize = fontSize;
        this.maxLines = maxLines;
        
        setFont(fontSize, font);
    }

    public void setFont(int fontSize, Font font)
    {
        this.font = font;
        this.fontSize = fontSize;
        
        StyleConstants.setFontFamily(attrBase, font.getFontName());
        StyleConstants.setFontSize(attrBase, fontSize);				// Default font size...
        StyleConstants.setForeground(attrBase, Color.white);		// ... and color
        StyleConstants.setBackground(attrBase, Color.black);

        FontRenderContext		frc = new FontRenderContext(new AffineTransform(), false, false);

        Rectangle2D		maxCharSize = font.getMaxCharBounds(frc);
        int				maxWidth = (int) (maxCharSize.getWidth());

        for (int i = 0; i < NUM_TAB_STOPS; i++)
            tabStops[i] = new TabStop(i * maxWidth * 8);

        tabSet = new TabSet(tabStops);
        
        StyleConstants.setTabSet(attrBase, tabSet);

        
        thisAttrSet = new SimpleAttributeSet();
        thisAttrSet.setResolveParent(attrBase);

        attrPlain = new SimpleAttributeSet();
        attrPlain.setResolveParent(attrBase);
        
        attrCommand = new SimpleAttributeSet();
        attrCommand.setResolveParent(attrBase);

        attrHudMessage = new SimpleAttributeSet();
        attrHudMessage.setResolveParent(attrBase);

        StyleConstants.setForeground(attrCommand, Color.blue);
        StyleConstants.setForeground(attrHudMessage, Color.black);
        StyleConstants.setBackground(attrHudMessage, Color.white);
    }

    public void setMaxLines(int maxLines)
    {
        this.maxLines = maxLines;
    }

    // -----------------------

    /**
      * By caching AttributeSets instead of creating new ones each time, we save a lot of memory and the HUD goes way faster.
      */
    protected SimpleAttributeSet cachedAttributeSet(MutableAttributeSet matchMe)
    {
        if (!cachedAttributes.containsKey(matchMe.toString()))
            cachedAttributes.put(matchMe.toString(), matchMe.copyAttributes());
        
        return (SimpleAttributeSet) cachedAttributes.get(matchMe.toString());
    }

    protected void clearCachedAttributeSets()
    {
        cachedAttributes = new HashMap();
    }
    
    /**
      * Converts color codes into strings, then returns the uncolored string for parsing.
      * @param l The raw string we want to convert
      */
    public ArrayList parseString(String l)
    {        
        StringBuffer				thisStr = new StringBuffer();
        int							numChars;
        int							i = 0;
        int							start = 0;
        int							parsePos = 0;
        boolean						done;

        ArrayList<ElementSpec>		elements = new ArrayList<ElementSpec>();	// we could specify an initial capacity here
        
        if (l == null)
            return elements;

        numChars = l.length();
        
        // The document is structured like a tree. There is a root element, which has as its branches
        // each line of the document. Each line of the document has leaf nodes which represent each of the types
        // that are in that lines text.
        // The EndTagType indicates that this next line we are making should be a child of the root element.
        // The StartTagType creates a new branch element on the root to indicate this new line.
        // The ContentTypes below create each branch element which represents the text and style.
        
        elements.add(new ElementSpec(cachedAttributeSet(thisAttrSet), ElementSpec.EndTagType));
        elements.add(new ElementSpec(cachedAttributeSet(thisAttrSet), ElementSpec.StartTagType));
        
        try
        {
            while (i < numChars)
            {
                if (l.charAt(i) == 0x1B)	// 0x1B is the ANSI escape code (\033)
                {
                    // Let's add a string to our document now
                    // (it should have the characteristics of the previous string)
                    // Don't bother adding an empty string
                    if (i - start > 0)
                    {
                        elements.add(new ElementSpec(cachedAttributeSet(thisAttrSet),
                                                     ElementSpec.ContentType,
                                                     l.substring(i - thisStr.length(), i).toCharArray(),
                                                     start,
                                                     thisStr.length() - start));
                        
                        start += thisStr.length() - start;
                    }

                    // Check to see what kind of color we have
                    // charCode1 and charCode2 are used to tell what the escape code is
                    // We're at the beginning of a new string,
                    // which should start with a sequence of escape codes to tell us what style it's in
                    // We can skip the escape code and the [ to get to the first digit of the first code

                    parsePos = 2;
                    done = false;

                    while (!done)
                    {
                        int		charCode1 = Character.digit(l.charAt(i + parsePos), 10);		// (i + parsePos) character, base 10
                        int		charCode2 = (charCode1 == 1 ? -1 : Character.digit(l.charAt(i + parsePos + 1), 10));  // Next character, base 10, or -1

                        if (ANSIParser.normalEscapeCode(charCode1))	// go back to our base attribute set
                        {
                            thisAttrSet.removeAttributes(thisAttrSet.getAttributeNames());
                            thisAttrSet.setResolveParent(attrBase);
                        }
                        else										// merge our current attributes with what these escape codes say
                        {
                            ANSIParser.parseEscapeCode(charCode1, charCode2, thisAttrSet);   
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
            {
                elements.add(new ElementSpec(cachedAttributeSet(thisAttrSet),
                                             ElementSpec.ContentType,
                                             l.substring(i - thisStr.length(), i).toCharArray(),
                                             start,
                                             thisStr.length() - start));
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: parseStringES: " + e);
        }

        return elements;
    }    

    /**
      * Insert a parsed string in bulk
      * @param es The array list containing the ElementSpecs
      */
    public void insertParsedString(ArrayList es)
    {
        try
        {
            ElementSpec[]		list = (ElementSpec[]) es.toArray(new ElementSpec[0]);
            
            insert(getLength(), list);

            // Remove a line if our document is too long
            Element				element = getDefaultRootElement();
            
            if (element.getElementCount() >= maxLines)
            {
                element = element.getElement(0);		// Get the first element

                remove(0, element.getEndOffset());                
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: insertParsedString: " + e);
        }
    }

    /**
      * Deletes the entire content of the document.
      *
      */
    public void clearDocument()
    {
        try {
            remove(0, getLength());
        } catch (Exception e) {
            System.out.println("Error: clearDocument: " + e);
        }
    }

    /**
      * Clears the document and inserts a set of new strings
      */
    public void clearAndInsertParsedString(ArrayList es)
    {
        try {            
            if (getLength() > 0)
                remove(0, getLength());		// clear the buffer

            insertParsedString(es);
            
        } catch (Exception e) {
            System.out.println("Error: insertParsedString: " + e);
        }
    }
    
    // Various methods for inserting pre-styled messages into the main window
    
    public void insertPlainString(String l)
    {
        try {
            insertString(getLength(), l + "\n", attrPlain);
        } catch (Exception e) {
            System.out.println("Error: insertPlainString: " + e);            
        }
    }

    public void insertMessageString(String l)
    {
        try {
            insertNewLine();
            insertString(getLength(), l + "\n", attrHudMessage);
        } catch (Exception e) {
            System.out.println("Error: insertMessageString: " + e);
        }
    }

    public void insertCommandString(String l)
    {
        try {
            insertNewLine();
            insertString(getLength(), l + "\n", attrCommand);
        } catch (Exception e) {
            System.out.println("Error: insertCommandString: " + e);
        }
    }

    public void insertNewLine()
    {
        try {
            insertString(getLength(), "\n\n", attrPlain);
        } catch (Exception e) {
            System.out.println("Error: insertNewLine: " + e);
        }
    }
}
