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
 * This class lists all standard cell types. Also individual cell types 
 * can be instantiated using the constructor.For each cell type
 * several informations are given. On one hand these are functional
 * specifications of the cell such as activation or integration function 
 * for both forward and backward pass. On the other hand these are
 * some more meta informations, which are mainly used for debugging.
 * <br></br>
 * @author Sebastian Otte
 */
public final class CellType implements Serializable {
    private static final long serialVersionUID = -807350423371938387L;
    //
    /**
     * A simple value cell with no functional specification. 
     */
    public static final CellType VALUE = new CellType(
        CellIntegration.NONE, 
        CellFunction.NONE,
        CellIntegration.NONE,
        CellFunction.NONE,
        "value",
        true,
        true
    );
    /**
     * A standard sigmoid with sum for integration. The enumeration instance
     * represents on of the standard non-linear perceptrons.
     */
    public static final CellType SIGMOID = new CellType(
        CellIntegration.SUM, 
        CellFunction.SIGMOID,
        CellIntegration.SUM,
        CellFunction.SIGMOIDDX,
        "sigmoid",
        true,
        false
    );
    
    /**
     * A cell with sigmoid activation of range [-2, 2] and sum integration.
     */
    public static final CellType SIGMOID2 = new CellType(
        CellIntegration.SUM, 
        CellFunction.SIGMOID2,
        CellIntegration.SUM,
        CellFunction.SIGMOID2DX,
        "sigmoid[-2,2]",
        true,
        false
    );

    /**
     * A cell with sigmoid activation of range [-1, 1] and sum integration.
     */
    public static final CellType SIGMOID1 = new CellType(
        CellIntegration.SUM, 
        CellFunction.SIGMOID1,
        CellIntegration.SUM,
        CellFunction.SIGMOID1DX,
        "sigmoid[-1,1]",
        true,
        false
    );
    
    /**
     * A tanh cell with sum for integration. The enumeration instance 
     * represents on of the standard non-linear perceptrons.
     */
    public static final CellType TANH = new CellType(
        CellIntegration.SUM,
        CellFunction.TANH,
        CellIntegration.SUM,
        CellFunction.TANHDX,
        "tanh",
        true,
        false
    );
    
    /**
     * A linear cell with id for activation and sum for integration.
     * In this cell the error will just been passed though.
     */
    public static final CellType LINEAR = new CellType(
        CellIntegration.SUM,
        CellFunction.ID,
        CellIntegration.SUM,
        CellFunction.CONST_ONE,
        "linear",
        true,
        true
    );
    
    /**
     * The multiplicative cell has an id activation and a product integration.
     * The error in this cell will be just multiplied with the previous input.
     * This is why this cell type regularly needs to be combined with DMULTIPLICATIVE 
     * cell to gain a correct gradient based error flow.
     */
    public static final CellType MULTIPLICATIVE = new CellType(
        CellIntegration.MULT,
        CellFunction.ID,
        CellIntegration.SUM,
        CellFunction.ID,
        "multiplicative",
        false,
        false
    );
    
    /**
     * The dmultiplicative cells (delta correction) just pass the input of their the last
     * incoming connection. In backward pass the divide the error by previous input (NaN are
     * avoided). Regularly this cells is placed before each incoming connection to a 
     * multiplicative cell to gain a correct gradient based error flow. 
     */
    public static final CellType DMULTIPLICATIVE = new CellType(
        CellIntegration.LASTID,
        CellFunction.ID,
        CellIntegration.LASTID,
        CellFunction.INVERT,
        "multiplicative delta correction",
        false,
        false
    );
    //
    
    //-------------------------------------------------------------------------
    
    /**
     * Gives the id of the used integration function in the forward pass.
     * @see CellIntegration
     */
    public final int integration;
    /**
     * Gives the id of the used activation function in the forward pass.
     */
    public final int activation;
    /**
     * Gives the id of the used integration function in the backward pass.
     */
    public final int revintegration;
    /**
     * Gives the id of the used "activation" correctly the devidation of the activation
     * function in the backward pass.
     */
    public final int revactivation;
    /**
     * Is the cell a perceptron?
     */
    public final boolean perceptron;
    /**
     * Is the cell linear?
     */
    public final boolean linear;
    /**
     * Gives the name of the cell.
     */
    public final String name;

    //-------------------------------------------------------------------------
    
    /**
     * Creates an individual instance of CellType.
     */
    public CellType(
            final int integration,
            final int activation,
            final int revintegration,
            final int revactivation,
            final String name,
            final boolean perceptron,
            final boolean linear
    ) {
        this.integration    = integration;
        this.activation     = activation;
        this.revintegration = revintegration;
        this.revactivation  = revactivation;
        this.name           = name;
        this.perceptron     = perceptron;
        this.linear         = linear;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name;
    }
    
}
