//
//  UndoableHexChange.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 18 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.data;

import java.util.*;

public class UndoableHexChange {

    LinkedList				changedHexes;

    public UndoableHexChange(LinkedList changedHexes)
    {
        this.changedHexes = changedHexes;
    }

    // ---------------

    public LinkedList changedHexes()
    {
        return changedHexes;
    }
}
