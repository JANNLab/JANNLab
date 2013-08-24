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

/**
 * This class is a vector of three float values x, y and z.
 * It contains some static methods for operating in R3 (3 dimensional space).
 * <br></br>
 * @author Sebastian Otte
 *
 */
public class Vector3f {

	public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object pvec) {
    	if (!(pvec instanceof Vector3f)) return false;     		

    	Vector3f vec = (Vector3f)pvec;
    	return ((this.x == vec.x) && (this.y == vec.y) && (this.z == vec.z)); 
    }
    /**
     * Copies the values of a given Vector3f.
     * @param pv A vector for copy.
     */
    public void copy(Vector3f pv) {
    	this.x = pv.x;
    	this.y = pv.y;
    	this.z = pv.z;
    }
    /**
     * Constructs Vector3f by three float parameter. 
     * @param px The x value.
     * @param py The y value.
     * @param pz The z value.
     */
    public Vector3f(float px, float py, float pz)  {
    	this.x = px;
    	this.y = py;
    	this.z = pz;
    }
    /**
     * Constructs Vector3f by an other Vector3f. 
     * @param pv Reference of other Vector3f.
     */
    public Vector3f(Vector3f pv) {
    	this.x = pv.x;
    	this.y = pv.y;
    	this.z = pv.z;
    }
    /**
     * Constructs an 0 initialized Vector3f. 
     */
    public Vector3f() {
        //
    }
    /**
     * Return the length of the vector in R3.
     * @return Vector length.
     */
    public float length() {
        return (float)Math.sqrt((this.x*this.x) + 
        						(this.y*this.y) + 
        						(this.z*this.z));
    }
    /**
     * Return the length of the vector in R3 without sqrt.
     * @return Vector length.
     */
    public float length2() {
        return (this.x*this.x) + 
        	   (this.y*this.y) + 
        	   (this.z*this.z);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Vector3f(" + this.x + "," + this.y + "," + this.z + ")";
    }
    
	// ----------------------------------------------------------------
	// static methods
	// ----------------------------------------------------------------
    
    /**
     * Adds two vectors and stores the result into third vector.
     * @param pv1 The left operand vector. 
     * @param pv2 The right operand vector.
     * @param pvret The result vector.
     */
    public static void add(Vector3f pv1, Vector3f pv2, Vector3f pvret) {
        pvret.x = pv1.x + pv2.x;
        pvret.y = pv1.y + pv2.y;
        pvret.z = pv1.z + pv2.z;
    }
    /**
     * Adds two vectors and returns a new vector within the result.
     * @param pv1 The left operand vector. 
     * @param pv2 The right operand vector.
     * @return A new vector within the result of the operation.
     */
    public static Vector3f add(Vector3f pv1, Vector3f pv2) {
        return new Vector3f(pv1.x + pv2.x, pv1.y + pv2.y, pv1.z + pv2.z);
    }
    /**
     * Subtracts two vectors and stores the result into third vector.
     * @param pv1 The left operand vector. 
     * @param pv2 The right operand vector.
     * @param pvret The result vector.
     */
    public static void sub(Vector3f pv1, Vector3f pv2, Vector3f pvret) {
        pvret.x = pv1.x - pv2.x;
        pvret.y = pv1.y - pv2.y;
        pvret.z = pv1.z - pv2.z;
    }
    /**
     * Subtracts two vectors and returns a new vector within the result.
     * @param pv1 The left operand vector. 
     * @param pv2 The right operand vector.
     * @return A new vector within the result of the operation.
     */
    public static Vector3f sub(Vector3f pv1, Vector3f pv2) {
        return new Vector3f(pv1.x - pv2.x, pv1.y - pv2.y, pv1.z - pv2.z);
    }
    /**
     * Multiplies a vector with a scalar and stores the result in second vector.
     * @param pv The left operand vector. 
     * @param psc The right operand scalar.
     * @param pvret The result vector.
     */
    public static void mul(Vector3f pv, float psc, Vector3f pvret) {
        pvret.x = pv.x * psc;
        pvret.y = pv.y * psc;
        pvret.z = pv.z * psc;
    }
    /**
     * Multiplies a vector with a scalar and returns the result as new vector.
     * @param pv The left operand vector. 
     * @param psc The right operand scalar.
     * @return A new vector within the result of the operation.
     */
    public static Vector3f mul(Vector3f pv, float psc) {
        return new Vector3f(pv.x * psc, pv.y * psc, pv.z * psc);
    }
    /**
     * Divides a vector by a scalar and stores the result in second vector.
     * @param pv The left operand vector. 
     * @param psc The right operand scalar.
     * @param pvret The result vector.
     */
    public static void div(Vector3f pv, float psc, Vector3f pvret) {
        float iv = 1 / psc;
        pvret.x = pv.x * iv;
        pvret.y = pv.y * iv;
        pvret.z = pv.z * iv;
    }
    /**
     * Divides a vector by a scalar and returns the result as new vector.
     * @param pv The left operand vector. 
     * @param psc The right operand scalar.
     * @return A new vector within the result of the operation.
     */    
    public static Vector3f div(Vector3f pv, float psc)  {
        float iv = 1 / psc;
        return new Vector3f(pv.x * iv, pv.y * iv, pv.z * iv);
    }
    /**
     * Normalizes a vector and stores the result that is a vector
     * in the same direction with length of 1 in pvret.
     * @param pv Source vector.
     * @param pvret Normalized vector.
     */
    public static void normalize(Vector3f pv, Vector3f pvret) {
        float l = 1 / pv.length();
        pvret.x = pv.x * l;
        pvret.y = pv.y * l;
        pvret.z = pv.z * l; 
    }
    /**
     * Normalizes a vector and returns the result that is a vector
     * in the same direction with length of 1 as a new vector.
     * @param pv The source vector.
     * @return A new normalized vector.
     */
    public static Vector3f normalize(Vector3f pv) {
        float l = 1 / pv.length();
        return new Vector3f(pv.x * l, pv.y * l, pv.z * l);
    }
    /**
     * Inverts a given vector and stores the result into pvret.
     * @param pv The source vector.
     * @param pvret Inverted vector.
     */
    public static void invert(Vector3f pv, Vector3f pvret) {
        pvret.x = -pv.x;
        pvret.y = -pv.y;
        pvret.z = -pv.z;
    }
    /**
     * Inverts a given vector and returns a new inverted vector.
     * @param pv The source vector.
     * @return A new inverted vector.
     */
    public static Vector3f invert(Vector3f pv) {
        return new Vector3f(-pv.x, -pv.y, -pv.z);
    }
    /**
     * Returns the scalar product of two vectors.
     * @param pv1 Left operand vector.
     * @param pv2 Right operand vector.
     * @return The scalar product.
     */
    public static float scalar(Vector3f pv1, Vector3f pv2) {
        return (pv1.x * pv2.x) + (pv1.y * pv2.y) + (pv1.z * pv2.z);
    }
    /**
     * Builds the cross product of two vectors an stores the result
     * into pvret.
     * @param pv1 Left operand vector.
     * @param pv2 Right operand vector.
	 * @param pvret The result vector.
     */
    public static void cross(Vector3f pv1, Vector3f pv2, Vector3f pvret) {
        pvret.x = (pv1.y * pv2.z) - (pv1.z * pv2.y);
        pvret.y = (pv1.z * pv2.x) - (pv1.x * pv2.z);
        pvret.z = (pv1.x * pv2.y) - (pv1.y * pv2.x);
    }
    /**
     * Returns the cross product of two vectors. 
     * @param pv1 Left operand vector.
     * @param pv2 Right operand vector.
     * @return A new vector within the result of the operation.
     */
    public static Vector3f cross(Vector3f pv1, Vector3f pv2) {
        return new Vector3f((pv1.y * pv2.z) - (pv1.z * pv2.y),
                            (pv1.z * pv2.x) - (pv1.x * pv2.z),
                            (pv1.x * pv2.y) - (pv1.y * pv2.x));
    }
    /**
     * Returns the angle (phi) between two vectors.
     * @param pv1 The first vector.
     * @param pv2 The second vector.
     * @return The angle.
     */
    public static float phi(Vector3f pv1, Vector3f pv2) {
        return (float)Math.acos(scalar(pv1, pv2) / 
                                (pv1.length() * pv2.length()));
    }
    /**
     * Rotates a vector around the x-axis and stores the result into pvret.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @param pvret The return vector.
     */    
    public static void rotateX(Vector3f pv, float pangle, Vector3f pvret) {
        float cosa = (float)Math.cos(pangle);
        float sina = (float)Math.sin(pangle);
        
        pvret.x = pv.x;
        pvret.y = (pv.y * cosa) + (pv.z * -sina);  
        pvret.z = (pv.y * sina) + (pv.z * cosa);         
    }
    /**
     * Rotates a vector around the x-axis and returns the result as new vector.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @return The return vector.
     */    
    public static Vector3f rotateX(Vector3f pv, float pangle) {
        Vector3f v = new Vector3f();
        rotateX(pv, pangle, v);
        return v;
    }
    /**
     * Rotates a vector around the y-axis and stores the result into pvret.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @param pvret The return vector.
     */    
    public static void rotateY(Vector3f pv, float pangle, Vector3f pvret) {
        float cosa = (float)Math.cos(pangle);
        float sina = (float)Math.sin(pangle);
        
        pvret.x = (pv.x * cosa) + (pv.z * sina);
        pvret.y = pv.y;  
        pvret.z = (pv.x * -sina) + (pv.z * cosa);         
    }
    /**
     * Rotates a vector around the y-axis and returns the result as new vector.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @return The return vector.
     */    
    public static Vector3f rotateY(Vector3f pv, float pangle) {
        Vector3f v = new Vector3f();
        rotateY(pv, pangle, v);
        return v;        
    }
    /**
     * Rotates a vector around the z-axis and stores the result into pvret.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @param pvret The return vector.
     */    
    public static void rotateZ(Vector3f pv, float pangle, Vector3f pvret) {
        //
    	float cosa = (float)Math.cos(pangle);
        float sina = (float)Math.sin(pangle);
        
        pvret.x = (pv.x * cosa) + (pv.y * -sina);
        pvret.y = (pv.x * sina) + (pv.y * cosa);  
        pvret.z = pv.z;  
    }
    /**
     * Rotates a vector around the z-axis and returns the result as new vector.
     * @param pv The vector which is to rotate.
     * @param pangle The angle in rad of the rotation.
     * @return The return vector.
     */    
    public static Vector3f rotateZ(Vector3f pv, float pangle) {
        //
    	Vector3f v = new Vector3f();
        rotateZ(pv, pangle, v);
        return v;        
    }
    
    /**
     * The methods builds the normalized plane normal of the input vectors and stores
     * the result into pvret. Note that this method uses the right hand rule.
     * <br></br>
     * @param pvec1 The first vector "a".
     * @param pvec2 The second vector "b".
     * @param pvec3 The third vector "c".
     */
    public static void normal(Vector3f pvec1, Vector3f pvec2, Vector3f pvec3, Vector3f pvret) {
    	//
    	// Build plane with two vectors with source point pvec1.
    	//
    	Vector3f ab = Vector3f.sub(pvec2, pvec1);
    	Vector3f ac = Vector3f.sub(pvec3, pvec1);
    	//
    	// Build the cross product to get the plane normal, and normalize the vector.
    	//
    	Vector3f.cross(ab, ac, pvret);
    	Vector3f.normalize(pvret, pvret);
    }
    
    /**
     * The methods builds the normalized plane normal of the input vectors and returns
     * the result as a new vector. Note that this method uses the right hand rule.
     * <br></br>
     * @param pvec1 The first vector "a".
     * @param pvec2 The second vector "b".
     * @param pvec3 The third vector "c".
     */
    public static Vector3f normal(Vector3f pvec1, Vector3f pvec2, Vector3f pvec3) {
    	//
    	Vector3f ret = new Vector3f();
    	normal(pvec1, pvec2, pvec3, ret);
    	return ret;
    }
    
    
}
