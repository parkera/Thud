//
//  MUCommandsTask.java
//  Thud
//
//  Created by Anthony Parker on Wed Oct 02 2002.
//  Copyright (c) 2002 Anthony Parker. All rights reserved.
//

package btthud.engine;

import java.util.*;
import btthud.data.*;

public class MUCommandsTask extends TimerTask {

    MUConnection		conn;
    MUData				data;
    MUPrefs				prefs;
    
    boolean				forceContacts;
    boolean				forceTactical;
    boolean				forceGeneralStatus;
    boolean				forceArmorStatus;

    int					count;
    
    public MUCommandsTask(MUConnection conn, MUData data, MUPrefs prefs)
    {
        this.conn = conn;
        this.data = data;
        this.prefs = prefs;
    }
    
    public void run()
    {
        // We've been woken up, now we need to decide which commands to send

        try
        {
            // Do we send general status?
            if (forceGeneralStatus || (count % 4 == 0))
                conn.sendCommand("hudinfo gs");

            // Do we send a contacts?
            if (forceContacts || (count % 4 == 0))
            {
                conn.sendCommand("hudinfo c");
                synchronized (data)
                {
                    data.expireAllContacts();                    
                }
            }

            // Do we send a tactical?
            if (forceTactical || (count % (4 * 20) == 0))
                conn.sendCommand("hudinfo t " + prefs.hudinfoTacHeight);

            // Do we send an armor status?
            if (forceArmorStatus || (count % (4 * 3) == 0))
                conn.sendCommand("hudinfo as");
            
        }
        catch (Exception e)
        {
            System.out.println("Error: MUCommandsTask: " + e);
        }
        
        // Increment our count
        count++;
    }
}
