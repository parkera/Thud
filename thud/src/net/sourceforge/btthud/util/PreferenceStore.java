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

import java.util.Map;
import java.util.StringTokenizer;

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


    /**
     * Utility method for creating an array of StringTokenizers.
     *
     * TODO: Should make a class for this.
     */
    static StringTokenizer[] newTokenArray (final int width, final String[] stringArray) {
        return newTokenArray(width, stringArray, ' ');
    }

    static StringTokenizer[] newTokenArray (final int width, final String[] stringArray, final char tokenSep) {
        final String tokenSepStr = Character.toString(tokenSep);

        StringTokenizer[] tokenizerArray = new StringTokenizer[width];

        for (int ii = 0; ii < width; ii++) {
            tokenizerArray[ii] = new StringTokenizer (stringArray[ii], tokenSepStr);
        }

        // Quick check for malformed input.
        final int tokenCount = tokenizerArray[0].countTokens();

        for (int ii = 1; ii < width; ii++) {
            if (tokenizerArray[ii].countTokens() != tokenCount) {
                throw new IllegalArgumentException ("Element count mismatch");
            }
        }

        return tokenizerArray;
    }

    /**
     * Utility method for extracting tokens using an array of StringTokenizers.
     */
    static boolean extractTokenArray (final StringTokenizer[] tokenArray, final String[] outputArray) {
        if (tokenArray[0].hasMoreTokens()) {
            for (int ii = 0; ii < tokenArray.length; ii++) {
                outputArray[ii] = tokenArray[ii].nextToken();
            }

            return true;
        } else {
            return false;
        }
    }


    /**
     * Utility method for creating an array of StringBuilders.
     *
     * TODO: Should make a class for this.
     */
    static StringBuilder[] newStringArray (final int width) {
        StringBuilder[] builderArray = new StringBuilder[width];

        for (int ii = 0; ii < width; ii++) {
            builderArray[ii] = new StringBuilder ();
        }

        return builderArray;
    }

    /**
     * Utility method for appending to an array of StringBuilders.
     */
    static void appendStringArray (final StringBuilder[] builderArray, final String[] tokenArray) {
        appendStringArray(builderArray, tokenArray, ' ');
    }

    static void appendStringArray (final StringBuilder[] builderArray, final String[] tokenArray, char tokenSep) {
        // TODO: Should check that the tokenArray strings don't contain
        // tokenSep, just for sanity purposes.  In practice, we're not going to
        // use it on data values where tokenSep should occur, because otherwise
        // we'd need to implement icky escaping in the general case.
        if (builderArray[0].length() == 0) {
            for (int ii = 0; ii < builderArray.length; ii++) {
                builderArray[ii].append(tokenArray[ii]);
            }
        } else {
            for (int ii = 0; ii < builderArray.length; ii++) {
                builderArray[ii].append(tokenSep);
                builderArray[ii].append(tokenArray[ii]);
            }
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
 */
class ColorArrayTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String[] strValue = new String[4];

        strValue[0] = store.get(field.getName() + ".r", null);
        strValue[1] = store.get(field.getName() + ".g", null);
        strValue[2] = store.get(field.getName() + ".b", null);
        strValue[3] = store.get(field.getName() + ".a", null);

        if (strValue[0] == null || strValue[1] == null || strValue[2] == null || strValue[3] == null) {
            System.err.println("Load warning: Preference '" + field.getName() + "' has mismatched elements.");
            return;
        }

        StringTokenizer[] tokenArray;
        
        try {
            tokenArray = newTokenArray(4, strValue);
        } catch (IllegalArgumentException e) {
            System.err.println("Load warning: Preference '" + field.getName() + "': " + e + ".");
            return;
        }

        // FIXME: Generalize the array length check.
        // We assume we can upgrade if there are more terrain colors later.
        if (tokenArray[0].countTokens() > prefs.terrainColors.length) {
            System.err.println("Load warning: Preference '" + field.getName() + "' has too many elements.");
            return;
        }

        final Color[] colorArray = new Color[tokenArray[0].countTokens()];

        for (int ii = 0; ii < colorArray.length; ii++) {
            // Assert this is true.
            extractTokenArray(tokenArray, strValue);

            final int rValue = Integer.valueOf(strValue[0]);
            final int gValue = Integer.valueOf(strValue[1]);
            final int bValue = Integer.valueOf(strValue[2]);
            final int aValue = Integer.valueOf(strValue[3]);

            colorArray[ii] = new Color (rValue, gValue, bValue, aValue);
        }

        setPrefField(prefs, field, colorArray);
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final Color[] colorArray = (Color[])getPrefField(prefs, field);

        if (colorArray == null)
            return;

        final StringBuilder[] stringArray = newStringArray (4);
        final String[] strValue = new String[4];

        for (Color color: colorArray) {
            strValue[0] = Integer.toString(color.getRed());
            strValue[1] = Integer.toString(color.getGreen());
            strValue[2] = Integer.toString(color.getBlue());
            strValue[3] = Integer.toString(color.getAlpha());

            appendStringArray(stringArray, strValue);
        }

        store.put(field.getName() + ".r", stringArray[0].toString());
        store.put(field.getName() + ".g", stringArray[1].toString());
        store.put(field.getName() + ".b", stringArray[2].toString());
        store.put(field.getName() + ".a", stringArray[3].toString());
    }
}

/**
 * ArrayList&lt;MUHost&gt; type handler.
 */
class HostListTypeHandler extends TypeHandler {
    public void load (final Preferences store, final MUPrefs prefs, final Field field) {
        final String[] strValue = new String[2];

        strValue[0] = store.get(field.getName() + ".host", null);
        strValue[1] = store.get(field.getName() + ".port", null);

        if (strValue[0] == null || strValue[1] == null) {
            System.err.println("Load warning: Preference '" + field.getName() + "' has mismatched elements.");
            return;
        }

        StringTokenizer[] tokenArray;

        try {
            tokenArray = newTokenArray(2, strValue);
        } catch (IllegalArgumentException e) {
            System.err.println("Load warning: Preference '" + field.getName() + "': " + e + ".");
            return;
        }

        final ArrayList<MUHost> hostArray = new ArrayList<MUHost> ();

        while (extractTokenArray(tokenArray, strValue)) {
            final String host = strValue[0];
            final int port = Integer.valueOf(strValue[1]);

            hostArray.add(new MUHost (host, port));
        }

        setPrefField(prefs, field, hostArray);
    }

    public void save (final Preferences store, final MUPrefs prefs, final Field field) {
        final ArrayList<MUHost> hostArray = (ArrayList<MUHost>)getPrefField(prefs, field);

        if (hostArray == null)
            return;

        final StringBuilder[] stringArray = newStringArray (2);
        final String[] strValue = new String[2];

        for (MUHost host: hostArray) {
            strValue[0] = host.getHost();
            strValue[1] = Integer.toString(host.getPort());

            appendStringArray(stringArray, strValue);
        }

        store.put(field.getName() + ".host", stringArray[0].toString());
        store.put(field.getName() + ".port", stringArray[1].toString());
    }
}
