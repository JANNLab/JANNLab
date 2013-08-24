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
 * This class contains static methods for computing serveral 
 * integrations function. An integration function "integrates" 
 * data from a source array into a destination array in a 
 * specific way. Which value are integrated and where the results
 * are stored is determined by a sequence of links. Thereby, each
 * source value is multiplied with the weight of the link (an
 * non-weighted link has weight 1.0). The overhead of multiplying
 * 1.0 on non-weighted links is much faster than an "if"-check for
 * each weight.
 * <br></br>
 * @see Link
 * @author Sebastian Otte
 *
 */
public final class CellIntegration {
    //
    /** None integration function. */
    public static final int NONE    = 0;
    /** Ordinary sum over all values. */
    public static final int SUM     = 1;
    /** Product over all values. */
    public static final int MULT    = 2;
    /** Copy last source value for dest. */
    public static final int LASTID = 3;
    //
    
    /**
     * This method computes the weighted sum for the given arguments.
     * <br></br>
     * @param src Refers the source data array.
     * @param dst Refers the destination data array.
     * @param cellsoff Refers the lower bound of the destination range.
     * @param cellsnum Refers the size of the destination range.
     * @param weights Refers the weights vector.
     * @param links Refers the links vector.
     * @param linksoff Gives the offset of the first link according 
     * the this current context.
     * @param linksnum Gives the number of links according to the current context. 
     */
    public static void sum(
            final double[] src,
            final double[] dst,
            final int cellsoff,
            final int cellsnum,
            final double[] weights,
            final int[] links,
            final int linksoff,
            final int linksnum
    ) {
        if (linksnum == 0) return;
        //
        int link = linksoff;
        //
        // clean cell inputs.
        //
        int end = (cellsoff + cellsnum);
        for (int i = cellsoff; i < end; i++) {
            dst[i] = 0.0;
        }   
        //
        // compute weighted sum over all links.
        //
        for (int i = 0; i < linksnum; i++) {
            //
            final int ci = links[link + Link.IDX_SRC]; 
            final int cj = links[link + Link.IDX_DST];
            final int ij = links[link + Link.IDX_WEIGHT];
            ///
            final double xi  = src[ci];
            final double wij = weights[ij];
            //
            dst[cj] += (xi * wij);
            //
            // next link.
            //
            link += Link.LINK_SIZE;
        }
    }
    
    
    /**
     * This method copies the value of the last source cell of all predecessor cells for
     * each destination cell. This method does not respect weights. 
     * <br></br>
     * @param src Refers the source data array.
     * @param dst Refers the destination data array.
     * @param cellsoff Refers the lower bound of the destination range.
     * @param cellsnum Refers the size of the destination range.
     * @param weights Refers the weights vector.
     * @param links Refers the links vector.
     * @param linksoff Gives the offset of the first link according 
     * the this current context.
     * @param linksnum Gives the number of links according to the current context. 
     */
    public static void lastID(
            final double[] src,
            final double[] dst,
            final int cellsoff,
            final int cellsnum,
            final double[] weights,
            final int[] links,
            final int linksoff,
            final int linksnum
    ) {
        if (linksnum == 0) return;
        //
        int link = linksoff;
        //
        // compute weighted sum over all links.
        //
        for (int i = 0; i < linksnum; i++) {
            //
            final int ci = links[link + Link.IDX_SRC]; 
            final int cj = links[link + Link.IDX_DST];
            ///
            final double xi = src[ci];
            //
            dst[cj] = xi;
            //
            // next link.
            //
            link += Link.LINK_SIZE;
        }
    }
    
    /**
     * This method computes product for the given arguments.
     * <br></br>
     * @param src Refers the source data array.
     * @param dst Refers the destination data array.
     * @param cellsoff Refers the lower bound of the destination range.
     * @param cellsnum Refers the size of the destination range.
     * @param weights Refers the weights vector.
     * @param links Refers the links vector.
     * @param linksoff Gives the offset of the first link according 
     * the this current context.
     * @param linksnum Gives the number of links according to the current context. 
     */
    public static void mult(
            final double[] src,
            final double[] dst,
            final int cellsoff,
            final int cellsnum,
            final double[] weights,
            final int[] links,
            final int linksoff,
            final int linksnum
    ) {
        if (linksnum == 0) return;
        //
        int link = linksoff;
        //
        // clean cell inputs.
        //
        int end = (cellsoff + cellsnum);
        for (int i = cellsoff; i < end; i++) {
            dst[i] = 1.0;
        }   
        //
        // compute weighted sum over all links.
        //
        for (int i = 0; i < linksnum; i++) {
            //
            final int ci = links[link + Link.IDX_SRC]; 
            final int cj = links[link + Link.IDX_DST];
            final int ij = links[link + Link.IDX_WEIGHT];
            //
            final double xi  = src[ci];
            final double wij = weights[ij];
            //
            /* 
            // # DEBUG #
            if (wij != 1.0) {
                System.out.println("ARG!");
                System.exit(1);
            }
            // #
            */
            dst[cj] *= (xi * wij);
            //
            // next link.
            //
            link += Link.LINK_SIZE;
        }
    }

    /**
     * This methods applies the a integration given by an index for the given arguments.
     * The methods uses a switch block to map to the specific integration function.
     * <br></br>
     * @param src Refers the source data array.
     * @param dst Refers the destination data array.
     * @param cellsoff Refers the lower bound of the destination range.
     * @param cellsnum Refers the size of the destination range.
     * @param weights Refers the weights vector.
     * @param links Refers the links vector.
     * @param linksoff Gives the offset of the first link according 
     * the this current context.
     * @param linksnum Gives the number of links according to the current context. 
     * @param integration Determines the specific integration function.
     */
    public static void perform(
            final double[] src,
            final double[] dst,
            final int cellsoff,
            final int cellsnum,
            final double[] weights,
            final int[] links,
            final int linksoff,
            final int linksnum,
            final int integration
    ) {
        //
        // Note: Is has been tested, that in Java a "short" switch block is much
        // faster than any other decision mechanism such as polymorphic
        // approaches like the strategy pattern.
        //
        switch (integration) {
            case CellIntegration.SUM:
                sum(
                    src, dst, cellsoff, cellsnum, 
                    weights, links, linksoff, linksnum
                );
                break;
                //
            case CellIntegration.MULT:
                mult(
                    src, dst, cellsoff, cellsnum, 
                    weights, links, linksoff, linksnum
                );
                break;
                //
            case CellIntegration.LASTID:
                lastID(
                    src, dst, cellsoff, cellsnum, 
                    weights, links, linksoff, linksnum
                );
                break;
                //
            default:
                //
                // none.
                //
                break;
        }
    }
    
    
}
