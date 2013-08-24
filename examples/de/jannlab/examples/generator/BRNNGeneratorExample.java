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
import de.jannlab.generator.MLPGenerator;
import de.jannlab.generator.NetCoreGenerator;

/**
 * This example demonstrates how to setup bidirectional
 * networks.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public class BRNNGeneratorExample {
    
    public static void main(String[] args) {
        //
        NetCoreGenerator gen = new NetCoreGenerator();
        //
        // define layers.
        //
        int input   = MLPGenerator.inputLayer(gen, 2);
        int hidden1 = MLPGenerator.hiddenLayer(gen, 4, CellType.TANH);
        int hidden2 = MLPGenerator.hiddenLayer(gen, 4, CellType.TANH);
        int output  = MLPGenerator.outputLayer(gen, 2, CellType.TANH);
        //
        // connect layers.
        //
        gen.weightedLinkLayer(input, hidden1);
        gen.weightedLinkLayer(input, hidden2);
        gen.weightedLinkLayer(hidden1, hidden1);
        gen.weightedLinkLayer(hidden2, hidden2);
        gen.weightedLinkLayer(hidden1, output);
        gen.weightedLinkLayer(hidden2, output);
        //
        // the following call, which can be used for LSTM layers analogous, 
        // makes the network bidirectional
        //
        gen.defineLayerAsReversed(hidden2);
        //
        final Net net = gen.generate();
        //
        // set max. sequences length. here 100 is just an example
        // value.
        //
        net.rebuffer(100);        
        System.out.println(net);
    }
    
}
