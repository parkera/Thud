//
//  MUContactList.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//

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

    MUConnection	conn;
    MUData			data;
    MUPrefs			prefs;

    Font			mFont;
    
    JTextPane		contactPane;
    Thread			thread = null;
    
    boolean			go = true;
    
    public MUContactList(MUConnection conn, MUData data, MUPrefs prefs)
    {
        super("Contact List");
        
        this.data = data;
        this.conn = conn;
        this.prefs = prefs;

        // Setup our new contact list pane
        contactPane = new JTextPane();
        contactPane.setBackground(Color.black);
        contactPane.setEditable(false);
        contactPane.setDoubleBuffered(true);
        mFont = new Font("Monospaced", Font.PLAIN, prefs.contactFontSize);
        contactPane.setFont(mFont);
        initStylesForTextPane(contactPane);

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

    protected void initStylesForTextPane(JTextPane textPane)
    {

        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
        getStyle(StyleContext.DEFAULT_STYLE);

        StyleConstants.setFontSize(def, prefs.contactFontSize);
        StyleConstants.setForeground(def, Color.white);
        Style regular = textPane.addStyle("con-regular", def);

        Style s = textPane.addStyle("con-enemy", regular);
        StyleConstants.setForeground(s, Color.yellow);
        StyleConstants.setBold(s, true);

        s = textPane.addStyle("con-locked", regular);
        StyleConstants.setForeground(s, Color.red);
        StyleConstants.setBold(s, true);
        
        s = textPane.addStyle("con-friend", regular);
        StyleConstants.setForeground(s, Color.white);

        s = textPane.addStyle("con-expired", regular);
        StyleConstants.setForeground(s, Color.gray);

        s = textPane.addStyle("con-destroyed", regular);
        StyleConstants.setForeground(s, Color.gray);
        StyleConstants.setStrikeThrough(s, true);
    }

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
        MUUnitInfo		unit = new MUUnitInfo();
        Document 		doc = contactPane.getDocument();
        
        while (go)
        {
            try
            {
                if (data.hudRunning)
                {
                    //conn.sendCommand("contacts h");		// send contacts command

                    if (doc.getLength() > 0)
                        doc.remove(0, doc.getLength());		// clear the buffer

                    TreeSet				contactsTree = null;
                    int					docL;
                    
                    synchronized (data.contacts)
                    {

                        contactsTree = new TreeSet((data.contacts).values());
            
                        docL = doc.getLength();
                        if (docL < 0)
                            System.out.println("*** -> docLength: " + docL);
                        
                        for (Iterator it = contactsTree.iterator(); it.hasNext(); )
                        {
                            unit = (MUUnitInfo) it.next();

                            if (unit.isDestroyed())
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-destroyed"));
                            else if (unit.isOld())
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-expired"));
                            else if (unit.friend)
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-friend"));
                            else if (!unit.friend && !unit.target)
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-enemy"));
                            else if (unit.target)
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-locked"));
                            else
                                doc.insertString(docL, unit.makeContactString() + "\n", contactPane.getStyle("con-regular"));
                        }

                    }

                    docL = doc.getLength();
                    if (docL < 0)
                        System.out.println("*** -> docLengthNew: " + docL);
                    contactPane.setCaretPosition(docL);
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
