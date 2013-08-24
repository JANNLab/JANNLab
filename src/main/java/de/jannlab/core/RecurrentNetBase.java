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

import de.jannlab.misc.DoubleTools;

/**
 * This is an abstract inter class for other recurrent network types.
 * It contains copy methods to tranfer data between different timesteps. 
 * <br></br>
 * @author Sebastian Otte
 *
 */
public abstract class RecurrentNetBase extends NetBase {
    private static final long serialVersionUID = 3076455342302573784L;
    
    RecurrentNetBase(NetStructure structure, NetData data) {
        super(structure, data);
    }

    /**
     * Copies output values from source time to destination time.
     * <br></br>
     * @param source Source time index.
     * @param dest Destination time index.
     */
    protected void copyOutput(final int source, final int dest) {
        //
        // copy output from previous context buffer.
        //
        for (CellArray a : this.structure.arrays) {
            if (a.celltype != CellType.VALUE) {
                DoubleTools.copy(
                    this.data.output[source], a.cellslbd,
                    this.data.output[dest], a.cellslbd, a.cellsnum
                );
                /** /
                DoubleTools.copy(
                    this.data.input[source], a.cellslbd,
                    this.data.input[dest], a.cellslbd, a.cellsnum
                );
                /**/
            }
        }
    }

     /**
     * Copies output values from source time to destination time 
     * just for one single layer.
     * <br></br>
     * @param source Source time index.
     * @param dest Destination time index.
     * @param layer Layer index.
     */
    protected void copyOutput(final int source, final int dest, final int layer) {
        //
        // copy output from previous context buffer.
        //
        final int albd = this.structure.layers[layer].arrayslbd;
        final int aubd = this.structure.layers[layer].arraysubd;
        //
        for (int i = albd; i <= aubd; i++) {
            final CellArray a = this.structure.arrays[i];
            //
            if (a.celltype != CellType.VALUE) {
                DoubleTools.copy(
                    this.data.output[source], a.cellslbd,
                    this.data.output[dest], a.cellslbd, a.cellsnum
                );
                /** /
                DoubleTools.copy(
                    this.data.input[source], a.cellslbd,
                    this.data.input[dest], a.cellslbd, a.cellsnum
                );
                /**/
            }
        }
    }

    /**
     * Copies gradient output values from source time to destination time.
     * <br></br>
     * @param source Source time index.
     * @param dest Destination time index.
     */
    protected void copyGradOutput(final int source, final int dest) {
        //
        // copy grad output from previous context buffer.
        //
        for (CellArray a : this.structure.arrays) {
            if (
                (a.celltype != CellType.VALUE) && 
                (a.layer != this.structure.outputlayer)
            ) {
                DoubleTools.copy(
                    this.data.gradoutput[source], a.cellslbd,
                    this.data.gradoutput[dest], a.cellslbd, a.cellsnum
                );
                /*
                DoubleTools.copy(
                    this.data.gradinput[source], a.cellslbd,
                    this.data.gradinput[dest], a.cellslbd, a.cellsnum
                );
                */
                           
            }
        }
    }
    
    /**
     * Copies gradient output values from source time to destination time 
     * just for one single layer.
     * <br></br>
     * @param source Source time index.
     * @param dest Destination time index.
     * @param layer Layer index.
     */
    protected void copyGradOutput(final int source, final int dest, final int layer) {
        //
        // copy grad output from previous context buffer.
        //
        final int albd = this.structure.layers[layer].arrayslbd;
        final int aubd = this.structure.layers[layer].arraysubd;
        //
        for (int i = albd; i <= aubd; i++) {
            final CellArray a = this.structure.arrays[i];
            if (
                (a.celltype != CellType.VALUE) && 
                (a.layer != this.structure.outputlayer)
            ) {
                DoubleTools.copy(
                    this.data.gradoutput[source], a.cellslbd,
                    this.data.gradoutput[dest], a.cellslbd, a.cellsnum
                );
                /*
                DoubleTools.copy(
                    this.data.gradinput[source], a.cellslbd,
                    this.data.gradinput[dest], a.cellslbd, a.cellsnum
                );
                */
                           
            }
        }
    }
    
}
