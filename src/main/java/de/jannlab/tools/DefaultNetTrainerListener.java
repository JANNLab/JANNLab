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

package de.jannlab.tools;

import de.jannlab.training.NetTrainer;
import de.jannlab.training.NetTrainerListener;

/**
 * This class implements a standard NetTrainerListener,
 * which just prints out the validation error over time.
 * <br></br>
 * @author Sebastian Otte
 */
public final class DefaultNetTrainerListener implements NetTrainerListener {

    private double minerror = Double.MAX_VALUE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void started(final NetTrainer trainer) {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void epoch(final NetTrainer trainer) {
        //
        final int epoch    = trainer.getEpoch();       
        final double error = trainer.getValidationError();
        String state = "-"; 
        //
        if (error < this.minerror){
            this.minerror = error;
            state         = "+";
        }
        //
        System.out.println(
            "epoch: " + epoch + "\t" + state + 
            "\terror: " + error +
            "\tminerror: " + this.minerror
        );        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finished(final NetTrainer trainer) {
        System.out.println("final validation error: " + trainer.getValidationError());
        minerror = Double.MAX_VALUE;
    }
    
}
