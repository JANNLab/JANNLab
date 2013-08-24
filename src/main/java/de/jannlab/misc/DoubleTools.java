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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;

/**
 * This class provides some methods for handling arrays
 * of doubles.
 * <br></br>
 * @author Sebastian Otte
 */
public final class DoubleTools {

    public static void copy(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = data[o1++];
        }
    }

    public static void copy(
            final double[] data,
            final int dataoffset,
            final int[] dataselection,
            final double[] result,
            final int resultoffset
    ) {
        int o2 = resultoffset;
        //
        for (int i = 0; i < dataselection.length; i++) {
            result[o2++] = data[dataoffset + dataselection[i]];
        }
    }

    public static void copy(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int[] resultselection
    ) {
        int o1 = dataoffset;
        //
        for (int i = 0; i < resultselection.length; i++) {
            result[resultoffset + resultselection[i]] = data[o1++];
        }
    }
    
    public static void copy(
            final double[] data,
            final int dataoffset,
            final int datastep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2] = data[o1];
            o1 += datastep;
            o2 += resultstep;
        }
    }
    
    
    public static void fill(
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size,
            final Random rnd,
            double lbd,
            double ubd
    ) {
        if (lbd > ubd) {
            final double temp = lbd;
            lbd = ubd;
            ubd = temp;
        }
        final double width = ubd - lbd;
        //
        int o = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = (rnd.nextDouble() * width) + lbd; 
            result[o] = value;
            o += resultstep;
        }
    }   
    
    public static void fill(
            final double[] result,
            final int resultoffset,
            final int size,
            final Random rnd,
            double lbd,
            double ubd
    ) {
        if (lbd > ubd) {
            final double temp = lbd;
            lbd = ubd;
            ubd = temp;
        }
        final double width = ubd - lbd;
        //
        int o = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = (rnd.nextDouble() * width) + lbd; 
            result[o++] = value;
        }
    }    
    
    public static void fill(
            final double[] result,
            final int resultoffset,
            final int size,
            final double value
    ) {
        int o = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o++] = value;
        }
    }    
    
    public static void fill(
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size,
            final double value
    ) {
        int o = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o] = value;
            o += resultstep;
        }
    }    
    
    public static void threshold(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size,
            final double threshold
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = (data[o1++] > threshold)?(1.0):(0.0);
        }
    }
    
    public static void threshold(
            final double[] data,
            final int dataoffset,
            final int datastep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size,
            final double threshold
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2] = (data[o1] > threshold)?(1.0):(0.0);
            o1 += datastep;
            o2 += resultstep;
        }
    }
    

    

        
    public static void map(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size,
            final UnaryFunctionDouble f
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = f.perform(data[o1++]);
        }
    }
    
    public static void map(
            final double[] data,
            final int dataoffset,
            final int datastep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size,
            final UnaryFunctionDouble f
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2] = f.perform(data[o1]);
            o1 += datastep;
            o2 += resultstep;
        }
    }
    
    public static void map(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size,
            final BinaryFunctionDouble f
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3++] = f.perform(first[o1++], second[o2++]);
        }
    }

    public static void map(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size,
            final BinaryFunctionDouble f
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3] = f.perform(first[o1], second[o2]);
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }

    public static double absSum(
            final double[] data,
            final int offset,
            final int size
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            final double x = data[o++];
            value += Math.abs(x);
        }
        //
        return value;
    }
    
    public static double absSum(
            final double[] data,
            final int offset,
            final int size,
            final int step
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            final double x = data[o];
            value += Math.abs(x);
            o += step;
        }
        //
        return value;
    }
    
    public static double meanSquareSum(
            final double[] data,
            final int offset,
            final int size
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            final double x = data[o++];
            value += (x * x);
        }
        //
        return value / ((double)size);
    }
    
    public static double squareSum(
            final double[] data,
            final int offset,
            final int size
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            final double x = data[o++];
            value += (x * x);
        }
        //
        return value;
    }
    
    public static double squareSum(
            final double[] data,
            final int offset,
            final int size,
            final int step
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            final double x = data[o];
            value += (x * x);
            o += step;
        }
        //
        return value;
    }
    
    public static double sum(
            final double[] data,
            final int offset,
            final int size
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            value += data[o++];
        }
        //
        return value;
    }

    public static double sum(
            final double[] data,
            final int offset,
            final int size,
            final int step
    ) {
        int o = offset;
        //
        double value = 0.0;
        //
        for (int i = 0; i < size; i++) {
            value += data[o];
            o += step;
        }
        //
        return value;
    }
    
    public static double mul(
            final double[] data,
            final int offset,
            final int size
    ) {
        int o = offset;
        //
        double value = 1.0;
        //
        for (int i = 0; i < size; i++) {
            value *= data[o++];
        }
        //
        return value;
    }

    public static double mul(
            final double[] data,
            final int offset,
            final int size,
            final int step
    ) {
        int o = offset;
        //
        double value = 1.0;
        //
        for (int i = 0; i < size; i++) {
            value *= data[o];
            o += step;
        }
        //
        return value;
    }
    
    public static void dot(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final int size,
            final double[] result,
            final int resultoffset
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        double sum = 0;
        //
        for (int i = 0; i < size; i++) {
            sum += (first[o1++] * second[o2++]);
        }
        //
        result[o3] = sum;
    }
    
    public static void dot(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final int size,
            final double[] result,
            final int resultoffset
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        double sum = 0;
        //
        for (int i = 0; i < size; i++) {
            sum += (first[o1] * second[o2]);
            o1 += firststep;
            o2 += secondstep;
        }
        //
        result[o3] = sum;
    }
    
    public static void add(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3++] = first[o1++] + second[o2++];
        }
    }
    
    public static void add(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3] = first[o1] + second[o2];
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }
    
    public static void sub(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3++] = first[o1++] - second[o2++];
        }
    }
    
    public static void sub(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3] = first[o1] - second[o2];
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }
    

    
    public static void mul(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double v1 = first[o1++];
            final double v2 = second[o2++];
            //
            final double value = v1 * v2; 
            result[o3++] = value;
        }
    }
    
    public static void mul(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3] = first[o1] * second[o2];
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }
    
    public static void div(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3++] = first[o1++] / second[o2++];
        }
    }
    
    public static void div(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o3] = first[o1] / second[o2];
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }
    
    public static void divSafe(
            final double[] first,
            final int firstoffset,
            final double[] second,
            final int secondoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = first[o1++];
            final double quo = second[o2++];
            result[o3++] = (quo == 0)?(value):(value / quo); 
        }
    }
    
    public static void divSafe(
            final double[] first,
            final int firstoffset,
            final int firststep,
            final double[] second,
            final int secondoffset,
            final int secondstep,
            final double[] result,
            final int resultoffset,
            final int resultstep,
            final int size
    ) {
        int o1 = firstoffset;
        int o2 = secondoffset;
        int o3 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = first[o1];
            final double quo = second[o2];
            result[o3] = (quo == 0)?(value):(value / quo); 
            o1 += firststep;
            o2 += secondstep;
            o3 += resultstep;
        }
    }

    public static String asString(final double[] data, final int decimals) {
        return asString(data, 0, 1, data.length, decimals);
    }

    public static String asString(final double value, final int decimals) {
        //
        DecimalFormat f = new DecimalFormat();
        f.setDecimalSeparatorAlwaysShown(true);
        f.setMaximumFractionDigits(decimals);
        f.setMinimumFractionDigits(decimals);
        f.setGroupingUsed(false);
        //
        f.setDecimalFormatSymbols(new DecimalFormatSymbols() {
            private static final long serialVersionUID = -2464236658633690492L;
            public char getGroupingSeparator() { return ' '; }
            public char getDecimalSeparator() { return '.'; }
        });
        return f.format(value);    
    }
    
    public static String asString(
            final double[] data, 
            final String delimiter, 
            final int decimals
    ) {
        return asString(data, delimiter, 0, 1, data.length, decimals);
    }

    public static String asString(
            final double[] data, final int offset, final int size, final int decimals
    ) {
        return asString(data, offset, 1, size, decimals);
    }

    public static String asString(
            final double[] data, 
            final String delimiter,
            final int offset, final int size, final int decimals
    ) {
        return asString(data, delimiter, offset, 1, size, decimals);
    }
    
    public static String asString(
            final double[] data, final int offset, 
            final int step, final int size,
            final int decimals
    ) {
        return asString(data, ", ", offset, step, size, decimals);
    }
    
    public static String asString(
            final double[] data, 
            final String delimiter,
            final int offset, 
            final int step, final int size,
            final int decimals
    ) {
        StringWriter out = new StringWriter();
        
        DecimalFormat f = new DecimalFormat();
        f.setDecimalSeparatorAlwaysShown(true);
        f.setMaximumFractionDigits(decimals);
        f.setMinimumFractionDigits(decimals);
        f.setGroupingUsed(false);
        
        f.setDecimalFormatSymbols(new DecimalFormatSymbols() {
            private static final long serialVersionUID = -2464236658633690492L;
            public char getGroupingSeparator() { return ' '; }
            public char getDecimalSeparator() { return '.'; }
        });
            
        int o = offset;
        for (int i = 0; i < size; i++) {
            if (i > 0) out.append(delimiter);
            out.append(f.format(data[o]));
            o += step;
        }
        return out.toString();
    }
    
    
    public static double[] tail(
            final double[] data,
            final int size
    ) {
        double[] result = new double[size];
        int offset = data.length - 1;
        for (int i = size - 1; i >= 0; i--) {
            if (offset < 0) break;
            result[i] = data[offset--];
        }
        return result;
    }
    
    
    public static double[] merge(final double[] ...arrays) {
        int length = 0;
        for(int i = 0; i < arrays.length; i++) {
            length += arrays[i].length;
        }
        double[] result = new double[length];
        int offset = 0;
        for(int i = 0; i < arrays.length; i++) {
            final int l = arrays[i].length;
            copy(arrays[i], 0, result, offset, l);
            offset += l;
        }
        return result;
    }
    
    
    
    
    
    
}
