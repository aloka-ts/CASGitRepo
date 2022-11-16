package com.baypackets.ase.spi.replication;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.util.AseObjectInputStream;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;

@DefaultSerializer(ExternalizableSerializer.class)
public class Replicables implements ReplicationSet, Replicable {
	
	private static final long serialVersionUID= -374518201148L;
	public static final short REPLICATION_FULL	= 1;
	public static final short REPLICATION_INC	= 2;
	//saneja @bug 10099 [
	public static final short REPLICATION_ERR	= 3;
	//]closed saneja @bug 10099
	
	private static final Logger logger = Logger.getLogger(Replicables.class);

	// Non transient attributes
	transient private String m_replicableId;
	
	transient private Map<String, Replicable> replicables = new HashMap<String, Replicable>();
	transient private ArrayList<Replicable> m_list = new ArrayList<Replicable>();
	transient private ArrayList<String> m_removed = new ArrayList<String>();
	transient private Object lock = new Object();

	// Transient attributes
	transient private boolean m_new = true;
	transient private boolean selectiveReplication = false;
	private boolean mFirstReplicationCompleted = false;
	
	public void setSelectiveReplication(boolean flag){
                this.selectiveReplication = flag;
        }

        public boolean  getSelectiveReplication(){
                return this.selectiveReplication;
        }

	//
	// ReplicationSet methods start
	//

	public Collection getAllReplicables() {
		synchronized(lock){
			return new ArrayList(m_list);
		}
	}

	public Replicable getReplicable(String id) {
		if(logger.isDebugEnabled()) {
                       logger.debug("Get Replicable object for Id : " +id );
                }
	
		return replicables.get(id);
	}

	public void removeReplicable(String id) {
		this.removeReplicable(id, !selectiveReplication);
	}
	protected void removeReplicable(String id, boolean trackRemoval) {
		if(logger.isDebugEnabled()) {
			logger.debug("Removing replicable with id = " + id);
		}

		synchronized(lock){
			Replicable rep = replicables.remove(id);
			if(rep != null){
				m_list.remove(rep);
			}
			if(rep != null && trackRemoval){
				m_removed.add(id);
			}
		}
		
	}

	public void setReplicable(Replicable replicable) {
		String id = replicable.getReplicableId();
		
		synchronized(lock){
			Replicable oldReplicable =replicables.get(id);
			replicables.put(id, replicable);
			boolean contains = false;
			for (Object obj : m_list) {
				if (obj == replicable) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				if(oldReplicable!=null){
					m_list.remove(oldReplicable);
					logger.error("Duplicate attempt Removed old replicable::"+id);
				}
				m_list.add(replicable);
			}
			m_removed.remove(id);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Added replicable with id = " + id);
		}
		
	}
	
	public void clear() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside clear");
		}
		synchronized(lock){
			replicables.clear();
			m_list.clear();
			m_removed.clear();
		}
	}

	//
	// ReplicationSet methods end
	//

	//
	// Replicable methods start
	//

	public void partialActivate(ReplicationSet parent) {
		Iterator itr = getAllReplicables().iterator();
		while(itr.hasNext()) {
			Replicable rep = (Replicable)itr.next();
			
			if(logger.isDebugEnabled())
				logger.debug("partialActivate called ");
			rep.partialActivate(parent);
		}
	}

	public void activate(ReplicationSet parent) {
		boolean  isDisableCreate =  false;
		if(this instanceof SelectiveReplicationContext){
			  isDisableCreate = ((SelectiveReplicationContext)this).isDisableCreate();
		}
		if(isDisableCreate){
			if(logger.isDebugEnabled())
			logger.debug("Using seperate for Selective replication context...");
			SelectiveRepContextActivator selectiveRepContextActivator = new SelectiveRepContextActivator(this, parent);
			Thread t = new Thread(selectiveRepContextActivator);
			t.start();
			
		}else{
			Iterator iter = getAllReplicables().iterator();
			while(iter.hasNext()) {
				Replicable rep = (Replicable)iter.next();
					rep.activate(parent);
			}
		}
		
	}

	public String getReplicableId() {
		return m_replicableId;
	}

	public boolean isReadyForReplication() {
		Iterator iter = getAllReplicables().iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			if(rep.isReadyForReplication()) {
				return true;
			}
		}

		return false;
	}

	public boolean isModified() {
		if(!m_removed.isEmpty() || m_new) {
			return true;
		}

		Iterator iter = getAllReplicables().iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			if(rep.isModified() || rep.isNew()) {
				return true;
			}
		}

		return false;
	}

	public boolean isNew() {
		return m_new;
	}


	public void replicationCompleted( ) {
		replicationCompleted(false);
	}
	
	public void replicationCompleted( boolean noReplication) {
		//Rajendra :start
		if(logger.isDebugEnabled())
			logger.debug("Inside replicationCompleted(): "+isReadyForReplication()+"  NoRep::"+noReplication);
		
		if(!isReadyForReplication())	{
			if(logger.isDebugEnabled())
			logger.debug("RETURNING FROM replicationCompleted()");
			return;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Replicables: replicationCompleted(): Setting m_new = false");
		//Rajendra: end
		if(!noReplication){
			m_new = false;
		}
		
		synchronized(lock){
			m_removed.clear();
		}

		Iterator iter = getAllReplicables().iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			//Rajendra:start if rep.isReadyForReplication() is true then invoke next method
			if(rep.isReadyForReplication())	{	
				rep.replicationCompleted(noReplication);
			}
		//Rajendra: end
		}
	}

	public void setReplicableId(String id) {
		if(m_replicableId == null) {
			m_replicableId = id;
			return;
		}

		throw new IllegalStateException("ReplicableId cannot be overwritten");
	}

	
	/**Added by Reeta
	 * if the Classloader is found for the Resource then change it.
	 * This will be true for the Resource Adaptors only
	 */
	public ClassLoader needToChangeRSLoader(String resourceHint) { 
		if(logger.isDebugEnabled())
		logger
				.debug("needToChangeRSLoader: for  Resourcehint........."
						+ resourceHint);
		ClassLoader classLoader = null;
		AseContainer container = (AseContainer) Registry
				.lookup(Constants.NAME_HOST);
		AseContainer[] children = container.findChildren();
		for (int j = 0; children != null && j < children.length; j++) {
			if (children[j] == null)
				continue;
			if (children[j] instanceof ResourceContext) {
				String protocolName = ((ResourceContext) children[j])
						.getProtocol();
				if (protocolName != null && protocolName.equals(resourceHint)) {
					if(logger.isDebugEnabled())
					logger
							.debug("needToChangeRSLoader: Resource Protocol name matches with Resource hint to getting ClassLoader.........");
					classLoader = ((ResourceContext) children[j])
							.getClassLoader();
					break;
				}
			}
		}
		return classLoader;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {

		// @abaxi Fixed for bug 20213 for timer replication issue 
		// Saving oldclassloader from AseObjectInputStream, so after needToChangeRSLoader()
		// changed class loader and replicable is read then set oldClass loader again in end of For Loop. Line this.getClass().getClassLoader() 
		// will return AseClassLoader and if previously app class loader was set then it will cause ClassNotFoundException while De-serialization

		//ClassLoader oldClassLoader=this.getClass().getClassLoader(); // @abaxi commented for bug 20213 fix

		ClassLoader oldClassLoader = ((AseObjectInputStream) in).getClassLoader();
		ClassLoader newClassLoader = null;

		int size = in.readInt();                    // READ: number of replicable objects

		if (logger.isDebugEnabled()) {
			logger.debug("Total Number of Replicables to be read : " + size);
		}

		// Read all replicables from input stream
		for (int i = 0; i < size; i++) {
			String id = (String) in.readObject();    // READ: replicable id
			short mode = in.readShort();            // READ: mode
			Replicable rep = null;

			if (id != null && id.indexOf(AseStrings.UNDERSCORE) != -1) {  //reeta
				String[] tmpId = id.split(AseStrings.UNDERSCORE);
				newClassLoader = needToChangeRSLoader(tmpId[0]); //resHint
				if (newClassLoader != null) {
					((AseObjectInputStream) in).setClassLoader(newClassLoader);
				}
			}

			if (mode == REPLICATION_FULL) {
				rep = (Replicable) in.readObject();    // READ: full replicable object
				rep.setReplicableId(id);
				setReplicable(rep);

				if (logger.isDebugEnabled()) {
					logger.debug("Read full Replicable with id : " + id);
				}
			} else if (mode == REPLICATION_INC) {
				rep = getReplicable(id);
				if (rep == null) {
					logger.error("Replicable is null for id::" + id);
				} else {
					rep.readIncremental(in);            // READ: incremental replicable object
					if (logger.isDebugEnabled()) {
						logger.debug("Read incremental Replicable with id : " + id);
					}
				}
			} else {
				logger.error("ERROR mode!!! For Replicable with id : " + id);
				//saneja @bug 10099 [
				if (logger.isDebugEnabled()) {
					logger.debug("Error mode:: [" + mode + " ] recived ignore replicable");
				}
				//] closed saneja @bug 10099
			}
			((AseObjectInputStream) in).setClassLoader(oldClassLoader);
		}

		size = in.readInt();                        // READ: number of replicabes to remove
		if (logger.isDebugEnabled()) {
			logger.debug("Total Number of Replicable objects to remove : " + size);
		}

		// Read all replicables from input stream
		for (int i = 0; i < size; i++) {
			String id = (String) in.readObject();    // READ: replicable id
			if (logger.isDebugEnabled()) {
				logger.debug("Read id to remove : " + id);
			}
			this.removeReplicable(id, false);
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside writeIncremental() of Replicables ");
		}
		ArrayList writableReps = getWritableReplicables(false);
		writeIncremental(out, writableReps, m_removed, replicationType);
	}

	private void writeIncremental(ObjectOutput out, ArrayList writableReps, ArrayList toRemove, int replicationType) throws IOException {
		out.writeInt(writableReps.size());			// WRITE: number of replicables to replicate

		if(logger.isDebugEnabled()) {
			logger.debug("Total Number of Replicables to be replicated  NEW: " + writableReps.size());
		}

		// Traverse through all replicables. Write replicable id and mode first.
		// Then write full/incremental object depending on mode.
		Iterator iter = writableReps.iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			
			if(logger.isDebugEnabled()) {
				logger.debug("isFirstReplicationCompleted: " + rep.isFirstReplicationCompleted());
			}
			String id = rep.getReplicableId();
			out.writeObject(id);					// WRITE: replicable id
			if(rep.isNew()|| !rep.isFirstReplicationCompleted()) {
				rep.setFirstReplicationCompleted(true);
				out.writeShort(REPLICATION_FULL);	// WRITE: mode = full
				out.writeObject(rep);				// WRITE: full replicable object
				if(logger.isDebugEnabled()) {
					logger.debug("Wrote full Replicable with id : " + id); 
				}
			} else if(rep.isModified()) {
				out.writeShort(REPLICATION_INC);	// WRITE: mode = incremental
				rep.writeIncremental(out, replicationType);			// WRITE: incremental replicable object
				if(logger.isDebugEnabled()) {
					logger.debug("Wrote incremental Replicable with id : " + id); 
				}
			} else {
				//saneja @bug 10099 [
				out.writeShort(REPLICATION_ERR);
				if(logger.isDebugEnabled()) {
					logger.debug("NO change in replicable setting error mode [" + REPLICATION_ERR +"]");
				}
				//] closed saneja @bug 10099
				logger.error("ERROR!!! For Replicable with id : " + id); 
			}
		} // while

		// Now write number and ids of Replicable objects removed
		out.writeInt(toRemove.size());				// WRITE: number of replicables to remove
		if(logger.isDebugEnabled()) {
			logger.debug("Total  number of replicable objects to remove : " + toRemove.size()); 
		}

		iter = toRemove.iterator();
		while(iter.hasNext()) {
			String id = (String)iter.next();
			out.writeObject(id);					// WRITE: replicable id
			if(logger.isDebugEnabled()) {
				logger.debug("Wrote id to remove : " + id); 
			}
		}
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (logger.isDebugEnabled())
			logger.debug("readExternal(ObjectInput) called");

		ClassLoader oldClassLoader = this.getClass().getClassLoader();
		ClassLoader newClassLoader = null;

		m_replicableId = (String) in.readObject();    // READ: replicable id
		int size = in.readInt();                    // READ: number of replicable objects
		if (logger.isDebugEnabled()) {
			logger.debug("Total Number of Replicables to be read : " + size);
		}

		// Read all replicables from input stream
		Replicable rep = null;
		for (int i = 0; i < size; i++) {
			String id = (String) in.readObject();    // READ: replicable id
			if (id != null && id.indexOf(AseStrings.UNDERSCORE) != -1) {  //reeta
				String[] tmpId = id.split(AseStrings.UNDERSCORE);
				newClassLoader = needToChangeRSLoader(tmpId[0]); //resHint
				if (newClassLoader != null) {
					if (in instanceof AseObjectInputStream) {
						oldClassLoader = this.getClass().getClassLoader();
						((AseObjectInputStream)in).setClassLoader(newClassLoader);
					} else if(in instanceof KryoObjectInput) {
						oldClassLoader = ((KryoObjectInput)in).getKryoObject().getClassLoader();
						((KryoObjectInput)in).getKryoObject().setClassLoader(newClassLoader);
					}
				}
			}
			rep = (Replicable) in.readObject();        // READ: full replicable object
			setReplicable(rep);

			if (in instanceof AseObjectInputStream) {
				((AseObjectInputStream)in).setClassLoader(oldClassLoader);
			} else if(in instanceof KryoObjectInput) {
				((KryoObjectInput)in).getKryoObject().setClassLoader(oldClassLoader);
			}
		} // for
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if(logger.isDebugEnabled())
		logger.debug("writeExternal(ObjectOutput) called");
				
		out.writeObject(m_replicableId);			// WRITE: replicable id

		ArrayList writableReps = getWritableReplicables(true);
		out.writeInt(writableReps.size());			// WRITE: number of replicables to replicate

		if(logger.isDebugEnabled()) {
			logger.debug("Total Number of Replicables to be replicated: " + writableReps.size());
		}

		// Traverse through all replicables. Write replicable id and mode first.
		// Then write full/incremental object depending on mode.
		Iterator iter = writableReps.iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			if (logger.isDebugEnabled()) {
				logger.debug("In writeExternal() of " + this.getReplicableId() +
						"; replicable is : " + rep);
			}
			if (logger.isDebugEnabled()){
					logger.debug("setFirstReplicationCompleted(true); for "+ this.getReplicableId());
			}
			rep.setFirstReplicationCompleted(true);
		
			String id = rep.getReplicableId();
			out.writeObject(id);					// WRITE: replicable id
			out.writeObject(rep);					// WRITE: full replicable object
		}
		
		if (logger.isDebugEnabled()){
			logger.debug("setFirstReplicationCompleted(true) ");
		}
		setFirstReplicationCompleted(true);
	}

	protected ArrayList getWritableReplicables(boolean replicateAllReady) {
		if(logger.isDebugEnabled())
		logger.debug("Inside getWritableReplicables() method");
		ArrayList temp = new ArrayList();
		Iterator iter = getAllReplicables().iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
			if(rep.isReadyForReplication()) {
				if(replicateAllReady) {
					temp.add(rep);
				} else if(rep.isNew() || rep.isModified()) {
					temp.add(rep);
				}
			}
		}
		return temp;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Replicables [");

		Iterator iter = getAllReplicables().iterator();
		while(iter.hasNext()) {
			buffer.append(AseStrings.ANGLE_BRACKET_OPEN).append(iter.next()).append(AseStrings.ANGLE_BRACKET_CLOSE);
		}

		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

	//============ Added to support selective replication =========
	public void replicationCompleted(String[] replicableIds, boolean noReplication) {
                if(logger.isDebugEnabled()){
                        logger.debug("replicationCompleted(String[] replicableIds) IN");
                }

                for(int i=0; i<replicableIds.length;i++){
                        Replicable rep = getReplicable(replicableIds[i]);
                        if(rep != null && rep.isReadyForReplication()) {
                                rep.replicationCompleted(noReplication);
                        }
                }
        }

	public void writeIncremental(ObjectOutput out, String[] replicableIds) throws IOException{
                if(logger.isDebugEnabled()) {
                        logger.debug("Inside writeIncremental(ObjectOutput, String[]) of Replicables ");
                }

                ArrayList toAdd = new ArrayList();
                ArrayList toRemove = new ArrayList();
                for(int i=0;i<replicableIds.length;i++){
                        Replicable rep = getReplicable(replicableIds[i]);
                        if(rep !=null)
                                toAdd.add(rep);
                        else
                                toRemove.add(replicableIds[i]);
                }
                if(logger.isDebugEnabled()){
                        logger.debug("toAddList:"+toAdd);
                        logger.debug("toRemoveList:"+toRemove);
                }
                writeIncremental(out, toAdd, toRemove, ReplicationEvent.TYPE_REGULAR);
        }

	@Override
	public boolean isFirstReplicationCompleted() {
		// TODO Auto-generated method stub
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
		
	}

}
