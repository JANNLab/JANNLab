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

/**
 * This is a implementation for offline computing RNNs. Here the computing
 * It computes all times steps per layer.
 * <br></br>
 * @author Sebastian Otte
 */
public final class OfflineRecurrentNetBase extends RecurrentNetBase {
    private static final long serialVersionUID = 3076455342302573784L;
    
    /**
     * Creates an instance of this class by a given NetStructure and
     * NetData instance. The given NetStructure should fulfill the 
     * requirements of an offline computing RNN, otherwise the correct 
     * behavior of the network could not be assured and is assumed as
     * undefined.   
     * <br></br>
     * @param structure The given net structure containing the topology
     * and functional specification.
     * @param data Instance of NetData containing data buffer and weights
     * and must fit the NetStructure.
     */
    public OfflineRecurrentNetBase(NetStructure structure, NetData data) {
        super(structure, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void compute() {
        //
        // reset time.
        //
        final int last = this.frameidx;
        this.setFrameIdx(0);
        //
        for (int t = 0; t <= last; t++) {
            //
            if (t > 0) {
                this.copyOutput(this.frameidx - 1, this.frameidx);
            }
            //
            // from first to last layer.
            //
            for (int l = 0; l < this.structure.layers.length; l++) {
                this.computeLayerActivations(l);
            }
            //
            if (t < last) this.incrFrameIdx();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    final public void computeGradient() {
        //
        final int last = this.frameidx;
        //
        for (int t = last; t >= 0; t--) {
            //
            if (t < last) {
                this.copyGradOutput(this.frameidx + 1, this.frameidx);
            }
            //
            // from last to first layer.
            //
            for (int l = this.structure.layers.length - 1; l >= 0; l--) {
                this.computeLayerGradients(l);
            }
            //
            if (t > 0) this.decrFrameIdx();
        }
        //
        this.setFrameIdx(last);
    }
}
