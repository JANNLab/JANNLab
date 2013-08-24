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

package de.jannlab.examples.math;

import java.util.Collection;


/**
 * This class provides some static method for statistical
 * analysis of Vector2f instances.
 * <br></br>
 * @author Sebastian Otte
 */
public class Vector2fTools {
	
    /**
     * Compute the mean vector of a given Collection of
     * vectors.
     * <br></br>
     * @param points Collection of Vector2f instances.
     * @return The mean vector.
     */
    public static Vector2f mean(Collection<Vector2f> points) {
        //
        Vector2f result = new Vector2f();
        //
        // sum all vectors.
        //
        for (Vector2f p : points) {
            Vector2f.add(result, p, result);
        }
        //
        // divide by the number of vectors.
        //
        final float size = points.size();
        final float ilength = (size > 0.0f)?(1.0f / size):(0.0f);
        //
        Vector2f.mul(result, ilength, result);
        //
        return result;
    }    
    
    /**
     * This methods uses the PCA (principle component analysis)
     * to compute the angle of the main axis (in relation to the x-axis)
     * of a given point cloud.
     * <br></br>
     * @param points A Collection of Vector2f instances.
     * @return The angle of the main axis.
     */
    public static float pcaAngle(Collection<Vector2f> points) {
        //
        final float size = points.size();
        if (size < 2) return 0.0f;
        //
        // determin mean vector.
        //
        final Vector2f mean = mean(points); 
        //
        double cov00 = 0.0; 
        double cov01 = 0.0; 
        //double cov10 = 0.0; 
        double cov11 = 0.0; 
        //
        // determine the covariance matrix of the point cloud.
        //
        for (Vector2f p : points) {
            final float x = mean.x - p.x;
            final float y = mean.y - p.y;
            //
            cov00 += (x * x);
            cov01 += (x * y);
            //cov10 += (y * x);
            cov11 += (y * y);
        }
        //
        final double in  = (size > 0.0f)?(1.0f / size):(0.0f);
        //
        cov00 *= (in);
        cov01 *= (in);
        //cov10 *= (in);
        cov11 *= (in);
        //
        // compute angle.
        //
        final double covd = (cov00 - cov11);
        if (covd == 0.0) return 0.0f;
        final double theta = 0.5 * Math.atan(
            (2.0 * cov01) / covd
        );
        /*
        //
        // compute eigenvalues.
        //
        final double term1   = (cov00 + cov11) * 0.5;
        final double term2t  = (4.0 * cov01 * cov01) + (covd * covd);
        final double term2   = Math.sqrt(term2t)  * 0.5;
        final double lambda1 = term1 + term2;
        final double lambda2 = term1 - term2;

        System.out.println(Math.toDegrees(theta));
        System.out.println(lambda1);
        System.out.println(lambda2);
        */
        return (float)theta;
    }

    /*
    public static void main(String[] args) {
        
        
        List<Vector2f> list = new LinkedList<Vector2f>();
        
        list.add(new Vector2f(1, 0));
        list.add(new Vector2f(2, 0));
        list.add(new Vector2f(7, 7));
        
        pca(list);
    }
    */
			
}
