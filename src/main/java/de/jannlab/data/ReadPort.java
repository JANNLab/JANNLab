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

package de.jannlab.data;


import java.io.Serializable;

/**
 * A read port provides a defined interface for access data for reading.
 * <br></br>
 * @author Sebastian Otte
 */
public interface ReadPort extends Serializable {
    /**
     * Reads data from the data source and stores the result in buffer.
     * <br></br> 
     * @param buffer Target data buffer.
     * @param offset Offset in the buffer. 
     */
    public void read(final double[] buffer, final int offset);
    /**
     * Reads data from the data source and stores the result in buffer
     * but only for a special selection.
     * <br></br> 
     * @param buffer Target data buffer.
     * @param offset Offset in the buffer. 
     * @param selection Selection as array of indices.
     */
    public void read(
        final double[] buffer, final int offset, final int[] selection
    );
}
