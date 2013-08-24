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
import de.jannlab.training.RandomSearch;
import de.jannlab.tools.ClassificationValidator;
import de.jannlab.tools.NetTools;

/**
 * The sequential parity problem is a typical sequence classification
 * problem, often used to demonstrate the ability of recurrent networks
 * to "connect" input events over time. Given a sequence over {0, 1}
 * the task is to determine either the number of 1s is even or odd.
 * In this example the problem is learned by an RNN via RandomSearch.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public class SequentialParityExample {
    //
    public static final double THRESHOLD = 0.5;
    public static final double[] EVEN    = {1.0, 0.0};
    public static final double[] ODD     = {0.0, 1.0};
    
    public static double[] generateSequence(
            final int length, 
            final Random rnd
    ) {
        double[] result = new double[length];
        //
        for (int i = 0; i < length; i++) {
            result[i] = 0.0; 
        }
        int n = rnd.nextInt((length / 2) + 1);
        for (int i = 0; i < n; i++) {
            result[rnd.nextInt(length)] = rnd.nextBoolean()?(1.0):(0.0); 
        }
        //
        return result;
    }
    
    public static boolean parity(final double[] data) {
        int ctr = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= THRESHOLD) {
                ctr++;
            }
        }
        return (ctr % 2) == 0;
    }
    
    public static Sample generateSample(
            final int length, 
            final Random rnd
    ) {
        //
        final double[] input  = generateSequence(length, rnd);
        final double[] target = (parity(input)?(EVEN):(ODD));
        //
        return new Sample(input, target, 1, length, 2, 1);
    }
    
    private static SampleSet generateSamples(
            final int samples, 
            final int length, 
            final Random rnd
    ) {
        final SampleSet set = new SampleSet();
        //
        for (int i = 0; i < samples; i++) {
            final Sample s = generateSample(length, rnd);
            set.add(s);
        }
        //
        return set;
    }
    
    public static double runExperiment(
            final SampleSet trainset,
            final SampleSet testset,
            final Net net,
            final Random rnd,
            final int epochs,
            final double lbd,
            final double ubd
    ) {
        //
        // setup network.
        //
        final int maxlength = Math.max(trainset.maxSequenceLength(), testset.maxSequenceLength());
        net.initializeWeights(rnd);
        net.rebuffer(maxlength);
        //
        // setup trainer.
        //
        RandomSearch trainer = new RandomSearch();
        trainer.setEpochs(epochs);
        trainer.setSearchLbd(lbd);
        trainer.setSearchUbd(ubd);
        trainer.setRnd(rnd);
        trainer.setNet(net);
        trainer.setTrainingSet(trainset);
        trainer.train();
        //
        // evaluate training.
        //
        final double error = NetTools.computeError(net, testset);
        return error;
    }
    
    
    public static Net RNN(final int in, final int hid, final int out) {
        RNNGenerator gen = new RNNGenerator();
        gen.inputLayer(in);
        gen.hiddenLayer(hid, CellType.SIGMOID, true, -1.0);
        gen.outputLayer(out, CellType.SIGMOID, true, -1.0);
        gen.getCoreGenerator().weightedLinkLayer(0, 2);
        return gen.generate();
    }
    
    public static void main(String[] args) {
        //
        // configure parameter.
        //
        final int length        = 20;
        final int trainset_size = 100;
        final int epochs        = 30000;
        //
        final Random rnd = new Random(System.currentTimeMillis());
        //
        final double weightslbd  = -100.0;
        final double weightsubd  = 100.0;      
        //
        // generate networks.
        //
        final Net rnn  = RNN(1, 3, 2);
        //
        // build sample sets.
        //
        final SampleSet trainset = generateSamples(
            trainset_size, length, rnd
        );
        System.out.println(trainset);
        final SampleSet testset = trainset;
        //  
        // evaluate learning success.
        //
        final double rnnerror = runExperiment(
            trainset, trainset, rnn, rnd, epochs, weightslbd, weightsubd
        );
        System.out.println("rnn error : " + rnnerror);
        {
            ClassificationValidator f = new ClassificationValidator(rnn);
            for (int i = 0; i < testset.size(); i++) {
                Sample s = testset.get(i);
                f.apply(s);
            }   
            double ratio = f.ratio();
            System.out.println("testset result (rnn): " + ratio * 100.0 + "%.");
        }
    }
    
}
