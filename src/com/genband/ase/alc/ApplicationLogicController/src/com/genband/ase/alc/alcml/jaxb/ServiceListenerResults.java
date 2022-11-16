package com.genband.ase.alc.alcml.jaxb;
import java.io.Serializable;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public final class ServiceListenerResults implements Serializable
{
	public static final ServiceListenerResults Halt = new ServiceListenerResults();
	public static final ServiceListenerResults Continue = new ServiceListenerResults();
	public static final ServiceListenerResults RemoveMeAsListener = new ServiceListenerResults();

	private ServiceListenerResults() {};
}
