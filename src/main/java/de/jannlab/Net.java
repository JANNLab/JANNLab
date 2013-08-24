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


package de.jannlab;

import java.io.Serializable;
import java.util.Random;

import de.jannlab.core.NetStructure;
import de.jannlab.data.ReadPort;
import de.jannlab.data.WritePort;


/**
 * This interfaces represents neural networks in this framework. All
 * ANN implementations are subclass of Net. The provided methods are
 * for reading and writing data from and into the network, for computing
 * and moving through the time (for recurrent networks).
 * <br></br>
 * @author Sebastian Otte
 */
public interface Net extends Serializable {
    //
    /**
     * Resets the network, which means that all inner
     * states (activations, derivations) are set to 0.0.
     * The method also resets the time index which is 0
     * after doing reset.
     */
    public void reset();
    /**
     * Initialize weights randomly.
     */
    public void initializeWeights();
    /**
     * Initialize weights randomly with a given Random
     * instance.
     * <br></br>
     * @param rnd Instance of Random.
     */
    public void initializeWeights(Random rnd);
    /**
     * Copies data to the input layer of the network. This method
     * depends on the the current time step.
     * <br></br>
     * @param data Source data buffer
     * @param offset Offset in the source data buffer.
     */
    public void input(final double[] data, final int offset);
    /**
     * Copies data to the input layer of the network only for a given selection.
     * This method depends on the the current time step.
     * <br></br>
     * @param data Source data buffer
     * @param offset Offset in the source data buffer.
     * @param selection A selection given as array of indices.
     */
    public void input(final double[] data, final int offset, final int[] selection);
    /**
     * Copies data from the outputlayer of the network into a given data buffer.
     * This method depends on the the current time step.
     * @param data Destination data buffer.
     * @param offset Offset in the destination data buffer.
     */
    public void output(final double[] data, final int offset);
    /**
     * Copies data from the outputlayer of the network into a given data buffer
     * only for a given selection. This method depends on the the current time step.
     * @param data Destination data buffer.
     * @param offset Offset in the destination data buffer.
     * @param selection A selection given as array of indices.
     */
    public void output(final double[] data, final int offset, final int[] selection);
    /**
     * Copies data to the output layer of the network (the target output). This method
     * depends on the the current time step.
     * <br></br>
     * @param data Source data buffer
     * @param offset Offset in the source data buffer.
     */
    public void target(final double[] data, final int offset);
    /**
     * Copies data to the output layer of the network (the target output) for a given
     * selection. This method depends on the the current time step.
     * <br></br>
     * @param data Source data buffer.
     * @param offset Offset in the source data buffer.
     * @param selection A selection given as array of indices.
     */
    public void target(final double[] data, final int offset, final int[] selection);
    //
    /**
     * Computes the error of the outputlayer, based on the current outputlayer 
     * activation and the injected target output. 
     * This method depends on the the current time step.
     * <br></br>
     * @return The current output array.
     */
    public double error();
    /**
     * Returns a WritePort for writing into the inputlayer.
     * <br></br>
     * @return Instance of WritePort.
     */
    public WritePort inputPort();
    /**
     * Returns a ReadPort for reading data from the outputlayer.
     * @return Instance of ReadPort.
     */
    public ReadPort outputPort();
    /**
     * Returns a WritePort for writing data into the outputlayer (the target output).
     * @return Instance of WritePort.
     */
    public WritePort targetPort();
    /**
     * Copies the entire network instance. This performs a deep copy.
     * @return Deep copy of this instance.
     */
    public Net copy();
    /**
     * Performs a shared copy of the network instance. Some data part such a links, weights 
     * etc. are shared. The rest (data buffers) are private. The shared copy provides
     * parallel computation with the "same" network.
     * @return Shared copy of this instance.
     */
    public Net sharedCopy();
    /**
     * Computes the activation of the network. The method strongly depends on the underlying
     * network implementation (online vs. offline).
     */
    public void compute();
    /**
     * Computes the gradient (error flow) of the network. The method strongly depends on 
     * the underlying network implementation (online vs. offline).
     */
    public void computeGradient();
    /**
     * Increases the current frame index (time index).
     */
    public void incrFrameIdx();
    /**
     * Sets the current frame index (time index) to the given value.
     * <br></br>
     * @param idx The new time index.
     */
    public void setFrameIdx(final int idx);
    /**
     * Get the frame width of the network.
     * @return Frame width of this instance.
     */
    public int getFrameWidth();
    /**
     * Return the current frame index (time index).
     * @return Current frame index (time index).
     */
    public int getFrameIdx();
    /**
     * Decreases the current frame index (time index).
     */
    public void decrFrameIdx();
    /**
     * Gives the internal output buffer for a given frame index (time index).
     * <br></br>
     * @param frameidx The requested frame idx (time index).
     * @return Output data buffer for frame idx (time index).
     */
    public double[] getOutputBuffer(final int frameidx);
    /**
     * Gives the internal gradient output buffer for a given frame index (time index).
     * <br></br>
     * @param frameidx The requested frame idx (time index).
     * @return Gradient output data buffer for frame idx (time index).
     */
    public double[] getGradOutputBuffer(final int frameidx);
    /**
     * Gives the internal gradient input buffer for a given frame index (time index).
     * <br></br>
     * @param frameidx The requested frame idx (time index).
     * @return Gradient input buffer for frame idx (time index).
     */
    public double[] getGradInputBuffer(final int frameidx);
    /**
     * Returns true if the network has recurrent connections, false otherwise.
     */
    public boolean isRecurrent();
    /**
     * Returns true if the network computes online, false otherwise.
     */
    public boolean isOnline();
    /**
     * Return true is the network is bidirectional, false otherwise.
     */
    public boolean isBidirectional();
    /**
     * Returns true if the network computes offline, false otherwise.
     */
    public boolean isOffline();
    /**
     * Returns the number of input cells.
     */
    public int getInputCells();
    /**
     * Returns the number of output cells.
     */
    public int getOutputCells();
    /**
     * Returns the number of values cells.
     */
    public int getValueCells();
    /**
     * Returns the number of computing cells.
     */
    public int getComputingCells();
    /**
     * Returns the current weight vector. Note that the first
     * weight of the vector is still 1.0. 
     */
    public double[] getWeights();
    /**
     * Returns the number of weights. Note that the first
     * weight of the vector is still 1.0. The weight is not counted
     * by this method.
     */
    public int getWeightsNum();
    /**
     * Writes the given values into the internal weight buffer.
     * @param data Reference to the source double array.
     * @param offset Gives an offset of the weights within the data array.
     */
    public void writeWeights(final double[] data, final int offset);
    /**
     * Copies the weights from the internal weight buffer into the given array.
     * @param data Reference to the target double array.
     * @param offset Gives an offset for the target array.
     */
    public void readWeights(final double[] data, final int offset);
    /**
     * Returns the forward links. The links are optimized for
     * the forward pass. 
     */
    public int[] getLinks();
    /**
     * Returns the backward links. The links are optimized for the
     * backward pass.
     */
    public int[] getLinksRev();
    /**
     * Returns the number of links, which can be bigger than the number
     * of weights.
     */
    public int getLinksNum();
    /**
     * Rebuffers (reallocates) the internal data buffers. 
     * @param frames The number of time steps provided by the network.
     */
    public void rebuffer(final int frames);
    /**
     * Performs a numerical check for debugging the framework. Return false
     * if at least one value is NaN or Infinity.
     */
    public boolean numericalCheck();
    /**
     * Returns the NetStructure instance of the current network.
     * <br></br>
     * Warning: The variables of the instance are all public accessible. 
     * Changing them may affect the inner consistency of the network.
     */
    public NetStructure getStructure();
}
