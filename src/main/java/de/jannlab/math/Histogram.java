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

/**
 * This class provides the construction of ordinary histograms based on the
 * type double. For setting up a histogram a number of quanta (bins) as well
 * as a lower and an upper bound of the value interval must be given. With
 * the note method a value can be "counted". After all, the histogram can
 * be normalized.
 * <br></br>
 * @author Sebastian Otte
 */
public class Histogram {
    /**
     * The lower bound of the value interval.
     */
    private double lbd;
    /**
     * The upper bound of the value interval.
     */
    private double ubd;
    /**
     * The width of the value interval.
     */
    private double irange;
    /**
     * The inverted range of the value interval
     * (for runtime improvements). 
     */
    private double range;
    /**
     * The number of quanta (bins).
     */
    private int quanta;
    /**
     * The number of quanta as double (slightly lower value).
     */
    private double dquanta;
    /**
     * The inner accumulator containing the histogram values.
     */
    private double[] accu;  
    
    /**
     * Returns the lower bound of the value interval.
     * <br></br>
     * @return Lower bound as double.
     */
    public final double getLbd() {
        return this.lbd;
    }
    /**
     * Return the upper bound of the value value interval.
     * <br></br>
     * @return Upper bound as double.
     */
    public final double getUbd() {
        return this.ubd;
    }
    /**
     * Returns the number of quanta (bins).
     * <br></br>
     * @return The number of quanta.
     */
    public final int getQuanta() {
        return this.quanta;
    }

    /**
     * Accumulates the given value in the current histogram with
     * weight of 1.0.
     * <br></br>
     * @param value Value within the given interval.
     */
    public void note(final double value) {
        this.note(value, 1.0);
    }
    
    /**
     * Normalized the histogram such that the range form the biggest value
     * to biggest negative value is about 1.0.
     */
    public void normalize() {
        this.normalize(1.0);
    }
    
    /**
     * Makes all values of the histogram positive.
     */
    public void abs() {
        for (int i = 0; i < this.quanta; i++) {
            this.accu[i] = Math.abs(this.accu[i]);
        }
    }
    
    /**
     * Normalized the histogram such that the sum of all values
     * (absolute values are taken) is about 1.0.
     */
    public void normalizeSum() {
        this.normalizeSum(1.0);
    }
    
    /**
     * Normalized the histogram such that the sum of all values
     * (absolute values are taken) is about a value given by height.
     */
    public void normalizeSum(final double height) {
        double sum = 0.0;
        //
        // collecting sum.
        //
        for (int i = 0; i < this.quanta; i++) {
            sum += Math.abs(this.accu[i]);
        }        
        final double isum = ((sum > 0.0)?(1.0 / sum):(0.0)) * height;
        //
        // normalizing values.
        //
        for (int i = 0; i < this.quanta; i++) {
            this.accu[i] *= isum;
        }
    }
    
    /**
     * Normalized the histogram such that the range form the biggest value
     * to biggest negative value is about a value given by height.
     */
    public void normalize(final double height) {
        //
        double min = 0.0;
        double max = 0.0;
        //
        // determine max postive and negative amplitude.
        //
        for (int i = 0; i < this.quanta; i++) {
            final double amp = this.accu[i];
            if (amp < min) min = amp;
            if (amp > max) max = amp;
            //
        }
        //
        // compute range.
        //
        final double width  = max - min;
        final double iwidth = (width > 0)?(1.0 / width):(0.0);
        //
        // normalize values with range.
        //
        for (int i = 0; i < this.quanta; i++) {
            this.accu[i] *= iwidth;
        }
    }
    
    /**
     * Quantizes a given value, which means that the
     * quantum (bin) index is calculated based on the
     * the histogram interval.  
     * <br></br>
     * @param value
     * @return The quantum index.
     */
    private int quantize(final double value) {
        //
        // normalize value to [0, 1].
        //
        final double norm = (value - this.lbd) * this.irange;
        //
        // then scale to number of quanta and compute floor value.
        //
        final int idx = (int)(norm * this.dquanta);
        return idx;
    }
    
    /**
     * Accumulates the given value in the current histogram with
     * an additionally given weight.
     * <br></br>
     * @param value Value within the given interval.
     * @param weight The weight of the value.
     */
    public void note(final double value, final double weight) {
        final int idx  =  quantize(value);
        if ((idx < 0) || (idx >= this.quanta)) return;
        this.accu[idx] += weight;
    }
    
    /**
     * Resets the current histogram. All accumulated values
     * are set to 0.0.
     */
    public void clear() {
        DoubleTools.fill(this.accu, 0, this.quanta, 0.0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[" + DoubleTools.asString(this.accu, 2) + "]";
    }
    
    /**
     * Instantiates a Histogram object for given values value range determined
     * by a lower and an upper bound, with a given number a quanta (bins). 
     * <br></br>
     * @param lbd Lower bound of the accumulation interval.
     * @param ubd Upper bound of the accumulation interval.
     * @param quanta Number of quanta (bins).
     */
    public Histogram(final double lbd, final double ubd, final int quanta) {
        //
        this.lbd     = Math.min(lbd,  ubd);
        this.ubd     = Math.max(lbd,  ubd);
        this.range   = this.ubd - this.lbd;
        this.irange  = (this.range > 0.0)?(1.0 / this.range):(0.0);
        //
        this.quanta  = Math.max(1, quanta);
        this.dquanta = ((double)this.quanta) - Double.MIN_VALUE;
        this.accu    = new double[this.quanta];
    }
    
    /**
     * Check, if at least one of the histogram values is positive (> 0.0).
     * <br></br>
     * @return True if one value is positive, false otherwise.
     */
    public boolean hasPositiveValues() {
        for (int i = 0; i < this.quanta; i++) {
            if (this.accu[i] > 0.0) return true;
        }
        return false;
    }
    
    /**
     * Check, if at least one of the histogram values is negative (< 0.0).
     * <br></br>
     * @return True if one value is negative, false otherwise.
     */
    public boolean hasNegativeValues() {
        for (int i = 0; i < this.quanta; i++) {
            if (this.accu[i] < 0.0) return true;
        }
        return false;
    }
    
    /**
     * This method produces a simple bar-chart representation of the
     * inner accumulator as a white-space aligned String. The given
     * height defines the number of text lines of which the highest bar
     * in the histogram may consists of. Note, that the resulting
     * string is just for ascii-style printouts, it should not be
     * used for extracting histogram data.
     * <br></br>
     * @param height The number of lines of the highest bar.
     * @return A string containing the bar-char representation of the histogram. 
     */
    public String printBars(final int height) {
        //
        StringBuilder out = new StringBuilder();
        
        final boolean neg = this.hasNegativeValues(); 
        final boolean pos = this.hasPositiveValues(); 
        //
        if (pos) {
            String[][] matrix = new String[height][this.quanta];
            //
            double max = 0.0;
            for (int i = 0; i < this.quanta; i++) {
                if (accu[i] > max) { 
                    max = accu[i];
                }
            }
            //
            final double step = (max / ((double)height));
            //
            for (int i = 0; i < this.quanta; i++) {
                
                double thres = 0;
                double value = this.accu[i];
                
                for (int j = 0; j < height; j++) {
                    if (value > thres) {
                        matrix[j][i] = " || ";
                    } else {
                        matrix[j][i] = "    ";
                    }
                    thres += step;
                }
            }
            for (int j = height - 1; j >= 0; j--) {
                for (int i = 0; i < this.quanta; i++) {
                    out.append(matrix[j][i]);
                }
                out.append("\n");
            }
            
        }
        //
        for (int i = 0; i < this.quanta; i++) {
            out.append("----");
        }
        out.append("\n");
        //
        if (neg) {
            String[][] matrix = new String[height][this.quanta];
            //
            double min = 0.0;
            for (int i = 0; i < this.quanta; i++) {
                if (this.accu[i] < min) { 
                    min = this.accu[i];
                }
            }
            //
            final double step = (min / ((double)height));
            //
            for (int i = 0; i < this.quanta; i++) {
                
                double thres = 0;
                double value = this.accu[i];
                
                for (int j = 0; j < height; j++) {
                    if (value < thres) {
                        matrix[j][i] = " || ";
                    } else {
                        matrix[j][i] = "    ";
                    }
                    thres += step;
                }
            }
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < this.quanta; i++) {
                    out.append(matrix[j][i]);
                }
                out.append("\n");
            }
            
        }
        return out.toString();
        
    }
    
    /**
     * Returns the inner accumulator, which contains all
     * histogram values.
     * <br></br>
     * @return Histogram values as double[].
     */
    public double[] getAccu() {
        return this.accu;
    }
    
}
