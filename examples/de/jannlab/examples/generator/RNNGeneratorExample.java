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
import de.jannlab.generator.RNNGenerator;

/**
 * This example demonstrates how to setup standard RNNs. Hereby, all
 * hidden-layers are fully self-recurrently connected.
 * <br></br>
 * @author Sebastian Otte
 */
public class RNNGeneratorExample {
    
    public static void main(String[] args) {
        RNNGenerator gen = new RNNGenerator();
        //
        // setup layers.
        //
        gen.inputLayer(3);
        gen.hiddenLayer(8, CellType.TANH);
        gen.outputLayer(2, CellType.TANH);
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
    
}
