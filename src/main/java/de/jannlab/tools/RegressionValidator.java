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

import de.jannlab.Net;
import de.jannlab.data.Sample;

/**
 * This class allows to simply evaluate regression tasks. Therefore
 * a threshold is given. A sample for which the reference network
 * produces a error < threshold is counted as recognized. After 
 * testing a couple of samples the recognition ratio can be extracted.  
 * <br></br>
 * @author Sebastian Otte
 */
public class RegressionValidator {
    
    /**
     * The reference network.
     */
    private Net net;
    /**
     * Number of tested samples.
     */
    private int ctr = 0;
    /**
     * Number of well recognized samples.
     */
    private int goodctr = 0;
    /**
     * The regression theshold.
     */
    private double threshold = 0.001;
    
    /**
     * Returns the internal counter.
     */
    public void reset() {
        this.ctr     = 0;
        this.goodctr = 0;
    }
    
    /**
     * Creates an instance of RegressionValidator.
     * @param net A given reference network.
     * @param threshold The regression threshold.
     */
    public RegressionValidator(final Net net, final double threshold) {
        this.net       = net;
        this.threshold = threshold;
    }
    
    /**
     * Returns the current recognition rate. 100 = 1.0
     * @return The current recognition rate.
     */
    public double ratio() {
        return ((double)this.goodctr) / ((double)this.ctr);
    }
    
    /**
     * Applies a given sample to the reference network for only given selection.
     * <br></br>
     * @param sample An instance of Sample.
     * @param features A selection as array of indices.
     * @return The output error of the network after processing the sample.
     */
    public double apply(final Sample sample, final int[] features) {
        this.net.reset();
        this.ctr++;
        //
        // feed net with sample (discard error).
        //
        double error = 0.0;
        if (features == null) {
            error = NetTools.performForward(this.net, sample);
        } else {
            error = NetTools.performForward(this.net, sample, features);
        }
        //
        if (error < this.threshold) {
            this.goodctr++;
        }
        //
        return error;
    }
    
    /**
     * Applies a given sample to the reference network.
     * <br></br>
     * @param sample An instance of Sample.
     * @return The output error of the network after processing the sample.
     */
    public double apply(final Sample sample) {
        return this.apply(sample, null);
    }
    
}
