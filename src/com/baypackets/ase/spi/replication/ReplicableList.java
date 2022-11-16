package com.baypackets.ase.spi.replication;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

@DefaultSerializer(ExternalizableSerializer.class)
public class ReplicableList extends ArrayList implements Replicable {
	
	private static final long serialVersionUID=-34518201141889L;
	private static final short ADD = 1;
	private static final short SET = 2;
	private static final short REMOVE = 3;
	private static final short CLEAR = 4;

	// Non-transient attributes
	private String m_replicableId;

	// Transient attributes
	private boolean m_new = true;
	private ArrayList m_modified = new ArrayList();
	private boolean mFirstReplicationCompleted = false;
	private static final Logger logger = Logger.getLogger(ReplicableList.class);	

	public ReplicableList() {
		super(10);
	/*	for (int i=0 ; i<10 ; i++ )
		  super.add(i , 0 );a*/
	}
	
	public ReplicableList(String replicableId) {
		super();
		this.setReplicableId(replicableId);
	}

	////////////////////Replicable Interface implementation starts //////////////

	public void partialActivate(ReplicationSet parent) {
		//NOOP
	}

	public void activate(ReplicationSet parent) {
	}

	public String getReplicableId() {
		return m_replicableId;
	}

	public void setReplicableId(String replicableId) {
		if(m_replicableId == null) {
			m_replicableId = replicableId;
			return;
		}

		throw new IllegalStateException("Cannot overwrite replicable id");
	}

	public boolean isModified() {
		return !m_modified.isEmpty();
	}

	public boolean isNew() {
		return m_new;
	}

	public boolean isReadyForReplication() {
		return true;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		int num = in.readInt();							// READ: number of entries

		for(int i=0; i<num; i++) {
			short opCode = in.readShort();				// READ: opCode
			int index = in.readInt();					// READ: index
			switch(opCode){
				case ADD:
					super.add(index, in.readObject());	// READ: value
					break;
				case SET:
					super.set(index, in.readObject());	// READ: value
					break;
				case REMOVE:
					if(super.size()>0 && index<super.size() && index>=0)
						super.remove(index);
					else{
						logger.debug("Remove operation received for out of range. index:"+index+" size:"+super.size());
					}
					break;
				case CLEAR:
					super.clear();
					break;
			}
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		int num = m_modified.size();
		out.writeInt(num);								// WRITE: number of values

		Iterator iter = m_modified.iterator();
		while(iter.hasNext()) {			
			Entry entry = (Entry)iter.next();
			if (logger.isDebugEnabled()){
				logger.debug("Writing Entry: "+entry);
			}
			out.writeShort(entry._opCode);				// WRITE: opCode
			out.writeInt(entry._index);					// WRITE: index

			if((entry._opCode == ADD) || (entry._opCode == SET)) {
				out.writeObject(entry._value);			// WRITE: value
			}
		} // while
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		m_replicableId = (String)in.readObject();		// READ: replicable-id
		int num = in.readInt();							// READ: number of values

		for(int i=0; i < num; i++) {
			super.add(in.readObject());					// READ: value
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		
		if (logger.isDebugEnabled()){
			logger.debug("setFirstReplication(true); ");
		}
		this.setFirstReplicationCompleted(true);
		
		out.writeObject(m_replicableId);				// WRITE: replicable-id
		int num = this.size();							// WRITE: number of values
		out.writeInt(num);

		Iterator iter = super.iterator();
		while(iter.hasNext()) {
			out.writeObject(iter.next());				// WRITE: value
		}
	}

	public void replicationCompleted() {
		if (logger.isDebugEnabled())
			logger.debug("ReplicableList: replicationCompleted(): Setting m_new = false");
		replicationCompleted(false);
	}

	public void replicationCompleted(boolean noReplication) {
		if (logger.isDebugEnabled())
			logger.debug(getReplicableId()
					+ "ReplicableList: replicationCompleted(): Setting m_new = false No rep"
					+ noReplication);
		if (!noReplication) {
			this.m_new = false;
		}
		this.m_modified.clear();		
	}
	////////////////////Externalizable Interface implementation starts //////////////

	////////////////////ArrayList implementation starts //////////////
	public void add(int index, Object element) {
		super.add(index , element);
		if(!m_new){
			this.m_modified.add(new Entry(index, ADD, element));
		}
	}
	
	public boolean add(Object element) {
		boolean ret = super.add(element);
		if(!m_new){
			this.m_modified.add(new Entry(this.size()-1, ADD, element));
		}
		return ret;
	}

	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException("ReplicableList.addAll(Collection) not supported");
	}

	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException("ReplicableList.addAll(int, Collection) not supported");
	}

	public void clear() {
		if(!m_new){
			Iterator iter = super.iterator();
			while(iter.hasNext()) {
				m_modified.add(new Entry(0, CLEAR, null));
			}
		}

		super.clear();
	}

	public Object set(int index, Object element) {
		Object ret =super.set(index, element);
		if(!m_new){
			m_modified.add(new Entry(index, SET, element));
		}
		return ret;
	}

	public Object remove(int index) {
		Object value = super.remove(index);
		if(!m_new && value != null){
			m_modified.add(new Entry(index, REMOVE, null));
		}
		return value;
	}

	public List subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("ReplicableList.subList(int, int) not supported");
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException("ReplicableList.remove(Object) not supported");
	}

	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException("ReplicableList.removeAll(Collection) not supported");
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException("ReplicableList.retainAll(Collection) not supported");
	}

	////////////////////List Interface implementation ends //////////////
	
	public String toString() {
		return m_replicableId;
	}
	
	public class Entry {
		private int _index;
		private short _opCode;
		private Object _value;

		Entry(int index, short opCode, Object value) {
			_index = index;
			_opCode = opCode;
			_value = value;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Entry [_index=" + _index + ", _opCode=" + _opCode
					+ ", _value=" + _value + "]";
		}
	}

	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
	}

}
