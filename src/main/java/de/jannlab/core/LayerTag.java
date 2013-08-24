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
 * This small class just contains some constants
 * defining the computational behavior of layer.
 * Whether it is regular (computing input sequences
 * in forward direction) or (reverted computing 
 * input sequences in backward direction). Constants
 * are chosen to allow adding other computational
 * behaviors later (e.g for multidimensional ANNs).
 * <br></br>
 * @author Sebastian Otte
 */
public final class LayerTag {
    /**
     * A layer defined as regular computes input sequences in
     * forward direction.
     */
    public static final int REGULAR = 0x1;
    /**
     * A layer defined as reverted computes input sequences in
     * backward direction.
     */
    public static final int REVERSED = 0x2;
    /**
     * Makes a string representation of a given id.
     * This method is mainly for debugging.
     * <br></br>
     * @param tag A layer tag id.
     * @return String representation.
     */
    public final static String asString(final int tag) {
        if (tag == REGULAR) return "REGULAR";
        if (tag == REVERSED)  return "REVERTED";
        //
        return "UNKNOWN";
    }
}
