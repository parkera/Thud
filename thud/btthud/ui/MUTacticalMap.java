//
//  MUTacticalMap.java
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
import java.awt.geom.*;
import java.awt.font.*;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.*;
import java.util.*;

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

        // Setup our new contact list pane
        map = new MUMapComponent(data, prefs);

        map.setDoubleBuffered(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(map);
        setContentPane(contentPane);

        setSize(prefs.tacSizeX, prefs.tacSizeY);
        setLocation(prefs.tacLoc);

        //addWindowListener(new OnTopHandler());
        // An attempt at providing "always on top" functionality: doesn't work too great
        
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                ((Window) e.getComponent()).toFront();
            }
        });
        
        
        // Show the window now
        this.show();

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

                Thread.sleep((int) (prefs.tacticalRedrawDelay * 1000.0));
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
    }

    
    public void actionPerformed(ActionEvent newEvent)
    {
        
    }


    public void newPreferences(MUPrefs prefs)
    {
        map.newPreferences(prefs);
    }
}

// Attempt at providing "always on top" functionality.. doesn't work too great
class OnTopHandler implements java.awt.event.ActionListener, java.awt.event.WindowListener
{
    public void actionPerformed(java.awt.event.ActionEvent e) {};
    public void windowActivated(java.awt.event.WindowEvent e) {};
    public void windowClosed(java.awt.event.WindowEvent e) {};
    public void windowClosing(java.awt.event.WindowEvent e) {};

    public void windowDeactivated(java.awt.event.WindowEvent e)
    {
        //e.getWindow().toFront();
    }

    public void windowDeiconified(java.awt.event.WindowEvent e) {};
    public void windowIconified(java.awt.event.WindowEvent e) {};
    public void windowOpened(java.awt.event.WindowEvent e) {};
}

