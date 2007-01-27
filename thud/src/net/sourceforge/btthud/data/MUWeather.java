//
//  MUWeather.java
//  Thud
// 
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;
/**
 * Stores information for current weather.
 *
 * @author Tim Krajcar
 */
public class MUWeather {
	// Statics
	public static final int	LIGHT_DAY = 0;
	public static final int	LIGHT_DAWN_DUSK = 1;
	public static final int	LIGHT_NIGHT = 2;
	
	// Weather data
	public int				light = 0;
	public int				visibility = 0;
	public int				gravity = 0;
	public int				ambientTemperature=0;
	public boolean			isVacuum = false;
	public boolean			isUnderground = false;
	public boolean			isDark = false;
		
	// Methods
	static public String lightString(int id) {
		switch(id)
		{
		case LIGHT_DAY:
			return "Day";
		case LIGHT_DAWN_DUSK:
			return "Dawn/Dusk";
		case LIGHT_NIGHT:
			return "Night";
		default:
			return "Unknown";				
		}
	}
	
}
