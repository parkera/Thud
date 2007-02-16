//
//  MUHost.java
//  Thud
//
//  Created by Anthony Parker on Wed Sep 25 2002.
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.io.*;

// XXX: If you make this mutable, implement Cloneable/clone() and update the
// clone() in MUPrefs to do a deep copy of the host list.
public class MUHost {

    String			host;
    int				port;

    /** 
     * Constructor with default host and port.
     */
    public MUHost()
    {
        this.host = null;
        this.port = 23;
    }
    
    /**
     * Constructor with specified host and port.
     */
    
    public MUHost(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public boolean equals(Object other)
    {
        if (other == null)
            return false;
        
        if ((this.toString()).equals(other.toString()))
            return true;
        else
            return false;
    }

    public String toString()
    {
        return (host + " " + port);
    }
}
