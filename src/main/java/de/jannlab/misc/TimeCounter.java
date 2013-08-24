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

package de.jannlab.misc;

/**
 * This class is for measurement of performance an timings. 
 * <br></br>
 * @author Sebastian Otte
 */
public class TimeCounter {
	//
	private final static long   SCALE_MICRO  = 1000L;
	private final static long   SCALE_MILLI  = 1000000L;
	//
	private final static double FACTOR_MICRO = 1.0 / 1000.0;
	private final static double FACTOR_MILLI = 1.0 / 1000000.0;
	
	protected long value = 0L;
	/**
	 * Resets the saved start time with the actual time.
	 */
	public void reset() {
		this.value = System.nanoTime();
	}
	/**
	 * Creates an instance of PerformanceCounter.
	 */
	public TimeCounter() {
		this.reset();
	}
	
	/**
	 * Returns the passed nanoseconds since the last counter reset.
	 */
	public long valueNano() {
		final long now = System.nanoTime();
		final long ns  = now - this.value;
		return ns;
	}
	
	/**
	 * Return the passed milliseconds since the last counter reset.
	 */
	public long value() {
		final long now = System.nanoTime();
		final long ns  = now - this.value;
		return ns / SCALE_MILLI;
	}
	
    /**
     * Return the passed microseconds since the last counter reset.
     */
	public long valueMicro() {
		final long now = System.nanoTime();
		final long ns  = now - this.value;
		return ns / SCALE_MICRO;
	}

   /**
     * Return the passed microseconds since the last counter reset.
     */
	public long valueMilli() {
		final long now = System.nanoTime();
		final long ns  = now - this.value;
		return ns / SCALE_MILLI;
	}
	
	
    /**
     * Return the passed nanoseconds since the last counter reset.
     */
	public double valueNanoDouble() {
		final long   now = System.nanoTime();
		final double ns  = now - this.value;
		return ns;
	}
	
    /**
     * Return the passed milliseconds since the last counter reset.
     */
	public double valueDouble() {
		final long   now = System.nanoTime();
		final double ns  = now - this.value;
		return ns * FACTOR_MILLI;
	}
	
    /**
     * Return the passed microseconds since the last counter reset.
     */
	public double valueMicroDouble() {
		final long   now = System.nanoTime();
		final double ns  = now - this.value;
		return ns * FACTOR_MICRO;
	}
	
    /**
     * Return the passed milliseconds since the last counter reset.
     */
	public double valueMilliDouble() {
		final long   now = System.nanoTime();
		final double ns  = now - this.value;
		return ns * FACTOR_MILLI;
	}
	
	/**
	 * Returns the current passed millisecond and calls reset().
	 */
	public long reval() {
		final long now = System.nanoTime();
		final long ns  = now - this.value;
		//
		this.value = now;
		//
		return ns / SCALE_MILLI;
	}

	/**
     * Returns the current passed milliseconds and calls reset().
     */
	public double revalDouble() {
		final long   now = System.nanoTime();
		final double ns  = now - this.value;
		//
		this.value = now;
		//
		return ns * FACTOR_MILLI;
	}
}
