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

import java.io.IOException;
import java.util.Random;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.generator.MLPGenerator;
import de.jannlab.training.GradientDescent;
import de.jannlab.misc.DoubleTools;

/**
 * A simple example of learning XOR with a small multilayer perceptron.
 * Using sigmoidal function of range [-2, 2] for activation provides the 
 * fastest convergence speed in training. Tanh works also nice. 
 * Note, that when standard sigmoid is used for activation, a bias neuron 
 * has to be added.
 * <br></br>
 * @author Sebastian Otte, Dirk Krechel
 */
public class XORExample {
    
    public static double[] array(double ...x) { return x; }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //
        Random rnd = new Random(0L);
        //
        // generator simple xor trainset.
        //
        SampleSet set = new SampleSet();
        set.add(new Sample(array(0, 0), array(0)));
        set.add(new Sample(array(0, 1), array(1)));
        set.add(new Sample(array(1, 0), array(1)));
        set.add(new Sample(array(1, 1), array(0)));
        //
        // generate simple mlp.
        //
        MLPGenerator gen = new MLPGenerator();
        gen.inputLayer(set.get(0).getInputSize());
        gen.hiddenLayer(2, CellType.SIGMOID2);
        gen.outputLayer(1, CellType.SIGMOID2);
        Net mlp = gen.generate();
        mlp.initializeWeights(rnd);
        //
        // setup trainer.
        //
        GradientDescent trainer = new GradientDescent();
        trainer.setTrainingSet(set);
        trainer.setNet(mlp);
        trainer.setRnd(rnd);
        trainer.setEpochs(500);
        trainer.setTargetError(0);
        trainer.setLearningRate(0.1);
        trainer.setMomentum(0.5);
        //
        // perform training and print final error.
        //
        trainer.train();
        //
        // print computation results.
        //
        double[] out = new double[1];
        for (Sample s : set) {
            mlp.reset();
            s.mapInput(mlp.inputPort());
            mlp.compute();
            mlp.output(out,  0);
            System.out.println(
                DoubleTools.asString(s.getInput(), 1) +
                " ==> " + 
                DoubleTools.asString(out, 1)
            );
        }
    }

}
