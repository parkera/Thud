//
//  BulkStyledDocument.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 29 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
package btthud.util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import java.lang.*;
import java.util.*;

public class BulkStyledDocument extends DefaultStyledDocument
{

    int						fontSize = 10;
    MutableAttributeSet		attrBase = new SimpleAttributeSet();
    MutableAttributeSet		thisAttrSet;

    MutableAttributeSet		attrPlain;
    MutableAttributeSet		attrCommand;
    MutableAttributeSet		attrHudMessage;

    LinkedList				cachedAttributes = new LinkedList();

    // ---------------
    
    public BulkStyledDocument(int fontSize)
    {
        super();

        this.fontSize = fontSize;

        newFontSize(fontSize);
    }

    public void newFontSize(int fontSize)
    {
        this.fontSize = fontSize;

        StyleConstants.setFontSize(attrBase, fontSize);				// Default font size...
        StyleConstants.setForeground(attrBase, Color.white);		// ... and color

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

    // -----------------------

    /**
      * By caching AttributeSets instead of creating new ones each time, we save a lot of memory and the HUD goes way faster.
      */
    protected SimpleAttributeSet cachedAttributeSet(MutableAttributeSet matchMe)
    {
        Iterator			it = cachedAttributes.iterator();

        while (it.hasNext())
        {
            SimpleAttributeSet 	attr = (SimpleAttributeSet) it.next();
            if (attr.isEqual(matchMe))
                return attr;
        }

        // Must not have matched any. Let's add this one to our list
        cachedAttributes.add(matchMe);
        return (SimpleAttributeSet) matchMe;
    }

    protected void clearCachedAttributeSets()
    {
        cachedAttributes = new LinkedList();
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

        ArrayList					elements = new ArrayList();	// we could specify an initial capacity here

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
                            thisAttrSet = new SimpleAttributeSet();
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
            Content				c = getContent();
            ElementSpec[]		list = (ElementSpec[]) es.toArray(new ElementSpec[0]);
            
            insert(getLength(), list);
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
