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

package de.jannlab.math;


import de.jannlab.misc.DoubleTools;


public class MatrixTools {
    
    public static final int VALUE_PADDING = 2;
    
    public static double[] allocate(final int m, final int n) {
        return new double[m * n];
    }
    
    public static double[] allocate(final int m) {
        return new double[m];
    }
    
    public static void setRow(
        final double[] matrix, final int n, final int m, 
        final int i, final double ...values
    ) {
        //
        setRow(matrix, n, m, i, values, 0);
    }
    
    public static void setRow(
        final double[] matrix, final int n, final int m, 
        final int i, final double[] values, final int offset
    ) {
        //
        DoubleTools.copy(
            matrix, i * n, values, offset, 
            Math.min(n, values.length - offset)
        );
    }
    
    public static int idx(final int i, final int j, final int n) {
        return (i * n) + j;
    }
    
    public static void transpose(
        final double[] A,
        final int m,
        final int n,
        final double[] At
    ) {
        //
        final double[] copy = A.clone();
        final int      size = m * n;
        //
        int offset = 0;
        //
        for (int i = 0; i < size; i++) {
            //
            At[offset] = copy[i];
            //
            offset += m;
            if (offset >= size) {
                offset = (offset - size) + 1;
            }
        }
    }
    
    public static String asString(
        final double[] A,
        int m,
        int n,
        int decimals
    ) {
        final StringBuilder out = new StringBuilder();
        final int      size     = m * n;
        final String[] elements = new String[size];
        //
        int max = 0;
        //
        for (int i = 0; i < size; i++) {
            elements[i] = DoubleTools.asString(A, i, 1, decimals);
            if (elements[i].length() > max) {
                max = elements[i].length();
            }
        }
        //
        final int celllength = max + VALUE_PADDING;
        //
        for (int i = 0; i < size; i++) {
            if ((i > 0) && ((i % n) == 0)) out.append("\n");
            //
            final String value = elements[i];
            final int diff = celllength - value.length();
            for (int j = 0; j < diff; j++) {
                out.append(" ");
            }
            out.append(value);
        }
        //
        return out.toString();
    }
    
    
    
}
