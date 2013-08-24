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

package de.jannlab.optimization.diffevo;

import java.util.Random;

import de.jannlab.math.MatrixTools;
import de.jannlab.misc.DoubleTools;
import de.jannlab.optimization.Objective;
import de.jannlab.optimization.OptimizerBase;
import de.jannlab.optimization.exception.NoObjective;

/**
 * 
 * @author Sebastian Otte
 */
public class DifferentialEvolution extends OptimizerBase<DifferentialEvolution>{
    
    public static final String KEY_POPSIZE  = "popsize";
    public static final String KEY_CR       = "CR";
    public static final String KEY_F        = "F";
    public static final String KEY_F2       = "F2";
    public static final String KEY_MUTATION = "mutation";
    public static final String KEY_INITLBD  = "initlbd";
    public static final String KEY_INITUBD  = "initubd";
    
    public static final int      DEFAULT_POPSIZE  = 100;
    public static final double   DEFAULT_CR       = 0.7;
    public static final double   DEFAULT_F        = 0.4;
    public static final double   DEFAULT_F2       = 0.4;
    public static final Mutation DEFAULT_MUTATION = Mutation.RAND_ONE;
    public static final double   DEFAULT_INITLBD  = -1.0;
    public static final double   DEFAULT_INITUBD  = 1.0;
    
    private double[] population;
    private double[] buffer;
    private double[] accu;
    private double[] fitness;
    
    private double   best_f;
    private int      best;
    
    private int      popsize;
    private double   CR;
    private double   F;
    private double   F2;
    private Mutation mutation;
    private double   initlbd;
    private double   initubd;

    private Random   rnd;
    
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        //
        out.append(super.toString());
        //
        out.append(KEY_POPSIZE + ": " + this.popsize + "\n");
        out.append(KEY_CR + ": " + this.CR + "\n");
        out.append(KEY_F + ": " + this.F + "\n");
        out.append(KEY_F2 + ": " + this.F2 + "\n");
        out.append(
            KEY_MUTATION + ": " + this.mutation.name() + "\n"
        );
        out.append(KEY_INITLBD + ": " + this.initlbd + "\n");
        out.append(KEY_INITUBD + ": " + this.initubd + "\n");
        //
        return out.toString();
    }
    
    public DifferentialEvolution() {
        //
        this.popsize    = DEFAULT_POPSIZE;
        this.CR         = DEFAULT_CR;
        this.F          = DEFAULT_F;
        this.F2         = DEFAULT_F2;
        //
        this.mutation = DEFAULT_MUTATION;
        //
        this.initlbd = DEFAULT_INITLBD;
        this.initubd = DEFAULT_INITUBD;
        //
        this.rnd = new Random(System.currentTimeMillis());
        //
        this.best   = -1;
        this.best_f = Double.POSITIVE_INFINITY;
    }
    
    public double getInitUbd() {
        return this.initubd;
    }
    
    @Override
    public boolean requiresGradient() {
        return false;    
    }
    
    
    @Override
    public double getBestError() {
        return this.best_f;
    }
    
    public void setInitUbd(final double initubd) {
        this.initubd = initubd;
    }
    
    public double getInitLbd() {
        return this.initlbd;
    }
    
    public void setInitLbd(final double initlbd) {
        this.initlbd = initlbd;
    }
    
    public Mutation getMutation() {
        return this.mutation;
    }
    
    public void setMutation(final Mutation mutation) {
        this.mutation = mutation;
    }
    
    public int getPopSize() {
        return this.popsize;
    }

    public void setPopSize(final int popsize) {
        this.popsize = popsize;
    }

    public double getCR() {
        return this.CR;
    }

    public void setCR(final double CR) {
        this.CR = CR;
    }

    public double getF() {
        return this.F;
    }

    public void setF(final double F) {
        this.F = F;
    }

    public double getF2() {
        return this.F2;
    }

    public void setF2(final double F2) {
        this.F2 = F2;
    }

    
    public void setRnd(final Random rnd) {
        this.rnd = rnd;
    }
    
   
    
    public int getPopulationSize() {
        return this.popsize;
    }
    
    public Random getRnd() {
        return this.rnd;
    }
    
    @Override
    protected DifferentialEvolution iterativeMethodMe() {
        return this;
    }

    final private int offset(final int idx) {
        return this.getParameters() * idx;
    }
    
    @Override
    protected void iterativeMethodInitialize() {
        //
        // initialize population buffer and 
        // generation working buffer.
        //
        this.population = MatrixTools.allocate(
            this.popsize, this.getParameters()
        );
        this.buffer = MatrixTools.allocate(
            this.popsize, this.getParameters()
        );
        //
        // initialize computation accu for
        // storing intermediate results.
        //
        this.accu = MatrixTools.allocate(
            this.getParameters()
        );
        //
        // initialize the fitness array with
        // +\inf for each individual.
        //
        this.fitness = MatrixTools.allocate(
            this.popsize
        );
        for (int i = 0; i < this.fitness.length; i++) {
            this.fitness[i] = Double.POSITIVE_INFINITY;
        }
    }
    
    private void initializePopulation() {
        //
        Objective obj = this.getObjective();
        if (obj == null) throw new NoObjective();
        //
        // value initialization.
        //
        DoubleTools.fill(
            this.population, 0, this.population.length,
            this.rnd, this.initlbd, this.initubd
        );
        //
        // evaluate fitness values for each individual.
        //
        int offset = 0;
        //
        int    lbest   = -1;
        double lbest_f = Double.POSITIVE_INFINITY;
        //
        for (int i = 0; i < this.popsize; i++) {
            //
            final double f = obj.compute(this.population, offset);
            this.fitness[i] = f;
            //
            if (f < lbest_f) {
                lbest   = i;
                lbest_f = f;
            }
            //
            offset += this.getParameters();
        }
        //
        this.best   = lbest;
        this.best_f = lbest_f;
        //
        this.updateError(lbest_f);
    }
    
    @Override
    protected void iterativeMethodReset() {
        this.initializePopulation();
    }

    private void preventRandomIndex(final int i) {
        //
        // TODO: Implement.
        //
    }
    
    private int nextRandomIndex() {
        return this.rnd.nextInt(this.popsize);
    }
    
    private void mutateRandOne(final int i) {
        this.preventRandomIndex(i);
        //
        // pick three indices randomly.
        //
        final int a = this.nextRandomIndex();
        final int b = this.nextRandomIndex();
        final int c = this.nextRandomIndex();
        //
        int offset_a = this.offset(a);
        int offset_b = this.offset(b);
        int offset_c = this.offset(c);
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            //
            this.accu[j] = (
                this.population[offset_a] + 
                (
                    this.F * (
                        this.population[offset_b] -
                        this.population[offset_c]
                    )
                )
            );
            //
            offset_a++;
            offset_b++;
            offset_c++;
        }
    }

    private void mutateBestOne(final int i) {
        this.preventRandomIndex(i);
        //
        // pick three indices randomly.
        //
        final int best = this.best;
        this.preventRandomIndex(best);
        final int a = this.nextRandomIndex();
        final int b = this.nextRandomIndex();
        //
        int offset_best = this.offset(best);
        int offset_a    = this.offset(a);
        int offset_b    = this.offset(b);
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            //
            this.accu[j] = (
                this.population[offset_best] + 
                (
                    this.F * (
                        this.population[offset_a] -
                        this.population[offset_b]
                    )
                )
            );
            //
            offset_best++;
            offset_a++;
            offset_b++;
        }
    }

    private void mutateRand2BestOne(final int i) {
        this.preventRandomIndex(i);
        //
        // pick three indices randomly.
        //
        final int best = this.best;
        this.preventRandomIndex(best);
        final int a = this.nextRandomIndex();
        final int b = this.nextRandomIndex();
        final int c = this.nextRandomIndex();
        //
        int offset_i    = this.offset(i);
        int offset_best = this.offset(best);
        int offset_a    = this.offset(a);
        int offset_b    = this.offset(b);
        int offset_c    = this.offset(c);
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            //
            this.accu[j] = (
                this.population[offset_i] + (
                    this.F * (
                        this.population[offset_best] -
                        this.population[offset_a]
                    )
                ) + (
                    this.F2 * (
                        this.population[offset_b] -
                        this.population[offset_c]
                    )
                )
            );
            //
            offset_i++;
            offset_best++;
            offset_a++;
            offset_b++;
            offset_c++;
        }
    }

    
    private void mutateRandTwo(final int i) {
        this.preventRandomIndex(i);
        //
        // pick three indices randomly.
        //
        final int a = this.nextRandomIndex();
        final int b = this.nextRandomIndex();
        final int c = this.nextRandomIndex();
        final int d = this.nextRandomIndex();
        final int e = this.nextRandomIndex();
        //
        int offset_a = this.offset(a);
        int offset_b = this.offset(b);
        int offset_c = this.offset(c);
        int offset_d = this.offset(d);
        int offset_e = this.offset(e);
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            //
            this.accu[j] = (
                this.population[offset_a] + (
                    this.F * (
                        this.population[offset_b] -
                        this.population[offset_c]
                    )
                ) + ( 
                    this.F2 * (
                        this.population[offset_d] -
                        this.population[offset_e]
                    )
                ) 
            );
            //
            offset_a++;
            offset_b++;
            offset_c++;
            offset_d++;
            offset_e++;
        }
        
    }

    private void mutateBestTwo(final int i) {
        this.preventRandomIndex(i);
        //
        // pick three indices randomly.
        //
        final int best = this.best;
        this.preventRandomIndex(best);
        final int a = this.nextRandomIndex();
        final int b = this.nextRandomIndex();
        final int c = this.nextRandomIndex();
        final int d = this.nextRandomIndex();
        //
        int offset_best = this.offset(best);
        int offset_a    = this.offset(a);
        int offset_b    = this.offset(b);
        int offset_c    = this.offset(c);
        int offset_d    = this.offset(d);
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            //
            this.accu[j] = (
                this.population[offset_best] + 
                (
                    this.F * (
                        this.population[offset_a] +
                        this.population[offset_b] -
                        this.population[offset_c] -
                        this.population[offset_d] 
                    )
                )
            );
            //
            offset_best++;
            offset_a++;
            offset_b++;
            offset_c++;
            offset_d++;
        }
        
    }
    
    private void mutate(final int i) {
        switch (this.mutation) {
            case RAND_ONE:
                this.mutateRandOne(i);
                break;
            case BEST_ONE:
                this.mutateBestOne(i);
                break;
            case RAND_TWO:
                this.mutateRandTwo(i);
                break;
            case BEST_TWO:
                this.mutateBestTwo(i);
                break;
            case RAND2BEST_ONE:
                this.mutateRand2BestOne(i);
                break;
        }
    }
        
    private void crossover(final int i) {
        //
        // building the crossover vector: the origin vector is given
        // through the population-buffer at offset(i). the mutation
        // vector comes from the accu at offset 0.
        //
        int orig_offset = this.offset(i);
        int accu_offset = 0;
        //
        final int n = this.getParameters();
        //
        for (int j = 0; j < n; j++) {
            final double r = this.rnd.nextDouble();
            //
            this.accu[accu_offset] = (
                (r < this.CR)?
                (this.accu[accu_offset]):
                (this.population[orig_offset])
            );
            //
            orig_offset++;
            accu_offset++;
        }
        //
    }
    
    @Override
    protected double iterativeMethodPerformIteration() {
        final Objective obj = this.getObjective();
        //
        int    lbest   = this.best;
        double lbest_f = this.best_f;
        int    offset  = 0;
        //
        final int n = this.getParameters();
        //
        // for all individuals in population...
        //
        for (int i = 0; i < this.popsize; i++) {
            //
            // first, create mutation vector based on the
            // selected mutation strategy. the resulting
            // vector is stored in this.accu at offset 0.
            //
            this.mutate(i);
            //
            // crossover operation of the current individual
            // and the mutation vector. the result is stored
            // this.accu at offset 0.
            //
            this.crossover(i);
            //
            // selection the new vector from this.accu is used
            // in the next generation, if its fitness is better
            // than the origin vector.
            //
            final double f_y = obj.compute(this.accu, 0);
            if (f_y < this.fitness[i]) {
                //
                // copy new vector and update fitness.
                //
                this.fitness[i] = f_y;
                DoubleTools.copy(
                    this.accu, 0, this.buffer, offset, n
                );                
                //
                // check for new best.
                //
                if (f_y < lbest_f) {
                    lbest_f = f_y;
                    lbest   = i;
                }
            } else {
                //
                // copy origin vector.
                //
                DoubleTools.copy(
                    this.population, offset, this.buffer, offset, n 
                );                
            }
            //
            offset += n;
        }
        //
        // copy new generation.
        //
        DoubleTools.copy(
            this.buffer, 0, this.population, 0, this.buffer.length 
        );
        //
        // update best.
        //
        this.best   = lbest;
        this.best_f = lbest_f;
        //
        return this.best_f;
    }
    
    @Override
    public void copyBestSolution(
        final double[] target, 
        final int offset) {
        //
        DoubleTools.copy(
            this.population, this.offset(this.best), 
            target, offset, this.getParameters()
        );
    }
}