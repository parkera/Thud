//
//  LineHolder.java
//  Thud
//
//  Created by Anthony Parker on Fri Sep 13 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.util;

/* The way this works is that MUParse uses the put() function to store a String in the LineHolder.
 * Then MUConnection uses the get() function to get the string and parse it. Splitting this up allows
 * us to use a Producer (MUConnection) / Consumer (MUParse) thread structure. Because of the wait() and
 * notifyAll() calls, the MUParse thread isn't 'busy waiting', wasting CPU time and holding up other threads.
 */

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
