//
//  StyleRunHolder.java
//  Thud
//
//  Created by Anthony Parker on Sun Dec 30 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
package btthud.util;

import javax.swing.text.*;

public class StyleRunHolder {

    public MutableAttributeSet		attr;
    public int						start;
    public int						length;
    
    public StyleRunHolder()
    {
        attr = new SimpleAttributeSet();
        start = 0;
        length = 0;
    }

    public StyleRunHolder(MutableAttributeSet attr, int start, int length)
    {
        this.attr = attr;
        this.start = start;
        this.length = length;
    }
}
