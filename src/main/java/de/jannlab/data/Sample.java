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

import de.jannlab.misc.DoubleTools;

/**
 * This class represents samples. A sample has 
 * an input and a target sequence. These sequences 
 * consist of vectors. All vectors of the sequences
 * are mapped on a flat buffer.
 * <br></br>
 * @author Sebastian Otte
 */
public final class Sample implements Serializable {
    private static final long serialVersionUID = 4129994911525616651L;
    /**
     * The specific sample tag.
     */
    private String tag;
    /**
     * The size of the input vectors.
     */
    private int inputsize;
    /**
     * The number of input vectors.
     */
    private int inputlength;
    /**
     * The size of the target vectors.
     */
    private int targetsize;
    /**
     * The number of target vectors.
     */
    private int targetlength;
    /**
     * The input data buffer.
     */
    private double[] input;
    /**
     * The output data buffer.
     */
    private double[] target;

    /**
     * Map the input into a write port.
     * @param input Destination write port.
     */
    final public void mapInput(
            final WritePort input
    ) {
        input.write(this.input, 0);
    }
    
    /**
     * Maps the input into a write port only for a given selection.
     * @param input Destination write port.
     */
    final public void mapInput(
            final WritePort input,
            final int[] selection
    ) {
        input.write(this.input, 0, selection);
    }

    /**
     * Maps the target into a write port.
     * <br><br>
     * @param target Destination write port.
     */
    final public void mapTarget(
            final WritePort target
    ) {
        target.write(this.target, 0);
    }

    /**
     * Maps the target into a write port only for a given selection. 
     * @param target Destination write port.
     * @param selection Sequence index.
     */
    final public void mapTarget(
            final WritePort target,
            final int[] selection
    ) {
        target.write(this.target, 0, selection);
    }
    
    /**
     * Maps input and target into two write ports.
     * <br></br>
     * @param input Input destination write port.
     * @param target Target destination write port.
     */
    final public void map(
            final WritePort input, 
            final WritePort target
    ) {
        input.write(this.input, 0);
        target.write(this.target, 0);
    }

    /**
     * Maps the input into a write port for a specific seq. index.
     * <br></br>
     * @param input Destination write port.
     * @param seqidx Sequence index.
     */
    final public void mapInput(
            final WritePort input, final int seqidx
    ) {
        input.write(this.input, this.inputsize * seqidx);
    }
    
    /**
     * Maps the target into a write port only for a given selection
     * and a specific seq. index.
     * <br></br>
     * @param target Target write port.
     * @param seqidx Sequence index.
     */
    final public void mapTarget(
            final WritePort target, final int seqidx
    ) {
        target.write(this.target, this.targetsize * seqidx);
    }
    
    /**
     * Maps the input into a write port only for a given selection
     * and a specific seq. index.
     * <br></br>
     * @param input Destination write port.
     * @param seqidx Sequence index.
     * @param selection Selection as array of indices.
     */
    final public void mapInput(
            final WritePort input, final int seqidx, final int[] selection
    ) {
        input.write(this.input, this.inputsize * seqidx, selection);
    }    
    
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final int inputsize,
            final int inputlength,
            final int targetsize,
            final int targetlength
    ) {
        this(
            null,
            inputsize,
            inputlength,
            targetsize,
            targetlength
        );
        //
    }
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final String tag,
            final int inputsize,
            final int inputlength,
            final int targetsize,
            final int targetlength
    ) {
        this(
            new double[inputsize * inputlength], 
            new double[targetsize * targetlength],
            inputsize,
            inputlength,
            targetsize,
            targetlength
        );
        //
    }
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final double[] input, 
            final double[] target
    ) {
        this(
            null,
            input,
            target
        );
    }
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final String tag,
            final double[] input, 
            final double[] target
    ) {
        this(
            tag,
            input,
            target,
            input.length,
            1,
            target.length,
            1
        );
    }
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final double[] input, 
            final double[] target,
            final int inputsize,
            final int targetsize
    ) {
        this(
            null,
            input, target, 
            inputsize,  
            targetsize
        );
    }
    
    /**
     * Create an instance of Sample.
     */
    public Sample(
            final String  tag,
            final double[] input, 
            final double[] target,
            final int inputsize,
            final int targetsize
    ) {
        this(
            tag,
            input, target, 
            inputsize, input.length / inputsize, 
            targetsize, target.length / targetsize
        );
    }

    /**
     * Create an instance of Sample.
     */
    public Sample(
            final double[] input, 
            final double[] target,
            final int inputsize,
            final int inputlength,
            final int targetsize,
            final int targetlength
    ) {
        this(
            null, input, target,
            inputsize, inputlength,
            targetsize, targetlength
        );
    }

    /**
     * Create an instance of Sample.
     */
    public Sample(
            final String tag,
            final double[] input, 
            final double[] target,
            final int inputsize,
            final int inputlength,
            final int targetsize,
            final int targetlength
    ) {
        this.tag    = tag;
        this.input  = input;
        this.target = target;
        //
        this.inputsize    = inputsize;
        this.inputlength  = inputlength;
        this.targetsize   = targetsize;
        this.targetlength = targetlength;
    }
    
    /**
     * Returns the tag of the sample.
     */
    final public String getTag() { return this.tag; }
    /**
     * Returns the input size.
     */
    final public int getInputSize()  { return this.inputsize; }
    /**
     * Returns the target size.
     */
    final public int getTargetSize() { return this.targetsize; }
    /**
     * Returns the input length.
     */
    final public int getInputLength()  { return this.inputlength; }
    /**
     * Returns the target length.
     */
    final public int getTargetLength() { return this.targetlength; }
    /**
     * Returns the input buffer.
     */
    final public double[] getInput()  { return this.input; }
    /**
     * Returns the target buffer.
     */
    final public double[] getTarget() { return this.target; }
    
    /**
     * Maps the target into a write port only for a given selection and 
     * specific seq. index.
     * <br></br>
     * @param target Destination write port.
     * @param seqidx Sequence index.
     * @param selection Selection as array of indices.
     */
    final public void mapTarget(
            final WritePort target, final int seqidx, final int[] selection
    ) {
        target.write(this.target, this.targetsize * seqidx, selection);
    }
    
    /**
     * Maps input and target into two write ports. 
     * @param input Input destination write port.
     * @param target Target destination write port.
     * @param seqidx Sequence index.
     */
    final public void map(
            final WritePort input, 
            final WritePort target,
            final int seqidx
    ) {
        input.write(this.input, this.inputsize * seqidx);
        target.write(this.target, this.targetsize * seqidx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder w = new StringBuilder();
        //
        w.append("[\n");
        //
        int ioff = 0;
        int toff = 0;
        //
        final int num = Math.max(this.inputlength, this.targetlength); 
        //
        for (int i = 0; i < num; i++) {
            //
            String sinput  = "";
            String starget = "";
            //
            if (i < this.inputlength) {
                sinput = DoubleTools.asString(
                    this.input, ioff, this.inputsize, 5
                );
            }
            if (i < this.targetlength) {      
                starget = DoubleTools.asString(
                    this.target, toff, this.targetsize, 5
                );
            }
            //
            w.append(
                "\t[" + sinput + "] -> " + 
                  "[" + starget + "]\n"
            );
            //
            ioff += this.inputsize;
            toff += this.targetsize;
        }
        w.append("]\n");
        return w.toString();
    }
    
    
}
