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

        // Setup our new contact list pane
        BulkStyledDocument	bsd = new BulkStyledDocument(prefs.contactFontSize);        
        contactPane = new JTextPane(bsd);
        contactPane.setBackground(Color.black);
        contactPane.setEditable(false);
        contactPane.setDoubleBuffered(true);

        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
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

        conEnemy = new SimpleAttributeSet();
        conEnemy.setResolveParent(conRegular);
        StyleConstants.setForeground(conEnemy, Color.yellow);
        StyleConstants.setBold(conEnemy, true);

        conLocked = new SimpleAttributeSet();
        conLocked.setResolveParent(conRegular);
        StyleConstants.setForeground(conLocked, Color.red);
        StyleConstants.setBold(conLocked, true);

        conFriend = new SimpleAttributeSet();
        conFriend.setResolveParent(conRegular);

        conExpired = new SimpleAttributeSet();
        conExpired.setResolveParent(conRegular);
        StyleConstants.setForeground(conExpired, Color.gray);

        conDestroyed = new SimpleAttributeSet();
        conDestroyed.setResolveParent(conRegular);
        StyleConstants.setForeground(conDestroyed, Color.gray);
        StyleConstants.setStrikeThrough(conDestroyed, true);
        
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
        DefaultStyledDocument.ElementSpec[]		list;
        
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

                            AttributeSet		whichAttrs;
                            if (unit.isDestroyed())
                                whichAttrs = conDestroyed;
                            else if (unit.isOld())
                                whichAttrs = conExpired;
                            else if (unit.isFriend())
                                whichAttrs = conFriend;
                            else if (!unit.isFriend() && !unit.isTarget())
                                whichAttrs = conEnemy;
                            else if (unit.isTarget())
                                whichAttrs = conLocked;
                            else
                                whichAttrs = conRegular;

                            elements.add(new DefaultStyledDocument.ElementSpec(whichAttrs,
                                                                               DefaultStyledDocument.ElementSpec.ContentType,
                                                                               unit.makeContactString().toCharArray(),
                                                                               0,
                                                                               unit.makeContactString().length()));
                        }

                    }

                    doc.clearAndInsertParsedString(elements);
                    
                    contactPane.setCaretPosition(doc.getLength());
                }

                // This should probably sleep until notified or something
                Thread.sleep((int) (prefs.contactsDelay * 1000.0));

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
    }

    /* ---------------------- */

    public void actionPerformed(ActionEvent newEvent)
    {

    }	
    
}
