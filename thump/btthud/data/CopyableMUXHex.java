//
//  CopyableMUXHex.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 18 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.data;

public class CopyableMUXHex {

    int			elevation;
    int			terrain;

    int			dx, dy;			// relationship of this hex to the previous hex

    // The first CopyableMUXHex in a list should have isEven set
    // If it's true, all the hexes are relative to an even hex
    // If it's false, all the hexes are relative to an odd hex
    // the pasting code will have to adjust accordingly
    boolean		isEven;

    public CopyableMUXHex(int terrain, int elevation, int dx, int dy, boolean isEven)
    {
        this.terrain = terrain;
        this.elevation = elevation;
        this.dx = dx;
        this.dy = dy;
        this.isEven = isEven;
    }

    public CopyableMUXHex(int terrain, int elevation, int dx, int dy)
    {
        this(terrain, elevation, dx, dy, false);
    }

    // -----------------

    public int getDx()
    {
        return dx;
    }

    public int getDy()
    {
        return dy;
    }

    public int getTerrain()
    {
        return terrain;
    }

    public int getElevation()
    {
        return elevation;
    }

    public boolean isEven()
    {
        return isEven;
    }

    // ------------------
    
    public String toString()
    {
        return "Terr: " + MUXHex.terrainForId(terrain) + " El: " + elevation + " dx,dy: " + dx + "," + dy;
    }

}
