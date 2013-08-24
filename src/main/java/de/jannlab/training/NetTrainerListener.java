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

package de.jannlab.training;

/**
 * Instances of this interface can be used to access the training
 * process of all NetTrainer implementations, e.g. for build 
 * lerning curves.  
 * <br></br>
 * @author Sebastian Otte
 */
public interface NetTrainerListener {
    /**
     * Will be called when the training has just been started.
     * <br></br>
     * @param trainer The NetTrainer instance which has fired the event.
     */
    public void started(final NetTrainer trainer);
    /**
     * Will be called after each epoch of training.
     * <br></br>
     * @param trainer The NetTrainer instance which has fired the event.
     */
    public void epoch(final NetTrainer trainer);
    /**
     * Will be called after the training has been finished.
     * <br></br>
     * @param trainer The NetTrainer instance which has fired the event.
     */
    public void finished(final NetTrainer trainer);
    //
}
