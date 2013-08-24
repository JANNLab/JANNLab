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

/**
 * This class provides some frequently used mathematical
 * helper methods.
 * <br></br>
 * @author Sebastian Otte
 */
public class MathTools {

    public static final double NULL_THRESHOLD_DOUBLE = 1.0e-9;
    public static final float  NULL_THRESHOLD_FLOAT  = 1.0e-9f;
    
    /**
     * Checks, if the given value is approximately 0.0 based on the 
     * threshold NULL_THRESHOLD_DOUBLE.
     * <br></br>
     * @param value The value which is to check.
     * @return True if absolute of the given value is smaller than the threshold.
     */
    public static boolean approxNull(final double value) {
        return Math.abs(value) < NULL_THRESHOLD_DOUBLE;
    }
    
    /**
     * Checks, if the given value is approximately 0.0 based on the 
     * threshold NULL_THRESHOLD_FLOAT.
     * <br></br>
     * @param value The value which is to check.
     * @return True if absolute of the given value is smaller than the threshold.
     */
    public static boolean approxNull(final float value) {
        return Math.abs(value) < NULL_THRESHOLD_FLOAT;
    }

    /**
     * Checks, if the given value is approximately 0.0 based on the 
     * threshold given by errorbound.
     * <br></br>
     * @param value The value which is to check.
     * @param errorbound The explicitly given threshold.
     * @return True if absolute of the given value is smaller than the threshold.
     */
    public static boolean approxNull(final double value, final double errorbound) {
        return Math.abs(value) < Math.abs(errorbound);
    }
    
    /**
     * Checks, if the given value is approximately 0.0 based on the 
     * threshold given by errorbound.
     * <br></br>
     * @param value The value which is to check.
     * @param errorbound The explicitly given threshold.
     * @return True if absolute of the given value is smaller than the threshold.
     */
    public static boolean approxNull(final float value, final float errorbound) {
        return Math.abs(value) < Math.abs(errorbound);
    }
    

    /**
     * Computes the apprixmation of the exponential function e^{x} using
     * the interpretation of IEEE-754 numbers. 
     * <br></br>
     * References:
     * <br></br>
     * Schraudolph, Nicol N.: A Fast, Compact Approximation of the 
     * Exponential Function. In:Neural Computation11 (1998), S. 11???4
     * <br></br>
     * Optimized Exponential Functions for Java. 
     * http://martin.ankerl.com/2007/02/11/optimized-exponential-
     * functions-for-java/, 2007. ??? Visited on Januar, 12st 2012
     * <br></br>
     * @param value The argument of the exp function.
     * @return Approximation of e^{x}.
     */
    public static double fastExp(final double value) {
        final long tmp = (long)(1512775 * value) + 1072632447;
        return Double.longBitsToDouble(tmp << 32);
    }
    
    /**
     * Computes a fast tanh function based on the approximation
     * of the expontial function.
     * <br></br>
     * @param value The argument of the tanh function.
     * @return Approximation of tanh(x).
     */
    public static double fastTanh(final double value) {
        final double pos = fastExp(value);
        final double neg = fastExp(-value);
        return (pos - neg) / (pos + neg);
    }
    
    /**
     * Return the index of the biggest values in an array of doubles.
     * Returns -1 if an empty array is given.
     * <br></br>
     * @param args Double array.
     * @return The index of the biggest value.
     */
    public static int argmax(final double ...args) {
        if (args.length == 0) return -1;
        //
        int maxidx = 0;
        double max = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] > max) {
                max = args[i];
                maxidx = i;
            }
        }
        return maxidx;
    }
    
    /**
     * Return the index of the smallest values in an array of doubles. 
     * Returns -1 if an empty array is given.
     * <br></br>
     * @param args Double array.
     * @return The index of the smallest value.
     */
    public static int argmin(final double ...args) {
        if (args.length == 0) return -1;
        //
        int minidx = 0;
        double min = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] < min) {
                min = args[i];
                minidx = i;
            }
        }
        return minidx;
    }
    
    /**
     * Clamps a given value x returned as x' within the interval [lbd, ubd] 
     * such that x' = lbd if x < lbd, x' = ubd if x > ubd or x' = x otherwise. 
     * @param value The given value.
     * @param lbd Lower bound of the clamp interval.
     * @param ubd Upper bound of the clamp interval.
     * @return A values within the interval [lbd, ubd]
     */
    public static double clamp(
       final double value, final double lbd, final double ubd
    ) {
        return (
            (value < lbd) ? (lbd) : (
                (value > ubd) ? (ubd) : (value)
            )
        );
    }
}
