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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.jannlab.Net;
import de.jannlab.data.SampleSet;
import de.jannlab.tools.Debug;
import de.jannlab.tools.DefaultNetTrainerListener;
import de.jannlab.training.NetTrainerListener;
import de.jannlab.training.exception.NetTrainerException;

/**
 * This class is the base class for all Traininer classes,
 * and contains general variables and methods which they are all sharing.
 * <br></br>
 * @author Sebastian Otte
 */
public abstract class NetTrainer {
    /**
     * A set of training samples.
     */
    protected SampleSet trainset = null;
    /**
     * A set of validation samples.
     */
    protected SampleSet validationset = null;
    /**
     * The reference network.
     */
    protected Net net = null;
    /**
     * The intern random number generator.
     */
    protected Random rnd = new Random(0L);
    /**
     * The target error.
     */
    protected double targeterror =  0.0;
    /**
     * The number of epochs.
     */
    protected int epochs = 100;
    /**
     * The current epoch.
     */
    protected int epoch = 0;
    /**
     * The current training error.
     */
    protected double trainerror = 0.0;
    /**
     * Gives a feature selection (null => select all)
     */
    protected int[] features = null;
    /**
     * The current validation error.
     */
    protected double validationerror = 0.0;
    /**
     * Returns the current training error.
     * <br></br>
     * @return Training error as double.
     */
    public final double getTrainingError() {
        return this.trainerror;
    }
    /**
     * Returns the current validation error.
     * @return Validation error as double.
     */
    public final double getValidationError() {
        return this.validationerror;
    }
    /**
     * Returns the current training epoch.
     * @return Epoch as int.
     */
    public int getEpoch() {
        return this.epoch;
    }
    /**
     * A list of listeners.
     */
    private List<NetTrainerListener> listener = 
            new LinkedList<NetTrainerListener>();
    /**
     * Add a NetTraininerListener to the list of listeners.
     * <br></br>
     * @param listener Instance of NetTrainerListener.
     */
    public final void addListener(final NetTrainerListener listener) {
        this.listener.add(listener);
    }
    /**
     * Removes a given NetTrainerListener from the list of listeners.
     * @param listener Instance of NetTrainerListener.
     */
    public final void removeListener(final NetTrainerListener listener) {
        this.listener.remove(listener);
    }
    /**
     * Notifies all listeners that the current epoch has been finished.
     */
    protected void notifyEpoch() {
        for (NetTrainerListener listener : this.listener) {
            listener.epoch(this);
        }
    }
    /**
     * Notifies all listeners that the training has been finished.
     */
    protected void notifyFinished() {
        for (NetTrainerListener listener : this.listener) {
            listener.finished(this);
        }
    }
    /**
     * Notifies all listeners that the training has been started.
     */
    protected void notifyStarted() {
        for (NetTrainerListener listener : this.listener) {
            listener.started(this);
        }
    }
    /**
     * Clears the list of listeners.
     */
    public final void clearListener() {
        this.listener.clear();
    }
    /**
     * Resets the trainer (errors, states).
     */
    public abstract void reset();

    /**
     * Initialization after before training.
     */
    protected void init() {
        //
        this.check();
    }

    /**
     * Check training configuration.
     */
    protected void check() {
        if (this.net == null) {
            throw new NetTrainerException("No network given.");
        }
        if (this.trainset == null) {
            throw new NetTrainerException("No trainset given.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        //
        out.append(this.getClass().getSimpleName() + "\n");
        //
        return out.toString();
    }
    
    /**
     * Create an instance of NetTrainer.
     */
    public NetTrainer() {
        //
        if (Debug.DEBUG) {
            this.addListener(new DefaultNetTrainerListener());
        }
    }
    
    /**
     * Return the number of training epochs.
     * @return Epochs as int.
     */
    public final int getEpochs() {
        return this.epochs;
    }
    /**
     * Returns the current feature selection (null == select all features). 
     * @return Feature selection as int[].
     */
    public final int[] getFeatures() {
        return this.features;
    }
    /**
     * Sets the current features selection (null == select all features).
     * @param features Feature selection as int[].
     */
    public final void setFeatures(final int[] features) {
        this.features = features;
    }
    
    /**
     * This the number of training epochs.
     * @param epochs Training epochs as int.
     */
    public final void setEpochs(final int epochs) {
        this.epochs = epochs;
    }
    /**
     * Returns the target error.
     * @return Target error as double.
     */
    public final double getTargetError() {
        return this.targeterror;
    }
    /**
     * Sets the target error.
     * @param targeterror Target error as double.
     */
    public final void setTargetError(final double targeterror) {
        this.targeterror = targeterror;
    }
    /**
     * Set the validation set.
     * @param validationset Istance of SampleSet.
     */
    public final void setValidationSet(final SampleSet validationset) {
        this.validationset = validationset;
    }
    /**
     * Returns the training set.
     * @return Instance of SampleSet.
     */
    public SampleSet getTrainingSet() {
        return this.trainset;
    }
    /**
     * Returns the validation set.
     * @return Instance of SampleSet.
     */    
    public SampleSet getValidationSet() {
        return this.validationset;
    }
    /**
     * Sets the reference networks.
     * @param net Instance of Net.
     */
    public final void setNet(final Net net) {
        this.net = net;
    }
    /**
     * Returns the reference network. 
     * @return Instance of Net.
     */
    public Net getNet() {
        return this.net;
    }
    /**
     * Returns the random number generator.
     * @return Instance of Random.
     */
    public Random getRnd() {
        return this.rnd;
    }
    /**
     * Sets the random number generator.
     * @param rnd Instance of Random.
     */
    public void setRnd(final Random rnd) {
        this.rnd = rnd;
    }
    /**
     * Sets the training set.
     * @param trainset Instance of SampleSet.
     */
    public final void setTrainingSet(final SampleSet trainset) {
        this.trainset = trainset;
    }
    /**
     * Forces the trainer to start the training process.
     */
    public abstract void train();
}
