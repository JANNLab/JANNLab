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

package de.jannlab.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jannlab.Net;
import de.jannlab.core.BidirectionalNetBase;
import de.jannlab.core.CellArray;
import de.jannlab.core.CellType;
import de.jannlab.core.FeedForwardNetBase;
import de.jannlab.core.Layer;
import de.jannlab.core.LayerTag;
import de.jannlab.core.Link;
import de.jannlab.core.NetData;
import de.jannlab.core.NetStructure;
import de.jannlab.core.OfflineRecurrentNetBase;
import de.jannlab.core.OnlineRecurrentNetBase;
import de.jannlab.core.RecurrentNetBase;
import de.jannlab.generator.exception.LinkArrayCorrupt;

/**
 * This is the core class of the generator concept. The 
 * methods of this class define the atomic building blocks
 * which can be used to model ANNs.
 * <br></br>
 * @author Sebastian Otte
 */
public class NetCoreGenerator implements NetGenerator {
    /**
     * Constant value for "weight is needed later".
     */
    private static final int WEIGHT_NEEDED = -2;
    /**
     * Constant value for "has no weight".
     */
    private static final int NOT_ASSIGNED = -1;
    /**
     * Default frame width.
     */
    private static final int DEFAULT_FRAMES = 1;
    /**
     * A data buffer for links.
     */
    private List<int[]> links = new ArrayList<int[]>();
    /**
     * A data buffer for cell arrays.
     */
    private List<CellArray> arrays = new ArrayList<CellArray>();
    /**
     * A data buffer for layers.
     */
    private List<Layer> layers = new ArrayList<Layer>();
    /**
     * Contains the net structure after modeling.
     */
    private NetStructure structure = null;
    /**
     * Contains the net data buffer.
     */
    private NetData data = null;
    /**
     * Maps cells to layers.
     */
    private int[] layermap = null;
    /**
     * Maps cells to cell arrays.
     */
    private int[] arraymap = null;
    /**
     * Contains the indeg for all cells.
     */
    private int[] indeg = null;
    /**
     * Contains the outdeg for all cells.
     */
    private int[] outdeg = null;
    /**
     * The current computation index counter.
     */
    private int compctr = 0;
    /**
     * The current computation index.
     */
    private int compidx = 0;
    /**
     * The pre-configured number of time steps.
     */
    private int framewidth = 0;
    /**
     * The map of assignment. The content will be transformated
     * later int arrays.
     */
    private Map<Integer, Double> assigns = new HashMap<Integer, Double>();
    /**
     * Defines the inputlayer.
     */
    private int inputlayer  = NOT_ASSIGNED;
    /**
     * Defines the outputlayer.
     */
    private int outputlayer = NOT_ASSIGNED;
    /**
     * Gives the current number of cells (or the cell offset).
     */
    private int cellsoffset = 0;
    /**
     * Gives the current number of computing cells.
     */
    private int comcellsnum = 0;
    /**
     * Gives the current number of values cells.s
     */
    private int valcellsnum = 0;
    /**
     * Gives the current number of layer (or the layer offset).
     */
    private int layeroffset = 0;
    /**
     * Gives the current connection mode.
     */
    private int ilctag  = CellArray.ILC_BOTH;
    /**
     * The current layer.
     */
    private Layer layer = null;
    /**
     * Are we in a layer?
     */
    private boolean inlayer = false;
    /**
     * Computes the network offline?
     */
    private boolean offline = false;
    
    /**
     * Create an instance of NetCoreGenerator.
     */
    public NetCoreGenerator() {
        this.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.links.clear();
        this.arrays.clear();
        this.layers.clear();
        //
        this.inputlayer   = NOT_ASSIGNED;
        this.outputlayer  = NOT_ASSIGNED;
        //
        this.cellsoffset  = 0;
        this.layeroffset  = 0;
        this.inlayer      = false;
        //
        this.comcellsnum  = 0;
        this.valcellsnum  = 0;
        //
        this.structure = null;
        this.data      = null;
        //
        this.layermap  = null;
        this.arraymap  = null;
        //
        this.indeg     = null;
        this.outdeg    = null;
        //
        this.compctr   = 0;
        this.compidx   = 0;
        //
        this.framewidth    = DEFAULT_FRAMES;
        //
        this.ilctag    = CellArray.ILC_BOTH;
        //
        this.assigns.clear();
    }

    //-------------------------------------------------------------------------
    // GENERATING METHODS.
    //-------------------------------------------------------------------------

    /**
     * Sorting links, redundant link elimination, rev. links generation.
     */
    private void setupLinks() {
        //
        // now acquire links.
        //
        final int rawlinksnum = this.links.size();
        int[] rawlinks = new int[rawlinksnum * Link.LINK_SIZE];
        int idx  = 0;
        //
        for (int i = 0; i < rawlinksnum; i++) {
            final int[] link = this.links.get(i);
            //
            final int src  = link[0];
            final int dst  = link[1];
            final int widx = link[2];
            //
            rawlinks[idx] = src;  idx++;
            rawlinks[idx] = dst;  idx++;
            rawlinks[idx] = widx; idx++;
        }
        //
        // sort raw links and eliminate redundant links.
        //
        Link.sortDstMaj(rawlinks, Link.ORDER_ASC);
        int[] links = Link.eliminateRedundantLinks(rawlinks);
        //
        if (links.length % Link.LINK_SIZE != 0) {
            throw new LinkArrayCorrupt();
        }
        //
        this.structure.links = links;
        final int linksnum = links.length / Link.LINK_SIZE;
        this.structure.linksnum = linksnum;
        //
        // count weights and assign weight indices. the
        // first weights is always 1.0.
        //
        int woff = 1;
        idx = 0;
        for (int i = 0; i < linksnum; i++) {
            final int src  = links[idx + Link.IDX_SRC];
            final int dst  = links[idx + Link.IDX_DST];
            final int widx = links[idx + Link.IDX_WEIGHT];
            //
            this.indeg[dst]++;
            this.outdeg[src]++;
            //
            // recurrence check.
            //
            final int lsrc = this.layermap[src];
            final int ldst = this.layermap[dst];
            if (lsrc >= ldst) {
                this.structure.recurrent = true;
            }
            //
            if (widx == WEIGHT_NEEDED) {
                links[idx + Link.IDX_WEIGHT] = woff; woff++; 
            } else if (widx == Link.NOWEIGHT) {
                links[idx + Link.IDX_WEIGHT] = 0;
            }
            //
            idx += Link.LINK_SIZE;
        }
        //
        // dont count the first weight, which is constant 1.
        //
        this.structure.weightsnum = (woff - 1);
        //
        // create reverted links. the weight indices are
        // now in linksrev too. the reverted are sorted in
        // source major order.
        //
        int[] linksrev = links.clone();
        Link.sortSrcMaj(linksrev, Link.ORDER_ASC);
        //
        // swap src and dst in linksrev.
        //
        idx = 0;
        for (int i = 0; i < linksnum; i++) {
            final int src  = linksrev[idx + Link.IDX_SRC];
            final int dst  = linksrev[idx + Link.IDX_DST];
            //
            linksrev[idx + Link.IDX_DST] = src;
            linksrev[idx + Link.IDX_SRC] = dst;
            //
            idx += Link.LINK_SIZE;
        }
        this.structure.linksrev = linksrev;
    }
    
    /**
     * Configure cell arrays. Map predecessor and successor on each cell array.
     */
    private void setupArrays() {
        //
        // prepare arrays and sum indeg and outdeg.
        //
        CellArray[] arrays = new CellArray[this.arrays.size()];
        for (int i = 0; i < arrays.length; i++) {
            final CellArray a = this.arrays.get(i);
            //
            for (int j = a.cellslbd; j <= a.cellsubd; j++) {
                a.indeg  += this.indeg[j];
                a.outdeg += this.outdeg[j];
            }
            //
            arrays[i] = a;
        }
        //
        // forward:
        // map arrays (pred) to links and count weights
        //
        int link = 0;
        //
        for (int i = 0; i < arrays.length; i++) {
            //
            final CellArray a = arrays[i];
            if ((a.cellsnum <= 0) || (a.indeg == 0)) continue;
            final int albd = a.cellslbd;
            final int aubd = a.cellsubd;
            //
            int lbd  = -1;
            int ctr  = 0;
            //
            // find links range begin by dst idx.
            //
            while (
                    ((link + Link.IDX_DST) < this.structure.links.length) &&
                    (this.structure.links[link + Link.IDX_DST] < albd)
            ) {
                link += Link.LINK_SIZE;
            }
            lbd = link;
            //
            // find links range end by dst idx.
            //
            while (
                    ((link + Link.IDX_DST) < this.structure.links.length) &&
                    (this.structure.links[link + Link.IDX_DST] <= aubd)
            ) {
                link += Link.LINK_SIZE;
                ctr++;
            }
            //
            a.predslbd = lbd;
            a.predsnum = ctr;
            //
            // changed: 01.02.2012, Sebastian Otte
            // a.predsubd = (a.predslbd + a.predsnum) - 1;
            //
            a.predsubd = link - Link.LINK_SIZE;
        }
        //
        // backward:
        // map arrays (succ) to links and count weights
        //
        link = 0; 
        //
        for (int i = 0; i < arrays.length; i++) {
            //
            final CellArray a = arrays[i];
            //
            if ((a.cellsnum <= 0) || (a.outdeg == 0)) continue;
            final int albd = a.cellslbd;
            final int aubd = a.cellsubd;
            //
            int lbd  = -1;
            int ctr  = 0;
            //
            // find links range begin by src idx (swapped src and dst)
            //
            while (
                    ((link + Link.IDX_DST) < this.structure.linksrev.length) &&
                    (this.structure.linksrev[link + Link.IDX_DST] < albd)
                    //((link + Link.IDX_SRC) < this.structure.linksrev.length) &&
                    //(this.structure.linksrev[link + Link.IDX_SRC] < albd)
            ) {
                link += Link.LINK_SIZE;
            }
            lbd = link;
            //
            // find links range end by src idx (swapped src and dst).
            //
            while (
                    ((link + Link.IDX_DST) < this.structure.linksrev.length) &&
                    (this.structure.linksrev[link + Link.IDX_DST] <= aubd)
                    //((link + Link.IDX_SRC) < this.structure.linksrev.length) &&
                    //(this.structure.linksrev[link + Link.IDX_SRC] <= aubd)
            ) {
                link += Link.LINK_SIZE;
                ctr++;
            }
            a.succslbd = lbd;
            a.succsnum = ctr;
            //
            // changed: 01.02.2012, Sebastian Otte
            // a.succsubd = (a.succslbd + a.succsnum) - 1;
            //
            a.succsubd = link - Link.LINK_SIZE;
        }
        this.structure.arrays    = arrays;
        this.structure.arraysnum = arrays.length;
    }
    
    /**
     * Setup up cell to X maps.
     */
    private void setupMaps() {
        //
        this.layermap = new int[this.cellsoffset];
        this.arraymap = new int[this.cellsoffset];
        this.indeg    = new int[this.cellsoffset];
        this.outdeg   = new int[this.cellsoffset];
        //
        //
        for (int i = 0; i < this.cellsoffset; i++) {
            this.layermap[i] = NOT_ASSIGNED;
            this.arraymap[i] = NOT_ASSIGNED;
        }
        //
        // determine layer assignment.
        //
        for (int i = 0; i < this.layers.size(); i++) {
            final Layer layer = this.layers.get(i);
            //
            for (int j = layer.cellslbd; j <= layer.cellsubd; j++) {
                this.layermap[j] = i;
            }
        }
        //
        // determine array assignment.
        //
        for (int i = 0; i < this.arrays.size(); i++) {
            final CellArray array = this.arrays.get(i);
            //
            for (int j = array.cellslbd; j <= array.cellsubd; j++) {
                this.arraymap[j] = i;
            }
        }
    }
    
    /**
     * Setup layers. This is mainly the computation order.
     */
    private void setupLayers() {
        //
        // prepare layers and sum indeg and outdeg.
        //
        Layer[] layers = new Layer[this.layers.size()];
        for (int i = 0; i < layers.length; i++) {
            final Layer layer = this.layers.get(i);
            if (layer.tag == LayerTag.REVERSED) {
                this.structure.offline       = true;
                this.structure.bidirectional = true;
            }
            //
            for (int j = layer.cellslbd; j <= layer.cellsubd; j++) {
                layer.indeg  += this.indeg[j];
                layer.outdeg += this.outdeg[j];
            }
            //
            // clean up compwidth (value arrays has compidx -1).
            //
            int maxidx = -1;
            for (int a = layer.arrayslbd; a <= layer.arraysubd; a++) {
                final CellArray array = this.structure.arrays[a];
                if (array.compidx > maxidx) {
                    maxidx = array.compidx;
                }
            }
            layer.compwidth = maxidx + 1;
            //
            // determine computation order lower and upper bounds.
            //
            layer.complbds = new int[layer.compwidth];
            layer.compubds = new int[layer.compwidth];
            int idx  = -1;
            int cidx = -1;
            //
            for (int a = layer.arrayslbd; a <= layer.arraysubd; a++) {
                final CellArray array = this.structure.arrays[a];
                if (array.compidx > cidx) {
                    idx++;
                    layer.complbds[idx] = a;
                    layer.compubds[idx] = a;
                    cidx = array.compidx;
                } else {
                    if (idx >= 0) layer.compubds[idx]++;
                }
            }
            //
            layers[i] = layer;
        }
        //
        // if net is not bidirectional force all layers to be regular.
        //
        if (!this.structure.recurrent || !this.structure.bidirectional) {
            for (int i = 0; i < layers.length; i++) {
                layers[i].tag = LayerTag.REGULAR;
            }
        }
        //
        this.structure.layers    = layers;
        this.structure.layersnum = layers.length;
        //
        final Layer in  = layers[this.inputlayer];
        final Layer out = layers[this.outputlayer];
        //
        this.structure.inputlayer  = this.inputlayer;
        this.structure.outputlayer = this.outputlayer;
        this.structure.incellslbd  = in.cellslbd;
        this.structure.incellsubd  = in.cellsubd;
        this.structure.incellsnum  = in.cellsnum;
        this.structure.outcellslbd  = out.cellslbd;
        this.structure.outcellsubd  = out.cellsubd;
        this.structure.outcellsnum  = out.cellsnum;
    }
    
    /**
     * Builds a feedforward network.
     */
    private FeedForwardNetBase buildFeedForwardNet() {
        return new FeedForwardNetBase(this.structure, this.data);
    }
    
    /**
     * Builds a unidirectional recurrent offline network. 
     */
    private OfflineRecurrentNetBase buildOfflineRecurrentNet() {
        return new OfflineRecurrentNetBase(this.structure, this.data);
    }
    /**
     * Builds a bidirectional network. 
     */
    private BidirectionalNetBase buildBidirectionalNet() {
        return new BidirectionalNetBase(this.structure, this.data);
    }
    /**
     * Builds a unidirectional recurrent network. 
     */
    private OnlineRecurrentNetBase buildOnlineRecurrentNet() {
        return new OnlineRecurrentNetBase(this.structure, this.data);
    }
    
    /**
     * Builds a reccurent network.
     */
    private RecurrentNetBase buildRecurrentNet() {
        //
        if (this.structure.offline) {
            if (this.structure.bidirectional) {
                return this.buildBidirectionalNet();
            } else {
                return this.buildOfflineRecurrentNet();
            }
        } else {
            return this.buildOnlineRecurrentNet();
        }
    }
    
    /**
     * Builds a network.
     */
    private Net buildNet() {
        if (this.structure.recurrent) {
            return this.buildRecurrentNet();
        } else {
            return this.buildFeedForwardNet();
        }
    }
    
    /**
     * Setup the data buffer. Important here are
     * the assignments.
     */
    private void setupData() {
        final int cells = this.structure.cellsnum;
        int fn    = this.framewidth;
        if (fn < 0) fn = 1;
        if (!this.structure.recurrent) fn = 1;
        //
        this.data.input      = new double[fn][cells];
        this.data.output     = new double[fn][cells];
        this.data.gradinput  = new double[fn][cells];
        this.data.gradoutput = new double[fn][cells];
        //
        // build assignments arrays.
        //
        final Set<Integer> keys = this.assigns.keySet();
        final int asgnsnum = keys.size();
        //
        this.data.asgns  = new int[asgnsnum];
        this.data.asgnsv = new double[asgnsnum];
        //
        int idx = 0;
        for (int key : keys) {
            final double value = this.assigns.get(key);
            this.data.asgns[idx]  = key;
            this.data.asgnsv[idx] = value;
            idx++;
        }
        //
        this.data.weightsnum = this.structure.weightsnum;
        this.data.weights    = new double[this.data.weightsnum + 1];
        this.data.framewidth     = fn;
        //
        // always set first weight to 1.0.
        //
        this.data.weights[0] = 1.0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Net generate() {
        //
        this.structure = new NetStructure();
        this.structure.cellsnum    = this.cellsoffset;
        this.structure.offline     = this.offline;
        this.structure.valcellsnum = this.valcellsnum;
        this.structure.comcellsnum = this.comcellsnum;
        //
        // setup maps:
        //     - cell to layer,
        //     - cell to array.
        //
        this.setupMaps();
        //
        // setup links:
        //     - sort and eliminate redundant links,
        //     - assign weights indices and determine number of weights,
        //     - create reverted links.
        //     - determine if net is feed forward or recurrent.
        //
        // ignore offline computation for feedforward networks.
        //
        if (this.structure.recurrent) {
            this.structure.offline = false;
        }
        this.setupLinks();
        //
        // setup arrays:
        //      - determine indeg and outdeg.
        //      - determine predecessors ranges,
        //      - determine successors ranges,
        //      - determine weighted? input/ouput links.
        //
        this.setupArrays();
        //
        // setup layer.
        //
        this.setupLayers();
        //
        /*
        if (DEBUG) {
            System.out.println("NetStructure : {");
            System.out.println(indent(this.structure.toString()));
            System.out.println("}");
            //
            System.out.println("");
            //
            System.out.println("Assignments : {");
            for (Integer i : this.assigns.keySet()) {
                System.out.println("\t" + i + " : " + this.assigns.get(i));
            }
            System.out.println("}");
            System.out.println("");
        }
        */
        //
        // setup data.
        //
        this.data = new NetData();
        this.setupData();
        //
        final Net net = this.buildNet();
        //
        // resetting performs assignments.
        //
        net.reset();
        return net;
    }
    
    //-------------------------------------------------------------------------
    // GENERAL NETWORK METHODS.
    //-------------------------------------------------------------------------
    
    /**
     * Forces the generated net to compute online. This will be ignored if
     * the net is bidirectional. A bidirectional net cannot compute online.
     * <br></<br>
     * A non bidirectional recurrent network computes online by default.
     */
    public void computeOnline() {
        this.offline = false;
    }
    /**
     * Forces the generated net to compute offline. This will be ignored
     * in the case of a feedforward network. A feedforward network has no
     * time context. We define that it would compute online.
     * <br></<br>
     * A non bidirectional recurrent network computes online by default.
     */
    public void computeOffline() {
        this.offline = true;
    }

    //-------------------------------------------------------------------------
    // CELL METHODS
    //-------------------------------------------------------------------------
    
    /**
     * Assigns a constant value to a given cell (cell should be a value cell).
     * <br></br>
     * @param cell Cell index.
     * @param value Assigment value.
     */
    public void assign(final int cell, final double value) {
        this.assigns.put(cell, value);
    }
    
    /**
     * Compute the new cells offset after num cells.
     * <br></br>
     * @param num Number of cells.
     * @return New celss offset.
     */
    private int cellsOffset(final int num) {
        final int value = this.cellsoffset;
        this.cellsoffset += num;
        return value;
    }
    
    /**
     * Creates a single value cell.
     * <br></br>
     * @return Index of the cell.
     */
    public int valueCell() {
        return this.valueCells(1);
    }
    /**
     * Creates an array of num value cells. 
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int valueCells(final int num) {
        return this.cells(num, CellType.VALUE);
    }
    
    /**
     * Creates a single cell of a given type.
     * <br></br>
     * @param type The specific cell type.
     * @return Index of the cell.
     */
    public int cell(final CellType type) {
        return this.cells(1, type);
    }
    
    /**
     * Creates an array of num cells of a given type.
     * <br></br>
     * @param num The number of cells.
     * @param type The specific cell type.
     * @return The index of the first cell of the array.
     */
    public int cells(final int num, final CellType type) {
        final int value = this.cellsOffset(num);
        //
        // setup up array.
        //
        CellArray array = new CellArray();
        //
        array.cellslbd = value;
        array.cellsubd = (value + num) - 1;
        array.cellsnum = num;
        //
        array.celltype    = type;
        array.ilctag      = this.ilctag;
        //
        array.layer   = (this.inlayer)?(this.layeroffset):(NOT_ASSIGNED);
        array.compidx = this.compidx;
        this.arrays.add(array);
        //
        if (type == CellType.VALUE) {
            this.valcellsnum += num;
            array.compidx = -1;
        } else {
            this.comcellsnum += num;
        }
        //
        // update computation counter (per computation idx).
        //
        if (this.inlayer) {
            this.compctr++;
        }
        //
        return value;
    }
    
    /**
     * Creates an array of num sigmoid cells.
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int sigmoidCells(final int num) {
        return this.cells(num, CellType.SIGMOID);
    }
    
    /**
     * Creates an array of num tanh cells.
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int tanhCells(final int num) {
        return this.cells(num, CellType.TANH);
    }
    
    /**
     * Creates an array of num linear cells.
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int linearCells(final int num) {
        return this.cells(num, CellType.LINEAR);
    }
    
    /**
     * Creates an array of num multiplicative cells.
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int multiplicativeCells(final int num) {
        return this.cells(num, CellType.MULTIPLICATIVE);
    }
    
    /**
     * Creates an array of num dmultiplicative cells 
     * (multiplicative delta correction).
     * <br></br>
     * @param num The number of cells.
     * @return The index of the first cell of the array.
     */
    public int dmultiplicativeCells(final int num) {
        return this.cells(num, CellType.DMULTIPLICATIVE);
    }

    
    //-------------------------------------------------------------------------
    // LINK METHODS
    //-------------------------------------------------------------------------

    /**
     * Links cell i with cell j (non weighted).
     * <br></br>
     * @param i Source cell.
     * @param j Destination cell.
     */
    public void link(final int i, final int j) {
        this.links.add(Link.link(i, j));
    }
    
    /**
     * Links cell i with cell j (weighted).
     * <br></br>
     * @param i Source cell.
     * @param j Destination cell.
     */
    public void weightedLink(final int i, final int j) {
        this.links.add(Link.link(i, j, WEIGHT_NEEDED));
    }
    
    /**
     * Fully links cell range with another cell range (non weighted).
     * <br></br>
     * @param i Source cells.
     * @param numi Number of source cells.
     * @param j Destination cells.
     * @param numj Number of destination cells.
     */
    public void link(final int i, final int numi, final int j, final int numj) {
        //
        final int jubd = (j + numj) - 1;
        final int iubd = (i + numi) - 1;
        //
        for (int jj = j; jj <= jubd; jj++) {
            for (int ii = i; ii <= iubd; ii++) {
                this.link(ii, jj);
            }
        }
    }
    
    /**
     * Fully links cell range with another cell range (weighted).
     * <br></br>
     * @param i Source cells.
     * @param numi Number of source cells.
     * @param j Destination cells.
     * @param numj Number of destination cells.
     */
    public void weightedLink(final int i, final int numi, final int j, final int numj) {
        //
        final int jubd = (j + numj) - 1;
        final int iubd = (i + numi) - 1;
        //
        for (int jj = j; jj <= jubd; jj++) {
            for (int ii = i; ii <= iubd; ii++) {
                this.weightedLink(ii, jj);
            }
        }
    }
    
    /**
     * Symmetrically links cell range with another cell range (non weighted).
     * <br></br>
     * @param i Source cells.
     * @param j Destination cells.
     * @param num Number of cells.
     */    
    public void link(final int i, final int j, final int num) {
        //
        int ii = i;
        int jj = j;
        //
        for (int k = 0; k < num; k++) {
            this.link(ii, jj);
            ii++;
            jj++;
        }
    }
    
    /**
     * Symmetrically links cell range with another cell range (weighted).
     * <br></br>
     * @param i Source cells.
     * @param j Destination cells.
     * @param num Number of cells.
     */  
    public void weightedLink(final int i, final int j, final int num) {
        //
        int ii = i;
        int jj = j;
        //
        for (int k = 0; k < num; k++) {
            this.weightedLink(ii, jj);
            ii++;
            jj++;
        }
    }
    
    /**
     * Fully links output connector cells of the first layer with the
     * input connector cells of the second layer (non weighted).
     * <br></br>
     * @param l1 First layer index.
     * @param l2 Second layer index.
     */
    public void linkLayer(final int l1, final int l2) {
        //
        final Layer layer1 = this.layers.get(l1);
        final Layer layer2 = this.layers.get(l2);
        //
        for (int i = layer1.arrayslbd; i <= layer1.arraysubd; i++) {
            final CellArray a1 = this.arrays.get(i);
            if ((a1.ilctag & CellArray.ILC_OUT) != CellArray.ILC_OUT) continue;
            //
            for (int j = layer2.arrayslbd; j <= layer2.arraysubd; j++) {
                final CellArray a2 = this.arrays.get(j);
                if ((a2.ilctag & CellArray.ILC_IN) != CellArray.ILC_IN) continue;
                //
                this.link(a1.cellslbd,a1.cellsnum, a2.cellslbd, a2.cellsnum);
            }
            
        }
    }
    
    /**
     * Fully links output connector cells of the first layer with the
     * input connector cells of the second layer (weighted).
     * <br></br>
     * @param l1 First layer index.
     * @param l2 Second layer index.
     */
    public void weightedLinkLayer(final int l1, final int l2) {
        //
        final Layer layer1 = this.layers.get(l1);
        final Layer layer2 = this.layers.get(l2);
        //
        for (int i = layer1.arrayslbd; i <= layer1.arraysubd; i++) {
            final CellArray a1 = this.arrays.get(i);
            if ((a1.ilctag & CellArray.ILC_OUT) != CellArray.ILC_OUT) continue;
            //
            for (int j = layer2.arrayslbd; j <= layer2.arraysubd; j++) {
                final CellArray a2 = this.arrays.get(j);
                if ((a2.ilctag & CellArray.ILC_IN) != CellArray.ILC_IN) continue;
                //
                this.weightedLink(a1.cellslbd,a1.cellsnum, a2.cellslbd, a2.cellsnum);
            }
            
        }
    }

    //-------------------------------------------------------------------------
    // LAYER METHODS
    //-------------------------------------------------------------------------

    /**
     * Marks all following cells in this layer as input connectors. 
     */
    public void inputConnectors() {
        this.ilctag = CellArray.ILC_IN;
    }
    
    /**
     * Marks all following cells in this layer as non connectors. 
     */
    public void nonConnectors() {
        this.ilctag = CellArray.ILC_NONE;
    }
    
    /**
     * Marks all following cells in this layer as output connectors. 
     */
    public void outputConnectors() {
        this.ilctag = CellArray.ILC_OUT;
    }
    
    /**
     * Marks all following cells in this layer as input connectors as well
     * as output connectors.
     */
    public void inputOutputConnectors() { 
        this.ilctag = CellArray.ILC_BOTH;
    }

    /**
     * Increment computation index. This models a time dependency 
     * in the computation process.
     */
    public void shiftComputationIndex() {
        if (this.inlayer && (this.compctr > 0)) {
            this.compidx++;
            this.compctr = 0;
        }
    }
    
    /**
     * Start a new layer. Returns the index of the new layer.
     * <br></br>
     * @return Layer index.
     */
    public int beginLayer() {
        if (this.inlayer) return this.layeroffset;
        this.layer = new Layer();
        this.layers.add(this.layer);
        //
        this.layer.arrayslbd = this.arrays.size();
        this.layer.cellslbd  = this.cellsoffset;
        this.layer.tag       = LayerTag.REGULAR;
        this.ilctag          = CellArray.ILC_BOTH;
        //
        this.inlayer = true;
        return this.layeroffset;
    }

    /**
     * Closes the current layer. Returns the index the closed layer.
     * <br></br>
     * @return Layer index.
     */
    public int endLayer() {
        if (!this.inlayer) return this.layeroffset;
        //
        this.layer.arraysnum = this.arrays.size() - this.layer.arrayslbd;
        this.layer.arraysubd = (this.layer.arrayslbd + this.layer.arraysnum) - 1;
        this.layer.cellsnum  = this.cellsoffset - this.layer.cellslbd;
        this.layer.cellsubd  = (this.layer.cellslbd + this.layer.cellsnum) - 1;
        this.layer.compwidth = (this.compidx + 1);
        //
        this.layer = null;
        //
        this.inlayer = false;
        this.compidx = 0;
        this.compctr = 0;
        return this.layeroffset++;
    }
    
    /**
     * Get the currently defined input layer index.
     * <br></br>
     * @return Layer index.
     */
    public int getInputLayer() {
        return this.inputlayer;
    }
    /**
     * Get the currently defined output layer index.
     * <br></br>
     * @return Layer index.
     */
    public int getOutputLayer() {
       return this.outputlayer;
    }
    /**
     * Defines the given layer as regular computing (default).
     * <br></br>
     * @param idx Layer index.
     */
    public void defineLayerAsRegular(int idx) {
        this.layers.get(idx).tag = LayerTag.REGULAR;
    }
    /**
     * Defines the given layer as reversed computing. The resulting
     * is then bidirectional.
     * <br></br>
     * @param idx Layer index.
     */
    public void defineLayerAsReversed(int idx) {
        this.layers.get(idx).tag = LayerTag.REVERSED;
    }
    /**
     * Defines the given layer as input layer.
     * <br></br>
     * @param idx Layer index.
     */
    public void defineInputLayer(int idx) {
        this.inputlayer = idx;
    }
    
    /**
     * Defines the given layer as output layer.
     * <br></br>
     * @param idx Layer index.
     */
    public void defineOutputLayer(int idx) {
        this.outputlayer = idx;
    }
    /**
     * Get the first cell index of the cells contained the
     * given layer.
     * <br></br>
     * @param layer Layer index.
     * @return Cell index.
     */
    public int getLayerCells(final int layer) {
        return this.layers.get(layer).cellslbd;
    }

}
