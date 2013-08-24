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

import de.jannlab.core.Link;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;
import de.jannlab.misc.DoubleTools;
import de.jannlab.misc.IntTools;
import de.jannlab.tools.NetTools;

/**
 * This class implements the common gradient descent learning algorithm.
 * <br></br>
 * @author Sebastian Otte
 */
public final class GradientDescent extends NetTrainer {
    //
    public static final double  DEFAULT_ALPHA           = 0.9;
    public static final double  DEFAULT_MU              = 0.0001;  // 10^{-5}
    public static final int     DEFAULT_VLDINTERVAL     = 5;
    public static final boolean DEFAULT_EARLYSTOP       = false;
    public static final int     DEFAULT_EARLYSTOPCOUNT  = 10;
    public static final int     DEFAULT_EPOCHS          = 100;
    public static final double  DEFAULT_TARGETERROR     = 0.0;
    public static final boolean DEFAULT_ONLINE          = true;
    public static final boolean DEFAULT_PERMUTE         = true;
    /**
     * Containing the permutation of the trainset.
     */
    private int[] permutation = null;
    /**
     * Do online or offline sampling?
     */
    private boolean online = DEFAULT_ONLINE;
    /**
     * Learning rate.
     */
    private double mu = DEFAULT_MU;
    /**
     * Mommentum factor.
     */
    private double alpha = DEFAULT_ALPHA;
    /**
     * Validation interval.
     */
    private int  validint = 1;
    /**
     * Use early stopping?
     */
    private boolean earlystop = DEFAULT_EARLYSTOP;
    /**
     * Early stopping count.
     */
    private int earlystopcount = DEFAULT_EARLYSTOPCOUNT;
    /**
     * Permute training set?
     */
    private boolean permute = DEFAULT_PERMUTE;
    /**
     * Weights vector of the reference network.
     */
    private double[] weights = null;
    /**
     * Number of weights of the reference network.
     */
    private int weightsnum = 0;
    /**
     * The last weight differences.
     */
    private double[] dweightslast = null;
    /**
     *  The current weight differences.
     */
    private double[] dweights = null;
    /**
     * The links of the reference network.
     */
    private int[] links = null;
    /**
     * The number of links.
     */
    private int  linksnum = 0;
    
    /*
    // # DEBUG #
    private double[] deltasdbg;
    private double[] deltasindbg;
    private double[] weightsdbg;
    // #
    */
    
    /**
     * Creates an instance of GradientDescent.
     */
    public GradientDescent() {
        //
        this.validint    = DEFAULT_VLDINTERVAL;
        this.targeterror = DEFAULT_TARGETERROR;
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
        out.append("permute            : " + this.permute + "\n");
        out.append("online:            : " + this.online + "\n");
        out.append("targeterror        : " + this.targeterror + "\n");
        out.append("epochs             : " + this.epochs + "\n");
        out.append("learningrate       : " + this.mu     + "\n");
        out.append("momentum           : " + this.alpha  + "\n");
        out.append("validationinterval : " + this.validint + "\n");
        out.append("earlystopping      : " + this.earlystop + "\n");
        out.append("earlystoppingcount : " + this.earlystopcount +  "\n");
        //
        return super.toString() + out.toString();
    }
    
    /**
     * Returns of the weights are updated online or offline.
     */
    public boolean getOnline() {
        return this.online;
    }
    /**
     * Sets if to update weights online or offline.
     */
    public void setOnline(final boolean value) {
        this.online = value;
    }
    /**
     * Returns if to permute the training set.
     */
    public boolean getPermute() {
        return this.permute;
    }
    /**
     * Sets if to permute the trianing set.
     * @param value Value as boolean.
     */
    public void setPermute(final boolean value) {
        this.permute = value;
    }
    /**
     * Returns validation interval.
     */
    public int getValidationInterval() {
        return this.validint;
    }
    /**
     * Sets validation interval.
     * @param value Valuie as int. 
     */
    public void setValidationInterval(final int value) {
        this.validint = Math.max(1, value);
    }
    /**
     * Return if to use early stopping.
     */
    public boolean getEarlyStopping() {
        return this.earlystop;
    }
    /**
     * Sets if to use early stopping.
     * @param value Value as boolean.
     */
    public void setEarlyStopping(final boolean value) {
        this.earlystop = value;
    }
    /**
     * Sets the number of misses before early stopping. 
     * @param value Value as int.
     */
    public void setEarlyStoppingCount(final int value) {
        this.earlystopcount = value;
    }
    /**
     * Returns the learning rate.
     */
    public final double getLearningRate() {
        return this.mu;
    }
    /**
     * Sets the learning rate.
     * @param mu Value as double.
     */
    public final void setLearningRate(final double mu) {
        this.mu = mu;
    }
    /**
     * Return the momentum factor.
     * @return Value as double.
     */
    public final double getMomentum() {
        return this.alpha;
    }
    /**
     * Sets the mumentum factor.
     * @param alpha Value as double.
     */
    public final void setMomentum(final double alpha) {
        this.alpha = alpha;
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();
        //
        this.permutation = new int[this.trainset.size()];
        for (int i = 0; i < this.permutation.length; i++) {
            this.permutation[i] = i;
        }
        this.weights      = this.net.getWeights();
        this.dweights     = new double[this.weights.length]; 
        this.dweightslast = new double[this.weights.length];
        //
        this.weightsnum = net.getWeightsNum();
        this.links      = net.getLinks(); 
        this.linksnum   = net.getLinksNum();
        //
        this.epoch           = 0;
        this.validationerror = 0.0;
        this.trainerror      = 0.0;
        //
    }
    
    /**
     * Resets and current weight differences and stores them
     * in history memory.
     */
    private void resetWeightDiffs() {
        //
        // initialize weight diffs.
        //
        for (int i = 1; i <= this.weightsnum; i++) {
            this.dweightslast[i] = this.dweights[i];
            this.dweights[i]     = 0.0;
        }
    }
    
    /**
     * Accumulates the weight differences for the given frame index (time index) 
     * and the current gradient results.
     * @param frameidx Frame index (time index).
     */
    private void accumulateWeightsDiffs(final int frameidx) {
        //
        // collect deltas * activations .
        //
        for (int t = 0; t <= frameidx; t++) {
            //
            final double[] outputs = this.net.getOutputBuffer(t);
            final double[] deltas  = this.net.getGradOutputBuffer(t);
            //
            /*
            // # DEBUG #
            final double[] deltasin = net.getGradInputBuffer(t);
            for (int i = 0; i < deltas.length; i++) {
                this.deltasdbg[i] += Math.abs(deltas[i]);
                this.deltasindbg[i] += Math.abs(deltasin[i]);
            }
            // #
            */
            int off = 0;
            //
            for (int i = 0; i < this.linksnum; i++) {
                //
                final int src  = this.links[off + Link.IDX_SRC];
                final int dst  = this.links[off + Link.IDX_DST];
                final int widx = this.links[off + Link.IDX_WEIGHT];
                //
                if (widx > 0) {
                    final double dk = deltas[dst];
                    final double xj = outputs[src];
                    //                     
                    final double dw = (dk * xj);
                    this.dweights[widx] += (dw);
                }
                //
                off += Link.LINK_SIZE;
            }
        }
    }
    
    /**
     * Finally computes the weight differences based on the accumulated
     * values.
     */
    private void computeWeightDiffs() {
        //
        // add momentum and multiply learning rate mu. 
        //
        for (int i = 1; i <= this.weightsnum; i++) {
            //
            // calculate momentum.
            //
            final double mw = this.dweightslast[i] * this.alpha; 
            this.dweights[i] = (this.mu * this.dweights[i]) + mw;
        }  
    }
    
    /**
     * Adjusts the weights.
     */
    private void adjustWeights() {
        //
        for (int i = 1; i <= this.weightsnum; i++) {
            //
            this.weights[i] += this.dweights[i];
            /*
            // # DEBUG #
            this.weightsdbg[i] += Math.abs(this.dweights[i]);
            // #
            */
        }
        
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
        final int tsetsize   = this.permutation.length;
        //
        int count           = 0;
        int nbetterctr      = 0;
        double[] minweights = this.net.getWeights().clone();
        double minerror = Double.MAX_VALUE;
        //
        for (int i = 0; i < this.epochs; i++) {
            this.epoch = i;
            //
            double epocherror = 0.0;
            if (this.permute) {
                IntTools.shuffle(this.permutation, this.rnd);
            }
            //
            // do online or offline gradient descent?
            //
            if (this.online) {
                //
                // for all patterns in trainset.
                //
                for (int j = 0; j < tsetsize; j++) {
                    //
                    // determine permuted index and sample.
                    //
                    final int idx = this.permutation[j];
                    final Sample sample = tset.get(idx);
                    //
                    // compute forward pass.
                    //
                    this.net.reset();
                    final double err = NetTools.performForward(
                        this.net, sample, this.features
                    );
                    final int frameidx = this.net.getFrameIdx();
                    //
                    // compute backward pass.
                    //
                    NetTools.performBackward(this.net);
                    //
                    // compute weight differences and adjust weights.
                    //
                    this.resetWeightDiffs();
                    this.accumulateWeightsDiffs(frameidx);
                    this.computeWeightDiffs();
                    this.adjustWeights();
                    //
                    epocherror += err;
                }
            } else {
                //
                // for all patterns in trainset.
                //
                this.resetWeightDiffs();
                for (int j = 0; j < tsetsize; j++) {
                    //
                    // determine permuted index and sample.
                    //
                    final int idx = this.permutation[j];
                    final Sample sample = tset.get(idx);
                    //
                    // compute forward pass.
                    //
                    this.net.reset();
                    final double err = NetTools.performForward(
                        this.net, sample, this.features
                    );
                    final int frameidx = this.net.getFrameIdx();
                    //
                    // compute backward pass.
                    //
                    NetTools.performBackward(this.net);
                    //
                    // compute weight differences and adjust weights.
                    //
                    this.accumulateWeightsDiffs(frameidx);
                    //
                    epocherror += err;
                }                    
                this.computeWeightDiffs();
                this.adjustWeights();
            }
            //
            epocherror = (epocherror / (double)tsetsize);
            this.trainerror = epocherror;
            //
            if ((this.trainset == this.validationset) || (this.validationset == null)) {
                this.validationerror = this.trainerror;
            } else {
                if ((i % this.validint) == 0) {
                    this.validationerror = NetTools.computeError(net, this.validationset); 
                }
            }
            //
            if ((i % this.validint) == 0) {
                if (this.validationerror < minerror) {
                    minerror   = this.validationerror;
                    minweights = this.weights.clone();
                    nbetterctr = 0;
                } else {
                    nbetterctr++;
                }
            }
            //
            this.notifyEpoch();
            //
            // early stopping.
            //
            if (nbetterctr > this.earlystopcount) {
                if (this.earlystop) break;
            }
            if (this.validationerror < this.targeterror) {
                break;
            }
            count++;
        }
        //
        // take best weights.
        //
        if (count > 0) {
            this.validationerror = minerror;
            DoubleTools.copy(minweights, 1, this.weights, 1, this.weightsnum);
        }
        //
        this.notifyFinished();
    }
}
