//
//  MUConnection.java
//  JavaTelnet
//
//  Created by asp on Fri Nov 16 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.engine;

import btthud.ui.Thud;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * This class is for handling the basic connection between the HUD and the MUX. 
 *
 * @author Anthony Parker
 * @version 1.0, 11.16.01
 */

public class MUConnection implements Runnable {

    
    /**
     * Holds the connection.
     */
    Socket			conn = null;
    String			host = null;
    MUParse			handler = null;
    Thud			errorHandler = null;
    int				port;
    BufferedReader	rd;
    InputStream		is;
    BufferedWriter	wr;
    
    boolean			go = true;
    
    private Thread	connThread = null;

    // ---------------------------------------------------------
    
    // Public constructors
    /**
     * Creates a new MUConnection object, connects to the host, and starts recieving data and storing it.
     * @param host The IP address or name of the host we're connecting to.
     * @param port The port of the host we're connecting to.
     * @see check
     * @see endConnection
     */
    public MUConnection(String host, int port, MUParse handler, Thud errorHandler) throws java.net.UnknownHostException, java.io.IOException
    {
        this.host = host;
        this.port = port;
        this.handler = handler;
        this.errorHandler = errorHandler;
        
        try
        {
            conn = new Socket(host, port);

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            
            start();
        }
        catch (Exception e)
        {
            // throw e;
        }
    }
    
    /**
     * Start the MUConnection thread going...
     */
    public void start()
    {
        if (connThread == null)
        {
            connThread = new Thread(this, "MUConnection");
            connThread.start();
        }
    }
    
    /**
     * Send a string to the MUX, usually to execute a command.
     * @param command The string which holds the command we want to run.
     */    
    public void sendCommand(String command) throws java.io.IOException
    {
        try
        {
            wr.write(command);
            wr.newLine();
            wr.flush();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }
    
    /**
     * Checks to see if there is new input that we should store here. If so, it puts it in the StringBuffer.
     */
    public void run()
    {
        String	line;
        boolean	done = false;
        byte	readChar;
        
        while (go)
        {
            try
            {
                line = rd.readLine();
                handler.parseLine(line);
            }
            catch (IOException ioe)
            {
                errorHandler.stopConnection();
            }
            catch (Exception e)
            {
                System.out.println("Error: connection: " + e);
            }
        }
    }
    
    public void pleaseStop()
    {
        try
        {
            conn.close();			// close the socket
        }
        catch (Exception e)
        {
            System.out.println("Error: closeSocket: " + e);
        }
        
        go = false;
    }
}
