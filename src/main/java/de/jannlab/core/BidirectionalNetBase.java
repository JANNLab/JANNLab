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
 * This class is a specific implementation for bidirectional networks.
 * It computes all times steps per layer and inverts the computing direction
 * for REVERSED layer.
 * <br></br>
 * @author Sebastian Otte
 */
public final class BidirectionalNetBase extends RecurrentNetBase {
    private static final long serialVersionUID = 3076455342302573784L;

    /**
     * Creates an instance of this class by a given NetStructure and
     * NetData instance. The given NetStructure should fulfill the 
     * requirements of an bidirectional (offline) RNN, otherwise the correct 
     * behavior of the network could not be assured and is assumed as
     * undefined.   
     * <br></br>
     * @param structure The given net structure containing the topology
     * and functional specification.
     * @param data Instance of NetData containing data buffer and weights
     * and must fit the NetStructure.
     */
    public BidirectionalNetBase(NetStructure structure, NetData data) {
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
        //
        // from first to last layer.
        //
        for (int l = 0; l < this.structure.layers.length; l++) {
            //
            if (l == this.structure.inputlayer) continue;
            final Layer layer = this.structure.layers[l];
            //
            // regular or reversed layer? 
            //
            if (layer.tag == LayerTag.REGULAR) {
                this.setFrameIdx(0);
                for (int t = 0; t <= last; t++) {
                    //
                    if (t > 0) {
                        this.copyOutput(this.frameidx - 1, this.frameidx, l);
                    }
                    this.computeLayerActivations(l);
                    //
                    this.incrFrameIdx();
                    
                }
            } else {
                this.setFrameIdx(last);
                for (int t = last; t >= 0; t--) {
                    //
                    if (t < last) {
                        this.copyOutput(this.frameidx + 1, this.frameidx, l);
                    }
                    this.computeLayerActivations(l);
                    //
                    this.decrFrameIdx();
                }
            }
        }//
        this.setFrameIdx(last);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void computeGradient() {
        //
        final int last = this.frameidx;
        //
        // from last to first layer.
        //
        for (int l = this.structure.layers.length - 1; l >= 0; l--) {
            //
            if (l == this.structure.inputlayer) continue;
            final Layer layer = this.structure.layers[l];
            //
            // regular or reversed layer? 
            //
            if (layer.tag == LayerTag.REGULAR) {
                this.setFrameIdx(last);
                for (int t = last; t >= 0; t--) {
                    //
                    if (t < last) {
                        this.copyGradOutput(
                            this.frameidx + 1, this.frameidx, l
                        );
                    }
                    this.computeLayerGradients(l);
                    //
                    this.decrFrameIdx();
                }
            } else {
                this.setFrameIdx(0);
                for (int t = 0; t <= last; t++) {
                    //
                    if (t > 0) {
                        this.copyGradOutput(
                            this.frameidx - 1, this.frameidx, l
                        );
                    }
                    this.computeLayerGradients(l);
                    //
                    this.incrFrameIdx();
                    
                }

            }
        }
        //
        this.setFrameIdx(last);
    }


    
}