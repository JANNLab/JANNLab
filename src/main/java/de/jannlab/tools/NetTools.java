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
import de.jannlab.data.SampleSet;

/**
 * This class contains common methods for handling net instance.
 * <br></br>
 * @author Sebastian Otte
 */
public final class NetTools  {
    
    /**
     * Computes the error for a given set of samples. Note that
     * the result error is divided by the number of samples.
     * <br></br>
     * @param net A instance of Net which computes the errors.
     * @param set A set of samples.
     * @return The error produces by the network for set of sample.
     */
    public static double computeError(
            final Net net, final SampleSet set
    ) {
        //
        double error = 0.0; 
        //
        // perform forward pass for each sample.
        //
        for (Sample s : set) {
            net.reset();
            final double err = performForward(net, s);
            error += err;
        }
        //
        return error / ((double)set.size());
    }
    
    /**
     * Computes the forward pass of a given net for a given sample.
     * Note that this method resets the frame index to zero before
     * feeding the input into the net.
     * <br></br>
     * @param net A network instance.
     * @param sample A sample.
     */
    public static double performForward(
            final Net net,
            final Sample sample
    ) {
        return performForward(net, sample, null);
    }
    
    /**
     * Computes the forward pass of a given net for a given sample only
     * on a selection of features.
     * Note that this method resets the frame index to zero before
     * feeding the input into the net.
     * <br></br>
     * @param net A network instance.
     * @param sample A sample.
     */
    public static double performForward(
            final Net net,
            final Sample sample,
            final int[] features
    ) {
        final int inputlength  = sample.getInputLength();
        final int targetlength = sample.getTargetLength();
        final int last         = (sample.getInputLength() - 1);
        //
        // reset time.
        //
        net.setFrameIdx(0);
        //
        double error = 0.0;
        //
        // choose computation paradigm.
        //
        if (net.isOnline()) {
            //
            // on online computation, we must feed the net with
            // inputs and perform the activation for each time step.
            //
            for (int t = 0; t <= last; t++) {
                if (features != null) {
                    sample.mapInput(net.inputPort(), t, features);
                } else {
                    sample.mapInput(net.inputPort(), t);
                }
                net.compute();
                if (t < last) net.incrFrameIdx();
            }
            //
            // compute error.
            //
            final int first = Math.max(0, inputlength - targetlength);
            int soff = targetlength - 1;
            //
            for (int t = last; t >= first; t--) {
                sample.mapTarget(net.targetPort(), soff--);
                error += net.error();
                if (t > 0) net.decrFrameIdx();
            }
            net.setFrameIdx(last);
            //
        } else {
            //
            // on offline computation, we must feed the net with
            // inputs frame wise and let then the net perform the activation at
            // once. it is important to not reset the frame index after
            // feed in the data, because the net uses the frame index
            // as upper bound for the computation range over time.
            //
            for (int t = 0; t <= last; t++) {
                if (features != null) {
                    sample.mapInput(net.inputPort(), t, features);
                } else {
                    sample.mapInput(net.inputPort(), t);
                }
                if (t < last) net.incrFrameIdx();
            }
            //
            net.compute();
            //
            // the computation process ensures, that we are in correct
            // frame index to acquire the error.
            //
            sample.mapTarget(net.targetPort());
            error = net.error();
        }
        return (error / ((double)targetlength));        
    }
  
    /**
     * Computes the backward pass of a given network.
     * Note that this method resets the frame index to zero before
     * feeding the input into the net.
     * <br></br>
     * @param net A network instance.
     */
    public static void performBackward(
            final Net net
    ) {
        //
        // we assume the error has already been fed in the network.
        // so we just need to compute the gradients back in time.
        //
        if (net.isOnline()) {
            //
            // on online computation, me must compute gradients for
            // each step.
            //
            final int T = net.getFrameIdx();
            for (int t = T; t >= 0; t--) {
                net.computeGradient();
                //
                // step back in time.
                //
                if (net.getFrameIdx() > 0) net.decrFrameIdx();
            }
        } else {
            //
            // on offline computation, the network will compute
            // the gradients for all time steps at once.
            //
            net.computeGradient();
        }
    }
    
}
