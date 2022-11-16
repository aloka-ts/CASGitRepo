package com.baypackets.ase.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import com.esotericsoftware.kryo.serializers.VersionFieldSerializer;
import de.javakaffee.kryoserializers.EnumMapSerializer;
import org.apache.log4j.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ObjectOutput;
import java.util.EnumMap;

/**
 * Created by ankitsinghal on 22/02/16.
 * Pool Manager for Kryo Objects.
 * Factory creates the kryo objects which contains the registered classes
 * being supplied by {@link KryoRegisterables}
 */
public class KryoPoolManager {

    private static Logger logger = Logger.getLogger(KryoPoolManager.class);

    static private final KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            //Setting the default as VersionFieldSerailizer to support NSA based upgrades
            kryo.setDefaultSerializer(VersionFieldSerializer.class);
            kryo.register(java.util.EnumMap.class, new EnumMapSerializer());

            registerClasses(kryo);
            return kryo;
        }
    };

    static private final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    static private final void registerClasses(Kryo kryo){
        for(String clazzName : KryoRegisterables.jdkClassesToRegister){
            registerClass(kryo, clazzName);
        }
        for(String clazzName : KryoRegisterables.aseClassesToRegister){
            registerClass(kryo, clazzName);
        }
        for(String clazzName : KryoRegisterables.dsClassesToRegister){
            registerClass(kryo, clazzName);
        }
        for(String clazzName : KryoRegisterables.miscClassesToRegister){
            registerClass(kryo, clazzName);
        }
    }

    static private final void registerClass(Kryo kryo, String fullyQualifiedClassName) {
        try {
            kryo.register(Class.forName(fullyQualifiedClassName));
        } catch (Exception e) {
            logger.error("Unable to register class with kryo: " + fullyQualifiedClassName + ", Reason: " + e.getMessage());
        }
    }

    static public final Kryo borrow() { return pool.borrow(); }

    static public final void release(Kryo kryo) {
        pool.release(kryo);
    }
}
