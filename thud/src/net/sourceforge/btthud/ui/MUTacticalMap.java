//
//  MUTacticalMap.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.*;
import net.sourceforge.btthud.engine.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class MUTacticalMap extends JFrame
                           implements Runnable, ActionListener
{

    MUData					data;
    MUConnection			conn;
    MUPrefs					prefs;
    
    Thread					thread = null;
    private boolean			go = true;

    MUMapComponent			map = null;
        
    public MUTacticalMap(MUConnection conn, MUData data, MUPrefs prefs)
    {
        super("Tactical Map");

        this.data = data;
        this.conn = conn;
        this.prefs = prefs;

        // Setup our new tactical map pane
        map = new MUMapComponent(data, prefs);

        map.setDoubleBuffered(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(map);
        setContentPane(contentPane);

        setSize(prefs.tacSizeX, prefs.tacSizeY);
        setLocation(prefs.tacLoc);
        
        setAlwaysOnTop(prefs.tacticalAlwaysOnTop);
        
        // Show the window now
        this.setVisible(true);

        start();
    }

    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this, "MUTacticalMap");
            thread.start();
        }
    }

    public void run()
    {
        while (go)
        {
            try
            {
                if (data.hudRunning)
                    map.repaint(map.getBounds());

                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // No big deal
            }
            catch (Exception e)
            {
                System.out.println("Error: TacticalMap refresh: " + e);
            }
        }
    }

    public void pleaseStop()
    {
        go = false;
        this.dispose();
    }

    
    public void actionPerformed(ActionEvent newEvent)
    {
        map.repaint(map.getBounds());
    }


    public void newPreferences(MUPrefs prefs)
    {
        map.newPreferences(prefs);
        this.setAlwaysOnTop(prefs.tacticalAlwaysOnTop);
    }
}
