//
//  MUHex.java
//  Thud
//
//  Created by Anthony Parker on Mon Dec 17 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package com.btthud.thud;

public class MUHex
{

    public char			terrain;
    public int			elevation;
    
    public MUHex()
    {
        // Default terrain is a level 0 unknown
        terrain = '?';
        elevation = 0;
    }

    public MUHex(char terrain, int elevation)
    {
        this.terrain = terrain;
        this.elevation = elevation;
    }

    public int absElevation()
    {
        return Math.abs(elevation);
    }
    
}
