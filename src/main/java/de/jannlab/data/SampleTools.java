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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class provides some useful methods for handling Samples
 * and SampleSets. These concern normalizing features using mean
 * and standard deviation or reading and writing
 * comma-seperated-value (csv) files
 * <br></br>
 * @see Sample
 * @see SampleSet
 * @author Sebastian Otte
 */
public class SampleTools {
    /**
     * This regular expression determines the delimiters, which
     * separate values of a vector in a csv file. 
     */
    public static final String DEFAULT_VALUEDELIMITER   = "[\\s]";
    /**
     * This regular expression determines the delimiters, which
     * separate vectors of sequence in a csv file.
     */
    public static final String DEFAULT_VECTORDELIMITER  = "[,;]";
    
    /**
     * Computes the mean of a features for a given set
     * of Samples. This method requires all Sample instances
     * to have the same input size. The result is a double array
     * with length of the input size containing the means for all
     * features.
     * <br></br>
     * @param set A set of Samples with the same input size.
     * @return The mean values for all features.
     */
    public static double[] mean(final SampleSet set) {
        if (set.size() == 0) return null;
        //
        final int inputsize   = set.get(0).getInputSize();
        final double[] result = new double[inputsize]; 
        long ctr = 0;
        //
        // add all features values.
        //
        for (Sample sample : set) {
            //
            final double[] input = sample.getInput();
            int offset = 0;
            for (int s = 0; s < sample.getInputLength(); s++) {
                for (int i = 0; i < inputsize; i++) {
                    result[i] += input[offset++];
                }
                ctr++;
            }
        }
        //
        // divide all values by the number of samples.
        //
        final double inv = (1.0 / (double)ctr);
        //
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i] * inv;
        }
        return result;
    }
    /**
     * Computes the standard deviation of all features for a given set of
     * Samples based on an additionally given array of means. This method 
     * requires all Sample instances to have the same input size, which must
     * also fit the length of the array of means. The result is a double array
     * with length of the input size containing the standard derivations for all
     * features.
     * <br></br>
     * @param set A set of Samples with the same input size.
     * @param mean A previously computed array of means with length of 
     * input size.
     * @return The standard derivations for all features.
     */
    public static double[] stdDeviation(final SampleSet set, final double[] mean) {
        if (set.size() == 0) return null;
        //
        final int inputsize   = set.get(0).getInputSize();
        final double[] result = new double[inputsize]; 
        long ctr = 0;
        //
        // compute the squared difference to the mean vector.
        //
        for (Sample sample : set) {
            //
            final double[] input = sample.getInput();
            int offset = 0;
            for (int s = 0; s < sample.getInputLength(); s++) {
                for (int i = 0; i < inputsize; i++) {
                    final double d = mean[i] - input[offset++];
                    result[i] += (d * d);
                }
                ctr++;
            }
        }
        //
        // finally compute the standard deviation.
        //
        final double inv = (1.0 / (double)(ctr - 1));
        //
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.sqrt(result[i] * inv);
        }
        return result;        
    }
    
    /**
     * Normalized all features determined by a list of indices of a set 
     * of samples. The method computes the mean and the standard deviation
     * first, which are both used for the normalization. It requires the 
     * samples to have the same input size.
     * <br></br>
     * @param set The set of samples with the same input size.
     * @param idxs A given list of indices determining the feature
     * which are to be normalized.
     */
    public static void normalize(
            final SampleSet set, 
            final int ...idxs
    ) {
        if (set.size() == 0) return;
        //
        // compute the means and standard deviations first.
        //
        final double[] mean   = mean(set);
        final double[] stddev = stdDeviation(set, mean);
        //
        normalize(set, mean, stddev, idxs);
    }
    
    /**
     * Normalizes all features determined by a list of indices of a set 
     * of samples. The normalization is based the given means and standard
     * deviations. The method requires the samples to have the same input size
     * which must also fit the length of the array of means and the array of 
     * standard deviations. 
     * <br></br>
     * @param set The set of samples with the same input size.
     * @param idxs A given list of indices determining the feature
     * which are to be normalized.
     */
    public static void normalize(
            final SampleSet set, 
            final double[] mean, 
            final double[] stddev,
            final int ...idxs
    ) {
        if (set.size() == 0) return;
        //
        final int inputsize   = set.get(0).getInputSize();
        //
        for (Sample sample : set) {
            //
            final double[] input = sample.getInput();
            //
            int offset = 0;
            for (int s = 0; s < sample.getInputLength(); s++) {
                //
                for (int i = 0; i < idxs.length; i++) {
                    final int idx = idxs[i];
                    final double x = input[offset + idx];
                    input[offset + idx] = ((x - mean[idx]) / stddev[idx]);
                }
                //
                offset += inputsize;
            }
        }
    }
    
    private static int maxSize(final double[][] data) {
        int max = 0;
        for (double[] s : data) {
            final int size = s.length;
            if (size > max) max = size;
        }
        return max;
    }
    
    private static double[][] transform(final String[] vector) {
        double[][] data = new double[vector.length][];
        for (int i = 0; i < vector.length; i++) {
            String   v  = vector[i].trim();
            String[] vs = v.split(DEFAULT_VALUEDELIMITER);
            data[i]     = new double[vs.length];
            for (int j = 0; j < data[i].length; j++) {
                //int k = j;
                data[i][j] = Double.parseDouble(vs[j]);
            }
        }
        return data;
    }
    
    private static void map(final double[][] source, final double[] dest, final int size) {
        int idx = 0;
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < size; j++) {
                if (j < source[i].length) {
                    dest[idx++] = source[i][j];
                } else {
                    idx++;
                }
            }
        }
    }
    
    public static SampleSet readCSV(final String filename) throws IOException {
        SampleSet set = new SampleSet();
        readCSV(filename, set);
        return set;
    }
    
    public static void readCSV(final String filename, final SampleSet set) throws IOException {
        //
        File file             = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        //
        while (reader.ready()) {
            //
            String line1 = null;
            String line2 = null;
            boolean grab1 = true;
            boolean grab2 = true;
            //
            // grab first line:
            //
            while (grab1 && reader.ready()) {
                line1 = reader.readLine().trim();
                if (line1.startsWith("#") || line1.length() == 0) continue;
                grab1 = false;
            }
            while (grab2 && reader.ready()) {
                line2 = reader.readLine().trim();
                if (line2.startsWith("#") || line2.length() == 0) continue;
                grab2 = false;
            }
            if ((line1 == null) || (line2 == null)) {
                break;
            }
            //
            String[] inputs  = line1.split(DEFAULT_VECTORDELIMITER);
            String[] targets = line2.split(DEFAULT_VECTORDELIMITER);
            //
            final int inputlength  = inputs.length;
            final int targetlength = targets.length;
            
            double[][] data1 = transform(inputs);
            double[][] data2 = transform(targets);
            
            final int inputsize  = maxSize(data1);
            final int targetsize = maxSize(data2);
            
            final double[] input  = new double[inputlength * inputsize];
            final double[] target = new double[targetlength * targetsize];
            //
            map(data1, input, inputsize);
            map(data2, target, targetsize);
            //
            Sample sample = new Sample(
                input, target, 
                inputsize, inputlength, 
                targetsize, targetlength
            );
            set.add(sample);
        }
        reader.close();
        //
    }

    private static String asSeqString(
            final double[] data, 
            final int size, 
            final int length
    ) {
        StringBuilder out = new StringBuilder();
        //
        int off = 0;
        //
        for (int i = 0; i < length; i++) {
            if (i > 0) out.append(DEFAULT_VECTORDELIMITER);
            //
            for (int j = 0; j < size; j++) {
                if (j > 0) out.append(DEFAULT_VALUEDELIMITER);
                final double value = data[off++];
                out.append(value);
            }
        }
        //
        return out.toString();
    }
    
    
    public static void writeCSV(final SampleSet set, final String filename) throws IOException {
        //
        File file          = new File(filename);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        //
        final int size = set.size();
        //
        out.write("# " + size + " samples\n");
        //
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                out.append("\n");
            }
            final Sample s = set.get(i);
            //
            final String input  = asSeqString(
                s.getInput(), s.getInputSize(), s.getInputLength()
            );
            final String target = asSeqString(
                s.getTarget(), s.getTargetSize(), s.getTargetLength()
            );
            out.append(input);
            out.append("\n");
            out.append(target);
        }
        out.flush();
        out.close();
    }
    
    
    
    
}
