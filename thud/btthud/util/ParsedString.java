//
//  ParsedString.java
//  Thud
//
//  Created by Anthony Parker on Sat Aug 03 2002.
//  Copyright (c) 2002 Anthony Parker. All rights reserved.
//
package btthud.util;

import java.util.*;

public class ParsedString {

    public String		l;
    public ArrayList	styles;

    public ParsedString(String l, ArrayList styles)
    {
        this.l = l;
        this.styles = styles;
    }
}