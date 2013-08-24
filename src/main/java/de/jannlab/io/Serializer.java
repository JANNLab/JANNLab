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

package de.jannlab.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * This class provides methods for persisting data objects. The
 * objects are stored serialized into GZip archives. 
 * <br></br>
 * @author Sebastian Otte
 */
public class Serializer {
    /**
     * Write a given object into a file. The instance obj 
     * must implement Serializable.
     * <br></br>
     * @param obj The object which is to be stored.
     * @param filename Destination filename.
     * @throws IOException
     */
    public static <T> void write(final T obj, final String filename) throws IOException {
        //
        File file = new File(filename);
        //
        ObjectOutputStream out = new ObjectOutputStream(
            new GZIPOutputStream(new FileOutputStream(file))
        );
        out.writeObject(obj);
        out.flush();
        out.close();
    }
    /**
     * Reads an object from a file. The instance if casted automatically.
     * <br></br>
     * @param filename Source filename.
     * @return Instance of the persisted object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> T read(final String filename) throws IOException, ClassNotFoundException {
        //
        File file = new File(filename);
        //
        ObjectInputStream in = new ObjectInputStream(
            new GZIPInputStream(new FileInputStream(file))
        );
        //
        @SuppressWarnings("unchecked")
        T obj = (T)in.readObject();
        in.close();
        return obj;
    }
    
}
