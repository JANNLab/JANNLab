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

package de.jannlab.examples.recurrent;

import java.util.Random;


import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.generator.RNNGenerator;
import de.jannlab.misc.TimeCounter;
import de.jannlab.tools.ClassificationValidator;
import de.jannlab.training.GradientDescent;

/**
 * This simple example demonstrates how Differential Evolution
 * can be used for learning simple sequential xor.
 * <br></br>
 * @author Sebastian Otte
 */
public class SequentialXorExample {
    
    private static TimeCounter TC = new TimeCounter();
    private static Random rnd = new Random(0L);
    
    
    public static void incrBit(final boolean[] data, final int offset) {
        if (offset >= data.length) return;
        //
        if (!data[offset]) {
            data[offset] = true;
            return;
        } 
        //
        data[offset] = false;
        incrBit(data, offset + 1);
    }
    
    public static boolean xor(final boolean[] data) {
        //
        boolean value = false;
        //
        for (int i = 0; i < data.length; i++) {
            value ^= data[i];
        }
        //
        return value;
    }
    
    public static SampleSet generateSamples(final int length) {
        SampleSet set = new SampleSet();
        //
        int size = 1 << length;
        boolean[] data = new boolean[length];
        //
        for (int i = 0; i < size; i++) {
            Sample s = new Sample(1, length, 1, 1);
            //
            for (int j = 0; j < data.length; j++) {
                s.getInput()[j] = (data[j]?(1.0):(0.0));
            }
            //
            s.getTarget()[0] = (xor(data)?(1.0):(0.0));
            incrBit(data, 0);
            set.add(s);
        }
        //
        return set;
    }
    

    
    /**
     * This static method generated a Recurrent Neural Network
     * with a specific number of hidden neurons. Hidden and output 
     * layer a activated with the standard sigmoidal function of
     * the range [-1, 1].
     */
    public static Net RNN(final int hidden) {
        RNNGenerator gen = new RNNGenerator();
        gen.inputLayer(1);
        gen.hiddenLayer(hidden, CellType.SIGMOID1);
        gen.outputLayer(1, CellType.SIGMOID1);
        Net net = gen.generate();
        return net;
    }
    
    public static void main(String[] args) {
        //
        // config parameter.
        //
        final int epochs = 80000;
        //
        final double learningrate = 0.02;
        final double momentum     = 0.9;
        //
        // generate samples.
        //
        SampleSet samples = generateSamples(2);
        //
        // generate network.
        //
        final Net net = RNN(2);
        //
        net.rebuffer(samples.maxSequenceLength());
        net.initializeWeights(rnd);
        //
        // setup trainer.
        //
        GradientDescent trainer = new GradientDescent();
        trainer.setNet(net);
        trainer.setRnd(rnd);
        trainer.setPermute(false);
        trainer.setTrainingSet(samples);
        trainer.setLearningRate(learningrate);
        trainer.setMomentum(momentum);
        trainer.setEpochs(epochs);
        //
        TC.reset();
        //
        // perform training.
        //
        trainer.train();
        System.out.println();
        //
        System.out.println(
            "training time: " + 
            TC.valueMilliDouble() +
            " ms."
        );
        //
        // evaluate learning success.
        //
        final double thres = 0.1;
        ClassificationValidator v = new ClassificationValidator(net, thres);
        for (Sample s : samples) {
            System.out.println(v.applyAsString(s, true));
            System.out.println();
        }
        System.out.println("traning results: " + (v.ratio() * 100) + "%.");
        
    }
}