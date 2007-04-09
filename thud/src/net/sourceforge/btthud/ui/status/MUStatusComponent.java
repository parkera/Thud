//
//  MUStatusComponent.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team.
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui.status;

import net.sourceforge.btthud.data.MUData;
import net.sourceforge.btthud.data.MUPrefs;

import net.sourceforge.btthud.data.MUConstants;
import net.sourceforge.btthud.data.MUColors;
import net.sourceforge.btthud.data.MUWeapon;
import net.sourceforge.btthud.data.MUMyInfo;
import net.sourceforge.btthud.data.MUUnitInfo;
import net.sourceforge.btthud.data.MUUnitWeapon;
import net.sourceforge.btthud.data.MUUnitAmmo;

import net.sourceforge.btthud.util.JTextPaneWriter;
import net.sourceforge.btthud.util.BulkStyledDocument;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

import java.util.*;
import java.text.*;

/**
 * Implements a status report window that displays heading, speed, heat, and
 * weapon information very similar to the MUX's 'status'.
 * @author tkrajcar
 */
public class MUStatusComponent extends JScrollPane {
	private MUPrefs prefs;

	private final JTextPane statusPane;
	private final JTextPaneWriter statusPaneWriter;

	private Font mFont;

	private SimpleAttributeSet conRegular, conIrregular;
	private final ArrayList<ElementSpec> elements = new ArrayList<ElementSpec> ();

	public MUStatusComponent (final MUPrefs prefs) {
		super (VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

		// Setup our new status pane
		statusPane = new JTextPane ();
		statusPane.setBackground(Color.black);
		statusPane.setEditable(false);

		statusPaneWriter = new JTextPaneWriter (statusPane);

		newPreferences(prefs);

		statusPane.setStyledDocument(new BulkStyledDocument (100, mFont));

		setViewportView(statusPane);
	}

	private void initAttributeSets () {
		conRegular = new SimpleAttributeSet ();
		StyleConstants.setFontFamily(conRegular, prefs.mainFont);
		StyleConstants.setFontSize(conRegular, prefs.statusFontSize);
		StyleConstants.setForeground(conRegular, Color.white);

		conIrregular = new SimpleAttributeSet ();
		StyleConstants.setFontFamily(conIrregular, prefs.mainFont);
		StyleConstants.setFontSize(conIrregular, prefs.statusFontSize);
		StyleConstants.setForeground(conIrregular, Color.white);
		StyleConstants.setBold(conIrregular, true);
	}

	public void newPreferences (final MUPrefs prefs) {
		this.prefs = prefs;
		mFont = new Font(prefs.mainFont, Font.PLAIN, prefs.statusFontSize);
		statusPane.setFont(mFont);
		initAttributeSets();
	}

	public void refresh (final MUData data) {
		if (!data.hudRunning)
			return;

		final MUMyInfo mydata = data.myUnit;

		elements.clear();

		String s;

		// Move/heat block
		s = mydata.leftJust(mydata.name, 14, true)
		    + "[" + mydata.id + "]"
		    + "  XYZ:" + mydata.rightJust(String.valueOf(mydata.getX()), 4, false) + ","
		    + mydata.rightJust(String.valueOf(mydata.getY()), 4, false) + ","
		    + mydata.rightJust(String.valueOf(mydata.getZ()), 4, false) + "  "
		    + "Heat Prod:  " + mydata.rightJust(String.valueOf(mydata.heat), 3, false) + " deg C.";
		addString(s, conRegular);
		addBlankLine();

		final NumberFormat speedFormatter = new DecimalFormat ("##0.0");
		s = "Speed:  " + mydata.rightJust(speedFormatter.format(mydata.speed), 6, false)
		    + " KPH  Heading:" + mydata.rightJust(String.valueOf(mydata.heading), 6, false)
		    + " deg  Heat Sinks: " + mydata.rightJust(String.valueOf(mydata.heatSinks), 3, false);
		addString(s, conRegular);
		addBlankLine();

		s = "Des.Spd:" + mydata.rightJust(speedFormatter.format(mydata.desiredSpeed), 6, false)
		    + " KPH  Des.Hdg:" + mydata.rightJust(String.valueOf(mydata.desiredHeading), 6, false)
		    + " deg  Heat Dissp: " + mydata.rightJust(String.valueOf(mydata.heatDissipation), 3, false) + " deg C.";
		addString(s, conRegular);
		addBlankLine();

		final StringBuilder sb = new StringBuilder ();

		if (mydata.maxVerticalSpeed != 0) {
			sb.append("Vrt Spd:" + mydata.rightJust(speedFormatter.format(mydata.verticalSpeed), 6, false) + " KPH  "
			          + "Des.VSp:" + mydata.rightJust(speedFormatter.format(mydata.desiredVerticalSpeed), 6, false) + " KPH  ");
		}

		if (mydata.maxFuel != 0) {
			sb.append("Fuel: " + mydata.rightJust(String.valueOf(mydata.fuel), 4, false) + "/" + mydata.rightJust(String.valueOf(mydata.maxFuel), 4, false)
		                  + " (" + speedFormatter.format(mydata.percentFuelLeft()) + "%)");
		}

		if (sb.length() > 0) {
			addString(sb.toString(), conRegular);
			sb.setLength(0);
		}
		addBlankLine();

		if (mydata.canHaveTurret()) {
			s = "Turret Hdg: " + mydata.rightJust(String.valueOf((mydata.turretHeading + mydata.heading + 180) % 360), 5, false) + " deg";
			addString(s, conRegular);
			addBlankLine();
		}

		// Add heat scale.
		// TODO: This would work better if HEAT_LEVEL_NONE were -17.
		int minHeat = mydata.heatDissipation / 10;
		int barHeat = mydata.heat / 10 - minHeat;

		if (minHeat > MUConstants.HEAT_LEVEL_NONE) {
			addString("Temp:<", conRegular);

			addHeatBar(MUColors.hx,
			           MUConstants.HEAT_LEVEL_LGREEN
			           - MUConstants.HEAT_LEVEL_NONE,
			           MUConstants.HEAT_LEVEL_LGREEN,
			           barHeat); // Black portion
		} else {
			addString("Temp: ", conRegular);

			addHeatBar(MUColors.hx,
			           MUConstants.HEAT_LEVEL_LGREEN
			           - minHeat,
			           MUConstants.HEAT_LEVEL_LGREEN,
			           barHeat); // Black portion
		}

		StyleConstants.setForeground(conIrregular, MUColors.hg); // Divider
		addString("|", conIrregular);

		addHeatBar(MUColors.g,
		           MUConstants.HEAT_LEVEL_LGREEN,
		           MUConstants.HEAT_LEVEL_BGREEN,
		           barHeat); // Green portion

		addHeatBar(MUColors.hg,
		           MUConstants.HEAT_LEVEL_BGREEN,
		           MUConstants.HEAT_LEVEL_LYELLOW,
		           barHeat); // Bright green portion

		StyleConstants.setForeground(conIrregular, MUColors.hy); // Divider
		addString("|", conIrregular);

		addHeatBar(MUColors.y,
		           MUConstants.HEAT_LEVEL_LYELLOW,
		           MUConstants.HEAT_LEVEL_BYELLOW,
		           barHeat); // Yellow portion

		addHeatBar(MUColors.hy,
		           MUConstants.HEAT_LEVEL_BYELLOW,
		           MUConstants.HEAT_LEVEL_LRED,
		           barHeat); // Bright yellow portion

		StyleConstants.setForeground(conIrregular, MUColors.hr); // Divider
		addString("|", conIrregular);

		addHeatBar(MUColors.r,
		           MUConstants.HEAT_LEVEL_LRED,
		           MUConstants.HEAT_LEVEL_BRED,
		           barHeat); // Red portion

		addHeatBar(MUColors.hr,
		           MUConstants.HEAT_LEVEL_BRED,
		           MUConstants.HEAT_LEVEL_TOP,
		           barHeat); // Bright red portion

		StyleConstants.setForeground(conIrregular, MUColors.h); // Divider
		addString("|", conIrregular);

		addBlankLine();

		// Status flags.
		if (mydata.status.length() > 0 && !mydata.status.equals("-")) {
			for (final char sc: mydata.status.toCharArray()) { // loop through mydata.status
				addString(getFlagName(sc));
				addString("  ", conRegular);
			}
		} else {
			// Using addBlankLine() here won't work because it
			// won't render two starttag-endtag pairs in a row - it
			// eats one
			addString("\n",conRegular);
		}

		addBlankLine();

		// Weapon list.
		addString("------- Weapon ------- [##] Loc - Status || --- Ammo Type --- Rds", conRegular);
		addBlankLine();

		final MUUnitWeapon[] weapons = mydata.unitWeapons;
		final MUUnitAmmo[] ammo = mydata.unitAmmo;

		// TODO: Find out if we're doing something special with
		// weapons.length to ensure that if we have more ammo than
		// weapons, that we handle that case.
		int weaponLines = (ammo.length > weapons.length)
		                  ? ammo.length : weapons.length;

		for (int ii = 0; ii < weaponLines; ii++) {
			// FIXME: Apparently, we just allocate a bunch of slots
			// and hope it's enough.
			if (weapons[ii] == null && ammo[ii] == null)
				break;

			// Weapons column.
			if (ii < weapons.length && weapons[ii] != null) {
				final MUUnitWeapon weapon = weapons[ii];
				final MUWeapon weapontype = MUUnitInfo.getWeapon(weapon.typeNumber);

				String weapname = weapontype.name;

				weapname = weapname.replaceAll("IS\\.","");
				weapname = weapname.replaceAll("Clan\\.","");
				weapname = weapname.replaceAll("CL\\.","");

				sb.setLength(0);

				sb.append(" " + mydata.leftJust(weapname, 19, true));

				if (weapon.fireMode.equals("-")) {
					sb.append(' ');
				} else {
					sb.append(weapon.fireMode);
				}

				if (weapon.ammoType.equals("-")) {
					sb.append(" ");
				} else {
					sb.append(weapon.ammoType);
				}

				sb.append(" [" + mydata.rightJust(String.valueOf(weapon.number),2,false) + "] "
				          + mydata.rightJust(weapon.loc, 3, false) + "   ");

				addString(sb.toString(), conRegular);

				// Weapon status.
				addString(getWeaponStatusString(weapon.status));
			} else {
				// Padding for # ammo > # weapons.
				addString("                                        ", conRegular);
			}

			addString(" || ", conRegular);

			// Ammo column.
			if (ii < ammo.length && ammo[ii] != null) {
				final MUUnitAmmo thisAmmo = ammo[ii];
				final MUWeapon thisWeapon = MUUnitInfo.getWeapon(thisAmmo.weaponTypeNumber);

				String weapname = thisWeapon.name;

				weapname = weapname.replaceAll("IS\\.","");
				weapname = weapname.replaceAll("Clan\\.","");
				weapname = weapname.replaceAll("CL\\.","");

				String mode = thisAmmo.ammoMode;

				if (mode.equals("-"))
					mode = " ";

				s = " " + mydata.leftJust(weapname, 15, false)
				    + mode + " ";
				addString(s, conRegular);

				s = mydata.rightJust(String.valueOf(thisAmmo.roundsRemaining), 3, false);
				StyleConstants.setForeground(conIrregular, MUUnitInfo.colorForPercent(mydata.percentAmmoLeft(thisAmmo)));
				addString(s, conIrregular);
			}

			// End of column.
			addBlankLine();
		}

		final BulkStyledDocument doc = (BulkStyledDocument)statusPane.getDocument();

		statusPaneWriter.reset();
		doc.insertParsedString(elements.toArray(new ElementSpec[0]));
	}

	/**
	 * Adds a blank line to ArrayList elements and sends it back. Used to
	 * eliminate code duplication
	 *
	 * @param elements ArrayList to append blank line elements to
	 */
	private void addBlankLine () {
		elements.add(new ElementSpec (conRegular, ElementSpec.EndTagType));
		elements.add(new ElementSpec (conRegular, ElementSpec.StartTagType));
	}

	/**
	 * Adds a given line to ArrayList elements and sends it back. Used to
	 * eliminate code duplication
	 *
	 * @param elements ArrayList to append blank line elements to
	 * @param s String of text to append
	 * @param attrs Attributes to use when adding string
	 */
	private void addString (String s, MutableAttributeSet attrs) {
		elements.add(new ElementSpec (new SimpleAttributeSet (attrs),
		                              ElementSpec.ContentType,
		                              s.toCharArray(), 0, s.length()));
	}

	// Draw heat bars for heat values from [min,max).
	// TODO: This API could use some improvement.  For example, the color
	// and bar min/max are not independent parameters.
	private void addHeatBar (final Color color,
	                         final int min, final int max,
	                         final int heat) {
		final StringBuilder sb = new StringBuilder ();

		// FIXME: Using for loops to append() is stupid when we can
		// just append 'c'xN characters, isn't it?
		for (int ii = min; ii < max; ii++) {
			sb.append((ii < heat) ? ':' : '.');
		}

		StyleConstants.setForeground(conIrregular, color);
		addString(sb.toString(), conIrregular);
	}

	// Expand status flag characters to (styled) names.
	static private class ColorString {
		private final Color color;
		private final String string;

		private ColorString (final Color color, final String string) {
			this.color = color;
			this.string = string;
		}
	}

	private void addString (ColorString cs) {
		if (cs.color != null) {
			StyleConstants.setForeground(conIrregular, cs.color);
			addString(cs.string, conIrregular);
		} else {
			addString(cs.string, conRegular);
		}
	}

	static private ColorString getFlagName (final char fc) {
		Color color = null;
		String name = null;

		switch (fc) {
		case 'B':
			color = MUColors.hr;
			name = "BURNING";
			break;

		case 'C':
			name = "CARRYING CLUB";
			break;

		case 'D':
			name = "DUG IN";
			break;

		case 'e':
			color = MUColors.hy;
			name = "AFFECTED BY ECM";
			break;

		case 'E':
			color = MUColors.hy;
			name = "EMITTING ECM";
			break;

		case 'f':
			color = MUColors.hy;
			name = "STANDING UP";
			break;

		case 'F':
			color = MUColors.hr;
			name = "FALLEN";
			break;

		case 'h':
			color = MUColors.hy;
			name = "GOING HULL DOWN";
			break;

		case 'H':
			color = MUColors.hy;
			name = "HULL DOWN";
			break;

		case 'I':
			color = MUColors.hr;
			name = "ON FIRE";
			break;

		case 'J':
			color = MUColors.hy;
			name = "JUMPING";
			break;

		case 'l':
			color = MUColors.hy;
			name = "ILLUMINATED";
			break;

		case 'L':
			color = MUColors.hy;
			name = "ILLUMINATING";
			break;

		case 'M':
			color = MUColors.hy;
			name = "SPRINTING";
			break;

		case 'm':
			color = MUColors.hy;
			name = "EVADING";
			break;

		case 'n':
			color = MUColors.hy;
			name = "ENEMY NARC ATTACHED";
			break;

		case 'N':
			color = MUColors.hy;
			name = "FRIENDLY NARC ATTACHED";
			break;

		case '+':
			color = MUColors.hy;
			name = "OVERHEATING";
			break;

		case 'O':
			color = MUColors.hy;
			name = "ORBITAL DROPPING";
			break;

		case 'p':
			color = MUColors.hy;
			name = "PROTECTED BY ECM";
			break;

		case 'P':
			color = MUColors.hy;	
			name = "PROTECTED BY ECCM";
			break;

		case 's':
			color = MUColors.hy;
			name = "STARTING UP";
			break;

		case 'S':
			color = MUColors.hr;
			name = "SHUTDOWN";
			break;

		case 'T':
			color = MUColors.hy;
			name = "BEING TOWED";
			break;

		case 't':
			color = MUColors.hy;
			name = "TOWING";
			break;

		case 'W':
			color = MUColors.hy;
			name = "SWARMING";
			break;

		case 'X':
			color = MUColors.hy;
			name = "SPINNING";
			break;

		default:
			name = "???";
			break;
		}

		return new ColorString (color, name);
	}

	static private ColorString getWeaponStatusString (final String ws) {
		Color color = null;
		String string = null;

		// TODO: A HashMap would be more efficient.
		if (ws.equals("R")) {
			color = MUColors.g;
			string = " Ready";
		} else if (ws.equals("*")) {
			color = MUColors.hx;
			string = " *****";
		} else if (ws.equals("A") || ws.equals("a") || ws.equals("J")) {
			color = MUColors.r;
			string = "JAMMED";
		} else if (ws.equals("D")) {
			color = MUColors.hx;
			string = "DISBLD";
		} else if (ws.equals("S")) {
			color = MUColors.r;
			string = "SHORTD";
		} else {
			// FIXME: rightJust() shouldn't be in MUUnitInfo.
			string = MUUnitInfo.rightJust(ws, 6, false);
		}

		return new ColorString (color, string);
	}
}
