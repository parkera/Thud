//
//  MUHost.java
//  Thud
//
//  Created by Anthony Parker on Wed Sep 25 2002.
//  Copyright (c) 2002 Anthony Parker. All rights reserved.
//

package btthud.data;

import java.io.*;

public class MUHost implements Serializable {

    String			host;
    int				port;

    public MUHost()
    {
        this.host = null;
        this.port = 23;
    }
    
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
