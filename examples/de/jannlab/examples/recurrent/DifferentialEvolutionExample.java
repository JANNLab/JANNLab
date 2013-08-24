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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.jannlab.Net;
import de.jannlab.core.CellType;
import de.jannlab.examples.tools.OnlineDiagram;
import de.jannlab.generator.RNNGenerator;
import de.jannlab.optimization.BasicIterationListener;
import de.jannlab.optimization.Objective;
import de.jannlab.optimization.diffevo.DifferentialEvolution;
import de.jannlab.optimization.diffevo.Mutation;

/**
 * This simple example demonstrates how Differential Evolution
 * can be used for learning a damped oscillation function
 * with a Recurrent Neural Network. 
 * <br></br>
 * @author Sebastian Otte
 */
public class DifferentialEvolutionExample {
    
    public static final Random rnd = new Random(0L);
    
    public static double X_LBD   = 0.0;
    public static double X_UBD   = Math.PI * 2.0 * 10.0;
    public static double X_RANGE = X_UBD - X_LBD;
    public static int    TICKS   = (int)(X_RANGE / (2.0 * Math.PI) * 20.0); 
    public static double X_INCR  = X_RANGE / (double)TICKS;
    
    /**
     * Damped oscillation function, which is learned
     * by the RNN.
     */
    public static double f(final double x) {
        final double xf = x * 1.0;
        return (
            Math.sin(xf) * Math.exp(-(0.075 * xf))
        );
    }
    
    /**
     * This error function computes the mean square error of the network 
     * output sequence and the oscillation function f. This function 
     * is used as objective function for the Differential Evolution 
     * optimization process.
     */
    public static double error(final Net net) {
        //
        net.reset();
        double[] input = new double[1];
        double[] output = new double[1];
        //
        double x  = X_LBD;
        double error = 0.0;
        //
        for (int i = 0; i < TICKS; i++) {
            input[0] = (i == 0)?(1.0):(0.0);
            net.input(input, 0);
            net.compute();
            net.output(output, 0);
            //
            double diff = output[0] - f(x);
            error += (diff * diff);

            x += X_INCR;
        }
        return error / (double)TICKS;
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
        // generate network.
        //
        final Net net = RNN(2);
        //
        // create a fitness (objective) function for differential evolution.
        //
        Objective obj = new Objective() {
            @Override
            public double compute(double[] args, int offset) {
                //
                // the arguments for the fitness function are used
                // as weights within the recurrent network.
                //
                net.writeWeights(args, offset);
                //
                // then the error is computed based on the output
                // of the network carrying the previously updated
                // weights.
                //
                return error(net);
            }
            //
            @Override
            public int arity() {
                //
                // the number of weights of the network gives
                // the arity of the fitness fuction.
                //
                return net.getWeightsNum();
            }
        };
        //
        // Now we setup an instance of the Differential Evolution
        // optimizer. The following configuration parameters work
        // well but others may also be suitable. Experience has
        // shown that increasing the dimension of the fitness function,
        // i.e., increasing the number of weights, the population size
        // should be increased as well.
        //
        DifferentialEvolution de = new DifferentialEvolution();
        de.setRnd(rnd);
        de.setF(0.4);
        de.setCR(0.7);
        de.setInitLbd(-1.0);
        de.setInitUbd(1.0);
        de.setMutation(Mutation.BEST_TWO);
        de.setParameters(net.getWeightsNum());
        de.setPopSize(25);
        de.updateObjective(obj);
        de.addListener(new BasicIterationListener<DifferentialEvolution>());
        //
        // Initializing the optimizer before using is obligatory. 
        //
        de.initialize();
        System.out.println(de);
        //
        // We now start the optimization process over 300 generations. This
        // results in 300*25 = 7500 fitness evaluation evaluations.
        //
        de.iterate(300, 0);
        //
        // Use the best solution vector of the optimizer as weight vector
        // in the network. 
        //
        double[] solution = new double[net.getWeightsNum()];
        de.copyBestSolution(solution, 0);
        net.writeWeights(solution, 0);
        net.reset();
        //
        // Visualize network output. A Timer forces the RNN to compute
        // a new state every 10 ms. The network output is plot we via a 
        // simple online diagram. Pressing the left or the right button
        // feeds a 1.0 or, respectively, a -1.0 signal into the network.
        //
        final long[] ticks = new long[1];
        final OnlineDiagram diagram = new OnlineDiagram(
            300, -1.0, 1.0, 1
        ) {
            private static final long serialVersionUID = 1L;
            //
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.drawString("cycles " + ticks[0], 10, 20);
            }
        };
        diagram.assignColor(0, new Color(200, 80, 20));
        //
        final double[] input  = new double[1];
        final double[] output = new double[1];
        //
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                net.input(input, 0);
                net.compute();
                ticks[0]++;
                //
                net.output(output, 0);
                diagram.record(output);
                diagram.getGraphics().setColor(Color.WHITE);
                diagram.repaint();
            }
        });
        //
        diagram.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                input[0] = (e.getButton() == MouseEvent.BUTTON1)?(1.0):-(1.0);
            }
            //
            @Override
            public void mouseReleased(MouseEvent e) {
                input[0] = 0.0;
                ticks[0] = 0;
            }
        });
        //
        JFrame frame = new JFrame("Damped Oscillation learned by a Recurrent Neural Network ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(diagram);
        frame.setSize(600, 200);
        frame.setVisible(true);
        timer.start();
    }
}