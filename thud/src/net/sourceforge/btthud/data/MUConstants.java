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
    public static final int			FAST_UPDATE = 1;
    public static final int			NORMAL_UPDATE = 2;
    public static final int			SLOW_UPDATE = 3;
    
    /* Heat levels taken from MUX mech.status.c */
    public static final int			HEAT_LEVEL_LGREEN=0;
    public static final int			HEAT_LEVEL_BGREEN=7;
    public static final int			HEAT_LEVEL_LYELLOW=13;
    public static final int			HEAT_LEVEL_BYELLOW=16;
    public static final int			HEAT_LEVEL_LRED=18;
    public static final int			HEAT_LEVEL_BRED=24;
    public static final int			HEAT_LEVEL_TOP=40;    
    public static final int			HEAT_LEVEL_NONE=17; // This one is different than the MUX to account for THUD's smaller status window     
}
