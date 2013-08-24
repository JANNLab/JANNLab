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

import de.jannlab.Net;
import de.jannlab.data.Sample;
import de.jannlab.data.SampleSet;


/**
 * This class provides some helper methods, to keep the code
 * corresponding to experiments simple.
 * <br></br>
 * @author Sebastian Otte
 */
public final class EvaluationTools {

    public static final double REGRESSION_THRESHOLD = 0.001;
    
    public static final double performClassification(
            final Net net,
            final SampleSet samples
    ) {
        ClassificationValidator val = new ClassificationValidator(net);
        for (Sample s : samples) {
            val.apply(s);
        }
        return val.ratio();
    }

    public static final double performRegression(
            final Net net,
            final SampleSet samples
    ) {
        RegressionValidator val = new RegressionValidator(net, REGRESSION_THRESHOLD);
        for (Sample s : samples) {
            val.apply(s);
        }
        return val.ratio();
    }
    
    public static final double performClassification(
            final Net net,
            final SampleSet samples,
            final int cls
    ) {
        ClassificationValidator val = new ClassificationValidator(net);
        for (Sample s : samples) {
            if (s.getTarget()[cls] > ClassificationValidator.DEFAULT_THRESHOLD) {
                val.apply(s);
            }
        }
        return val.ratio();
    }
    
    
}
