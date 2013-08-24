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

package de.jannlab.optimization;

import java.util.LinkedList;
import java.util.List;

import de.jannlab.optimization.exception.IterativeMethodException;
import de.jannlab.optimization.exception.NotAllowedWhileRunning;
import de.jannlab.optimization.exception.NotInitialized;

public abstract class IterativeMethodBase<I extends IterativeMethod<I>> 
    implements IterativeMethod<I> {

    private int     iteration;
    private double  error;
    private boolean initialized;
    private boolean abort;
    private boolean running;
    
    private List<IterationListener<I>> listener;

    
    protected IterativeMethodBase() {
        this.listener    = new LinkedList<IterationListener<I>>();
        this.initialized = false;
        this.abort       = false;
        this.running     = false;
    }
    
    @Override
    public void addListener(IterationListener<I> listener) {
        this.listener.add(listener);
    }

    @Override
    public void removeListener(IterationListener<I> listener) {
        this.listener.remove(listener);
    }

    @Override
    public void clearListener(IterationListener<I> listener) {
        this.listener.clear();
    }
    
    @Override
    synchronized
    public void requestAbort() {
        if (this.running) {
            this.abort = true;
        }
    }
    
    protected void abort() {
        this.abort = true;
    }
    
    protected boolean getAbort() {
        return this.abort;
    }
    
    protected abstract I iterativeMethodMe();
    
    private void beforeIteration() {
        //
        final I me = this.iterativeMethodMe();
        //
        for (IterationListener<I> l : this.listener) {
            l.beforeIteration(this.iteration, me);
        }
    }

    private void afterIteration() {
        //
        final I me = this.iterativeMethodMe();
        //
        for (IterationListener<I> l : this.listener) {
            l.afterIteration(this.iteration, me);
        }
    }

    private void started() {
        //
        final I me = this.iterativeMethodMe();
        //
        for (IterationListener<I> l : this.listener) {
            l.started(me);
        }
    }

    protected abstract void iterativeMethodInitialize();
    
    @Override
    public void initialize() {
        this.iterativeMethodInitialize();
        this.initialized = true;
        this.reset();
    }
    
    private void finished() {
        //
        final I me = this.iterativeMethodMe();
        //
        for (IterationListener<I> l : this.listener) {
            l.finished(this.iteration, this.error, me);
        }
    }

    @Override
    public int getIteration() {
        return this.iteration;
    }
    
    protected abstract void iterativeMethodReset();
    protected abstract double iterativeMethodPerformIteration();
    
    protected void updateError(final double error) {
        this.error = error;
    }
    
    @Override
    public void reset() {
        if (this.running) throw new NotAllowedWhileRunning();
        this.iteration = 0;
        this.abort = false;
        this.updateError(Double.POSITIVE_INFINITY);
        this.iterativeMethodReset();
    }

    
    protected void preIterationCheck() throws IterativeMethodException {
        if (!this.initialized) throw new NotInitialized();
    }
    
    protected void preIterationsCheck() throws IterativeMethodException {
        if (!this.initialized) throw new NotInitialized();
    }
    
    
    @Override
    public double performIteration() {
        //
        this.preIterationCheck();
        this.beforeIteration();
        final double result = this.iterativeMethodPerformIteration();
        this.updateError(result);
        this.afterIteration();
        this.iteration++;
        return result;
    }
    
    @Override
    public double getError() {
        return this.error;
    }

    @Override
    synchronized
    public double iterate(final int iterations, final double targeterror) {
        if (this.running) throw new NotAllowedWhileRunning();
        this.abort   = false;
        this.running = true;
        //
        this.preIterationsCheck();
        //
        this.started();
        //
        for (int i = 0; i < iterations; i++) {
            final double result = this.performIteration();
            if ((result < targeterror) || this.abort) {
                break;
            }
        }
        //
        this.finished();
        //
        this.running = false;
        return this.error;
    }
}