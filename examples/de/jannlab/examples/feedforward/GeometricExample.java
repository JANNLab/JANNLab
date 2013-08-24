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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.examples.math.Vector2f;
import de.jannlab.generator.MLPGenerator;
import de.jannlab.training.GradientDescent;
import de.jannlab.training.NetTrainer;
import de.jannlab.training.NetTrainerListener;
import de.jannlab.tools.ClassificationValidator;

/**
 * This examples demonstrates the generalization ability of neural
 * networks on learning geometric figures (here classification of circles). 
 * Through a visualization during the training, the learning process 
 * (convergence) can be observed. The visualization also illustrates out well 
 * the effect of an insufficient number of hidden neurons .
 * <br></br>
 * The samples are points positioned on the uni-square and each point
 * corresponds to one of two classes. The first class is defined by three
 * circles (white drawn points) and second class is given by all remaining 
 * samples (gray drawn points). These classes are obviously not linear
 * separable.
 * The more colored the background at a position (x,y) is, the more the MLP 
 * "thinks" this point would correspond to the first class. 
 * 
 * @author Sebastian Otte, Dirk Krechel
 */
public class GeometricExample {
    
    public static final Vector2f c1 = new Vector2f(0.4f, 0.3f);
    public static final double   r1 = 0.2;
    public static final Vector2f c2 = new Vector2f(0.7f, 0.6f);
    public static final double   r2 = 0.12;
    public static final Vector2f c3 = new Vector2f(0.3f, 0.75f);
    public static final double   r3 = 0.10;

    public static final Color    CLASS_1 = new Color(255, 255, 255);
    public static final Color    CLASS_2 = new Color(130, 130, 130);
    
    
    public static SampleSet generateData(
            final int samples, final float rel, final Random rnd
    ) {
        //
        final SampleSet result = new SampleSet();
        //
        int size1 = (int)(samples * rel);
        int size2 = samples - size1;
        //
        int size = size1 + size2;
        while (size > 0) {
            Vector2f v = new Vector2f(rnd.nextFloat(), rnd.nextFloat());
            Vector2f l1 = Vector2f.sub(c1, v);
            Vector2f l2 = Vector2f.sub(c2, v);
            Vector2f l3 = Vector2f.sub(c3, v);
            if (l1.length() > r1 && l2.length() > r2 && l3.length() > r3) {
                //
                // outer point
                //
                if (size2 > 0) {
                    result.add(new Sample(
                        new double[]{v.x, v.y}, new double[]{0.0})
                    );
                    size2--;
                }
            } else {
                //
                // inner point
                //
                if (size1 > 0) {
                    result.add(
                        new Sample(new double[]{v.x, v.y}, new double[]{1.0})
                    );
                    size1--;
                }
            }
            size--;
        }
        return result;
    }
    
    
    public static void main(String[] args) {
        //
        final Random rnd = new Random(System.currentTimeMillis());
        //
        // network parameter.
        //
        final int input   = 2;
        final int hidden  = 8;
        //
        final int trainset_size = 5000;
        final int testset_size  = 100;
        //
        // training parameter
        //
        final int    epochs       = 4000;
        final double learningrate = 0.004;
        final double momentum     = 0.9;
        //
        // Generate a multilayer perceptron using the
        // MLPGenerator class. 
        //
        final MLPGenerator gen = new MLPGenerator();
        //
        gen.inputLayer(input);
        gen.hiddenLayer(hidden, CellType.SIGMOID, true, -1.0);
        gen.outputLayer(1, CellType.SIGMOID);
        //
        final Net mlp = gen.generate();
        mlp.initializeWeights(rnd);
        //
        // Generate samples sets.
        //
        final SampleSet trainset = generateData(trainset_size + testset_size, 0.5f, rnd);
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
        trainer.addListener(new NetTrainerListener() {
            @Override
            public void started(NetTrainer trainer) {
            }
            @Override
            public void finished(NetTrainer trainer) {
            }
            
            @Override
            public void epoch(NetTrainer trainer) {
                //
                final int ep = trainer.getEpoch() + 1;
                //
                if ((ep) % (epochs / 16) != 0 && ep != 1) return;
                final BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
                
                double[] p = new double[2];
                double[] o = new double[2];
                
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        //
                        p[0] = ((double)x) / ((double)(img.getWidth()));
                        p[1] = ((double)y) / ((double)(img.getHeight()));
                        //
                        mlp.reset();
                        mlp.input(p, 0);
                        mlp.compute();
                        mlp.output(o, 0);
                        //
                        int v = ((int)(o[0] * 255));
                        img.setRGB(x, y, ((v>>1) << 8) | v);
                    }
                }
                
                final Graphics2D g = (Graphics2D)img.getGraphics();
                g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                for (Sample s : trainset) {
                    int x = (int)((double)(img.getWidth() - 1) *  
                            s.getInput()[0]);
                    int y = (int)((double)(img.getHeight() - 1) * 
                            s.getInput()[1]);
                    if (s.getTarget()[0] > 0.5) {
                        g.setColor(CLASS_1);
                    } else {
                        g.setColor(CLASS_2);
                    }
                    g.fillOval(x-2, y-2, 5, 5);
                }
                
                JFrame frame = new JFrame("Geometry Learning - Epoch: " + ep);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //
                JPanel panel = new JPanel() {
                    private static final long serialVersionUID = -4307908552010057652L;

                    @Override
                    protected void paintComponent(Graphics gfx) {
                        super.paintComponent(gfx);
                        gfx.drawImage(
                            img,  0,  0, 
                            img.getWidth(),  img.getHeight(),  null
                        );
                    }
                };
                panel.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
                frame.add(panel);
                frame.setResizable(false);
                frame.pack();
                frame.setVisible(true);
            }
        });
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
