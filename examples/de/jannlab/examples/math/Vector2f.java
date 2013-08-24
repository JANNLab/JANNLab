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

import de.jannlab.examples.math.Vector3f;

/**
 * This class is a vector of two float values x, y.
 * It contains some static methods for operating in R2 (2 dimensional space).
 * <br></br>
 * @author Sebastian Otte
 *
 */
public class Vector2f {
	
    public float x = 0.0f;
	public float y = 0.0f;
	
	/**
	 * Create an instance of Vector2f with zero values.
	 */
	public Vector2f() {
		//
	}
	
	/**
	 * Creates in instance of Vector2f by a given Vector3f.
	 * The x and y value will by divided by the z-value.  
	 * @param v Instance of Vector3f.
	 */
	public Vector2f(final Vector3f v) {
	    this(v.x / v.z, v.y / v.z);
	}
	
    /**
     * Normalizes a vector and stores the result that is a vector
     * in the same direction with length of 1 in pvret.
     * @param pv Source vector.
     * @param pvret Normalized vector.
     */
    public static void normalize(Vector2f pv, Vector2f pvret) {
        float l = 1.0f / pv.length();
        pvret.x = pv.x * l;
        pvret.y = pv.y * l;
    }
    /**
     * Normalizes a vector and returns the result that is a vector
     * in the same direction with length of 1 as a new vector.
     * @param pv The source vector.
     * @return A new normalized vector.
     */
    public static Vector2f normalize(Vector2f pv) {
        float l = 1.0f / pv.length();
        return new Vector2f(pv.x * l, pv.y * l);
    }
    
    /**
     * Creates an instance of Vector2f for a given x and y value.
     * <br></br>
     * @param px The x value.
     * @param py The y value.
     */
	public Vector2f(float px, float py) {
		this.x = px;
		this.y = py;
	}
	
    /**
     * Creates an instance of Vector2f by a given Vector2f.
     * <br></br>
     * @param pv An Instance of Vector2f.
     */
	public Vector2f(Vector2f pv) {
		this.x = pv.x;
		this.y = pv.y;
	}
	
    /**
     * Copy the values of a given Vector2f.
     * <br></br>
     * @param pv An instance of Vector2f.
     */	
	public void copy(Vector2f pv) {
		this.x = pv.x;
		this.y = pv.y;
	}
	
	/**
	 * Creates a copy this instance.
	 * <br></br>
	 * @return Copy of this instance.
	 */
	public Vector2f copy() {
		return new Vector2f(this.x, this.y);
	}
	
	/**
	 * Copies the values of a given float array.
	 * @param pd Values as float[].
	 */
	public void copy(float[] pd) {
		this.x = pd[0];
		this.y = pd[1];
	}
	
	/**
	 * Copies the values of a given float array at an specific index.
	 * @param pd Values as float[].
	 * @param poffset The values offset.
	 */
	public void copy(float[] pd, int poffset) {
		this.x = pd[poffset];
		this.y = pd[poffset + 1];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Vector2f(" + this.x + "," + this.y + ")";
	}
	
	/**
	 * Computes the length of the vector (euclidian).
	 * @return Vector length.
	 */
	public float length() {
		return (float)Math.sqrt((this.x * this.x) + (this.y * this.y));
	}

	/**
     * Computes the squared length of the vector (euclidian). Can be 
     * used as a faster metric then length, of only the relative order
     * of the vectors is needed. 
     * @return Vector length without final sqrt.
     */
	public float length2() {
		return ((this.x * this.x) + (this.y * this.y));
	}
	/**
	 * Adds two given Vector2f instances and stores the value in a third instance. 
	 * @param pv1 The first instance.
	 * @param pv2 The second instance.
	 * @param pvret The result instance.
	 */
    public static void add(Vector2f pv1, Vector2f pv2, Vector2f pvret) {
        pvret.x = pv1.x + pv2.x;
        pvret.y = pv1.y + pv2.y;
    }

    /**
     * Adds two given Vector2f instances and returns a new result instance. 
     * @param pv1 The first instance.
     * @param pv2 The second instance.
     * @return Resulting vector of pv1 + pv2.
     */
    public static Vector2f add(Vector2f pv1, Vector2f pv2) {
        return new Vector2f(pv1.x + pv2.x, pv1.y + pv2.y);
    }
    
    /**
     * Subtracts two given Vector2f instances and stored the result
     * in a third Vector2f instance. 
     * @param pv1 The first instance.
     * @param pv2 The second instance.
     * @param pvret The Third instance (result).
     */
    public static void sub(Vector2f pv1, Vector2f pv2, Vector2f pvret) {
        pvret.x = pv1.x - pv2.x;
        pvret.y = pv1.y - pv2.y;
    }

    /**
     * Subtracts two given Vector2f instances and return a
     * new instance of Vector2f.
     * @param pv1 The first instance.
     * @param pv2 The second instance.
     * @return Resulting vector of pv1 - pv2.
     */
    public static Vector2f sub(Vector2f pv1, Vector2f pv2) {
        return new Vector2f(pv1.x - pv2.x, pv1.y - pv2.y);
    }

    /**
     * Multiplies a Vector2f instance with a given scalar value and stores
     * the result in a second Vector2f instance.
     * @param pv An instance of Vector2f.
     * @param pscalar A scalar value.
     * @param presult The second instance (result).
     */
    public static void mul(Vector2f pv, float pscalar, Vector2f presult) {
		presult.x = pv.x * pscalar;
		presult.y = pv.y * pscalar;
	}
    /**
     * Returns the scalar product of two vectors.
     * @param pv1 Left operand vector.
     * @param pv2 Right operand vector.
     * @return The scalar product.
     */
    public static float scalar(Vector2f pv1, Vector2f pv2) {
        return (pv1.x * pv2.x) + (pv1.y * pv2.y);
    }

    /**
     * Returns the cosinus of the angle between two vectors.
     * @param pv1 The first vector.
     * @param pv2 The second vector.
     * @return The angle.
     */
    public static float cos(Vector2f pv1, Vector2f pv2) {
        final float scalar = scalar(pv1, pv2);
        final float length = (pv1.length() * pv2.length());
        return scalar / length;
    }
    
    /**
     * Returns the angle (phi) between two vectors.
     * @param pv1 The first vector.
     * @param pv2 The second vector.
     * @return The angle.
     */
    public static float phi(Vector2f pv1, Vector2f pv2) {
        return (float)Math.acos(cos(pv1, pv2));
    }
    
    /**
     * Returns the angle (phi) between two vectors signed (counterclockwise).
     * @param pv1 The first vector.
     * @param pv2 The second vector.
     * @return The angle.
     */
    public static float signedPhi(Vector2f pv1, Vector2f pv2) {
        //
        final double a1 = Math.atan2(pv2.y, pv2.x);
        final double a2 = Math.atan2(pv1.y, pv1.x);
        //
        return (float)(a1 - a2);
    }
    /**
     * Multiplies a given vector with a scalar value. The method changes
     * the given vector instance.
     * @param pv The vector.
     * @param pscalar The scalar value.
     * @return The scales vector.
     */
    public static Vector2f mul(Vector2f pv, float pscalar) {
		return new Vector2f(pv.x * pscalar, pv.y * pscalar);
	}
    /**
     * Rotates a vector and returns the result as new vector.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @return The return vector.
     */    
    public static Vector2f rotate(Vector2f pv, float pangle) {
        Vector2f v = new Vector2f();
        rotate(pv, pangle, v);
        return v;
    }
    
    /**
     * Rotates a vector and stores the result into pvret.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @param pvret The return vector.
     */    
    public static void rotate(Vector2f pv, float pangle, Vector2f pvret) {
        float cos = (float)Math.cos(pangle);
        float sin = (float)Math.sin(pangle);
        //
        final float x = pv.x;
        final float y = pv.y;
        //
        pvret.x = (x * cos) + (y * -sin);  
        pvret.y = (x * sin) + (y * cos);         
    }
			
}
