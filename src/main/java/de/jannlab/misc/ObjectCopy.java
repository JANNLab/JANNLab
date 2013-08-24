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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class contains a static method for deep copy
 * object instances. Note that the objects must implement
 * the Serializable interface.
 * <br></br>
 * @author Sebastian Otte
 */
public class ObjectCopy {
    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     */
    public static <T extends Serializable> T copy(final T obj) {
        try {
            //
            // serialize object.
            //
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out       = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.flush();
            out.close();
            //
            // de-serialize object.
            //
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray())
            );
            //
            @SuppressWarnings("unchecked")
            final T result = (T)in.readObject();
            in.close();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
