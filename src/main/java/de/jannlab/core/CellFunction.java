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
 * This class contains static methods for computing the activations and 
 * derivations for serveral types of activation functions. These are unary functions
 * for computing just single values and more complex functions for computing the
 * activation for a range cells. For such a function an argument array and a result array
 * with offsets are given.
 * <br></br>
 * @author Sebastian Otte
 */
public final class CellFunction {
    //
    /** No computation. */
    public static final int NONE          = 0;
    /** Const value of one. */
    public static final int CONST_ONE     = 1;
    /** ID function (just copy). */
    public static final int ID            = 2;
    /** Standard sigmoid function. */
    public static final int SIGMOID       = 3;
    /** Derivation of the sigmoid function. */
    public static final int SIGMOIDDX     = 4;
    /** Sigmoid function with range of [-1,1]. */
    public static final int SIGMOID1      = 5;
    /** Derivation of the sigmoid11 function. */
    public static final int SIGMOID1DX    = 6;
    /** Sigmoid function with range of [-2,2]. */
    public static final int SIGMOID2      = 7;
    /** Derivation of the sigmoid22 function. */
    public static final int SIGMOID2DX    = 8;
    /** Tangens hyperbolicus */
    public static final int TANH          = 9;
    /** Derivation of the tanges hyperbolicus */
    public static final int TANHDX        = 10;
    /** Multiplicative invert function (1/x). */
    public static final int INVERT        = 11;

    // ------------------------------------------------------------------------
    // Unary functions.
    // ------------------------------------------------------------------------
    
    /**
     * This is just a exp-wrapper method. In this method
     * the exp-function can be replaced by a fast approx. version of exp.
     * <br></br>
     * @param value The exponent.
     * @return The exponentiation of e^{value}.
     */
    public static double exp(final double value) {
        //
        // replacing the exp with fastExp improves dramatically the runtime 
        // performance of computing activation but with less accuracy. 
        // Experiments could show, that for some training problems, the
        // fast exp works great but on most problems the convergence is
        // lower and for example LSTMs may produce NaN values.
        // 
        //return de.sotte.math.MathTools.fastExp(value);
        //
        return Math.exp(value);
    }
    /**
     * Computes the standard sigmoid function.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoid(final double value) {
        return 1.0 / (1.0 + exp(-value));
    }
    /**
     * Computes the derivation of the standard sigmoid function.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoidDx(final double value) {
        final double sig = sigmoid(value);
        return sig * (1.0 - sig);
    }
    /**
     * Computes the sigmoid function with range [-2, 2].
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoid2(final double value) {
        return (4.0 / (1.0 + exp(-value))) - 2.0;
    }
    /**
     * Computes the derivation of the sigmoid22 function.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoid2Dx(final double value) {
        final double ex = exp(value);
        return (4.0 * ex) / ((1 + ex) * (1 + ex));
        //final double sig = sigmoid(value);
        //return 4.0 * (sig * (1.0 - sig));
    }
    /**
     * Computes the sigmoid function with range [-1, 1].
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoid1(final double value) {
        return (2.0 / (1.0 + exp(-value))) - 1.0;
    }
    /**
     * Computes the derivation of the sigmoid11 function.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double sigmoid1Dx(final double value) {
        final double ex = exp(value);
        return (2.0 * ex) / ((1 + ex) * (1 + ex));
    }
    /**
     * Computes the tangens hyperbolicus.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double tanh(final double value) {
        //
        //final double exp = exp(2.0 * value);
        //return 1.0 - (2.0 / (exp + 1.0)); 
        /*
        return -1.0 + (2.0 / (
                1 + exp(-2.0 * value)
        ));
        */
        //
        return Math.tanh(value);
    }
    /**
     * Computes the derivation of the tangens hyperbolicus.
     * <br></br>
     * @param value Argument of the function.
     * @return The function result.
     */
    public static double tanhDx(final double value) {
        final double tanh = tanh(value);
        return 1.0 - (tanh * tanh);
    }
    
    // ------------------------------------------------------------------------
    // Functions for cell ranges.
    // ------------------------------------------------------------------------
    
    /**
     * Applies the identification function (copy) on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void id(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = data[o1++];
            result[o2++] = value; 
        }
    }
    
    /**
     * Applies the inversion function (1/x) on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void invert(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double value = data[o1++];
            result[o2++] = (value == 0.0)?(0.0):(1.0 / value);
        }
    }

    /**
     * Assigns constant 1.0 to a given memory block.
     * <br></br>
     * @param data Is not relevant here.
     * @param dataoffset Is not relevant here.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void constOne(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = 1.0;
        }
    }

    /**
     * Applies the standard sigmoid function on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoid(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double sig = sigmoid(data[o1++]);
            result[o2++] = sig;
        }
    }
    
    /**
     * Applies the derivation of the standard sigmoid function on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoidDx(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = sigmoidDx(data[o1++]);
        }
    }

    /**
     * Applies the sigmoid function with result range of [-2,2] on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoid2(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double sig = sigmoid2(data[o1++]);
            result[o2++] = sig;
        }
    }

    /**
     * Applies the derivation of the standard sigmoid function of range [-2, 2] on a 
     * given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoid2Dx(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = sigmoid2Dx(data[o1++]);
        }
    }

    /**
     * Applies the sigmoid function with result range of [-1, 1] on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoid1(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            final double sig = sigmoid1(data[o1++]);
            result[o2++] = sig;
        }
    }

    /**
     * Applies the derivation of the standard sigmoid function of range [-1, 1] on a 
     * given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void sigmoid1Dx(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = sigmoid1Dx(data[o1++]);
        }
    }

    
    /**
     * Applies the tanh function on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void tanh(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = tanh(data[o1++]);
        }
    }

    /**
     * Applies the derivation of tanh function on a given memory block.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     */
    public static void tanhDx(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size
    ) {
        int o1 = dataoffset;
        int o2 = resultoffset;
        //
        for (int i = 0; i < size; i++) {
            result[o2++] = tanhDx(data[o1++]);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Applies the function given by an index on a given memory block. The methods uses
     * a switch block to map to the specific activation function.
     * <br></br>
     * @param data Refers the source array containing the function arguments.
     * @param dataoffset Gives the lower bound for the argument values.
     * @param result Refers the destination array where the function results will be stored in.
     * @param resultoffset Give the lower bound for the result values.
     * @param size Gives the number of arguments.
     * @param function Determines the specific activation function.
     */
    public static void perform(
            final double[] data,
            final int dataoffset,
            final double[] result,
            final int resultoffset,
            final int size,
            final int function
    ) {
        //
        // Note: Is has been tested, that in Java a "short" switch block is MUCH!
        // faster than any other decision mechanism such as polymorphic
        // approaches like the strategy pattern.
        //
        switch (function) {
            case CellFunction.ID:
                id(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.CONST_ONE:
                constOne(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOID:
                sigmoid(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOIDDX:
                sigmoidDx(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOID1:
                sigmoid1(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOID1DX:
                sigmoid1Dx(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOID2:
                sigmoid2(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.SIGMOID2DX:
                sigmoid2Dx(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.TANH:
                tanh(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.TANHDX:
                tanhDx(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            case CellFunction.INVERT:
                invert(
                    data, dataoffset, result, resultoffset, size
                );
                break;
            default:
                //
                // none.
                //
                break;
        }
    }
    
}
