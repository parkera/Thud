//
//  ChangedMUXHex.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 18 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.data;

import java.awt.*;

public class ChangedMUXHex {

    Point			p;
    int				oldTerrain, oldElevation;
    
    public ChangedMUXHex(Point p, MUXHex oldHex)
    {
        this.p = p;
        this.oldTerrain = oldHex.terrain();
        this.oldElevation = oldHex.elevation();
    }

    // -------------

    public Point getLocation()
    {
        return p;
    }

    public int getPrevTerrain()
    {
        return oldTerrain;
    }

    public int getPrevElevation()
    {
        return oldElevation;
    }

    // ------------

    public String toString()
    {
        return "\np: " + p + " oldTerr: " + MUXHex.terrainForId(oldTerrain) + " oldEl: " + oldElevation;
    }

    public boolean matchesHex(Point h)
    {
        if (h.getX() == p.getX() && h.getY() == p.getY())
            return true;
        else
            return false;
    }
}
