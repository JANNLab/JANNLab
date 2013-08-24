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

package de.jannlab.core;

import java.io.Serializable;
import java.util.Random;

import de.jannlab.Net;
import de.jannlab.data.ReadPort;
import de.jannlab.data.WritePort;
import de.jannlab.misc.DoubleTools;
import de.jannlab.misc.ObjectCopy;
import de.jannlab.tools.Debug;

/**
 * This class is an abstract base-class for all different implementations 
 * in this framework. It contains general methods, which are equal for different
 * network types. The aim is to reduce the amount of code each special network
 * implementation needs. Such general methods are input, output and buffering
 * mechanisms, computing a layer integration or activation (or the gradient) for 
 * one time step, computing the error and some other data- and meta-methods.
 * <br></br>
 * @see Net
 * @author Sebastian Otte
 */
public abstract class NetBase implements Net, Serializable, Cloneable {
    private static final long serialVersionUID = 4485482287977886674L;
    
    /**
     * Gives the standard lower bound for random weight initialization.
     */
    public static final double RANDOM_WEIGHT_LBD = -0.1;
    /**
     * Gives the standard upper bound for random weight initialization.
     */
    public static final double RANDOM_WEIGHT_UBD = 0.1;
    /**
     * Provides the network structure of this network instance.
     * @see NetStructure
     */
    protected NetStructure structure = null;
    /**
     * Provides the network data of this network instance.
     */
    protected NetData data = null;
    /**
     * Gives the current frameidx of the network. This can be
     * considered as an inner state during the computation process.
     */
    protected int frameidx = 0;
    /**
     * Provides a read port to the output data of this network. This port 
     * targets to the range of output cells in the output buffer 
     * using the current frameidx.
     */
    private ReadPort outputport = null;
    /**
     * Provides a write port to the input data of this network. This port
     * targets to the range of input cells in the output buffer
     * using the current frameidx. 
     */
    private WritePort inputport = null;
    /**
     * Provides a write port to the target data of this network (necessary
     * to compute the error). This port targets to the range of output cells
     * the gradinput buffer.
     */
    private WritePort targetport = null;
    /**
     * This methods computes the activation (also integration) of a single 
     * layer given by idx, based on the current frameidx. The method 
     * ensures that the activations of all arrays of the layer are computed 
     * in correct time order, which has been defined during generating the network.
     * <br></br>
     * @param idx Gives a layer index.
     */
    protected void computeLayerActivations(final int idx) {
        final Layer layer = this.structure.layers[idx];
        //
        // respect shifted computation indices.
        //
        for (int c = 0; c < layer.compwidth; c++) {
            //
            final int lbd = layer.complbds[c];
            final int ubd = layer.compubds[c];
            //
            // integration.
            //
            for (int a = lbd; a <= ubd; a++) {
                final CellArray array = this.structure.arrays[a];
                //
                CellIntegration.perform(
                    this.data.output[this.frameidx], this.data.input[this.frameidx], 
                    array.cellslbd, array.cellsnum,
                    this.data.weights, this.structure.links,
                    array.predslbd, array.predsnum,
                    array.celltype.integration
                );
            }
            //
            // activation.
            //
            for (int a = lbd; a <= ubd; a++) {
                final CellArray array = this.structure.arrays[a];
                //
                CellFunction.perform(
                    this.data.input[this.frameidx], array.cellslbd,
                    this.data.output[this.frameidx], array.cellslbd,
                    array.cellsnum, array.celltype.activation
                );
            }
        }
    }
    
    /**
     * This methods computes the gradient (reverse integration and derivative 
     * activation)for a single layer given by idx, based on the current frameidx. 
     * The method ensures that gradient of the arrays of the layer are computed
     * in correct (reverse) time order, which has been defined during generating
     * the network.
     * <br></br>
     * @param idx Gives a layer index.
     */
    protected void computeLayerGradients(final int idx) {
        final Layer layer = this.structure.layers[idx];
        //
        // respect shifted computation indices.
        //
        for (int c = layer.compwidth - 1; c >= 0; c--) {
            //
            final int lbd = layer.complbds[c];
            final int ubd = layer.compubds[c];
            //
            // reverse integration, which is always SUM.
            //
            for (int a = ubd; a >= lbd; a--) {
                /*
                // # DEBUG #
                if (a == 6) {
                    System.out.println("STOP");
                }
                // #
                */
                final CellArray array = this.structure.arrays[a];
                //
                CellIntegration.perform(
                    this.data.gradoutput[this.frameidx], 
                    this.data.gradinput[this.frameidx], 
                    array.cellslbd, array.cellsnum,
                    this.data.weights, this.structure.linksrev,
                    array.succslbd, array.succsnum,
                    array.celltype.revintegration
                );
            }
            //
            // activation derivation.
            //
            for (int a = lbd; a <= ubd; a++) {
                /*
                // # DEBUG #
                if (a == 6) {
                    System.out.println("STOP");
                }
                // #
                */
                final CellArray array = this.structure.arrays[a];
                //
                // compute f'(input) first and then store
                // multiplication with reverse integration.
                //
                CellFunction.perform(
                    this.data.input[this.frameidx], array.cellslbd, 
                    this.data.gradoutput[this.frameidx], array.cellslbd,
                    array.cellsnum, array.celltype.revactivation
                );
                //
                DoubleTools.mul(
                    this.data.gradinput[this.frameidx], array.cellslbd, 
                    this.data.gradoutput[this.frameidx], array.cellslbd,
                    this.data.gradoutput[this.frameidx], array.cellslbd, 
                    array.cellsnum
                );
            }
        }
    }
    
    /**
     * Clears the entire data buffer by setting all cell values to zero.
     * After that the constant assignments to data cells are restored.
     * This method effects all particular buffers over all time steps. 
     */
    private void clearData() {
        final int size = this.structure.cellsnum;
        //
        // setting all values to zero. Maybe it would be faster
        // just to reallocate the data and drop the previous.
        //
        for (int f = 0; f < this.data.framewidth; f++) {
            DoubleTools.fill(this.data.input[f], 0, size, 0.0);
            DoubleTools.fill(this.data.output[f], 0, size, 0.0);
            DoubleTools.fill(this.data.gradinput[f], 0, size, 0.0);
            DoubleTools.fill(this.data.gradoutput[f], 0, size, 0.0);
        }
        //
        // perform assignments.
        //
        for (int t = 0; t < this.data.framewidth; t++) {
            for (int i = 0; i < this.data.asgns.length; i++) {
                final int idx = this.data.asgns[i];
                this.data.output[t][idx] = this.data.asgnsv[i]; 
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuffer(final int frames) {
        final int size = this.structure.cellsnum;
        //
        this.data.input      = new double[frames][size];
        this.data.output     = new double[frames][size];
        this.data.gradinput  = new double[frames][size];
        this.data.gradoutput = new double[frames][size];
        this.data.framewidth     = frames;
        //
        // this will restore assigments.
        //
        this.reset();
    }
    
    /**
     * This methods sets up all data ports provides by the network. The ports by them self use
     * the input, output and target methods of this network.
     */
    private void setupPorts() {
        //
        // setup input port.
        //
        this.inputport = new WritePort() {
                    private static final long serialVersionUID = 5944146445529987404L;
            @Override
            public void write(final double[] buffer, final int offset) {
                NetBase.this.input(buffer, offset);
            }
            @Override
            public void write(final double[] buffer, final int offset, final int[] selection) {
                NetBase.this.input(buffer, offset, selection);
            }
        };
        //
        // setup output port.
        //
        this.outputport = new ReadPort() {
            private static final long serialVersionUID = -7424453538318475273L;
            @Override
            public void read(final double[] buffer, final int offset) {
                NetBase.this.output(buffer, offset);
            }
            @Override
            public void read(final double[] buffer, final int offset, final int[] selection) {
                NetBase.this.output(buffer, offset, selection);
            }
        };
        //
        // setup target port.
        //
        this.targetport = new WritePort() {
            private static final long serialVersionUID = 3258515325902331582L;
            @Override
            public void write(final double[] buffer, final int offset) {
                NetBase.this.target(buffer,  offset);
            }
            @Override
            public void write(final double[] buffer, final int offset, final int[] selection) {
                NetBase.this.target(buffer, offset, selection);
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void incrFrameIdx() {
        this.frameidx++;
        if (this.frameidx >= this.data.framewidth) {
            this.frameidx--;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameIdx(final int idx) {
        if (idx < 0) { 
            this.frameidx = 0; 
        } else if (idx >= this.data.framewidth) {
            this.frameidx = this.data.framewidth - 1;
        } else {
            this.frameidx = idx;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void decrFrameIdx() {
        if (this.frameidx <= 0) return;
        this.frameidx--;
    }
    
    /**
     * {@inheritDoc}
     */
    public Net sharedCopy() {
        try {
            //
            // no deep copy -> instance copy. all sub-objects are shared.
            //
            NetBase copy = (NetBase)this.clone();
            //
            // deep copy only data and re-setup ports.
            //
            copy.data = this.data.sharedCopy();
            copy.setupPorts();
            //
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public Net copy() {
        return ObjectCopy.copy(this);
    }
    
    /**
     * Creates a NetBase instance. The constructor is only visible in the core package.
     * This method assigns given NetStructure and NetData instance and sets up fundamental
     * things such as data ports or buffer initialization.
     * <br></br>
     * @param structure
     * @param data
     */
    NetBase(
        final NetStructure structure, 
        final NetData data
    ) {
        //
        this.structure = structure;
        this.data      = data;
        //
        this.setupPorts();
        this.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameIdx() {
        return this.frameidx;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameWidth() {
        return this.data.framewidth;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public WritePort inputPort() {
        return this.inputport;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public WritePort targetPort() {
        return this.targetport;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ReadPort outputPort() {
        return this.outputport;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.frameidx = 0;
        this.clearData();
    }
    
    /**
     * {@inheritDoc}
     */
    public double[] getOutputBuffer(final int frameidx) {
        return this.data.output[frameidx];
    }

    /**
     * {@inheritDoc}
     */
    public double[] getGradOutputBuffer(final int frameidx) {
        return this.data.gradoutput[frameidx];
    }

    /**
     * {@inheritDoc}
     */
    public double[] getGradInputBuffer(final int frameidx) {
        return this.data.gradinput[frameidx];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    final public NetStructure getStructure() {
        return this.structure;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean isBidirectional() {
        return this.structure.bidirectional;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean isRecurrent() {
        return this.structure.recurrent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean isOnline() {
        return !this.isOffline();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean isOffline() {
        return this.structure.offline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public int getInputCells() {
        return this.structure.incellsnum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public int getOutputCells() {
        return this.structure.outcellsnum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public int getValueCells() {
        return this.structure.valcellsnum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public int getComputingCells() {
        return this.structure.comcellsnum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double error() {
        //
        // compute difference of the expected data
        // and output of the output layer. store result 
        // in output layer (gradinput again).
        //
        DoubleTools.sub(
            this.data.gradinput[this.frameidx], this.structure.outcellslbd,
            this.data.output[this.frameidx], this.structure.outcellslbd,
            this.data.gradinput[this.frameidx], this.structure.outcellslbd,
            this.structure.outcellsnum
        );
        /*
        //
        // build sum of absolute error.
        //
        final double err = DoubleTools.absSum(
            this.data.gradinput[this.frameidx], this.structure.outcellslbd,
            this.structure.outcellsnum
        );
        */
        //
        // build sum of square error.
        //
        final double err = DoubleTools.meanSquareSum(
            this.data.gradinput[this.frameidx], this.structure.outcellslbd,
            this.structure.outcellsnum
        );
        return err;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeWeights() {
        this.initializeWeights(new Random(System.currentTimeMillis()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeWeights(final Random rnd) {
        //
        int size   = this.data.weightsnum;
        int offset = 1;
        //
        DoubleTools.fill(
            this.data.weights, offset, size, rnd, 
            RANDOM_WEIGHT_LBD, RANDOM_WEIGHT_UBD
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getWeights() {
        return this.data.weights;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWeights(final double[] data, final int offset) {
        DoubleTools.copy(
            data,  offset, this.data.weights, 1, this.data.weightsnum
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void readWeights(final double[] data, final int offset) {
        DoubleTools.copy(
            this.data.weights, 1, data,  offset, this.data.weightsnum
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getLinks() {
        return this.structure.links;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getLinksRev() {
        return this.structure.linksrev;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getWeightsNum() {
        return this.data.weightsnum;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getLinksNum() {
        return this.structure.linksnum;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void input(final double[] data, final int offset) {
        //
        // copy given data into current output buffer.
        //
        DoubleTools.copy(
            data, offset, this.data.output[this.frameidx], 
            this.structure.incellslbd,
            this.structure.incellsnum
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void input(final double[] data, final int offset, final int[] selection) {
        //
        // copy given data into current output buffer, respecting only
        // the indices determined in selection.
        //
        DoubleTools.copy(
            data, offset, selection, this.data.output[this.frameidx], 
            this.structure.incellslbd
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void output(final double[] data, final int offset) {
        //
        // copy values from the current output buffer in the given data array.
        //
        DoubleTools.copy(
            this.data.output[this.frameidx], this.structure.outcellslbd,
            data, offset, this.structure.outcellsnum
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void output(final double[] data, final int offset, final int[] selection) {
        //
        // copy values from the current output buffer in the given data array,
        // respecting only the indices determined in selection.
        //
        DoubleTools.copy(
            data, this.structure.outcellslbd, this.data.output[this.frameidx], 
            offset, selection
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void target(final double[] data, final int offset) {
        //
        // write expected data in output layer (gradinput).
        //
        DoubleTools.copy(
            data, offset, this.data.gradinput[this.frameidx], 
            this.structure.outcellslbd,
            this.structure.outcellsnum
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void target(final double[] data, final int offset, final int[] selection) {
        //
        // write expected data in output layer (gradinput), respecting only
        // the indices determined in selection.
        //
        DoubleTools.copy(
            data, offset, selection, this.data.gradinput[this.frameidx], 
            this.structure.outcellslbd
        );
    }
    
    /**
     * Performs a numerical check for a given value.
     * <br></br> 
     * @param value The value that is to check.
     * @return True if the given value is not NaN or infinite.
     */
    private static boolean check(final double value) {
        if (Double.isNaN(value)) {
            if (Debug.DEBUG) {
                System.out.println("NaN occurs.");
            }
            return false;
        }
        if (Double.isInfinite(value)) {
            if (Debug.DEBUG) {
                System.out.println("Infinite occurs.");
            }
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean numericalCheck() {
        //
        // over all time steps.
        //
        for (int f = 0; f < this.data.framewidth; f++) {
            //
            // for all cells.
            //
            for (int j = 0; j < this.structure.cellsnum; j++) {
                if (!check(this.data.input[f][j]))      return false;
                if (!check(this.data.output[f][j]))     return false;
                if (!check(this.data.gradinput[f][j]))  return false;
                if (!check(this.data.gradoutput[f][j])) return false;
            }
        }
        //
        // for all weights.
        //
        for (int i = 0; i < this.data.weightsnum; i++) {
            if (!check(this.data.weights[i])) return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("NetStructure : {\n");
        out.append("" + Debug.indent(this.structure.toString()) + "\n");
        out.append("}\n\n");
        //
        out.append("Assignments : {\n");
        for (int i = 0; i < this.data.asgns.length; i++) {
            out.append("\t" + this.data.asgns[i] + " : " + this.data.asgnsv[i] + "\n");
        }
        out.append("}\n\n");
        return out.toString();
    }
    
}
