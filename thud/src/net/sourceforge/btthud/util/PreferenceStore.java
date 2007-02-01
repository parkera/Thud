//
//  PreferenceStore.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 22 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.util;

import net.sourceforge.btthud.data.MUPrefs;
import net.sourceforge.btthud.data.MUHost;
import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class PreferenceStore
{
    /**
     * Load preferences.
     */
    static public void load (MUPrefs prefs) {
        final Preferences store = getPrefStore();

        for (Field field: MUPrefs.class.getFields()) {
            final TypeHandler handler = typeHandlerMap.get(field.getType());

            if (handler == null) {
                System.err.println("Load error: Preference '" + field.getName() + "' is of unsupported type '" + field.getType() + "'.");
                continue;
            }

            handler.load(store, prefs, field);
        }
    }

    /**
     * Save preferences.
     */
    static public void save (MUPrefs prefs) {
        final Preferences store = getPrefStore();

        for (Field field: MUPrefs.class.getFields()) {
            final TypeHandler handler = typeHandlerMap.get(field.getType());

            if (handler == null) {
                System.err.println("Save error: Preference '" + field.getName() + "' is of unsupported type '" + field.getType() + "'.");
                continue;
            }

            handler.save(store, prefs, field);
        }
    }


    // TODO: If we use preference listeners, we can do fancy things like have
    // multiple running copies of Thud not clobber each other's preferences.

    static private Preferences getPrefStore () {
        // Initialize the type handler map the first time it's needed.
        if (typeHandlerMap == null) {
            typeHandlerMap = new java.util.HashMap<Type, TypeHandler> ();
            registerTypeHandlers();
        }

        // Because we don't want net/sourceforge/btthud/data, or ui, or etc.
        return Preferences.userRoot().node("net/sourceforge/btthud");
    }


    static private Map<Type, TypeHandler> typeHandlerMap = null;

    static private void registerTypeHandlers() {
        // boolean
        typeHandlerMap.put(boolean.class, new BooleanTypeHandler ());

        // int
        typeHandlerMap.put(int.class, new IntegerTypeHandler ());

        // float
        typeHandlerMap.put(float.class, new FloatTypeHandler ());

        // double
        typeHandlerMap.put(double.class, new DoubleTypeHandler ());

        // String
        typeHandlerMap.put(String.class, new StringTypeHandler ());

        // Point
        typeHandlerMap.put(Point.class, new PointTypeHandler ());

        // Color
        typeHandlerMap.put(Color.class, new ColorTypeHandler ());

        // Color[]
        typeHandlerMap.put(Color[].class, new ColorArrayTypeHandler ());

        // ArrayList<MUHost>
        typeHandlerMap.put(ArrayList.class, new HostListTypeHandler ());
    }
}

/**
 * Base class for TypeHandlers.  Each type handler needs to provide machinery
 * to convert values to and from strings.  More complicated types may require
 * more elaborate storage.
 */
abstract class TypeHandler {
    /**
     * Load MUPrefs field from preferences backing store.
     */
    abstract public void load (final Preferences store, final MUPrefs prefs, final Field field);

    /**
     * Store MUPrefs field to preferences backing store.
     */
    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final Object objValue = getPrefField(prefs, field);

        if (objValue == null)
            return;

        savePref(store, field, objValue.toString());
    }


    /**
     * Utility method for getting preference field.
     */
    static protected Object getPrefField (final MUPrefs prefs, final Field field) {
        try {
            return field.get(prefs);
        } catch (IllegalAccessException e) {
            System.err.print("Save error: Preference '" + field.getName() + "' inaccessible.");
            return null;
        }
    }

    /**
     * Utility method for setting preference field.
     */
    static protected void setPrefField (final MUPrefs prefs, final Field field, final Object value) {
        try {
            field.set(prefs, value);
        } catch (IllegalAccessException e) {
            System.err.print("Load error: Preference '" + field.getName() + "' inaccessible.");
        }
    }

    /**
     * Utility method for getting preference as a string.  Returns null if
     * the preference does not exist. (Use the default value.)
     */
    static protected String loadPref (final Preferences store, final Field field) {
        return store.get(field.getName(), null);
    }

    /**
     * Utility method for setting preference as a string.
     */
    static protected void savePref (final Preferences store, final Field field, final String value) {
        store.put(field.getName(), value);
    }
}

/**
 * Sequence-type preference reader.  Sequences are stored as
 * baseName/subName.&lt;index&gt;, where &lt;index&gt; ranges consecutively
 * from 0 to N - 1 (with N being the number of elements in the list).
 *
 * Note that since the sequence is stored in a subnode, names should be chosen
 * as not to conflict with future package names that may also want to use the
 * preference store.  It's unlikely this will be a problem for Thud.
 *
 * The constructor may throw a BackingStoreException if it can't communicate
 * with the backing store, or an IllegalStateException if the sequence node
 * doesn't exist.
 */
class SequenceLoader {
    private final Preferences store;

    private final int size;

    public SequenceLoader (final Preferences store, final String baseName) throws BackingStoreException {
        if (!store.nodeExists(baseName)) {
            throw new IllegalStateException (baseName + " doesn't exist");
        }

        this.store = store.node(baseName);

        // The "size" of this sequence is the maximum consecutive index of any
        // of the subnames.  In other words, if we sort the keys, we should be
        // able to count consecutively from 0 to <size> for at least one
        // subname.  Note that some subnames may not be valid for the full
        // range; this eases compatibility.
        final Map<String,SortedSet<Integer>> maxIndex = new java.util.HashMap<String,SortedSet<Integer>> ();

        for (final String key: this.store.keys()) {
            // Explode key.
            final String[] keySplit = key.split("\\.", 2);

            if (keySplit.length != 2) {
                // Not an index key, ignore.
                System.err.println("Load warning: Preference '" + baseName + "[]': Unindexed element '" + key + "'.");
                continue;
            }

            int tmpIndex;

            try {
                tmpIndex = Integer.parseInt(keySplit[0]);
            } catch (NumberFormatException e) {
                // Bad index.
                System.err.println("Load warning: Preference '" + baseName + "[]': Unindexed element '" + key + "'.");
                continue;
            }

            // Tally index.
            SortedSet<Integer> indexSet = maxIndex.get(keySplit[1]);
            if (indexSet == null) {
                indexSet = new java.util.TreeSet<Integer> ();
                maxIndex.put(keySplit[1], indexSet);
            }

            indexSet.add(tmpIndex);
        }

        // Compute size.
        int tmpSize = 0;

        for (final SortedSet<Integer> indexSet: maxIndex.values()) {
            int lastIndex = -1;

            for (int index: indexSet) {
                if (index != lastIndex + 1)
                    break;

                lastIndex = index;
            }

            if (tmpSize <= lastIndex)
                tmpSize = lastIndex + 1;
        }

        size = tmpSize;
    }

    public int size () {
        return size;
    }

    public List<String> loadSequence (final String subName) {
        final List<String> valueList = new java.util.LinkedList<String> ();

        for (int ii = 0; ii < size(); ii++) {
            final String value = store.get(ii + "." + subName, null);

            if (value == null)
                break;

            valueList.add(value);
        }

        return valueList;
    }
}

/**
 * Sequence-type preference writer.  Sequences are stored as
 * baseName/subName.&lt;index&gt;, where &lt;index&gt; ranges consecutively
 * from 0 to N - 1 (with N being the number of elements in the list).
 *
 * Note that since the sequence is stored in a subnode, names should be chosen
 * as not to conflict with future package names that may also want to use the
 * preference store.  It's unlikely this will be a problem for Thud.
 *
 * The constructor may throw a BackingStoreException if it can't communicate
 * with the backing store.
 *
 * The constructor clears the entire contents of the associated preference
 * node.
 */
class SequenceStorer {
    private final Preferences store;

    public SequenceStorer (final Preferences store, final String baseName) throws BackingStoreException {
        this.store = store.node(baseName);

        // TODO: Instead of clearing all preferences, we might just want to
        // clear those keys which match the <index>.subkey template.
        this.store.clear();
    }

    public void saveSequence (final String subName, final List<String> valueList) {
        int ii = 0;
        for (String value: valueList) {
            assert value != null;
            store.put(ii++ + "." + subName, value);
        }
    }
}


/**
 * Boolean type handler.
 */
class BooleanTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String strValue = loadPref(store, field);

        if (strValue == null)
            return;

        setPrefField(prefs, field, new Boolean (strValue));
    }
}

/**
 * Integer type handler.
 */
class IntegerTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String strValue = loadPref(store, field);

        if (strValue == null)
            return;

        setPrefField(prefs, field, new Integer (strValue));
    }
}

/**
 * Float type handler.
 */
class FloatTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String strValue = loadPref(store, field);

        if (strValue == null)
            return;

        setPrefField(prefs, field, new Float (strValue));
    }
}

/**
 * Double type handler.
 */
class DoubleTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String strValue = loadPref(store, field);

        if (strValue == null)
            return;

        setPrefField(prefs, field, new Double (strValue));
    }
}

/**
 * String type handler.
 */
class StringTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String strValue = loadPref(store, field);

        if (strValue == null)
            return;

        setPrefField(prefs, field, strValue);
    }
}

/**
 * Point compound type handler.
 */
class PointTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String xStrValue = store.get(field.getName() + ".x", null);
        final String yStrValue = store.get(field.getName() + ".y", null);

        if (xStrValue == null || yStrValue == null)
            return;

        final int xValue = Integer.valueOf(xStrValue);
        final int yValue = Integer.valueOf(yStrValue);

        setPrefField(prefs, field, new Point (xValue, yValue));
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final Point value = (Point)getPrefField(prefs, field);

        if (value == null)
            return;

        store.put(field.getName() + ".x", Integer.toString(value.x));
        store.put(field.getName() + ".y", Integer.toString(value.y));
    }
}

/**
 * Color compound type handler.  This class shows a bit of the problem with the
 * whole approach of creating our own type handlers, as color has a complex
 * internal representation.  We could serialize out to the preferences store,
 * but we'll just assume we always use 8-bit sRGB color with alpha.
 *
 * Serializing also has the disadvantage that it makes it hard for humans to
 * edit the preferences.
 */
class ColorTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String rStrValue = store.get(field.getName() + ".r", null);
        final String gStrValue = store.get(field.getName() + ".g", null);
        final String bStrValue = store.get(field.getName() + ".b", null);
        final String aStrValue = store.get(field.getName() + ".a", null);

        if (rStrValue == null || gStrValue == null || bStrValue == null || aStrValue == null)
            return;

        final int rValue = Integer.valueOf(rStrValue);
        final int gValue = Integer.valueOf(gStrValue);
        final int bValue = Integer.valueOf(bStrValue);
        final int aValue = Integer.valueOf(aStrValue);

        setPrefField(prefs, field, new Color (rValue, gValue, bValue, aValue));
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final Color value = (Color)getPrefField(prefs, field);

        if (value == null)
            return;

        store.put(field.getName() + ".r", Integer.toString(value.getRed()));
        store.put(field.getName() + ".g", Integer.toString(value.getGreen()));
        store.put(field.getName() + ".b", Integer.toString(value.getBlue()));
        store.put(field.getName() + ".a", Integer.toString(value.getAlpha()));
    }
}

/**
 * Terrain colors Color[] array type handler.
 *
 * TODO: Make this more generic, or the type more specific.
 */
class ColorArrayTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        SequenceLoader colorLoader;
        
        try {
            colorLoader = new SequenceLoader (store, field.getName());
        } catch (BackingStoreException e) {
            // Can't get node keys.
            System.err.println("Load error: Preference '" + field.getName() + "[]': " + e);
            return;
        } catch (IllegalStateException e) {
            // Missing node.
            return;
        }

        final List<String> rList = colorLoader.loadSequence("r");
        final List<String> gList = colorLoader.loadSequence("g");
        final List<String> bList = colorLoader.loadSequence("b");
        final List<String> aList = colorLoader.loadSequence("a");
        
        if (rList.size() != gList.size() || rList.size() != bList.size()
            || rList.size() != aList.size()) {
            System.err.println("Load error: Preference '" + field.getName() + "[]' has mismatched elements.");
            return;
        }

        // FIXME: Generalize the array length check.
        // We assume we can upgrade if there are more terrain colors later.
        if (rList.size() > prefs.terrainColors.length) {
            System.err.println("Load error: Preference '" + field.getName() + "' has too many elements.");
            return;
        }

        // FIXME: Generalize copy.
        final Color[] colorArray = prefs.terrainColors.clone();

        final Iterator<String> rIter = rList.iterator();
        final Iterator<String> gIter = gList.iterator();
        final Iterator<String> bIter = bList.iterator();
        final Iterator<String> aIter = aList.iterator();

        for (int ii = 0; ii < rList.size(); ii++) {
            try {
                final int r = Integer.parseInt(rIter.next());
                final int g = Integer.parseInt(gIter.next());
                final int b = Integer.parseInt(bIter.next());
                final int a = Integer.parseInt(aIter.next());

                colorArray[ii] = new Color (r, g, b, a);
            } catch (NumberFormatException e) {
                System.err.println("Load error: Preference '" + field.getName() + "' has malformed element.");
                return;
            }
        }

        setPrefField(prefs, field, colorArray);
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final Color[] colorArray = (Color[])getPrefField(prefs, field);

        if (colorArray == null)
            return;

        SequenceStorer colorStorer;
        
        try {
            colorStorer = new SequenceStorer (store, field.getName());
        } catch (BackingStoreException e) {
            // Can't clear removed node.
            System.err.println("Save error: Preference '" + field.getName() + "[]': " + e);
            return;
        }

        final List<String> rList = new java.util.LinkedList<String> ();
        final List<String> gList = new java.util.LinkedList<String> ();
        final List<String> bList = new java.util.LinkedList<String> ();
        final List<String> aList = new java.util.LinkedList<String> ();

        for (Color color: colorArray) {
            rList.add(Integer.toString(color.getRed()));
            gList.add(Integer.toString(color.getGreen()));
            bList.add(Integer.toString(color.getBlue()));
            aList.add(Integer.toString(color.getAlpha()));
        }

        colorStorer.saveSequence("r", rList);
        colorStorer.saveSequence("g", gList);
        colorStorer.saveSequence("b", bList);
        colorStorer.saveSequence("a", aList);
    }
}

/**
 * ArrayList&lt;MUHost&gt; type handler.
 *
 * TODO: Make this more geneirc, or the type more specific.
 */
class HostListTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        SequenceLoader hostLoader;
        
        try {
            hostLoader = new SequenceLoader (store, field.getName());
        } catch (BackingStoreException e) {
            // Can't get node keys.
            System.err.println("Load error: Preference '" + field.getName() + "[]': " + e);
            return;
        } catch (IllegalStateException e) {
            // Missing node.
            return;
        }

        final List<String> hostList = hostLoader.loadSequence("host");
        final List<String> portList = hostLoader.loadSequence("port");

        if (hostList.size() != portList.size()) {
            System.err.println("Load error: Preference '" + field.getName() + "[]' has mismatched elements.");
            return;
        }

        final ArrayList<MUHost> hostArray = new ArrayList<MUHost> ();

        final Iterator<String> hostIter = hostList.iterator();
        final Iterator<String> portIter = portList.iterator();

        for (int ii = 0; ii < hostList.size(); ii++) {
            try {
                final String host = hostIter.next();
                final int port = Integer.parseInt(portIter.next());

                hostArray.add(new MUHost (host, port));
            } catch (NumberFormatException e) {
                System.err.println("Load error: Preference '" + field.getName() + "' has malformed element.");
                return;
            }
        }

        setPrefField(prefs, field, hostArray);
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final ArrayList<MUHost> hostArray = (ArrayList<MUHost>)getPrefField(prefs, field);

        if (hostArray == null)
            return;

        SequenceStorer hostStorer;
        
        try {
            hostStorer = new SequenceStorer (store, field.getName());
        } catch (BackingStoreException e) {
            // Can't clear removed node.
            System.err.println("Save error: Preference '" + field.getName() + "[]': " + e);
            return;
        }

        final List<String> hostList = new java.util.LinkedList<String> ();
        final List<String> portList = new java.util.LinkedList<String> ();

        for (MUHost host: hostArray) {
            hostList.add(host.getHost());
            portList.add(Integer.toString(host.getPort()));
        }

        hostStorer.saveSequence("host", hostList);
        hostStorer.saveSequence("port", portList);
    }
}
