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

package de.jannlab.core;

import java.io.Serializable;

import de.jannlab.misc.ObjectCopy;

/**
 * An instance of this class contains the entire data of an ANN. This are on one hand
 * the data buffers for forward and backward computation (input, ouput, gradinput, 
 * gradoutput) for each time step (the timesteps are defined by the frame width).
 * @author Sebastian Otte
 */
public final class NetData implements Serializable {
    private static final long serialVersionUID = 9138637243730569419L;
    /**
     * provides the input buffer. the results of the forward
     * integrations are stored here.
     */
    public double[][] input;
    /**
     * provides the output buffer. the results of the forward
     * activations and also the network input are stored here. 
     */
    public double[][] output;
    /**
     * provides the grad. input buffer. the results of the backward
     * integrations and also the network error are stored here.
     */
    public double[][] gradinput;
    /**
     * provides the grad. output buffer. the results of the activation
     * deviations and also the "back-flowing" error are stored here. 
     */
    public double[][] gradoutput;
    /**
     * determines the frame width, which is the first dimension of the
     * data buffers. MLPs or trained RNNs only need a frame width of 1.
     * Online offline computing RNNs or online computing RNNs during
     * training need a frame width > 1.
     */
    public int framewidth;
    /**
     * provides the vector of weigts. note that the first value of the vector
     * is generally 1.0. this is founded in some runtime peformance improvements
     * concerning non weighted links.
     */
    public double[] weights;
    /**
     * Gives the number of weights exclusive the constant 1.0 weight.
     */
    public int weightsnum;
    /**
     * Stores the indices to the cells used for constant value assignments. 
     */
    public int[] asgns;
    /**
     * Stores the constant values assignments corresponding to the indices
     * given in asgns.
     */
    public double[] asgnsv;
    
    /**
     * This method returns a shared copy of the current data record. This means
     * that the entire data buffer is "really" duplicated while the weights and 
     * the assignments are shared. A shared copy can be used for independent
     * computations on the same weights vector, which is an important requirement
     * for computing/training the same problem in parallel with multiple Net instances. 
     * <br></br>
     * @return Copy of this data record with shared weights and assigments.
     */
    public NetData sharedCopy() {
        NetData copy = new NetData();
        //
        // copy.
        //
        copy.input      = ObjectCopy.copy(this.input);         
        copy.output     = ObjectCopy.copy(this.output);
        copy.gradinput  = ObjectCopy.copy(this.gradinput);
        copy.gradoutput = ObjectCopy.copy(this.gradoutput);
        copy.framewidth = this.framewidth;
        //
        // share.
        //
        copy.weights    = this.weights;
        copy.weightsnum = this.weightsnum;
        copy.asgns      = this.asgns;
        copy.asgnsv     = this.asgnsv;
        //
        return copy;
    }
    
    /**
     * Makes a complete "real" copy of the current data record including the weight vectors.
     * No data is shared between the copy and original instance.
     * @return A copy of this data record.
     */
    public NetData copy() {
        return ObjectCopy.copy(this);
    }
}
