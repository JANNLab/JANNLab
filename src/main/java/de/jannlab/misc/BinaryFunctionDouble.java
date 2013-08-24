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

package de.jannlab.misc;

/**
 * This interface defines a binary function over double.
 * <br></br>
 * @author Sebastian Otte
 */
public interface BinaryFunctionDouble {
    /**
     * Computes the operation (first o second), where o is 
     * a implementation specific operation.
     * <br></br>
     * @param first Left operand.
     * @param second Right operand.
     * @return The result of the operation.
     */
    public double perform(double first, double second);
}
