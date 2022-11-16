package com.baypackets.ase.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.InputChunked;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.OutputChunked;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by ankitsinghal on 29/02/16.
 * Manage incremental read/write operations through intermediate kryo output buffer / chunked encoding.
 */
public class KryoIncrementalStreamProcessor {

    private static Logger logger = Logger.getLogger(KryoPoolManager.class);
    private static final int CHUNKED_BUFFER_SIZE = 256;
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_BUFFER_SIZE = 10 * BUFFER_SIZE;

    @Deprecated
    private static Object read(Kryo kryo, byte[] bytes) {
        if (null == kryo) {
            throw new IllegalArgumentException("Kryo object cannot be null!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Reading Object with Kryo serializer");
        }
        return kryo.readClassAndObject(new Input(bytes));
    }

    @Deprecated
    protected static Object readObjectAndLengthFromStream(Kryo kryo, ObjectInput objectInput) throws IOException {
        int bufferLength = objectInput.readInt();
        byte kryoBytes[] = new byte[bufferLength];
        objectInput.readFully(kryoBytes);
        return read(kryo, kryoBytes);
    }

    @Deprecated
    private static byte[] write(Kryo kryo, Object obj) {
        if (null == kryo) {
            throw new IllegalArgumentException("Kryo object cannot be null!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Writing Object with Kryo serializer: " + obj);
        }
        Output op = new Output(BUFFER_SIZE, MAX_BUFFER_SIZE);
        kryo.writeClassAndObject(op, obj);
        op.flush();
        return op.toBytes();
    }

    @Deprecated
    protected static void writeObjectAndLengthFromStream(Kryo kryo, Object obj, ObjectOutput objectOutput) throws IOException {
        byte[] kryoBytes = write(kryo, obj);
        objectOutput.writeInt(kryoBytes.length);
        objectOutput.write(kryoBytes);
    }

    protected static Object readChunkedObjectFromStream(Kryo kryo, ObjectInput objectInput) throws IOException {
        if (null == kryo) {
            throw new IllegalArgumentException("Kryo object cannot be null!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Reading Object with Kryo serializer");
        }
        InputChunked ip = new InputChunked((ObjectInputStream) objectInput, CHUNKED_BUFFER_SIZE);
        return kryo.readClassAndObject(ip);
    }

    protected static void writeChunkedObjectToStream(Kryo kryo, Object obj, ObjectOutput objectOutput) throws IOException {
        if (null == kryo) {
            throw new IllegalArgumentException("Kryo object cannot be null!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Writing Object with Kryo serializer: " + obj);
        }
        OutputChunked op = new OutputChunked((ObjectOutputStream) objectOutput, CHUNKED_BUFFER_SIZE);
        kryo.writeClassAndObject(op, obj);
        op.endChunks();
    }

    /**
     * Reads an object serialized with Kryo using writeObjectToStream API {@link #writeObjectToStream(Object, ObjectOutput)}.
     * This class requires an optional classloader which is set in kryo object that is being borrowed from pool for deserialization.
     * After the object is read from stream, the class loader is reset to K ryo object original classloader.
     * @param objectInput Input stream object
     * @param newClassLoader ClassLoader to be used for deserialization
     * @return Deserialized object
     * @throws IOException
     */
    public static Object readObjectFromClassAwareStream(ObjectInput objectInput, ClassLoader newClassLoader) throws IOException {
        Kryo kryo = null;
        ClassLoader previousCl = null;
        try {
            kryo = KryoPoolManager.borrow();
            previousCl = kryo.getClassLoader();
            //If the newClassLoader is null, let the Kryo stream be old input stream
            if(null != newClassLoader) {
                kryo.setClassLoader(newClassLoader);
            }
            return readChunkedObjectFromStream(kryo, objectInput);
        } finally {
            if (null != kryo) {
                if(null != newClassLoader) {
                    kryo.setClassLoader(previousCl);
                }
                KryoPoolManager.release(kryo);
            }
        }
    }

    /**
     * Writes the given object to out stream. Firstly the number of bytes being used by the serialized object is
     * written to the stream and finally the serialized object.
     * Deserilaization is followed in reverse process. See:  {@link #readObjectFromClassAwareStream(ObjectInput, ClassLoader)}
     * @param obj Object which is to be serialized
     * @param objectOutput Output stream
     * @throws IOException
     */
    public static void writeObjectToStream(Object obj, ObjectOutput objectOutput) throws IOException {
        Kryo kryo = null;
        try {
            kryo = KryoPoolManager.borrow();
            writeChunkedObjectToStream(kryo, obj, objectOutput);
        } finally {
            if (null != kryo) {
                KryoPoolManager.release(kryo);
            }
        }
    }
}
