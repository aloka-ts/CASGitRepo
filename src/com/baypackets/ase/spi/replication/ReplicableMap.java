package com.baypackets.ase.spi.replication;

import com.baypackets.ase.serializer.kryo.KryoIncrementalStreamProcessor;
import com.baypackets.ase.util.AseObjectInputStream;
import com.baypackets.ase.util.AseUnmodifiableSet;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.*;

@DefaultSerializer(ExternalizableSerializer.class)
public class ReplicableMap extends HashMap implements Replicable {
	
	private static final long serialVersionUID= -37451820114205L;
	private static final short PUT = 1;
	private static final short REMOVE = 2;

	//This is added as we can't get the class loader here directly from
	//AseHost, as we experience build problem because of cyclic dependency
	private ClassLoader latestSbbCL;
	// Non-transient attributes
	private String m_replicableId = null;
	private boolean alwaysFullReplication = false;
	
	private static final String ASE_SBB = "ASE_SBB";

	// Transient attributes
	private HashMap m_changed = new HashMap();
	private boolean m_new = true;
	private boolean mFirstReplicationCompleted = false;
	private static final Logger logger = Logger.getLogger(ReplicableMap.class);	
	private static final boolean isKryoSerializer = BaseContext.getConfigRepository().getValue(Constants.IS_KRYO_SERIALIZER_ACTIVATED).equals("1");

	{
		try{
			Class registry = Class.forName("com.baypackets.ase.common.Registry", true, ReplicableMap.class.getClassLoader());
			Class constants = Class.forName("com.baypackets.ase.util.Constants", true, ReplicableMap.class.getClassLoader());
			Field hostField = constants.getField("NAME_HOST");
			Object aseHostObj = null;
			aseHostObj = registry.getMethod("lookup",String.class).invoke(null, hostField.get(null));
			if (aseHostObj != null){
				Class aseHost = Class.forName("com.baypackets.ase.container.AseHost", true, ReplicableMap.class.getClassLoader());
				Object appClassLoaderObj = aseHost.getMethod("getLatestSbbCL").invoke(aseHostObj);
				this.setLatestSbbCL((ClassLoader)appClassLoaderObj);
			}
		}catch (Throwable e) {
			// TODO: handle exception
		}
	 }
	///////////////////////////////////// Private Class ///////////////////////////////////////
	private class ChangeEntry {
		private short _opType;
		private Object _value;

		ChangeEntry(short opType, Object value) {
			_opType = opType;
			_value = value;
		}
	}
	///////////////////////////////////// Private Class ///////////////////////////////////////

	public ReplicableMap() {
		super();
	}

	public ReplicableMap(String name, boolean afr) {
		super();
		this.alwaysFullReplication = afr;
		this.setReplicableId(name);
	}

	///////////////////// Replicable interface impl starts //////////////

	public String getReplicableId() {
		return this.m_replicableId;
	}

	public void setReplicableId(String replicableId) {
		if(m_replicableId == null) {
			m_replicableId = replicableId;
			return;
		}

		throw new IllegalStateException("Cannot override replicable id");
	}

	public boolean isReadyForReplication() {
		return true;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if (alwaysFullReplication) {
			int num = in.readInt();                        // READ: number of key-value pairs
			int changedSize = in.readInt();

			if (logger.isDebugEnabled()) {
				logger.debug("alwaysFullReplication: Number of Map items to remove = " + changedSize);
			}

			for (int i = 0; i < num; i++) {
				Object key = in.readObject();            // READ: key
				Object value = null;

				if (!isKryoSerializer) {
					if (key instanceof String && ((String) key).endsWith(ASE_SBB) && in instanceof AseObjectInputStream) {
						((AseObjectInputStream) in).setSbbLoader(this.getLatestSbbCL());
						value = in.readObject();            // READ: value
						((AseObjectInputStream) in).setSbbLoader(null);
					} else {
						value = in.readObject();            // READ: value
					}
				} else {
					ClassLoader newClassLoader = null;
					if (key instanceof String &&
							((String) key).endsWith(ASE_SBB) && in instanceof AseObjectInputStream) {
						newClassLoader = this.getLatestSbbCL();
					} else {
						newClassLoader = ((AseObjectInputStream) in).getClassLoader();
					}
					value = KryoIncrementalStreamProcessor.readObjectFromClassAwareStream(in, newClassLoader);
				}
				super.put(key, value);
			}

			for (int i = 0; i < changedSize; i++) {
				Object key = in.readObject();
				short opType = in.readShort();            // READ: opType
				if (opType == REMOVE) {
					super.remove(key);
				}
			}

		} else {
			int num = in.readInt();                        // READ: number of key-value pairs
			if (logger.isDebugEnabled()) {
				logger.debug("Number of Map items read = " + num);
			}

			for (int i = 0; i < num; i++) {
				Object key = in.readObject();            // READ: key
				short opType = in.readShort();            // READ: opType

				if (opType == PUT) {
					Object value = null;        // READ: value
					if (key instanceof String && ((String) key).endsWith(ASE_SBB) && in instanceof AseObjectInputStream) {
						((AseObjectInputStream) in).setSbbLoader(this.getLatestSbbCL());
						value = in.readObject();            // READ: value
						((AseObjectInputStream) in).setSbbLoader(null);
					} else {
						value = in.readObject();            // READ: value
					}
					super.put(key, value);
				} else {
					super.remove(key);
				}
			}
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		synchronized (this) {
			if (alwaysFullReplication) {
				int num = this.size();
				out.writeInt(num);                            // WRITE: number of key-value pairs

				//Need to remove the attributes at standby if someone has called
				//removeAttribute, previously it was not happening as alwaysFullReplication
				//was always on
				int changedSize = m_changed.size();
				out.writeInt(changedSize);                            // WRITE: number of key-ChangeEntry pairs

				Iterator keyIter = super.keySet().iterator();

				while (keyIter.hasNext()) {
					Object key = keyIter.next();
					Object value = super.get(key);
					out.writeObject(key);                    // WRITE: key
					if (!isKryoSerializer) {
						out.writeObject(value);                    // WRITE: value
					} else {
						KryoIncrementalStreamProcessor.writeObjectToStream(value, out);
					}
				}

				Iterator changedKeyIter = m_changed.keySet().iterator();
				while (changedKeyIter.hasNext()) {
					Object key = changedKeyIter.next();
					ChangeEntry ce = (ChangeEntry) m_changed.get(key);

					out.writeObject(key);                    // WRITE: key
					out.writeShort(ce._opType);                // WRITE: opType
				}

			} else {
				int num = m_changed.size();
				out.writeInt(num);                            // WRITE: number of key-ChangeEntry pairs

				Iterator keyIter = m_changed.keySet().iterator();
				while (keyIter.hasNext()) {
					Object key = keyIter.next();
					ChangeEntry ce = (ChangeEntry) m_changed.get(key);

					out.writeObject(key);                    // WRITE: key
					out.writeShort(ce._opType);                // WRITE: opType

					if (ce._opType == PUT) {
						out.writeObject(ce._value);            // WRITE: value
					}
				}
			}
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		m_replicableId = (String)in.readObject();	// READ: replicable-id
		alwaysFullReplication = (boolean)in.readBoolean();	// READ: full replication flag
		int num = in.readInt();						// READ: number of key-value pairs

		for(int i=0; i < num; i++) {
			Object key = in.readObject();			// READ: key
			Object value = null;
			//This is done to load the Media Server SBB at standby SAS during deserializing
			//by setting the appropriate SBB Class Loader. This is done to avoid adding the sbb classes
			//to the AseClassLoader
//			if (key instanceof String && ((String)key).endsWith(ASE_SBB) && in instanceof AseObjectInputStream){
//				((AseObjectInputStream)in).setSbbLoader(this.getLatestSbbCL());
//				value = in.readObject();			// READ: value
//				((AseObjectInputStream)in).setSbbLoader(null);
//			}else{
//				value = in.readObject();			// READ: value
//			}
//			super.put(key, value);

			if (in instanceof AseObjectInputStream) {
				if (key instanceof String && ((String)key).endsWith(ASE_SBB)) {
					((AseObjectInputStream) in).setSbbLoader(this.getLatestSbbCL());
					value = in.readObject();            // READ: value
					((AseObjectInputStream) in).setSbbLoader(null);
				} else {
					value = in.readObject();
				}
			} else if(in instanceof KryoObjectInput) {
				if (key instanceof String && ((String)key).endsWith(ASE_SBB)) {
					ClassLoader previousCl = ((KryoObjectInput) in).getKryoObject().getClassLoader();
					((KryoObjectInput) in).getKryoObject().setClassLoader(this.getLatestSbbCL());
					value = in.readObject();
					((KryoObjectInput) in).getKryoObject().setClassLoader(previousCl);
				}else{
					value = in.readObject();
				}
			}
			super.put(key, value);
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		synchronized (this) {

			if (logger.isDebugEnabled()){
					logger.debug("setFirstReplicationCompleted(true); ");
			}
			this.setFirstReplicationCompleted(true);
                           	
			out.writeObject(m_replicableId);			// WRITE: replicable-id
			out.writeBoolean(alwaysFullReplication);	// WRITE: full replication flag
			int num = this.size();
			out.writeInt(num);							// WRITE: number of key-value pairs

			Iterator keyIter = super.keySet().iterator();
			while(keyIter.hasNext()) {
				Object key = keyIter.next();
				Object value = super.get(key);

				out.writeObject(key);					// WRITE: key
				out.writeObject(value);					// WRITE: value
			}

		}	
	}

	public void partialActivate(ReplicationSet parent) {
		// NOOP
	}
	public void activate(ReplicationSet parent) {
		// NOOP
	}

	public boolean isModified() {
		return alwaysFullReplication ? true : !this.m_changed.isEmpty();
	}

	public boolean isNew() {
		return this.m_new;
	}
	
	
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}


	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted = isFirstReplicationCompleted;
	}

	public void replicationCompleted() {
		if (logger.isDebugEnabled())
			logger.debug("ReplicableMap: replicationCompleted(): Setting m_new = false");
		replicationCompleted(false);
	}

	public void replicationCompleted(boolean noReplication) {
		if (logger.isDebugEnabled())
			logger.debug(getReplicableId()
					+ "ReplicableMap: replicationCompleted(): Setting m_new = false No rep"
					+ noReplication);
		if (!noReplication) {
			this.m_new = false;
		}
		this.m_changed.clear();
	}

	///////////////////// Replicable interface impl ends //////////////
	
	///////////////////// Map interface impl starts //////////////

	public void clear() {
		super.clear();
	}

	public Set entrySet() {
		return new AseUnmodifiableSet(super.entrySet());
	}

	public Set keySet() {
		return new AseUnmodifiableSet(super.keySet());
	}

	public Object put(Object key, Object value) {
		synchronized (this) {
			Object ret = super.put(key, value);
			if(!m_new) {
				m_changed.put(key, new ChangeEntry(PUT, value));		
			}
			return ret;
		}
	}

	public void putAll(Map m) {
		throw new UnsupportedOperationException("ReplicableMap.putAll(Map) not supported");
	}
	
	public Object remove(Object key) {
		synchronized (this) {
			Object ret = super.remove(key);
			if(!m_new) {
				m_changed.put(key, new ChangeEntry(REMOVE, null));		
			}
			return ret;
		}
	}

	public Collection values() {
		throw new UnsupportedOperationException("ReplicableMap.values() not supported");
	}

	////////////////////// Map interface impl ends //////////////
	
	public String toString() {
		return m_replicableId;
	}
	
	public static void main(String[] args){
	}
	public ClassLoader getLatestSbbCL() {
		return latestSbbCL;
	}

	public void setLatestSbbCL(ClassLoader latestSbbCL) {
		this.latestSbbCL = latestSbbCL;
	}
	

}
