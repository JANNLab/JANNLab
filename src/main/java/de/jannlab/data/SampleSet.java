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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * This class represents collections of samples. It is implemened as
 * a simple ArrayList<Sample>.
 * <br></br>
 * @author Sebastian Otte
 *
 */
public final class SampleSet extends ArrayList<Sample> {
    //
    private static final long serialVersionUID = 1346099801457631110L;
    //
    
    /**
     * Computed the maximum length of all sequences in this collection.
     * @return The maximum sequence length.
     */
    public int maxSequenceLength() {
        int max = 0;
        for (Sample s : this) {
            final int length = Math.max(
                s.getInputLength(), s.getTargetLength()
            );
            if (length > max) {
                max = length;
            }
        }
        return max;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }
    
    /**
     * Splits n random selected samples from the sample set.
     * 
     * @return A set of n random selected samples.
     */
    public SampleSet split(final int n, final Random rnd) {
        SampleSet result = new SampleSet();
        //
        // select n samples.
        //
        final int size  = Math.min(n, this.size());
        for (int i = 0; i < size; i++) {
            final int idx = rnd.nextInt(this.size());
            result.add(this.get(idx));
        }
        //
        // remove samples from set.
        //
        this.removeAll(result);
        return result;
    }
    
    /**
     * This methods shuffles the sample using the method "Collections.shuffle". 
     * <br></br>
     * @param rnd An instance of Random.
     */
    public void shuffle(final Random rnd) {
        Collections.shuffle(this, rnd);
    }
    
    /**
     * This static helper method joins givens sample set into one
     * sample set.
     * <br></br>
     * @param sets A various number of SampleSet instances.
     * @return An instance of SampleSet containing all samples of the given sets.
     */
    public static SampleSet join(final SampleSet ...sets) {
        SampleSet set = new SampleSet();
        //
        for (SampleSet s : sets) {
            set.addAll(s);
        }
        //
        return set;
    }
}
