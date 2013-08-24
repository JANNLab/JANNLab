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


import de.jannlab.data.SampleSet;
import de.jannlab.misc.DoubleTools;
import de.jannlab.tools.NetTools;

/**
 * This class implements a simple RandomSearch learning algorithm.
 * Per epoch a new random weight vector is generated and at the end
 * of the training the best found vector will be kept.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public final class RandomSearch extends NetTrainer {
    //
    public static final double  DEFAULT_LBD = -100.0;
    public static final double  DEFAULT_UBD = 100.0;
    //
    /**
     * Lower bound of the random choice.
     */
    private double lbd = DEFAULT_LBD;
    /**
     * Upper bound of the random choice.
     */
    private double ubd = DEFAULT_UBD;
    //
    /**
     * Weight vector of the reference network.
     */
    private double[] weights = null;
    /**
     * Number of weights.
     */
    private int weightsnum = 0;
    /**
     * Creates an instance of RandomSearch.
     */
    public RandomSearch() {
        //
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        //
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        //
        out.append("targeterror  : " + this.targeterror + "\n");
        out.append("epochs       : " + this.epochs + "\n");
        out.append("searchlbd    : " + this.lbd + "\n");
        out.append("searchubd    : " + this.ubd + "\n");
        //
        return super.toString() + out.toString();
    }
    
    /**
     * Returns the lower bound of the search space.
     * @return Lower bound as double.
     */
    public final double getSearchLbd() {
        return this.lbd;
    }
    /**
     * Sets the lower bound of the search space.
     * @param value Lower bound as double.
     */
    public final void setSearchLbd(final double value) {
        this.lbd = value;
    }
    /**
     * Sets the upper bound of the search space.
     * @param value Upper bound as double.
     */
    public final void setSearchUbd(final double value) {
        this.ubd = value;
    }
    /**
     * Returns the upper bound of the search space.
     * @return Upper bound as double.
     */
    public final double getSearchUbd() {
        return this.ubd;
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();
        //
        this.weights    = this.net.getWeights();
        this.weightsnum = net.getWeightsNum();
        //
        this.epoch           = 0;
        this.validationerror = 0.0;
        this.trainerror      = 0.0;
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized
    public void train() {
        //
        this.init();
        //
        this.notifyStarted();
        //
        // iterate epochs.
        //
        final SampleSet tset = this.trainset;
        //
        double[] minweights = this.net.getWeights().clone();
        double minerror = Double.MAX_VALUE;
        int count = 0;
        //
        for (int i = 0; i < this.epochs; i++) {
            this.epoch = i;
            //
            DoubleTools.fill(this.weights, 1, this.weightsnum, this.rnd, this.lbd, this.ubd);
            double error = NetTools.computeError(this.net, tset);
            //
            this.trainerror = error;
            this.validationerror = error;
            //
            if (error < minerror) {
                minerror   = error;
                minweights = this.net.getWeights().clone();
            }
            //
            this.notifyEpoch();
            //
            if (this.validationerror < this.targeterror) {
                break;
            }
            count++;
        }
        //
        // take best weights.
        //
        if (count > 0) {
            this.trainerror      = minerror;
            this.validationerror = minerror;
            DoubleTools.copy(minweights, 1, this.weights, 1, this.weightsnum);
        }
        //
        this.notifyFinished();
    }
}
