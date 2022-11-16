package com.genband.ase.alc.alcml.jaxb;

import java.io.Serializable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ServiceBlock implements Serializable
{
	public ServiceBlock() {
	}
	
	
}

