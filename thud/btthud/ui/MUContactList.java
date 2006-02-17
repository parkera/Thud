//
//  MUContactList.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import btthud.data.*;
import btthud.engine.*;
import btthud.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.util.*;

public class MUContactList extends JFrame
                           implements Runnable,
                                      ActionListener
{
    MUConnection		conn;
    MUData				data;
    MUPrefs				prefs;

    Font				mFont;
    
    JTextPane			contactPane;
    Thread				thread = null;
    
    boolean				go = true;

    MutableAttributeSet	conRegular;
    MutableAttributeSet	conEnemy;
    MutableAttributeSet	conLocked;
    MutableAttributeSet	conFriend;
    MutableAttributeSet	conExpired;
    MutableAttributeSet	conDestroyed;
    
    public MUContactList(MUConnection conn, MUData data, MUPrefs prefs)
    {
        super("Contact List");
        
        this.data = data;
        this.conn = conn;
        this.prefs = prefs;

        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
        
        // Setup our new contact list pane
        BulkStyledDocument	bsd = new BulkStyledDocument(prefs.contactFontSize, 1000, mFont);        // Yes, max of 1000 contacts. So sue me.
        contactPane = new JTextPane(bsd);
        contactPane.setBackground(Color.black);
        contactPane.setEditable(false);
        contactPane.setDoubleBuffered(true);

        contactPane.setFont(mFont);
        initAttributeSets();

        JScrollPane scrollPane = new JScrollPane(contactPane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setDoubleBuffered(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane);
        contentPane.setDoubleBuffered(true);
        
        setContentPane(contentPane);

        setSize(prefs.contactsSizeX, prefs.contactsSizeY);
        setLocation(prefs.contactsLoc);
        // Show the window now

        this.show();
        
        start();
    }

    protected void initAttributeSets()
    {
        conRegular = new SimpleAttributeSet();
        StyleConstants.setFontSize(conRegular, prefs.contactFontSize);
        StyleConstants.setForeground(conRegular, Color.white);
    }
    
    public void newPreferences(MUPrefs prefs)
    {
        this.prefs = prefs;
        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
        contactPane.setFont(mFont);
        initAttributeSets();
    }
    
    // --------------------
    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this, "MUContactList");
            thread.start();
        }
    }
    
    public void run()
    {
        MUUnitInfo								unit = new MUUnitInfo();
        BulkStyledDocument 						doc = (BulkStyledDocument) contactPane.getDocument();
        ArrayList								elements = new ArrayList();
        
        while (go)
        {
            try
            {
                if (data.hudRunning)
                {                    
                    elements.clear();
                    
                    synchronized (data)
                    {
                        Iterator		contacts = data.getContactsIterator(true);		// Sorted list
                                                
                        while (contacts.hasNext())
                        {
                            unit = (MUUnitInfo) contacts.next();

                            elements.add(new DefaultStyledDocument.ElementSpec(conRegular, DefaultStyledDocument.ElementSpec.EndTagType));
                            elements.add(new DefaultStyledDocument.ElementSpec(conRegular, DefaultStyledDocument.ElementSpec.StartTagType));

                            SimpleAttributeSet		whichAttrs = new SimpleAttributeSet(conRegular);

                            if (unit.isOld())
                            {
                                StyleConstants.setForeground(whichAttrs, Color.gray);
                            }

                            if (!unit.isFriend() && !unit.isTarget())
                            {
                                StyleConstants.setForeground(whichAttrs, Color.yellow);
                                StyleConstants.setBold(whichAttrs, true);
                            }

                            if (unit.isTarget())
                            {
                                StyleConstants.setForeground(whichAttrs, Color.red);
                            }

                            if (unit.isDestroyed())
                            {
                                StyleConstants.setStrikeThrough(whichAttrs, true);
                                StyleConstants.setBold(whichAttrs, false);
                            }
                            
                            elements.add(new DefaultStyledDocument.ElementSpec(whichAttrs,
                                                                               DefaultStyledDocument.ElementSpec.ContentType,
                                                                               unit.makeContactString().toCharArray(),
                                                                               0,
                                                                               unit.makeContactString().length()));
                        }
                        

                    }

                    doc.clearAndInsertParsedString(elements);
                    
                    // Don't scroll
                    // contactPane.setCaretPosition(doc.getLength());
                }

                // This should probably sleep until notified or something
                Thread.sleep(1000);

            }
            catch (InterruptedException e)
            {
                // no big deal
            }
            catch (Exception e)
            {
                System.out.println("Error: contacts refresh: " + e);
            }
        }
    }
    
    public void pleaseStop()
    {
        go = false;
        this.dispose();
    }

    /* ---------------------- */

    public void actionPerformed(ActionEvent newEvent)
    {

    }	
    
}
