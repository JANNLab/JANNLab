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
import de.jannlab.math.MathTools;
import de.jannlab.misc.DoubleTools;

/**
 * This class allows to simply evaluate classification tasks. Therefore
 * after presentation the sample, the output component with the highest
 * value is taken as class index. If the result fits the target vector
 * is counted as well recognized.
 * <br></br>
 * @author Sebastian Otte
 */
public class ClassificationValidator {
    /**
     * On threshold.
     */
    public static final double DEFAULT_THRESHOLD = 0.5;
    /**
     * The reference network.
     */
    private Net net;
    /**
     * Counts the numbers of samples.
     */
    private int ctr = 0;
    /**
     * Counts the number of well classified samples.
     */
    private int goodctr = 0;
    /**
     * A threshold which defines a correct classification.
     */
    private double threshold = DEFAULT_THRESHOLD;
    /**
     * Returns the internal counter.
     */
    public void reset() {
        this.ctr     = 0;
        this.goodctr = 0;
    }
    
    /**
     * Creates an instance of ClassificationValidator. Here DEFAULT_THRESHOLD
     * is used as classification threshold. 
     * @param net A given reference network.
     */
    public ClassificationValidator(final Net net) {
        this.net = net;
    }
    
    /**
     * Creates an instance of ClassificationValidator.
     * @param net A given reference network.
     * @param threshold Gives a specific classification threshold.
     */
    public ClassificationValidator(final Net net, final double threshold) {
        this.net       = net;
        this.threshold = threshold;
    }
    
    /**
     * Returns the classification threshold.
     * @return Current classification threshold.
     */
    public double getThreshold() {
        return this.threshold;
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
     * @return The output vector of the network after classification.
     */
    public double[] apply(final Sample sample, final int[] features) {
        this.net.reset();
        this.ctr++;
        //
        double[] result = new double[this.net.getOutputCells()];
        //
        // feed net with sample (discard error).
        //
        if (features == null) {
            NetTools.performForward(this.net, sample);
        } else {
            NetTools.performForward(this.net, sample, features);
        }
        //
        // read net output.
        //
        this.net.output(result, 0);
        //
        // choose class.
        //
        if (result.length == 1) {
            if (Math.abs(sample.getTarget()[0] - result[0]) < this.threshold) {
                this.goodctr++;
            }
        } else {
            final int maxidx = MathTools.argmax(result);
            //
            if ((sample.getTarget()[maxidx] > this.threshold)) {
                this.goodctr++;
            }
        }
        //
        return result;
    }
    
    /**
     * Applies a given sample to the reference network.
     * <br></br>
     * @param sample An instance of Sample.
     * @return The output vector of the network after classification.
     */
    public double[] apply(final Sample sample) {
        return this.apply(sample, null);
    }

    /**
     * Applies a given sample to the reference network. Returns a string
     * representation of the output.
     * <br></br>
     * @param sample An instance of Sample.
     * @return The output vector of the network after classification as String.
     */
    public String applyAsString(final Sample sample, final boolean printsample) {
        //
        StringBuilder out = new StringBuilder();
        //
        // first apply sample.
        //
        double[] output = this.apply(sample);
        //
        // print result into string.
        //
        if (printsample) {
            out.append(sample.toString());
        } else {
            out.append(
                "target: [" + 
                DoubleTools.asString(
                    sample.getTarget(), 0, sample.getTargetSize(), 5
                ) + "]\n"
            );
        }
        out.append(
            "output: [" + 
            DoubleTools.asString(
                output, 0, output.length, 5
            ) + "]\b"
        );
        //
        return out.toString();
    }
    
}
