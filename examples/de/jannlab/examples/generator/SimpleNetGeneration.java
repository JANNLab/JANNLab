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
import de.jannlab.generator.NetCoreGenerator;
import de.jannlab.misc.TimeCounter;

/**
 * This examples demonstrates handling the NetCoreGenerator.
 * It also shows the runtime performance of generating networks.
 * <br></br>
 * @author Sebastian Otte
 */
public class SimpleNetGeneration {
    
    public static final TimeCounter TC = new TimeCounter(); 
    
    public static void main(String[] args) {
        NetCoreGenerator gen = new NetCoreGenerator();
        //
        // input layer.
        //
        int inputsize  = 7;
        int inputlayer = gen.beginLayer();
        int inputcells = gen.valueCells(inputsize);
        gen.endLayer();
        gen.defineInputLayer(inputlayer);
        //
        // add bias neuron.
        //
        int bias = gen.valueCell();
        gen.assign(bias, -1);
        //
        // hidden layer.
        //
        int hiddensize  = 30;
        @SuppressWarnings("unused")
        int hiddenlayer = gen.beginLayer();
        int hiddencells = gen.sigmoidCells(hiddensize);
        gen.endLayer();
        //
        // output layer.
        //
        int outputlayer = gen.beginLayer();
        int outputcells = gen.sigmoidCells(2);
        gen.endLayer();
        gen.defineOutputLayer(outputlayer);
        //
        // connect input with output layer and non input layer with bias.
        //
        gen.weightedLink(inputcells,  inputsize, hiddencells, hiddensize);
        gen.weightedLink(hiddencells, hiddensize, outputcells, 1);
        //
        gen.weightedLink(bias, 1, hiddencells, hiddensize);
        gen.weightedLink(bias, 1, outputcells, 1);
        //
        // generate network and measure time.
        //
        TC.reset();
        Net net = gen.generate();
        final double time_gen = TC.valueMilliDouble();
        //
        // print generated network.
        //
        System.out.println(net);
        System.out.println("generation time    : " + time_gen + " ms.");
    }
    
}
