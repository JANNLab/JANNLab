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

package de.jannlab.core;

/**
 * This class provides constants and static methods for handling links and arrays
 * of links. A link consists of a 3-tuple (i,j,k) where i gives a source index,
 * j gives a destination index and k gives a weight index. 
 * <br></br>
 * All links of a ANN are contained by a large 1-dimensional int-array. This is to
 * improve the runtime performance by supporting the system's caching mechanisms:
 * A multidimensional array could be theoretically distributed over the whole memory.
 * Having all links in one int-array aligned corresponding to the computation order 
 * of the network highly allows the benefit from caching (memory burst). For this
 * it is recommend to enable JIT of the JVM.
 * <br></br>
 * The code of the two merge sort implementations is largely redundant.
 * But a particular implementation for links with a static compare function improves 
 * the runtime performance in comparison with
 * the native sorting methods which needs a comparator object.
 * <br></br>
 * @author Sebastian Otte
 */
public final class Link {

    /**
     * Gives the index of the soruce part in a link triple.
     */
    public static final int IDX_SRC = 0; // index of source index.
    /**
     * Gives the index of the destination part in a link triple.
     */
    public static final int IDX_DST = 1; // index of destination index.
    /**
     * Gives the index of the weight part in a link triple.
     */
    public static final int IDX_WEIGHT = 2; // index of weight index.
    /**
     * Just gives the number of part of a link triple (which is 3).
     */
    public static final int LINK_SIZE = 3; // size of a link tuple.
    /**
     * This constant means (a < b) in a comparison of a and b.
     */
    private static final int SMALLER = -1; // comparison constant: a < b
    /**
     * This constant means (a == b) in a comparison of a and b.
     */
    private static final int EQUAL = 0; // comparison constant: a == b
    /**
     * This constant means (a > b) in a comparison of a and b.
     */
    private static final int BIGGER = 1; // comparison constant: a > b
    /**
     * The constant defines to sort in ascending order.
     */
    public  static final int ORDER_ASC  = BIGGER; // sort in ascending order.
    /**
     * The constant defines to sort in descending order.
     */
    public  static final int ORDER_DESC = SMALLER; // sort in descending order.
    /**
     * The constant it used, when a links a no weight. In generated network this
     * will be replaced by an index to a constant-one-weight. 
     */
    public  static final int NOWEIGHT = -1; // tag for non weighted link.
    //

    //-------------------------------------------------------------------------
    
    /**
     * Creates a non weighted link from src to dst as int[3].
     * In a non weighted link the weight index is set to NOWEIGHT.
     * @param src Source index.
     * @param dst Destination index.
     * @return Non weighted link.
     */
    public static int[] link(final int src, final int dst) {
        return link(src, dst, NOWEIGHT);
    }
    /**
     * Creates a weighted link from src to dst with weight widx as int[3].
     * @param src Source index.
     * @param dst Destination index.
     * @param widx Weight index.
     * @return Weighted link.
     */
    public static int[] link(final int src, final int dst, final int widx) {
        return new int[]{ src, dst, widx };
    }
    
    /**
     * Allocates a memory block for n links.
     * @param links Number of links.
     * @return Allocated memory block.
     */
    public static int[] alloc(final int links) {
        return new int[links * LINK_SIZE];
    }
    /**
     * Returns the source index of a link.
     * @param buffer Array of links.
     * @param offset Link offset.
     * @return Source index.
     */
    public static int src(final int[] buffer, final int offset) {
        return buffer[offset + IDX_SRC];
    }
    /**
     * Returns the destination index of a link.
     * @param buffer Array of links.
     * @param offset Link offset.
     * @return Destination index.
     */
    public static int dst(final int[] buffer, final int offset) {
        return buffer[offset + IDX_DST];
    }
    /**
     * Returns the weight index of a link.
     * @param buffer Array of links.
     * @param offset Link offset.
     * @return Weight index.
     */
    public static int weight(final int[] buffer, final int offset) {
        return buffer[offset + IDX_WEIGHT];
    }
    
    //-------------------------------------------------------------------------
    
    /**
     * Swaps two given links.
     * @param buffer Array of links.
     * @param first The first link.
     * @param second The second link.
     */
    public static void swap(final int[] buffer, final int first, final int second) {
        //
        final int tmp1 = buffer[first];
        buffer[first]  = buffer[second];
        buffer[second] = tmp1;
        //
        final int tmp2     = buffer[first + 1];
        buffer[first + 1]  = buffer[second + 1];
        buffer[second + 1] = tmp2;
        //
        final int tmp3     = buffer[first + 2];
        buffer[first + 2]  = buffer[second + 2];
        buffer[second + 2] = tmp3;
        
    }
    /**
     * Compares two links in dst-major order.
     * @param buffer Array of links.
     * @param first The first link.
     * @param second The second link.
     * @return SMALLER, BIGGER or EQUAL.
     */
    public static int cmpDstMaj(final int[] buffer, final int first, final int second) {
        //
        if (buffer[first + IDX_DST] < buffer[second + IDX_DST]) return SMALLER;
        if (buffer[first + IDX_DST] > buffer[second + IDX_DST]) return BIGGER;
        if (buffer[first + IDX_SRC] < buffer[second + IDX_SRC]) return SMALLER;
        if (buffer[first + IDX_SRC] > buffer[second + IDX_SRC]) return BIGGER;
        //
        return EQUAL;
    }
    /**
     * Compares two links in src-major order.
     * @param buffer Array of links.
     * @param first The first link.
     * @param second The second link.
     * @return SMALLER, BIGGER or EQUAL.
     */
    public static int cmpSrcMaj(final int[] buffer, final int first, final int second) {
        //
        if (buffer[first + IDX_SRC] < buffer[second + IDX_SRC]) return SMALLER;
        if (buffer[first + IDX_SRC] > buffer[second + IDX_SRC]) return BIGGER;
        if (buffer[first + IDX_DST] < buffer[second + IDX_DST]) return SMALLER;
        if (buffer[first + IDX_DST] > buffer[second + IDX_DST]) return BIGGER;
        //
        return EQUAL;
    }
    /**
     * Compares two links to equality.
     * @param buffer Array of links.
     * @param first The first link.
     * @param second The second link.
     * @return true or false.
     */
    public static boolean equal(final int[] buffer, final int first, final int second) {
        return (
                (buffer[first + IDX_SRC] == buffer[second + IDX_SRC]) && 
                (buffer[first + IDX_DST] == buffer[second + IDX_DST])
        );
    }
    
    /**
     * Adaption of fast merge sort algorithm from 
     * http://www.java2s.com/Code/Java/Collections-Data-Structure/FastMergeSort.htm
     * which uses insertion sort for smaller arrays. Sorting in dst-major order.
     * <br></br> 
     * @param src Unsorted data as int[].
     * @param dst Result data as int[].
     * @param low Lower bound of data frame.
     * @param high Exclusive upper bound of data frame. 
     * @param order ORDER_ASC or ORDER_DESC for ascending respectively descending order.
     */
    public static void sortDstMaj(
            final int[] src, final int[] dst, final int low, final int high, final int order
    ) {
        final int threshold = LINK_SIZE * 20; 
        final int length    = high - low;
        // 
        // use insertion sort on smallest arrays < 7 elements (LINK_SIZE * 7).
        // 
        if (length < threshold) {
            for (int i = low; i < high; i += LINK_SIZE) {
                //
                // cmp > 0 ~ cmp > EQUAL ~ cmp == order.
                //
                for (
                        int j = i; 
                        (j > low) && (cmpDstMaj(dst, j - LINK_SIZE, j) == order); 
                        j -= LINK_SIZE
                ) { 
                    swap(dst, j - LINK_SIZE, j);
                }
            }
            return;
        }
        //
        // calculate mid idx.
        // OPZIMIZED: final int mid = (((low + high) / LINK_SIZE) >> 1) * LINK_SIZE;
        //
        final int rng = (low + high) >> 1;
        final int mid = rng - (rng % LINK_SIZE);
        //
        // recursively sort halves of dest into src.
        //
        sortDstMaj(dst, src, low, mid, order);
        sortDstMaj(dst, src, mid, high, order);
        //
        // is list already sorted? cmp <= 0 ~ cmp <= EQUAL ~ cmp != order.
        //
        if (cmpDstMaj(src, mid - LINK_SIZE, mid) != order) {
            System.arraycopy(src, low, dst, low, length);
            return;
        }
        //
        // merge sorted halves from src into dest.
        // OPZIMIZED: for (int i = low, p = low, q = mid; i < high; i += LINK_SIZE) {
        //
        for (int i = low, p = low, q = mid; i < high;) {
            //
            // cmp <= 0 ~ cmp <= EQUAL ~ cmp != order.
            //
            if ((q >= high) || ((p < mid) && (cmpDstMaj(src, p, q) != order))) {
                //
                // OPZIMIZED:
                // copy(src, p, dst, i);
                // p += LINK_SIZE;
                //
                dst[i] = src[p]; p++; i++;
                dst[i] = src[p]; p++; i++;
                dst[i] = src[p]; p++; i++;
            } else {
                //
                // OPZIMIZED:
                // copy(src, q, dst, i);
                // q += LINK_SIZE;
                //
                dst[i] = src[q]; q++; i++;
                dst[i] = src[q]; q++; i++;
                dst[i] = src[q]; q++; i++;
            }
        }
    }
    /**
     * Adaption of fast merge sort algorithm from 
     * http://www.java2s.com/Code/Java/Collections-Data-Structure/FastMergeSort.htm
     * which uses insertion sort for smaller arrays. Sorting in src-major order.
     * <br></br> 
     * @param src Unsorted data as int[].
     * @param dst Result data as int[].
     * @param low Lower bound of data frame.
     * @param high Exclusive upper bound of data frame. 
     * @param order ORDER_ASC or ORDER_DESC for ascending respectively descending order.
     */
    public static void sortSrcMaj(
            final int[] src, final int[] dst, final int low, final int high, final int order
    ) {
        final int threshold = LINK_SIZE * 20; 
        final int length    = high - low;
        // 
        // use insertion sort on smallest arrays < 7 elements (LINK_SIZE * 7).
        // 
        if (length < threshold) {
            for (int i = low; i < high; i += LINK_SIZE) {
                //
                // cmp > 0 ~ cmp > EQUAL ~ cmp == order.
                //
                for (
                        int j = i; 
                        (j > low) && (cmpSrcMaj(dst, j - LINK_SIZE, j) == order); 
                        j -= LINK_SIZE
                ) { 
                    swap(dst, j - LINK_SIZE, j);
                }
            }
            return;
        }
        //
        // calculate mid idx.
        // OPZIMIZED: final int mid = (((low + high) / LINK_SIZE) >> 1) * LINK_SIZE;
        //
        final int rng = (low + high) >> 1;
        final int mid = rng - (rng % LINK_SIZE);
        //
        // recursively sort halves of dest into src.
        //
        sortSrcMaj(dst, src, low, mid, order);
        sortSrcMaj(dst, src, mid, high, order);
        //
        // is list already sorted? cmp <= 0 ~ cmp <= EQUAL ~ cmp != order.
        //
        if (cmpSrcMaj(src, mid - LINK_SIZE, mid) != order) {
            System.arraycopy(src, low, dst, low, length);
            return;
        }
        //
        // merge sorted halves from src into dest.
        // OPZIMIZED: for (int i = low, p = low, q = mid; i < high; i += LINK_SIZE) {
        //
        for (int i = low, p = low, q = mid; i < high;) {
            //
            // cmp <= 0 ~ cmp <= EQUAL ~ cmp != order.
            //
            if ((q >= high) || ((p < mid) && (cmpSrcMaj(src, p, q) != order))) {
                //
                // OPZIMIZED:
                // copy(src, p, dst, i);
                // p += LINK_SIZE;
                //
                dst[i] = src[p]; p++; i++;
                dst[i] = src[p]; p++; i++;
                dst[i] = src[p]; p++; i++;
            } else {
                //
                // OPZIMIZED:
                // copy(src, q, dst, i);
                // q += LINK_SIZE;
                //
                dst[i] = src[q]; q++; i++;
                dst[i] = src[q]; q++; i++;
                dst[i] = src[q]; q++; i++;
            }
        }
    }    
    /**
     * Copy link from source array to destination array.
     * @param src Source array of links.
     * @param srcoff Source link offset.
     * @param dst Destination array of links.
     * @param dstoff Destination link offset.
     */
    public static void copy(
            final int[] src, 
            final int srcoff, 
            final int[] dst,
            final int dstoff
    ) {
        dst[dstoff + IDX_SRC]    = src[srcoff + IDX_SRC];
        dst[dstoff + IDX_DST]    = src[srcoff + IDX_DST];
        dst[dstoff + IDX_WEIGHT] = src[srcoff + IDX_WEIGHT];
    }
    
    /**
     * Sorts a given array of links in src-major order.
     * @param data Array of links.
     * @param order ORDER_ASC or ORDER_DESC for ascending respectively descending order.
     */
    public static void sortSrcMaj(
            final int[] data, final int order
    ) {
        int[] buffer = data.clone();
        sortSrcMaj(buffer, data, 0, data.length, order);
    }
    /**
     * Sorts a given array of links in dst-major order.
     * @param data Array of links.
     * @param order ORDER_ASC or ORDER_DESC for ascending respectively descending order.
     */
    public static void sortDstMaj(
            final int[] data, final int order
    ) {
        int[] buffer = data.clone();
        sortDstMaj(buffer, data, 0, data.length, order);
    }
    /**
     * Eliminates redundant links in a sorted array of links.
     * @param links Array of links.
     * @return A "clean" array of links. 
     */
    public static int[] eliminateRedundantLinks(int[] links) {
        //
        final int tag = Integer.MIN_VALUE;
        //
        int last = 0;
        int ctr = 0;
        //
        if (links.length > 0) {
            ctr += LINK_SIZE;
        }
        //
        // first make redundant links invalid.
        //
        for (int i = LINK_SIZE; i < links.length; i += LINK_SIZE) {
            if (equal(links, i, last)) {
                //
                // links equal, eliminate link.
                //
                links[i] = tag;
            } else {
                last = i;
                ctr += LINK_SIZE;
            }
        }
        //
        // collect non-null links
        //
        int[] result = new int[ctr];
        int idx = 0;
        for (int i = 0; i < links.length; i += LINK_SIZE) {
            if (links[i] != tag) {
                copy(links, i, result, idx);
                idx += LINK_SIZE;
            }
        }
        return result;
    }

    /**
     * Builds a string for a given range of links. The resulting string is just one 
     * single text line.
     * <br></br>
     * @param links The links.
     * @param off The offset of the links range.
     * @param num The number of links.
     * @return String representation of the link range.
     */
    public static String asString(final int[] links, final int off, final int num) {
        StringBuilder out = new StringBuilder();
        int idx = off;
        for (int i = 0; i < num; i++) {
            if (i > 0) {
                out.append(" ");
            }
            final int src  = links[idx + Link.IDX_SRC];
            final int dst  = links[idx + Link.IDX_DST];
            final int widx = links[idx + Link.IDX_WEIGHT];
            out.append("(" + src + "," + dst + "," + widx + ")");
            idx += LINK_SIZE;
        }
        return out.toString();
    }
    /**
     * Builds a string for a given range of links. The resulting string contains of 
     * multiple lines. A line break is set after every numbreak links.
     * <br></br>
     * @param links The links.
     * @param off The offset of the links range.
     * @param num The number of links.
     * @param numbreak Number of links per text line.
     * @return String representation of the link range.
     */
    public static String asString(
            final int[] links, final int off, final int num, final int numbreak
    ) {
        StringBuilder out = new StringBuilder();
        int idx = off;
        for (int i = 0; i < num; i++) {
            if (i > 0) {
                if (i % numbreak == 0) {
                    out.append("\n");
                } else {
                    out.append(" ");
                }
            }
            final int src  = links[idx + Link.IDX_SRC];
            final int dst  = links[idx + Link.IDX_DST];
            final int widx = links[idx + Link.IDX_WEIGHT];
            out.append("(" + src + "," + dst + "," + widx + ")");
            idx += LINK_SIZE;
        }
        return out.toString();
    }
    
}
