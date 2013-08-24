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
import java.io.StringWriter;

import static de.jannlab.tools.Debug.*;

/**
 * An instance of the class NetStructure contains the while structural specification
 * of a network. This includes the cells and their partition into layers and cellarrays.
 * Note that a cells is still determined by a single index. 
 * Also additional informations of the network are given, e.g. whether the network
 * if recurrent or not. The structure of the network mainly defines its computation
 * process. From this point of view, the structure instance can be seen as an "algorithm"
 * performed by the ANN framework.
 * <br></br>
 * Note that some data values in the structure are redundant with values over layers
 * and arrays. That is to minimize frequently performed hierarchical data acquisitions,
 * which affect the runtime performance.    
 * <br></br>
 * @author Sebastian Otte
 */
public final class NetStructure implements Serializable {
    private static final long serialVersionUID = -4202226869611119404L;
    //
    /**
     * Gives the complete number of cells.
     */
    public int cellsnum;
    /**
     * Gives only the number of values cells.
     */
    public int valcellsnum;
    /**
     * Gives the number computing cells (non-value).
     */
    public int comcellsnum;
    /**
     * Gives the lower bound of the range of input cells.
     */
    public int incellslbd;
    /**
     * Gives the upper bound of the range of input cells.
     */
    public int incellsubd;
    /**
     * Gives the number of input cells.
     */
    public int incellsnum;
    /**
     * Gives the number of output cells.
     */
    public int outcellsnum;
    /**
     * Gives lower bound of the range of output cells.
     */
    public int outcellslbd;
    /**
     * Gives the upper bound of the range of output cells.
     */
    public int outcellsubd;
    /**
     * provides the array of layers.
     */
    public Layer[] layers;
    /**
     * gives the number of layers.
     */
    public int layersnum;
    /**
     * determines whether the network has recurrent connections or not.
     */
    public boolean recurrent;
    /**
     * determines whether the network computes "offline" or "online".
     */
    public boolean offline;
    /**
     * determines whether the network is bidirectional, which a special form
     * of offline computing networks. 
     */
    public boolean bidirectional;
    /**
     * gives the index of the input layer.
     */
    public int inputlayer;
    /**
     * gives the index of the output layer.
     */
    public int outputlayer;
    /**
     * provides all CellArray instances of the networks.
     */
    public CellArray[] arrays;
    /**
     * gives the number of cellarrays.
     */
    public int arraysnum;
    /**
     * provides all links of the network in dst major order. this
     * links are used in forward computation.
     */
    public int[] links;
    /**
     * provides all links of the network in src major order, where
     * this src and dst values are additionally swapped. so we can say
     * that linksrev contains the inverted links.
     */
    public int[] linksrev;
    /**
     * gives the number of links in this network.
     */
    public int linksnum;
   /**
     * Gives the number of weights exclusive the constant 1.0 weight.
     * (redundant to NetData.weightsnum). The number of weights can be
     * less than the number of links.
     */
    public int weightsnum;
    //
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringWriter w = new StringWriter();
        //
        w.append("cellsnum      : " + this.cellsnum + "\n");
        w.append("valcellsnum   : " + this.valcellsnum + "\n");
        w.append("comcellsnum   : " + this.comcellsnum + "\n");
        w.append("incellslbd    : " + this.incellslbd + "\n");
        w.append("incellsubd    : " + this.incellsubd + "\n");
        w.append("incellsnum    : " + this.incellsnum + "\n");
        w.append("outcellsnum   : " + this.outcellsnum + "\n");
        w.append("outcellslbd   : " + this.outcellslbd + "\n");
        w.append("outcellsubd   : " + this.outcellsubd + "\n");
        
        w.append("recurrent     : " + this.recurrent + "\n");
        w.append("offline       : " + this.offline + "\n");
        w.append("bidirectional : " + this.bidirectional + "\n");
        w.append("layersnum     : " + this.layersnum + "\n");
        w.append("inputlayer    : " + this.inputlayer + "\n");
        w.append("outputlayer   : " + this.outputlayer + "\n");
        w.append("\n");
        //
        for (int i = 0; i < this.layersnum; i++) {
            w.append("layers["+ i +"] : {\n");
            w.append(indent(this.layers[i].toString()) + "\n");
            w.append("}\n");
        }
        //
        w.append("\n");
        w.append("arraysnum : " + this.arraysnum + "\n");
        w.append("\n");
        //
        for (int i = 0; i < this.arraysnum; i++) {
            w.append("arrays["+ i +"] : {\n");
            w.append(indent(this.arrays[i].toString()) + "\n");
            w.append("}\n");
        } 
        //
        w.append("\n");
        w.append("linksnum   : " + this.linksnum + "\n");
        w.append("weightsnum : " + this.weightsnum + "\n");
        w.append("\n");
        //
        // print connections.
        //
        w.append("links : {\n");
        w.append(indent(Link.asString(this.links, 0, this.linksnum, 10)));
        w.append("\n");
        w.append("}\n");
        w.append("\n");
        //
        w.append("linksrev : {\n");
        w.append(indent(Link.asString(this.linksrev, 0, this.linksnum, 10)));
        w.append("\n");
        w.append("}\n");
        w.append("\n");
        //
        return w.toString();
    }
}
