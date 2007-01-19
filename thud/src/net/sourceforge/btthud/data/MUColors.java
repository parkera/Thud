//
//  MUColors.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.Color;

public class MUColors {
	public static final Color  w = new Color(192, 192, 192);
	public static final Color  h = new Color(255, 255, 255);
	public static final Color hx = new Color(128, 128, 128);
	public static final Color hr = new Color(255,   0,   0);
	public static final Color  r = new Color(160,   0,   0);
	public static final Color hg = new Color(  0, 255,   0);
	public static final Color  g = new Color(  0, 160,   0);
	public static final Color hb = new Color(  0,   0, 255);
	public static final Color  b = new Color(  0,   0, 160);	
	public static final Color hy = new Color(255, 255,   0);
	public static final Color  y = new Color(160, 160,   0);
	public static final Color hm = new Color(255,   0, 255);
	public static final Color  m = new Color(160,   0, 160);
	public static final Color hc = new Color(  0, 255, 255);
	public static final Color  c = new Color(  0, 160, 160);
	
	public static Color withTransparency (Color color, int a) {		
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);		
	}			
}
