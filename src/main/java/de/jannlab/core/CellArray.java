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

/**
 * A CellArray defines a range of cells with the same functional specification 
 * and same computation-point-of-time. Semantically the 
 * computation-point-of-time can be considered as parallel for cells in the 
 * array.<br></br>
 * @author Sebastian Otte
 */
public final class CellArray implements Serializable {
    private static final long serialVersionUID = -322002318603819740L;

    // ------------------------------------------------------------------------
    
    /** Cells are not automatically connected. */
    public static final int ILC_NONE = 0x0;
    /** Cells are only connected through incoming connections. */
    public static final int ILC_IN   = 0x1;
    /** Cells are only connected through outgoing connections. */
    public static final int ILC_OUT  = 0x2;
    /** Cells are connected through both incoming and outgoing connections. */
    public static final int ILC_BOTH = ILC_IN | ILC_OUT;

    // ------------------------------------------------------------------------
    
    /**
     * The lower bound of the cell range specified by the CellArray.
     */
    public int cellslbd;
    /**
     * The upper bound of the cell range specified by the CellArray.
     */
    public int cellsubd;
    /**
     * The number of cells in the range specified by the CellArray.
     */
    public int cellsnum;
    /**
     * The input degree of all cells contained by the layer. This field is redundant
     * to predsnum but is used while the generation process to verify result.
     */
    public int indeg;
    /**
     * The output degree of all cells contained by the layer. This field is redundant
     * to succsnum but is used while the generation process to verify result.
     */
    public int outdeg;
    /**
     * The lower bound of the links from all predecessors of the cells in the CellArray.
     */
    public int predslbd;
    /**
     * The upper bound of the links from all predecessors of the cells in the CellArray.
     */
    public int predsubd;
    /**
     * The number of links from all predecessors of the cells in the CellArray.
     */
    public int predsnum;
    /**
     * The number of weights of all predecessor links.
     */
    public int predswnum;
    /**
     * The lower bound of the links to all successors of the cells in the CellArray.
     */
    public int succslbd;
    /**
     * The upper bound of the links to all successors of the cells in the CellArray.
     */
    public int succsubd;
    /**
     * The number of links to all successors of the cells in the CellArray.
     */
    public int succsnum;
    /**
     * The number of weights of all successor links.
     */
    public int succswnum;
    /**
     * Refers the parent layer. 
     */
    public int layer;
    /**
     * Defines the computation-point-of-time in the corresponding layer. If the value is -1
     * this indicates that the CellArray contains non-computing cells.  
     */
    public int compidx;
    /**
     * This tag can be one of ILC_IN, ILC_OUT or ILC_BOTH. It indicates the "connective-type"
     * of the cells. This allows to define input, output and "hidden" cells per layer.
     */
    public int ilctag;
    /**
     * The CellType gives the functional specification (i.e. the integration and activation 
     * function) of the cells in this CellArray.
     * @see CellType
     */
    public CellType celltype;
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder w = new StringBuilder();
        //
        w.append("cellslbd     : " + this.cellslbd + "\n");
        w.append("cellsubd     : " + this.cellsubd + "\n");
        w.append("cellsnum     : " + this.cellsnum + "\n");
        w.append("indeg        : " + this.indeg + "\n");
        w.append("outdeg       : " + this.outdeg + "\n");
        w.append("predslbd     : " + this.predslbd + "\n");
        w.append("predsubd     : " + this.predsubd + "\n");
        w.append("predsnum     : " + this.predsnum + "\n");
        w.append("predswnum    : " + this.predswnum + "\n");
        w.append("succslbd     : " + this.succslbd + "\n");
        w.append("succsubd     : " + this.succsubd + "\n");
        w.append("succsnum     : " + this.succsnum + "\n");
        w.append("succswnum    : " + this.succswnum + "\n");
        w.append("layer        : " + this.layer + "\n");
        w.append("compidx      : " + this.compidx + "\n");
        w.append("ilctag       : " + this.ilctag + "\n");
        w.append("celltype     : " + this.celltype.toString() + "\n");
        //w.append("tag          : " + this.tag + "\n");
        //
        return w.toString();
    }
}
