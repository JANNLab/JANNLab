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
import de.jannlab.generator.exception.GeneratingFailed;
import de.jannlab.generator.exception.NoInputLayerDefined;
import de.jannlab.generator.exception.NoModificationAllowed;
import de.jannlab.generator.exception.OnlyPerceptronsAllowed;

/**
 * This class provides macro methods to easy setup MLPs. 
 * The class uses the NetCoreGenerator internal. 
 * <br></br>
 * @see NetCoreGenerator
 * @author Sebastian Otte
 */
public class MLPGenerator implements NetGenerator { 
    /**
     * The core generator.
     */
    private NetCoreGenerator gen = new NetCoreGenerator();
    
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
        this.sealed = false;
        this.lastlayer = -1;
    }

    /**
     * Creates an instance of MLPGenerator.
     */
    public MLPGenerator() {
        this.clear();
    }
    /**
     * Adds a input layer to the current model.
     * @param num The number of input neurons.
     * @return Layer index.
     */
    public int inputLayer(final int num) {
        final int layer = inputLayer(this.gen, num);
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
    * Adds a new hidden layer to the current model of given cell type.
    * @param num Number if hidden neurons.
    * @param type Instance of CellType.
    * @return Layer index.
    */
    public int hiddenLayer(final int num, final CellType type) {
        return this.hiddenLayer(num, type, false, 0.0);
    }
    /**
     * Adds a new hidden layer to the current model of given cell type with bias support.
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
        final int layer = hiddenLayer(
            this.gen, num, type, usebias, bias
        );
        this.linkWithLastLayer(layer);
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
        final int layer = outputLayer(
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
        if (result.isRecurrent()) {
            throw new GeneratingFailed();
        }
        //
        return result;
    }

    //-------------------------------------------------------------------------
    // STATIC MACRO METHODS.
    //-------------------------------------------------------------------------
    
    /**
     * Checks the perceptron-constraint for a given cell type.
     * @param type Instance of CellType.
     */
    private static void perceptronCheck(final CellType type) {
        if (!type.perceptron) {
            throw new OnlyPerceptronsAllowed();
        }
    }
    
    /**
     * Macro for building a bias neuron.
     * @param gen Instance of NetCoreGenerator.
     * @param value Value of the bias neuron.
     * @return Cell index.
     */
    public static int bias(final NetCoreGenerator gen, final double value) {
        final int cell = gen.valueCell();
        gen.assign(cell,  value);
        return cell;
    }
    /**
     * Creates an input layer with the given NetCoreGenerator.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @return Layer index.
     */
    public static int inputLayer(final NetCoreGenerator gen, final int num) {
        final int layer = gen.beginLayer();
        gen.valueCells(num);
        gen.endLayer();
        gen.defineInputLayer(layer);
        return layer;
    }
    /**
     * Creates an non input layer with the given NetCoreGenerator with bias support.
     * @param gen Instance of NetCoreGenerator.
     * @param num Number of input neurons.
     * @param type Instance of CellType.
     * @param usebias Enable a bias neuron for this layer.
     * @param bias Gives the value for the bias neuron.
     * @return Layer index.
     */
    private static int nonInputLayer(
            final NetCoreGenerator gen, 
            final int num,
            final CellType type,
            final boolean usebias,
            final double bias
    ) {
        perceptronCheck(type);
        final int layer = gen.beginLayer();
        gen.cells(num, type);
        gen.endLayer();
        //
        if (usebias) {
            //
            // set bias.
            //
            final int bcell = bias(gen, bias);
            //
            // connect to layer.
            //
            gen.weightedLink(bcell, 1, gen.getLayerCells(layer), num);
        }
        return layer;
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
        return nonInputLayer(gen, num, type, false, 0.0);
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
        return nonInputLayer(gen, num, type, usebias, bias);
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
        final int layer = nonInputLayer(gen, num, type, false, 0.0);
        gen.defineOutputLayer(layer);
        return layer;
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
        final int layer = nonInputLayer(gen, num, type, usebias, bias);
        gen.defineOutputLayer(layer);
        return layer;
    }
}
