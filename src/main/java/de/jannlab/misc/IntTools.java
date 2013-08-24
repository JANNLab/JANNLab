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

package de.jannlab.misc;

import java.io.StringWriter;
import java.util.Random;

/**
 * This class provides some methods for handling
 * arrays of ints.
 * <br></br>
 * @author Sebastian Otte
 */
public final class IntTools {
    
    public static void shuffle(
            final int[] data, 
            final Random rnd
    ) {
       //
       final int size = data.length;
       //
       for (int i = size; i > 1; i--) {
           final int ii = i - 1;
           final int r  = rnd.nextInt(i);
           //
           final int temp = data[ii];
           data[ii] = data[r];
           data[r] = temp;
       }
    }

    public static String asString(final int[] data) {
        return asString(data, 0, data.length);
    }
    
    public static String asString(final int[] data, final int offset, final int size) {
        StringWriter out = new StringWriter();
            
        int o = offset;
        for (int i = 0; i < size; i++) {
            if (i > 0) out.append(", ");
            out.append(Integer.toString(data[o]));
            o++;
        }
        return out.toString();
    }
    
}
