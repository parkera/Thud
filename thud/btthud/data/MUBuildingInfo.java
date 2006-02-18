//
//  MUBuildingInfo.java
//  Thud
//
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.data;

import java.util.*;

// See hudinfo.txt for detailed info on what MUBuildingInfo must have

public class MUBuildingInfo extends MUUnitInfo {

    public int			cf = 1;
    public int			maxCf = 1;

    /**
     * Creates a human-readable contact string from a building.
     * 
     * @return		the human-readable string
     */
    public String makeContactString()
    {
        /* Example:
          * Laser Battery Station   x: 73 y: 50 z: 4 r:125.9 b:326 CF:1000 /1000 S:X
        */
        StringBuffer	sb = new StringBuffer();

        sb.append(' '); sb.append(' ');
        sb.append(arc);
        sb.append(' ');
        sb.append(leftJust(name, 23, true));
        sb.append(' ');

        sb.append('x'); sb.append(':');
        sb.append(rightJust(String.valueOf(x), 3, false));
        sb.append(' ');
        sb.append('y'); sb.append(':');
        sb.append(rightJust(String.valueOf(y), 3, false));
        sb.append(' ');
        sb.append('z'); sb.append(':');
        sb.append(rightJust(String.valueOf(z), 3, false));
        sb.append(' ');

        sb.append('r'); sb.append(':');
        sb.append(rightJust(String.valueOf(range), 4, true));
        sb.append(' ');
        sb.append('b'); sb.append(':');
        sb.append(rightJust(String.valueOf(bearing), 3, false));
        sb.append(' ');

        sb.append('C'); sb.append('F'); sb.append(':');
        sb.append(rightJust(String.valueOf(cf), 4, false));
        sb.append(' '); sb.append('/');
        sb.append(rightJust(String.valueOf(maxCf), 4, false));
        sb.append(' ');
        sb.append('S'); sb.append(':');
        sb.append(status);

        return sb.toString();
    }    
}