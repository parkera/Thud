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

        // If I want to change the font size of all the text previous to this, I may have
        // to create a new MutableAttributeSet and setCharacterAttributes for the whole document
        // For now, I just reset the foreground font size
        
        StyleConstants.setFontSize(attrBase, fontSize);				// Default font size...
        StyleConstants.setForeground(attrBase, Color.white);		// ... and color
        
        thisAttrSet = new SimpleAttributeSet(attrBase);
        attrPlain = new SimpleAttributeSet(attrBase);
        attrCommand = new SimpleAttributeSet(attrBase);
        attrHudMessage = new SimpleAttributeSet(attrBase);

        StyleConstants.setForeground(attrCommand, Color.blue);
        StyleConstants.setForeground(attrHudMessage, Color.black);
        StyleConstants.setBackground(attrHudMessage, Color.white);
    }

    /**
      * Converts color codes into strings, then returns the uncolored string for parsing.
      * @param l The raw string we want to convert
      */

    // -----------------------

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
            while (i < numChars)
            {
                if (l.charAt(i) == 0x1B)	// 0x1B is the ANSI escape code (\033)
                {
                    // Let's add a string to our document now
                    // (it should have the characteristics of the previous string)
                    // Don't bother adding an empty string
                    if (i - start > 0)
                    {
                        styles.add(new StyleRunHolder(new SimpleAttributeSet(thisAttrSet), start, thisStr.length() - start));
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

                        if (ANSIParser.boldEscapeCode(charCode1))			// set ourselves bold
                            StyleConstants.setBold(thisAttrSet, true);
                        else if (ANSIParser.normalEscapeCode(charCode1))	// go back to our base attribute set
                            thisAttrSet = new SimpleAttributeSet(attrBase);
                        else												// merge our current attributes with what these escape codes say
                            thisAttrSet.addAttributes(ANSIParser.parseEscapeCode(charCode1, charCode2));

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
                styles.add(new StyleRunHolder(new SimpleAttributeSet(thisAttrSet), start, thisStr.length() - start));
            
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
            insertString(docLength, ps.l + "\n", attrBase);

            // Format the string we just inserted
            getWriteLock();
            for (int i = 0; i < ps.styles.size(); i++)
            {
                StyleRunHolder		srh = (StyleRunHolder) ps.styles.get(i);
                setCharacterAttributes(docLength + srh.start,
                                       srh.length,
                                       srh.attr,
                                       true);
            }
            releaseWriteLock();
        }
        catch (Exception e)
        {
            System.out.println("Error: insertParsedString: " + e);
        }
        finally
        {
            releaseWriteLock();
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
            insertString(getLength(), l + "\n", attrHudMessage);
        } catch (Exception e) {
            System.out.println("Error: insertMessageString: " + e);
        }
    }

    public void insertCommandString(String l)
    {
        try {
            insertString(getLength(), l + "\n", attrCommand);
        } catch (Exception e) {
            System.out.println("Error: insertCommandString: " + e);
        }
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
