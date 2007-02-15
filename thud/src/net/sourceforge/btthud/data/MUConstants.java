//
//  MUConstants.java
//  Thud
//  
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.io.*;
import java.awt.*;

import java.util.*;

public class MUConstants
{
    /* Heat levels taken from MUX mech.status.c */
    public static final int			HEAT_LEVEL_LGREEN=0;
    public static final int			HEAT_LEVEL_BGREEN=7;
    public static final int			HEAT_LEVEL_LYELLOW=13;
    public static final int			HEAT_LEVEL_BYELLOW=16;
    public static final int			HEAT_LEVEL_LRED=18;
    public static final int			HEAT_LEVEL_BRED=24;
    public static final int			HEAT_LEVEL_TOP=40;    
    public static final int			HEAT_LEVEL_NONE=17; // This one is different than the MUX to account for THUD's smaller status window     


    /*
     * Scaling constants taken from hcode/btech/mech.h.
     */

    // 1/update. (How many ticks it takes to cross 1 hex in y at 1 KPH.)
    public static final float			SCALEMAP = 322.5f;

    // Levels/hex. (How many levels is the same as 1 hex in height.)
    public static final int			HEXLEVEL = 5;

    // Alpha. (sqrt(3) / 6; actually, BTMUX uses SCALEMAP * sqrt(3) / 6.)
    //
    // Alpha helps describe the horizontal dimensions of a hex grid cell.  If a
    // hex is one unit high, it is 4 alpha wide, with the top and bottom being
    // 2 alpha wide, and the sides being 1 alpha wide each.
    //
    // If each column is 1 alpha units wide, and each row 1/2 units high, then
    // a hex grid has the following structure:
    // __
    //   \__/
    // __/  \
    public static final float			ALPHA = 0.288675135f;
}
