/*******************************************************************************
 * JANNLab Neural Network Framework for Java
 * Copyright (C) 2012-2013 Sebastian Otte
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.jannlab.tools;


/**
 * This class provides Methods for handling String containing lines,
 * which can be used for debugging tasks.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public final class Debug {
    /**
     * A debug flag which can be used as a switch in source code.
     */
    public static final boolean DEBUG = true;
    
    /**
     * Merges a array of Strings into one String with
     * some "glue".
     * <br></br>
     * @param s The array of strings.
     * @param glue The glue.
     * @return The merged string.
     */
    public static String combine(String[] s, String glue) {
        int k=s.length;
        if (k==0) {
            return null;
        }
        //
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        //
        for (int x=1;x<k;++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }
    /**
     * Indents all lines in a given String.
     * <br></br>
     * @param lines A string containing lines.
     * @return All lines indented.
     */
    public static String indent(String lines) {
        String[] split = lines.split("\n");
        for (int i = 0; i < split.length; i++) {
            split[i] = "\t" + split[i];
        }
        return combine(split, "\n");
    }

}
