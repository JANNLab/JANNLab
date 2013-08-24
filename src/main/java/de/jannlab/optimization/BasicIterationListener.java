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

package de.jannlab.optimization;

import de.jannlab.misc.DoubleTools;


public class BasicIterationListener<I extends IterativeMethod<I>> 
    implements IterationListener<I> {

    private double best;

    @Override
    public void started(I reference) {
        //
        this.best = reference.getError();
    }

    @Override
    public void beforeIteration(int iteration, I reference) {
        //
    }

    private static String paddingBack(String data, int length) {        
        StringBuilder s = new StringBuilder();
        s.append(data);
        int rest = length - s.length();
        for (int i = 0; i < rest; i++) {
            s.append(" ");
        }
        return s.toString();
    }
    
    @Override
    public void afterIteration(int iteration, I reference) {
        //
        final int width1 = 20;
        final int width2 = 8;
        final int width3 = 30;
        //
        final double error = reference.getError();
        
        String imprec = "(-)";
        
        if (error < this.best) {
            this.best = error;
            imprec    = "(+)"; 
        }
        //
        System.out.print(paddingBack("iteration: " + iteration, width1));
        System.out.print(paddingBack(imprec, width2));
        System.out.print(
            paddingBack("error: " + DoubleTools.asString(error, 10), width3)
        );
        System.out.print(
            "best error: " + DoubleTools.asString(this.best, 10)
        );
        System.out.println();
    }

    @Override
    public void finished(int iterations, double error, I reference) {
        //
    }
}