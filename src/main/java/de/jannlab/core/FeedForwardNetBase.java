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
 * This class is to implement feed-forwards networks. It simplifies the 
 * activation/derivation computation compared to recurrent networks. If
 * all cells (exclusive the input layer) are non linear perceptrons and there
 * is an hidden layer an instance of this class is a multilayer perceptron.
 * <br></br>
 * @author Sebastian Otte
 */
public final class FeedForwardNetBase extends NetBase {
    private static final long serialVersionUID = 3076455342302573784L;

    /**
     * Creates an instance of this class by a given NetStructure and
     * NetData instance. The given NetStructure should fulfill the 
     * requirements of an feed forward network, otherwise the correct 
     * behavior of the network could not be assured and is assumed as
     * undefined.   
     * <br></br>
     * @param structure The given net structure containing the topology
     * and functional specification.
     * @param data Instance of NetData containing data buffer and weights
     * and must fit the NetStructure.
     */
    public FeedForwardNetBase(final NetStructure structure, final NetData data) {
        super(structure, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void compute() {
        //
        // from first to last layer.
        //
        for (int l = 0; l < this.structure.layers.length; l++) {
            this.computeLayerActivations(l);
        }
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void computeGradient() {
        //
        // from last to first layer.
        //
        for (int l = this.structure.layers.length - 1; l >= 0; l--) {
            this.computeLayerGradients(l);
        }
    }
}
