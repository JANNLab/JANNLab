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

package de.jannlab.optimization.exception;

import de.jannlab.exception.JANNLabException;


public class IterativeMethodException extends JANNLabException {
    private static final long serialVersionUID = -4578796617966258713L;

    
    public IterativeMethodException(
        final String msg
    ) {
        super(msg);
    }

    public IterativeMethodException(
        final String msg,
        final Throwable cause
    ) {
        super(msg, cause);
    }

    
    public IterativeMethodException() {};


}