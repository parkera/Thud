//
//  LineHolder.java
//  Thud
//
//  Created by Anthony Parker on Fri Sep 13 2002.
//  Copyright (c) 2002 Anthony Parker. All rights reserved.
//
package btthud.util;

public class LineHolder {

    String			s = null;
    boolean			available = false;
    
    public synchronized String get()
    {
        while (!available)
        {
            try {
                // Wait for the MUConnection class to put a value in here for us to retrieve
                wait();
            } catch (InterruptedException e) {
                
            }
        }

        // Ok, the MUParse class has retrieved a value.. notify MUConnection
        available = false;
        notifyAll();

        return s;        
    }

    public synchronized void put(String s)
    {
        while (available)
        {
            try {
                // Wait for the MUParse to get the value
                wait();
            } catch (InterruptedException e) {
                
            }
        }

        // Set the values
        this.s = s;

        // Ok, the MUConnection class has set a value.. notify MUParse
        available = true;
        notifyAll();
    } 
}
