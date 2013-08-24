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

package de.jannlab.examples.feedforward;

import java.util.Collections;
import java.util.Random;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.generator.MLPGenerator;
import de.jannlab.training.GradientDescent;
import de.jannlab.tools.ClassificationValidator;

/**
 * This examples shows how to learn the  parity problem with 
 * multilayer perceptrons (MLPs) using gradient descent with momentum term. 
 * This problem differs from the sequential parity problem, a well known toy 
 * problem for recurrent networks, in such a way, that samples are not 
 * sequences with variable lengths, but simple equal-sized patterns, which can
 * be learn by MLPs. Therefore we refer here as static parity problem.
 * <br></br><br></br>
 * For a given number of bits n, 2^n samples containing every possible bit
 * combination are generated. Initially the samples are shuffled. After that, a
 * small subset of the samples are drawn as test set, while the remaining
 * samples are used as train set.
 * <br></br><br></br>
 * This example also provides to observe the effect of over-fitting when using
 * too large hidden layers.
 * <br></br> 
 * @author Sebastian Otte, Dirk Krechel
 */
public class StaticParityExample {
    
    public static final double ON  = 1.0;
    public static final double OFF = 0.0;
    
    public static final int CLASS_EVEN = 0;
    public static final int CLASS_ODD  = 1;
    
    public static double bitAsDouble(final boolean bit) {
        return ((bit)?(ON):(OFF));
    }
    
    public static SampleSet generateData(final int bits, final Random rnd) {
        //
        final SampleSet result = new SampleSet();
        //
        final double[] input  = new double[bits];
        final double[] output = new double[2];
        //
        final int samples = 1 << bits;
        //
        for (int i = 0; i < samples; i++) {
            //
            // add 1 bit to bit-string.
            //
            for (int j = 0; j < bits; j++) {
                if (input[j] == OFF) {
                    //
                    // 0**** -> 1**** 
                    //
                    input[j] = ON;
                    break;
                } else {
                    //
                    // *1**** -> *0****
                    //
                    input[j] = OFF;
                }
            }
            //
            // generate parity sample
            //
            boolean parity = true;
            //
            for (int j = 0; j < bits; j++) {
                if (input[j] == ON) {
                    parity = !parity;
                }  
            }
            //
            if (parity) {
                output[CLASS_EVEN] = ON;
                output[CLASS_ODD]  = OFF; 
            } else {
                output[CLASS_EVEN] = OFF;
                output[CLASS_ODD]  = ON; 
            }
            //
            result.add(new Sample(input.clone(), output.clone()));
        }
        //
        Collections.shuffle(result, rnd);
        //
        return result;
    }
    
    
    public static void main(String[] args) {
        //
        final Random rnd = new Random(System.currentTimeMillis());
        //
        // network parameter:
        //
        // o A good generalization can be achieved using a 8,4,4,2 MLP. In many
        //   cases (depending on the random initialization) the testset accuracy
        //   reaches 100% and is thereby often better than the trainset accurary.
        // 
        // o To force over-fitting, try a 8,32,2 MLP. In some cases the MLP 
        //   learns the samples and not the problem, which results in a clearly
        //   worser testset accuracy.
        //
        final int input   = 8;
        final int hidden1 = 4;
        final int hidden2 = 4;
        //
        // Usually, a significant smaller test set is drawn from the
        // samples to prove the generalization result.
        //
        final int testset_size  = 96;
        //
        // training parameter
        //
        final int    epochs       = 5000;
        final double learningrate = 0.001;
        final double momentum     = 0.9;
        //
        // Generate a multilayer perceptron using the
        // MLPGenerator class. 
        //
        // o While tuning the network parameters, we found out
        //   that an additional bias of 1.0 for hidden and output
        //   layer works good on learning the problem. 
        //
        // o Note, that its absolutely
        //   necessary to randomly inialize the weights of 
        //   the MLP for symmetry-breaking. 
        // 
        //
        final MLPGenerator gen = new MLPGenerator();
        //
        gen.inputLayer(input);
        gen.hiddenLayer(hidden1, CellType.TANH, true, 1.0);
        gen.hiddenLayer(hidden2, CellType.TANH, true, 1.0);
        gen.outputLayer(2, CellType.TANH, true, 1.0);
        //
        final Net mlp = gen.generate();
        mlp.initializeWeights(rnd);
        //
        // Generate samples sets.
        //
        final SampleSet trainset = generateData(input, rnd);
        final SampleSet testset  = trainset.split(testset_size, rnd);
        //
        // Setup back-propagation trainer.
        //
        GradientDescent trainer = new GradientDescent();
        trainer.setEpochs(epochs);
        trainer.setLearningRate(learningrate);
        trainer.setMomentum(momentum);
        trainer.setPermute(true);
        trainer.setRnd(rnd);
        trainer.setTargetError(10E-5);
        trainer.setNet(mlp);
        trainer.setTrainingSet(trainset);
        //
        // perform training.
        //
        trainer.train();
        //
        // We use the ClassificationValidator to measure the recognition
        // accuracy on the training set as well as on the test set.
        //
        System.out.println();
        ClassificationValidator val = new ClassificationValidator(mlp);
        //
        for (Sample s : trainset) {
            val.apply(s);
        }
        System.out.println(
            "recognition accuracy on trainset: " + 
            (val.ratio() * 100.0) + "%"
        );
        //
        val.reset();
        //
        for (Sample s : testset) {
            val.apply(s);
        }
        System.out.println(
            "recognition accuracy on testset: " + 
            (val.ratio() * 100.0) + "%"
        );
    }
}
