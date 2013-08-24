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

import de.jannlab.misc.IntTools;

/**
 * An instance of this class defines a Layer in ANN. It contains some information
 * on the CellArray corresponding to this layer and some other computational
 * specification (e.g. the computational dependencies of the containing CellArrays). 
 * <br></br>
 * @author Sebastian Otte
 */
public final class Layer implements Serializable{
    private static final long serialVersionUID = 52359162370719687L;

    public static final int NO_LAYER = -1;
    
    //-------------------------------------------------------------------------
    
    /**
     * Gives the lower bound of the cells contained by this layer.
     */
    public int cellslbd = 0;
    /**
     * Gives the upper bound of the cells contained by this layer.
     */
    public int cellsubd = -1;
    /**
     * Gives the number of the cells contained by this layer.
     */
    public int cellsnum = 0;
    /**
     * Gives the lower bound of the arrays contained by this layer.
     */
    public int arrayslbd = 0;
    /**
     * Gives the upper bound of the arrays contained by this layer.
     */
    public int arraysubd = -1;
    /**
     * Gives the number of the arrays contained by this layer.
     */
    public int arraysnum = 0;
    /**
     * Gives the lower bounds of the packages of computational independent
     * arrays. Arrays with the same computation index are computed at the same
     * time.
     */
    public int[] complbds = null;
    /**
     * Gives the uppder bounds of the packages of computational independent
     * arrays. Arrays with the same computation index are computed at the same
     * time.
     */
    public int[] compubds = null;
    /**
     * Gives the number of the packages of computational independent
     * arrays. 
     */    
    public int compwidth = 0;
    /**
     * Gives the degree of incoming connections to all cells contained by all
     * array in this layer.
     */
    public int indeg = 0;
    /**
     * Gives the degree of outgoing connections of all cells contained by all
     * array in this layer.
     */
    public int outdeg = 0;
    /**
     * Gives the tag of the layer, which contains information of its
     * computational behavior.
     */
    public int tag = LayerTag.REGULAR;
    
    //-------------------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder w = new StringBuilder();
        //
        w.append("cellslbd  : " + this.cellslbd + "\n");
        w.append("cellsubd  : " + this.cellsubd + "\n");
        w.append("cellsnum  : " + this.cellsnum + "\n");
        w.append("arrayslbd : " + this.arrayslbd + "\n");
        w.append("arraysubd : " + this.arraysubd + "\n");
        w.append("arraysnum : " + this.arraysnum + "\n");
        w.append("compwidth : " + this.compwidth + "\n");
        w.append("complbds  : " + "(" + IntTools.asString(this.complbds) + ")\n");
        w.append("compubds  : " + "(" + IntTools.asString(this.compubds) + ")\n");
        w.append("indeg     : " + this.indeg + "\n");
        w.append("outdeg    : " + this.outdeg + "\n");
        w.append("tag       : " + LayerTag.asString(this.tag) + "\n");
        //
        return w.toString();
    }
}
