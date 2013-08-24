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

import de.jannlab.optimization.exception.IterativeMethodException;
import de.jannlab.optimization.exception.NoObjective;
import de.jannlab.optimization.exception.RequiresDifferentiableObjective;

public abstract class OptimizerBase<I extends Optimizer<I>> 
    extends IterativeMethodBase<I> implements Optimizer<I> {
    //
    public static final String KEY_PARAMS = "parameters";
    //
    private Objective objective;
    
    private int parameters;
    
    public void setParameters(final int parameters) {
        this.parameters = parameters;
    }
    
    public int getParameters() {
        return this.parameters;
    }
    

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        //
        out.append(super.toString());
        //
        out.append(
            KEY_PARAMS + ": " +  this.getParameters() + "\n"
        );
        //
        return out.toString();
    }
    
    
    public OptimizerBase() {
        this.parameters = 0;
    }
    
    @Override
    public void reset() {
        super.reset();
    }
    
    @Override
    public Objective getObjective() {
        return this.objective;
    }

    @Override
    public void updateObjective(final Objective objective) {
        if (this.requiresGradient()) {
            if (!(objective instanceof DifferentiableObjective)) {
                throw new RequiresDifferentiableObjective();
            }
        }
        this.objective = objective;
    }

    @Override
    protected void preIterationCheck() throws IterativeMethodException {
        super.preIterationCheck();
        if (this.objective == null) throw new NoObjective();
    }
    
    @Override
    protected void preIterationsCheck() throws IterativeMethodException {
        super.preIterationCheck();
        if (this.objective == null) throw new NoObjective();
    }
    
}