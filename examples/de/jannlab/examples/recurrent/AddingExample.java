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

import java.io.IOException;
import java.util.Random;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.generator.LSTMGenerator;
import de.jannlab.training.GradientDescent;
import de.jannlab.misc.TimeCounter;
import de.jannlab.tools.RegressionValidator;

/**
 * In this example the ability of LSTMs to learn adding from samples trained by
 * gradient descent is proven. We are using a modified variation of the adding 
 * problem from [1]. 
 * <br></br>
 * For this experiment samples are generated as follows:
 * <br></br>
 * <ul>
 * <li>Given a sequence of T vectors from R2, where the x part of each sequence is
 *   randomly chosen from [0,1].</li>
 * <li>Two indices are rolled: i1 from {0, ..., 9}, and i2 from {0, ..., T/2}.</li>
 * <li>In the y part the values at the indices i1 and i2 are set to 1.0.</li>
 * <li>The target value of a sequence is (x[i1] + x[i2]) / 2.
 * </ul>
 * <br></br>
 * [1] S. Hochreiter und J. Schmidhuber, "LSTM Can Solve Hard Long Time Lag Problems", 
 *     ADVANCES IN NEURAL INFORMATION PROCESSING SYSTEMS 9, S. 473--479, 1997.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public class AddingExample {
    
    private static TimeCounter TC = new TimeCounter();
    private static Random rnd = new Random(0L);
    
    private static int x(final int i) {
        return (i * 2);
    }
    
    private static int y(final int i) {
        return (i * 2) + 1;
    }
    
    public static Sample generateSample(final int length) {
        double[] data = new double[length * 2];
        //
        for (int i = 0; i < length; i++) {
            data[x(i)] = (rnd.nextDouble()); //(rnd.nextDouble() * 2.0) - 1.0;
        }
        //
        // select i1
        //
        final int i1 = rnd.nextInt(10);
        final int i2 = rnd.nextInt((length) / 2);
        //
        data[y(i1)] = 1.0;
        data[y(i2)] = 1.0;
        //
        double[] target = {((data[x(i1)] + data[x(i2)])/2.0)};
        return new Sample(data, target, 2, length, 1, 1);
    }
    
    public static SampleSet generate(final int n, final int length) {
        SampleSet set = new SampleSet();
        //
        for (int i = 0; i < n; i++) {
            set.add(generateSample(length));
        }
        //
        return set;
    }
  
    public static Net LSTM(final int in, final int hid, final int out) {
        //
        LSTMGenerator gen = new LSTMGenerator();
        gen.inputLayer(in);
        gen.hiddenLayer(
            hid, CellType.SIGMOID, CellType.TANH, CellType.TANH, true
        );
        gen.outputLayer(out,  CellType.TANH);
        //
        return gen.generate();

    }
    
 
    public static void main(String[] args) throws IOException {
        //
        // config parameter.
        //
        final int epochs = 200;
        //
        final double learningrate = 0.001;
        final double momentum     = 0.9;
        //
        final int length    = 30;
        final int trainsize = 5000;  
        //
        // build network. only 1 LSTM block
        // is required to learn the problem.
        // using 2 or 3 blocks slightly increases
        // the recognition accuracy.
        //
        Net net = LSTM(2, 1, 1);
        //
        net.rebuffer(length);
        net.initializeWeights();
        //
        // even much longer sequences are tested on the
        // trained network later.
        //
        SampleSet trainset = generate(trainsize, length);
        SampleSet testset  = generate(1000, length);
        SampleSet testset2 = generate(1000, 100);
        SampleSet testset3 = generate(1000, 1000);
        //
        // setup network.
        //
        final int maxlength = Math.max(trainset.maxSequenceLength(), testset.maxSequenceLength());
        net.initializeWeights(rnd);
        net.rebuffer(maxlength);
        //
        // setup trainer.
        //
        GradientDescent trainer = new GradientDescent();
        trainer.setNet(net);
        trainer.setRnd(rnd);
        trainer.setPermute(true);
        trainer.setTrainingSet(trainset);
        trainer.setLearningRate(learningrate);
        trainer.setMomentum(momentum);
        trainer.setEpochs(epochs);
        //
        TC.reset();
        //
        // perform training.
        //
        trainer.train();
        //
        System.out.println(
            "training time: " + 
            TC.valueMilliDouble() +
            " ms."
        );
        //
        // evaluate learning success.
        //
        final double thres = 0.04;
        {
            RegressionValidator v = new RegressionValidator(net, thres);
            for (Sample s : testset) {
                v.apply(s);
            }
            
            System.out.println("validation set result (1): " + (v.ratio() * 100) + "%.");
        }
        {
            RegressionValidator v = new RegressionValidator(net, thres);
            for (Sample s : testset2) {
                v.apply(s);
            }
            System.out.println("validation set result (2): " + (v.ratio() * 100) + "%.");
        }
        {
            RegressionValidator v = new RegressionValidator(net, thres);
            for (Sample s : testset3) {
                v.apply(s);
            }
            System.out.println("validation set result (3): " + v.ratio() * 100 + "%.");
        }

    }
    
    
}
