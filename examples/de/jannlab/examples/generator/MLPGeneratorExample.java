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

/**
 * This example demonstrates how to setup standard MLPs.
 * <br></br>
 * @author Sebastian Otte
 */
public class MLPGeneratorExample {
    
    public static void main(String[] args) {
        MLPGenerator gen = new MLPGenerator();
        //
        // setup layers.
        //
        gen.inputLayer(2);
        gen.hiddenLayer(2, CellType.TANH);
        gen.outputLayer(2, CellType.SIGMOID, true, -1.0);
        //
        // just generate.
        //
        Net net = gen.generate(); 
        System.out.println(net);
    }
    
}
