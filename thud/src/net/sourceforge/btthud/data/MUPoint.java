//
// MUPoint.java
// Thud
//
// Copyright (c) 2001-2007 Anthony Parker & the THUD team.
// All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.geom.Point2D;

/**
 * Represents a point on the map grid.  These values are unscaled; multiply the
 * floating point coordinates by MUConstants.SCALEMAP if you want scaled
 * values.  Also note that the "hex_z" value is in units of levels, not hexes
 * (MUConstants.HEXLEVEL levels = 1 hex unit).
 */
public class MUPoint extends Point2D {
	/*
	 * Some private constants.
	 */

	private enum HexRegion {
		I, II, III, IV, V
	}


	/*
	 * Coordinate values.  These shouldn't be publicly accessible, since
	 * the hex_*, hexf_*, and f_* values depend on each other.
	 */

	protected int hex_x;
	protected int hex_y;
	protected int hex_z;

	protected float hexf_x;
	protected float hexf_y;
	protected float hexf_z;

	protected float f_x;
	protected float f_y;
	protected float f_z;

	/*
	 * Constructors galore.
	 */

	public MUPoint () {
		hex_x = 0;
		hex_y = 0;
		hex_z = 0;

		hexf_x = 2.0f * MUConstants.ALPHA;
		hexf_y = 0.5f;
		hexf_z = 0.0f;

		f_x = hexf_x;
		f_y = hexf_y;
		f_z = hexf_z;
	}

	public MUPoint (final MUPoint p) {
		setLocation(p);
	}

	public MUPoint (final Point2D pt) {
		setLocation(pt);
	}

	public MUPoint (final int hex_x, final int hex_y) {
		this(hex_x, hex_y, 0);
	}

	public MUPoint (final int hex_x, final int hex_y, final int hex_z) {
		setHexLocation(hex_x, hex_y, hex_z);
	}

	public MUPoint (final float f_x, final float f_y) {
		this(f_x, f_y, 0);
	}

	public MUPoint (final float f_x, final float f_y, final float f_z) {
		setLocation(f_x, f_y, f_z);
	}

	public MUPoint (final int hex_x, final int hex_y,
	                final float rtc, final int btc) {
		this(hex_x, hex_y, 0, rtc, btc);
	}

	public MUPoint (final int hex_x, final int hex_y, final int hex_z,
	                final float rtc, final int btc) {
		setFromCenterLocation(hex_x, hex_y, hex_z, rtc, btc);
	}


	/*
	 * MUPoint-specific methods.
	 */

	public int getHexX () {
		return hex_x;
	}

	public int getHexY () {
		return hex_y;
	}

	public int getHexZ () {
		return hex_z;
	}


	public float getCenterFX() {
		return hexf_x;
	}

	public float getCenterFY() {
		return hexf_y;
	}

	public float getCenterFZ() {
		return hexf_z;
	}


	public float getFX () {
		return f_x;
	}

	public float getFY () {
		return f_y;
	}

	public float getFZ () {
		return f_z;
	}


	public void setHexLocation (final int x, final int y) {
		setHexLocation(x, y, 0);
	}

	public void setHexLocation (final int x, final int y, final int z) {
		hex_x = x;
		hex_y = y;
		hex_z = z;

		setHexCenter();

		f_x = hexf_x;
		f_y = hexf_y;
		f_z = hexf_z;
	}


	public void setFromCenterLocation (final int x, final int y,
	                                   final float rtc, final int btc) {
		setFromCenterLocation(x, y, 0, rtc, btc);
	}

	public void setFromCenterLocation (final int x, final int y, final int z,
	                                   final float rtc, final int btc) {
		// We should never be given values that cause us to leave the
		// hex, so we can just go ahead and set our location now.
		setHexLocation(x, y, z);

		// TODO: We should probably assert/check that the above is
		// true.  Even if we don't, though, the worst that will happen
		// is that the hex coords won't match the (correct) floating
		// point coords.

		// Compute and apply the offset vector.
		final float dirX = (float)Math.sin(Math.toRadians(btc));
		final float dirY = (float)-Math.cos(Math.toRadians(btc));

		f_x -= rtc * dirX;
		f_y -= rtc * dirY;
	}


	public void setLocation (final float x, final float y, final float z) {
		f_x = x;
		f_y = y;
		f_z = z;

		// Set hex_* values from f_* values.  This is essentially
		// RealCoordToMapCoord() from hcode/btech/mech.utils.c.
		//
		// The following diagram comes from there, modified slightly:
		//
		// x  0a  1a  2a  3a  4a  5a
		// y  ________________________
		// 0 |   /        \
		//   |IV/          \    III
		//   | /            \
		//   |/              \________
		// 1 |\       I      /
		//   | \            /   II
		//   |V \          /
		//   |   \        /
		//
		// The original diagram is shifted to combine the two regions
		// on the left edge into one.  While we do have more regions
		// than the original algorithm, the number of operations is the
		// same.  Meanwhile, we avoid having to pre-shift the
		// horizontal hex coordinate.
		//
		// Another change in the algorithm is that we're more
		// consistent about which edges are part of which hexes.  The
		// topmost, leftmost edges are considered part of a hex, while
		// the other three lowermost, rightmost edges are considered
		// part of neighboring hexes:
		//  __
		// /
		// \
		//
		// We also don't special case hexes off the left edge of the
		// map; like any other hex, user code is expected to check that
		// these coordinates are out of bounds.  They might even be
		// useful for some applications.

		// Guess hex coordinates from "repeatable box".
		hex_x = (int)Math.floor(f_x / (MUConstants.ALPHA * 6.f)) * 2;
		hex_y = (int)Math.floor(f_y);

		// Compute "repeatable box" offsets.
		final float off_x = f_x / MUConstants.ALPHA - 3 * hex_x;
		final float off_y = f_y - hex_y;

		// Determine region.
		HexRegion region;

		switch ((int)off_x) {
		case -1:
			// This should almost never happen?
			System.out.println("Warning: off_x < 0: " + off_x);
		case 0:
			// [0,1): Region I, IV, or V.
			if (off_y < 0.5f) {
				// Region I or IV.
				if (off_x < 2f * (0.5f - off_y)) {
					region = HexRegion.IV;
				} else {
					region = HexRegion.I;
				}
			} else {
				// Region I or V.
				if (off_x < 2f * (off_y - 0.5f)) {
					region = HexRegion.V;
				} else {
					region = HexRegion.I;
				}
			}
			break;

		case 1:
		case 2:
			// [1,3): Region I.
			region = HexRegion.I;
			break;

		case 3:
			// [3,4): Region I, II, or III.
			if (off_y < 0.5f) {
				// Region I or III.
				if ((off_x - 3f) < 2f * (off_y - 0.0f)) {
					region = HexRegion.I;
				} else {
					region = HexRegion.III;
				}
			} else {
				// Region I or II.
				if ((off_x - 3f) < 2f * (1.0f - off_y)) {
					region = HexRegion.I;
				} else {
					region = HexRegion.II;
				}
			}
			break;

		case 6:
			// This should almost never happen?
			System.out.println("Warning: off_x >= 6: " + off_x);
		case 4:
		case 5:
			// [4,6): Region II or III.
			if (off_y < 0.5f) {
				region = HexRegion.III;
			} else {
				region = HexRegion.II;
			}
			break;

		default:
			// This should never happen.  Really.
			throw new Error ("Bad off_x computation: " + off_x);
		}

		// Adjust hex coordinates.
		switch (region) {
		case I: // 0 0
			break;

		case II: // + +
			hex_x++;
			hex_y++;
			break;

		case III: // + 0
			hex_x++;
			break;

		case IV: // - 0
			hex_x--;
			break;

		case V: // - +
			hex_x--;
			hex_y++;
			break;
		}

		// Update hexf_* values.
		setHexCenter();
	}


	/*
	 * Internal routines.
	 */

	private void setHexCenter () {
		// Set hexf_* values from hex_* values.  This is essentially
		// MapCoordToRealCoord() from hcode/btech/mech.utils.c.

		// TODO: Can use some integer math if we're careful about
		// overflow.
		hexf_x = (2.f + 3.f * (float)hex_x) * MUConstants.ALPHA;
		hexf_y = (((hex_x & 0x1) == 0) ? 0.5f : 0.f) + (float)hex_y;
		hexf_z = (float)hex_z / (float)MUConstants.HEXLEVEL;
	}


	/*
	 * Required to implement Point2D.
	 */

	public double getX () {
		return f_x;
	}

	public double getY () {
		return f_y;
	}

	public void setLocation (double x, double y) {
		setLocation(x, y, 0.);
	}


	/*
	 * Override for correct behavior.
	 */

	// Bit of a dilemma here, since setting the Z coordinate will create
	// the potential for 2D points that aren't equal, yet equal.  You may
	// want to create a "real" Point2D from this object if this is
	// important to you.
	//
	// Also, there are a lot of ways to define equal (by value) MUPoints.
	// This one uses the strictest, where all the floating point
	// coordinates must match.
	public boolean equals (final Object obj) {
		if (obj instanceof MUPoint) {
			if (getZ() != ((MUPoint)obj).getZ())
				return false;
		}

		return super.equals(obj);
	}

	// A better hash code would probably distribute values more evenly, and
	// take into account the floating point values.
	public int hashCode () {
		return hex_z + super.hashCode();
	}

	public Object clone () {
		MUPoint cloned_pt = (MUPoint)super.clone();
		cloned_pt.setLocation(this);
		return cloned_pt;
	}


	/*
	 * Logical 3D extensions of various Point2D methods.
	 */

	public double getZ () {
		return f_z;
	}

	public void setLocation (double x, double y, double z) {
		setLocation((float)x, (float)y, (float)z);
	}

	public void setLocation (MUPoint p) {
		hex_x = p.getHexX();
		hex_y = p.getHexY();
		hex_z = p.getHexZ();

		hexf_x = p.getCenterFX();
		hexf_y = p.getCenterFY();
		hexf_z = p.getCenterFZ();

		f_x = p.getFX();
		f_y = p.getFY();
		f_z = p.getFZ();
	}


	public double distance (double PX, double PY, double PZ) {
		return Math.sqrt(distanceSq(PX, PY, PZ));
	}

	public static double distance (double X1, double Y1, double Z1,
	                        double X2, double Y2, double Z2) {
		return Math.sqrt(distanceSq(X1, Y1, Z1, X2, Y2, Z2));
	}

	public double distance (final MUPoint pt) {
		return Math.sqrt(distanceSq(pt));
	}


	public double distanceSq (double PX, double PY, double PZ) {
		return distanceSq(getX(), getY(), getZ(), PX, PY, PZ);
	}

	public static double distanceSq (double X1, double Y1, double Z1,
	                          double X2, double Y2, double Z2) {
		return X1 * X2 + Y1 * Y2 + Z1 * Z2;
	}

	public double distanceSq (final MUPoint pt) {
		return pt.distanceSq(getX(), getY(), getZ());
	}


	// For debugging.
	public String toString () {
		return "(" + getHexX() + ", " + getHexY() + ", " + getHexZ() + ")+("
		       + (getFX() - getCenterFX()) + ", " + (getFY() - getCenterFY()) + ", " + (getFZ() - getCenterFZ()) + ")";
	}
}
