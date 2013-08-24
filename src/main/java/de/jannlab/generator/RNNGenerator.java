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

package de.jannlab.generator;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.generator.exception.NoInputLayerDefined;
import de.jannlab.generator.exception.NoModificationAllowed;

/**
 * This class provides macro methods to easy setup RNNs. 
 * The class uses the NetCoreGenerator internal. 
 * <br></br>
 * @see NetCoreGenerator
 * @author Sebastian Otte
 */
public class RNNGenerator implements NetGenerator { 
    /**
     * The core generator.
     */
    private NetCoreGenerator gen = new NetCoreGenerator();
    //
    private boolean sealed = false;
    private int lastlayer  = -1;
 
    /**
     * Returns the internal core generator.
     * @return Instance of CoreGenerator.
     */
    public NetCoreGenerator getCoreGenerator() {
        return this.gen;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.gen.clear();
        this.sealed    = false;
        this.lastlayer = -1;
    }
    
    /**
     * Creates an instance of MLPGenerator.
     */
    public RNNGenerator() {
        this.clear();
    }
    /**
     * Adds a input layer to the current model.
     * @param num The number of input neurons.
     * @return Layer index.
     */
    public int inputLayer(final int num) {
        final int layer = MLPGenerator.inputLayer(this.gen, num);
        this.updateLastLayer(layer);
        return layer;
    }
    /**
     * Links last with current layer.
     * <br></br>
     * @param layer Current layer index.
     */
    private void linkWithLastLayer(final int layer) {
        this.gen.weightedLinkLayer(this.lastlayer, layer);
    }
    /**
     * Links current with current layer (recurrent).
     * <br></br>
     * @param layer Current layer index.
     */
    private void linkWithCurrentLayer(final int layer) {
        this.gen.weightedLinkLayer(layer, layer);
    }
    /**
     * Check if hidden layer is well defined.  
     */
    private void hiddenLayerCheck() {
        if (this.lastlayer < 0) {
            throw new NoInputLayerDefined();
        }
        if (this.sealed) {
            throw new NoModificationAllowed();
        }
    }  
    /**
     * Updates the last layer index to the current layer index.
     * @param layer Layer index.
     */
    private void updateLastLayer(final int layer) {
        this.lastlayer = layer;
    }
    /**
     * Checks if output layer if well defined.
     */
    private void outputLayerCheck() {
        this.hiddenLayerCheck();
    }
  
    /**
     * Adds a new hidden layer (with sigmoid cells) to the current model.
     * @param num Number of hidden neurons.
     * @return Layer index.
     */
    public int hiddenLayer(final int num) {
        return this.hiddenLayer(num, CellType.SIGMOID);
    }

    /**
    * Adds a new recurrent hidden layer to the current model of given cell type.
    * @param num Number if hidden neurons.
    * @param type Instance of CellType.
    * @return Layer index.
    */
    public int hiddenLayer(final int num, final CellType type) {
        return this.hiddenLayer(num, type, false, 0.0);
    }
    /**
     * Adds a new recurrent hidden layer to the current model of given cell type with bias support.
     * @param num Number if hidden neurons.
     * @param type Instance of CellType.
     * @param usebias True if bias sould be used.
     * @param bias Value of the bias.
     * @return Layer index.
     */    
    public int hiddenLayer(
            final int num, final CellType type, 
            final boolean usebias, final double bias
    ) {
        this.hiddenLayerCheck();
        final int layer = MLPGenerator.hiddenLayer(
            this.gen, num, type, usebias, bias
        );
        //
        // create feed forward links. 
        //
        this.linkWithLastLayer(layer);
        //
        // create recurrent links.
        //
        this.linkWithCurrentLayer(layer);
        this.updateLastLayer(layer);
        return layer;
    }
    /**
     * Adds a output layer (with sigmoid cells) to the current model.
     * @param num Number of hidden neurons.
     * @return Layer index.
     */    
    public int outputLayer(final int num) {
        return this.outputLayer(num, CellType.SIGMOID);
    }
   /**
    * Adds a new hidden layer to the current model of given cell type.
    * @param num Number if hidden neurons.
    * @param type Instance of CellType.
    * @return Layer index.
    */
    public int outputLayer(final int num, final CellType type) {
        return this.outputLayer(num, type, false, 0.0);
    }
    /**
     * Adds a output layer to the current model of given cell type with bias support.
     * @param num Number if hidden neurons.
     * @param type Instance of CellType.
     * @param usebias True if bias sould be used.
     * @param bias Value of the bias.
     * @return Layer index.
     */  
    public int outputLayer(
            final int num, final CellType type, 
            final boolean usebias, final double bias
    ) {
        this.outputLayerCheck();
        final int layer = MLPGenerator.outputLayer(
            this.gen, num, type, usebias, bias
        );
        this.linkWithLastLayer(layer);
        this.updateLastLayer(layer);
        this.sealed = true;
        return layer;  
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Net generate() {
        //
        Net result = this.gen.generate();
        return result;
    }

    //-------------------------------------------------------------------------
    // STATIC MACRO METHODS.
    //-------------------------------------------------------------------------
   
    /**
     * Macro for building a bias neuron.
     * @param gen Instance of NetCoreGenerator.
     * @param value Value of the bias neuron.
     * @return Cell index.
     */
    public static int bias(final NetCoreGenerator gen, final double value) {
        return MLPGenerator.bias(gen, value);
    }
    
    /**
     * Creates an input layer with the given NetCoreGenerator.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @return Layer index.
     */
    public static int inputLayer(final NetCoreGenerator gen, final int num) {
        return MLPGenerator.inputLayer(gen, num);
    }
    /**
     * Creates a hidden layer with the given NetCoreGenerator.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @param type Instance of CellType.
     * @return Layer index.
     */
    public static int hiddenLayer(
            final NetCoreGenerator gen, 
            final int num,
            final CellType type
    ) {
        return MLPGenerator.hiddenLayer(gen, num, type);
    }
    /**
     * Creates a hidden layer with the given NetCoreGenerator with bias support.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @param type Instance of CellType.
     * @param usebias Enable a bias neuron for this layer.
     * @param bias Gives the value for the bias neuron.
     * @return Layer index.
     */
    public static int hiddenLayer(
            final NetCoreGenerator gen, 
            final int num,
            final CellType type,
            final boolean usebias,
            final double bias
    ) {
        return MLPGenerator.hiddenLayer(gen, num, type, usebias, bias);
    }
    
    /**
     * Creates an output layer with the given NetCoreGenerator.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @param type Instance of CellType.
     * @return Layer index.
     */
    public static int outputLayer(
            final NetCoreGenerator gen, 
            final int num,
            final CellType type
    ) {
        return MLPGenerator.outputLayer(gen, num, type);
    }
    /**
     * Creates a output layer with the given NetCoreGenerator with bias support.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @param type Instance of CellType.
     * @param usebias Enable a bias neuron for this layer.
     * @param bias Gives the value for the bias neuron.
     * @return Layer index.
     */
    public static int outputLayer(
            final NetCoreGenerator gen, 
            final int num,
            final CellType type,
            final boolean usebias,
            final double bias
    ) {
        return MLPGenerator.outputLayer(gen, num, type, usebias, bias);
    }
    
}
