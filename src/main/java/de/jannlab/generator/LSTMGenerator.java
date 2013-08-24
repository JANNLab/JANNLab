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
 * This class provides macro methods to easy setup LSTM networks. 
 * The class uses the NetCoreGenerator internal. 
 * <br></br>
 * @see NetCoreGenerator
 * @author Sebastian Otte
 */
public class LSTMGenerator implements NetGenerator { 
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
     * Creates an instance of LSTMGenerator.
     */
    public LSTMGenerator() {
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
     * Adds a hidden layer to the current model with n LSTM blocks.
     * <br></br>
     * @param blocksnum Number of LSTM blocks
     * @param gates CellType of the gates.
     * @param netin CellType of cell-input.
     * @param states CellType of cell-outpt.
     * @param peepholes Use peepholes?
     * @return Layer index.
     */
    public int hiddenLayer(
        final int blocksnum,
        final CellType gates,
        final CellType netin,
        final CellType states,
        final boolean peepholes
    ) {
        this.hiddenLayerCheck();
        final int layer = hiddenLayer(
            gen, blocksnum, gates, netin, states, 
            peepholes
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
    
    public static int inputLayer(final NetCoreGenerator gen, final int num) {
        return MLPGenerator.inputLayer(gen,  num);
    }
  
    /**
     * Creates a hidden layer with n LSTM blocks.
     * <br></br>
     * @param gen Instance of NetCoreGenerator.
     * @param blocksnum Number of LSTM blocks
     * @param gates CellType of the gates.
     * @param netin CellType of cell-input.
     * @param netout CellType of cell-output.
     * @param peepholes Use peepholes?
     * @return Layer index.
     */
    public static int hiddenLayer(
        final NetCoreGenerator gen,
        final int blocksnum,
        final CellType gates,
        final CellType netin,
        final CellType netout,
        final boolean peepholes
    ) {
        return hiddenLayer(
            gen, blocksnum, gates, netin, netout, peepholes, 
            false, 0.0, false, 0.0, false, 0.0
        );
    }
    
    /**
     * Creates a hidden layer with n LSTM blocks.
     * <br></br>
     * @param gen Instance of NetCoreGenerator.
     * @param blocksnum Number of LSTM blocks
     * @param gates CellType of the gates.
     * @param netin CellType of cell-input.
     * @param netout CellType of cell-output.
     * @param peepholes Use peepholes?
     * @param usegatesbias Add bias to the gates?
     * @param gatesbias The bias value for the gates.
     * @param useinputbias Add bias to input?
     * @param inputbias The bias value for the input.
     * @return Layer index.
     */
    public static int hiddenLayer(
        final NetCoreGenerator gen,
        final int blocksnum,
        final CellType gates,
        final CellType netin,
        final CellType netout,
        final boolean peepholes,
        final boolean usegatesbias,
        final double gatesbias,
        final boolean useinputbias,
        final double inputbias,
        final boolean useoutputbias,
        final double outputbias
    ) {
        //
        // create lstmlayer.
        //
        final int layer = gen.beginLayer();
        //
        // input gates, forget gates and input cells.
        //
        gen.inputConnectors();
        final int input_gates  = gen.cells(blocksnum, gates);
        final int forget_gates = gen.cells(blocksnum, gates);
        final int input_cells  = gen.cells(blocksnum, netin);
        gen.shiftComputationIndex();
        //
        // mul1, mul2 and dmul11, dmul12, dmul21, dmul22.
        //
        gen.nonConnectors();
        final int dmul11 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        final int dmul12 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        final int dmul21 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        final int dmul22 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        gen.shiftComputationIndex();
        final int mul1 = gen.cells(blocksnum, CellType.MULTIPLICATIVE);
        final int mul2 = gen.cells(blocksnum, CellType.MULTIPLICATIVE);
        gen.shiftComputationIndex();
        //
        // state.
        //
        final int state_cells = gen.cells(blocksnum, CellType.LINEAR);
        gen.shiftComputationIndex();
        //
        // state-squash and output gates.
        //
        final int output_squash = gen.cells(blocksnum, netout);
        gen.inputConnectors();
        final int output_gates = gen.cells(blocksnum, gates);
        gen.shiftComputationIndex();
        //
        // mul3 and dmul31, dmul32
        //
        gen.nonConnectors();
        final int dmul31 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        final int dmul32 = gen.cells(blocksnum, CellType.DMULTIPLICATIVE);
        gen.shiftComputationIndex();
        //
        gen.outputConnectors();
        final int mul3 = gen.cells(blocksnum, CellType.MULTIPLICATIVE);
        //
        // define links.
        //
        gen.link(forget_gates, dmul11, blocksnum);
        gen.link(input_gates,  dmul21, blocksnum);
        gen.link(input_cells,  dmul22, blocksnum);
        //
        gen.link(dmul11, mul1, blocksnum);
        gen.link(dmul12, mul1, blocksnum);
        gen.link(mul1, state_cells, blocksnum);
        //
        gen.link(state_cells, dmul12, blocksnum);
        //
        gen.link(dmul21, mul2, blocksnum);
        gen.link(dmul22, mul2, blocksnum);
        gen.link(mul2, state_cells, blocksnum);
        //
        gen.link(state_cells, output_squash, blocksnum);
        gen.link(output_squash, dmul31, blocksnum);
        gen.link(output_gates, dmul32, blocksnum);
        gen.link(dmul31, mul3, blocksnum);
        gen.link(dmul32, mul3, blocksnum);
        //
        // use weighted peepholes?
        //
        if (peepholes) {
            gen.weightedLink(state_cells, forget_gates, blocksnum);
            gen.weightedLink(state_cells, input_gates, blocksnum);
            gen.weightedLink(state_cells, output_gates, blocksnum);
        }
        //
        // add biases if requested.
        //
        if (usegatesbias) {
            //
            final int bias = gen.valueCell();
            gen.assign(bias, gatesbias);
            //
            // link bias to the gates.
            //
            gen.weightedLink(bias, 1, forget_gates, blocksnum);
            gen.weightedLink(bias, 1, input_gates, blocksnum);
            gen.weightedLink(bias, 1, output_gates, blocksnum);
        }
        //
        if (useinputbias) {
            //
            final int bias = gen.valueCell();
            gen.assign(bias, inputbias);
            //
            // link bias to the input.
            //
            gen.weightedLink(bias, 1, input_cells, blocksnum);
        }
        //
        if (useoutputbias) {
            //
            final int bias = gen.valueCell();
            gen.assign(bias, outputbias);
            //
            // link bias to output.
            //
            gen.weightedLink(bias, 1, output_squash, blocksnum);
        }
        //
        gen.endLayer();
        //
        return layer;
    }
    
    /**
     * Creates a output layer with the given NetCoreGenerator with bias support.
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
