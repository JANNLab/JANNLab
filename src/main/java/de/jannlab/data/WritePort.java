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
 * A write port provides a defined interface for access data for writing.
 * <br></br>
 * @author Sebastian Otte
 */
public interface WritePort extends Serializable{
    /**
     * Write the data given by buffer into the WritePort.
     * <br></br>
     * @param buffer Source data buffer.
     * @param offset  Source data offset.
     */
    public void write(final double[] buffer, final int offset);
    /**
     * Write the data given by buffer into the WritePort only for a given selection.
     * <br></br>
     * @param buffer Source data buffer.
     * @param offset  Source data offset.
     * @param selection A selection as an array of indices.
     */
    public void write(final double[] buffer, final int offset, final int[] selection);
}
