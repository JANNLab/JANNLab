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

package de.jannlab.examples.generator;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.generator.LSTMGenerator;
import de.jannlab.generator.NetCoreGenerator;

/**
 * This example demonstrates how to setup LSTM networks.
 * <br></br>
 * @author Sebastian Otte
 */
public class LSTMGeneratorExample {
    
    public static void main(String[] args) {
        LSTMGenerator gen = new LSTMGenerator();
        //
        // define layers.
        //
        gen.inputLayer(5);
        gen.hiddenLayer(5, CellType.SIGMOID, CellType.TANH, CellType.TANH, true);
        gen.outputLayer(3, CellType.SIGMOID, true, -1.0);
        //
        // generate network.
        //
        Net net = gen.generate();
        //
        // set max. sequences length. here 100 is just an example
        // value.
        //
        net.rebuffer(100);
        System.out.println(net);
    }
    
    // ######################################################################
    // The following method contains the code used by the LSTMGenerator
    // for setting up an LSTM hidden-layer. As you can see, LSTMs block
    // are modeled only with simple buildings blocks, such that the LSTMs 
    // blocks could be easily modified.
    // ######################################################################
    
    /**
     * Creates a hidden layer with n LSTM blocks.
     * <br></br>
     * @param gen Instance of NetCoreGenerator.
     * @param blocksnum Number of LSTM blocks
     * @param gates CellType of the gates.
     * @param netin CellType of cell-input.
     * @param states CellType of cell-outpt.
     * @param peepholes Use peepholes?
     * @return Layer index.
     */
    public static int hiddenLayer(
        final NetCoreGenerator gen,
        final int blocksnum,
        final CellType gates,
        final CellType netin,
        final CellType states,
        final boolean peepholes
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
        final int state_squash = gen.cells(blocksnum, states);
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
        gen.link(state_cells, state_squash, blocksnum);
        gen.link(state_squash, dmul31, blocksnum);
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
        gen.endLayer();
        //
        return layer;
    }    
    
    
}
