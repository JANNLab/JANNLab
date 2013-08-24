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

package de.jannlab.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jannlab.data.SampleSet;
import de.jannlab.data.SampleTools;
import de.jannlab.exception.JANNLabException;
import de.jannlab.io.Serializer;

/**
 * This class provides some helper methods for data handling.
 * <br></br>
 * @author Sebastian Otte
 */
public final class DataTools {
    
    public static final String FEATURE_SEPERATOR = "[;,]";
    public static final String FILE_SEPARATOR    = "[;,]";
    public static final String EXTENSION_CSV     = ".csv";
    public static final String EXTENSION_GZ      = ".gz";
    
    public static int[] features(final String features) {
        if (features.length() == 0) return null;
        List<Integer> buffer   = new ArrayList<Integer>(); 
        String[]      elements = features.split(FEATURE_SEPERATOR);
        for (String e : elements) {
            String f = e.trim();
            if (f.length() > 0) {
                buffer.add(Integer.parseInt(f));
            }
        }
        int[] result = new int[buffer.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = buffer.get(i);
        }
        return result;
    }
    
    private static String extension(final String filename) {
        final int dotPos = filename.lastIndexOf(".");
        return filename.substring(dotPos);
    }

        public static void loadToSampleSet(final SampleSet set, final String filename) 
            throws IOException, ClassNotFoundException {
        //
        final File file  = new File(filename);
        final String ext = extension(file.getName());
        //
        //
        if (ext.equalsIgnoreCase(EXTENSION_CSV)) {
            SampleSet s = SampleTools.readCSV(filename);
            set.addAll(s);
        } else if (ext.equalsIgnoreCase(EXTENSION_GZ)) {
            final SampleSet s = Serializer.read(filename); 
            set.addAll(s);
        } else {
            throw new JANNLabException("file type '" + ext + "' not supported.");
        }
    }
    
    public static SampleSet loadSampleSets(final String filenames) 
            throws IOException, ClassNotFoundException  {
        //
        final String[] files = filenames.split(FILE_SEPARATOR);
        return loadSampleSets(files);
    }
        
    public static SampleSet loadSampleSets(final String ...files) 
            throws IOException, ClassNotFoundException {
        //
        SampleSet set = new SampleSet();
        //
        for (String f : files) {
            String filename = f.trim();
            if (filename.length() > 0) {
                loadToSampleSet(set, filename);
            }
        }
        //
        return set;
    }

}
