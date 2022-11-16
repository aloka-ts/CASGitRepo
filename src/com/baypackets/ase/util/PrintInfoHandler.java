/*
 * Created on Dec 6, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * @author Ravi
 */
public class PrintInfoHandler implements CommandHandler, MComponent{
	
	private static Logger logger = Logger.getLogger(PrintInfoHandler.class);
	
	private static final String CMD_PRINT_INFO = "print-info";
	private static final String CATEGORY_NAME = "<category>";
	private static final String FILE_NAME = "<file-name>";
	
	public static PrintInfoHandler instance(){
		return (PrintInfoHandler) Registry.lookup(Constants.NAME_PRINT_HANDLER);
	}
		
	private short[] keys = new short[0];
	private ArrayList categories = new ArrayList();
	
	public synchronized void registerInternalCategory(short type, String name, String prefix, int maxSize) throws Exception{
		int index = this.addKey(type);
		InternalDataCategory category = new InternalDataCategory(type, name, prefix, maxSize);
		this.categories.add(index, category);
	}

	
	public synchronized void registerExternalCategory(short type, String name, String prefix, Object obj) throws Exception {
		this.registerExternalCategory(type, name, prefix, obj, null);
	}

	public synchronized void registerExternalCategory(short type, String name, String prefix, Object obj, Renderer renderer) throws Exception{
		int index = this.addKey(type);
		if(obj == null){
			throw new Exception("Object cannot be NULL");
		}
		ExternalDataCategory category = null;
		if (obj instanceof Map){
			category = new ExternalDataCategory(type, name, prefix, (Map)obj, renderer);
		} else if (obj instanceof Collection) {
			category = new ExternalDataCategory(type, name, prefix, (Collection)obj, renderer);
		} else {
			throw new Exception("Object should be either Map or a Collection...");
		}
		this.categories.add(index, category);
	}

	public synchronized void registerConfigCategory(short type, String name, Properties prop) throws Exception{
		int index = this.addKey(type);
		if(prop == null){
			throw new Exception("Properties cannot be NULL");
		}
		ConfigDataCategory category = new ConfigDataCategory(type, name, prop);
		this.categories.add(index, category);
	}
	
	
	public synchronized void unregisterCategory(short type){
		int index = this.findKey(type);
		if(index < 0)
			return;
		Category category = (Category) this.categories.remove(index);
		this.removeKey(type);
	}
	
	public void addValue(short type, Object data){
		if(logger.isDebugEnabled()) {
			logger.debug("Entering addValue() : Setting value = "+data);
		}
		int index = this.findKey(type);
		if(index < 0)
			return;
		Category category = (Category) this.categories.get(index);
		if(category instanceof InternalDataCategory){
			((InternalDataCategory)category).setValue(data);
		}
	}
	
	public void printCategory(Category category, PrintStream out){
		if(category == null)
			return;
		out.println("Printing the details of \"" + category.name +"\" at :" + new Date());
		out.println(category.toString());
	}
	
	public void printSize(Category category, PrintStream out){
		if(category == null || category.size() < 0)
			return;
		out.println("SizeOf (\"" + category.name +"\") = "  + category.size());
	}
	
	private synchronized int addKey(short type) throws Exception{
		int index = this.findKey(type);
		if(index >= 0){
			throw new Exception("Category already present");
		}
		
		short[] temp = new short[this.keys.length + 1 ];
		System.arraycopy(this.keys,0,temp,0,this.keys.length);
		temp[this.keys.length] = type;
		Arrays.sort(temp);
		this.keys = temp;
		
		return this.findKey(type);		
	}
	
	private synchronized void removeKey(short type){
		int index = this.findKey(type);
		if(index < 0)
			return;
		short[] temp = new short[this.keys.length - 1 ];
		System.arraycopy(this.keys,0,temp,0,index);
		System.arraycopy(this.keys,index+1, temp, index, temp.length - index);
		this.keys = temp;
	}
	
	private int findKey(short type){
		return Arrays.binarySearch(keys, type);
	}
	
	
	abstract class Category{
		private short type;
		private String name;
		private String prefix;
		
		public Category(short type, String name, String prefix){
			this.type = type;
			this.name = name;
			this.prefix = prefix;
		}
		
		public abstract int size();
	}
	
	class InternalDataCategory extends Category{
		private Object[] data;
		private int maxSize;
		private int index;
		private int size;
	
		public InternalDataCategory(short type, String name, String prefix, int maxSize){
			super(type, name, prefix);
			this.maxSize = maxSize;
			this.data = new Object[this.maxSize];
		}
		
		public void setValue(Object obj){
			//Set the data
			this.data[this.index] = obj;
			
			//Calculate the Size
		  	if(this.size < this.maxSize){
				this.size++;
		  	}

		  	//Calculate the index
		  	if(this.index < this.maxSize-1){
				this.index++;
		  	}else{
				this.index = 0;
		  	}
		}
		
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			for(int i=0; i<size;i++){
				buffer.append("\r\n\t");
				buffer.append(i+1);
				buffer.append(AseStrings.PARENTHESES_CLOSE);
				buffer.append(super.prefix);
				if(size == maxSize){
					buffer.append(data[(this.index +i) % this.maxSize ]);
				}else{
					buffer.append(data[i]);
				}
			}
			return buffer.toString();
		}
		
		public int size(){
			return this.size;
		}
	}

	class ExternalDataCategory extends Category{
		private Map map;
		private Collection collection;
		private Renderer renderer;

		public ExternalDataCategory(short type, String name, String prefix, Map map, Renderer renderer) {
			super(type, name, prefix);
			this.map = map;
			this.renderer = renderer;
		}

		public ExternalDataCategory(short type, String name, String prefix, Collection collection, Renderer renderer) {
			super(type, name, prefix);
			this.collection = collection;
			this.renderer = renderer;
		}

		public String toString(){
			StringBuffer buffer = new StringBuffer();
			if(this.map != null){
				this.mapString(buffer);
			} else if (this.collection != null) {
				this.collectionToString(buffer);
			}
			return buffer.toString();
		}
			
		
		private void mapString(StringBuffer buffer){
			synchronized (this.map) {
				Iterator it = this.map.entrySet().iterator();
				for(int i=0; it != null && it.hasNext();i++){
					Map.Entry entry = (Map.Entry) it.next();
					Object key = (entry != null) ? entry.getKey() : AseStrings.BLANK_STRING;
					Object value = (entry != null) ? entry.getValue() : AseStrings.BLANK_STRING;
					buffer.append("\r\n\t");
					buffer.append(key);
					buffer.append(AseStrings.EQUALS);
					buffer.append(value);
				}
			}
		}

		private void collectionToString(StringBuffer buffer) {
			if (this.renderer != null) {
				String string = this.renderer.toString(this.collection);

				if (string != null) {
					buffer.append(string);
					return;
				}
			}
			
			buffer.append("[\r\n");
		
			synchronized (this.collection) {
				Iterator iterator = this.collection.iterator();

				while (iterator.hasNext()) {
					buffer.append(AseStrings.TAB);
				
					if (this.renderer != null) {
						buffer.append(this.renderer.toString(iterator.next()));
					} else {
						buffer.append(iterator.next());
					}
					if (iterator.hasNext()) {
						buffer.append(",\r\n\r\n");
					}
				}
			}
			buffer.append("\r\n]");
		}
		
		public int size(){
			int size = 0;
			if(this.map != null){
				size += this.map.size();
			} else if (this.collection != null) {
				size += this.collection.size();
			}
			return size;
		}
	}

	class ConfigDataCategory extends Category{
		private Properties prop;
		
		public ConfigDataCategory(short type, String name, Properties prop){
			super(type, name, "");
			this.prop = prop;
		}
	
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			Enumeration enr = this.prop.propertyNames();
			for(int i=0; enr != null && enr.hasMoreElements();i++){
				String key = (String) enr.nextElement();
				buffer.append("\r\n\t");
				buffer.append(key);
				buffer.append(AseStrings.EQUALS);
				buffer.append(BaseContext.getConfigRepository().getValue(key));
			}
			return buffer.toString();
		}
		
		public int size(){
			return -1;
		}
	}
	
	public static void main(String[] args){
		short TYPE1 = 1;
		short TYPE2 = 2;
		short TYPE3 = 3;
		try{
			ArrayList list = new ArrayList();
			PrintInfoHandler store = new PrintInfoHandler();
			store.registerInternalCategory(TYPE1, "Type1", "type1=", 10);
			store.registerInternalCategory(TYPE2, "Type2", "type1=",100);
			store.registerExternalCategory(TYPE3, "Type3", "type1=",list);
			
			TelnetServer server = new TelnetServer(10000);
			server.registerHandler(CMD_PRINT_INFO, store);
			
			store.addValue(TYPE1, "100");
			store.addValue(TYPE1, "200");
			store.addValue(TYPE1, "300");
			store.addValue(TYPE1, "400");
			store.addValue(TYPE1, "500");
			store.addValue(TYPE1, "600");
			store.addValue(TYPE1, "700");
			store.addValue(TYPE1, "800");
			store.addValue(TYPE1, "900");
			store.addValue(TYPE1, "1000");
			store.addValue(TYPE1, "1100");
			store.addValue(TYPE1, "1200");
			store.addValue(TYPE1, "1300");
			store.addValue(TYPE1, "1400");
			store.addValue(TYPE1, "1500");
			store.addValue(TYPE1, "1600");
			store.addValue(TYPE1, "1700");
			
			list.add("test3....1");
			list.add("test3....2");
			list.add("test3....3");
			list.add("test3....4");
			list.add("test3....5");
			
			server.start();
			server.join();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public String execute(String command,
							String[] args,
							InputStream in,
							OutputStream out)
							throws CommandFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("");
		}

		FileOutputStream fStream = null;
		PrintStream pstream = null;
		try {
			//If the number of arguments is less than 1 , print the usage statement
			if(args.length < 1){
				return this.getUsage(command);
			}
			
			short category = -1;
			boolean printAll = args[0] != null && args[0].equals("all");
			boolean printSize = args[0] != null && args[0].equals("size");
			
			
			String fileName = "";
			try{
				category = Short.parseShort(args[0]);
			}catch(NumberFormatException e){}
			
			if(args.length >= 2){
				fileName = args[1];
				File file = new File(fileName);
				
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdir();
				}else{
					if(!file.getParentFile().isDirectory()){
						throw new Exception(file.getParent() + " is not a directory");
					}
				}
				fStream = new FileOutputStream(fileName);
				out.write(("\r\nRedirecting output to file :"+fileName).getBytes());
			}
			
			pstream = new PrintStream(fStream != null ? fStream : out);
			if(printAll){
				for(int i=0; i<this.categories.size();i++){
					Category temp = (Category) categories.get(i);
					this.printCategory(temp, pstream);
				}
			}else if(printSize){
				pstream.println("Printing the Size of all the categories at :" + new Date());
				for(int i=0; i<this.categories.size();i++){
					Category temp = (Category) categories.get(i);
					this.printSize(temp, pstream);
				}
			}else{
				int index = this.findKey(category);
				if(index >= 0){
					Category temp = (Category) categories.get(index);
					this.printCategory(temp, pstream);
				}else{
					throw new Exception("Unable to get the category information");
				}
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
			return e.getMessage();
		} finally{
			if(fStream != null){
				try{
					fStream.close();
					pstream.close();
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
		return AseStrings.NEWLINE_WITH_CR +command + " Completed successfully.";
	}

	public String getUsage(String command) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Usage :");
		buffer.append(command);
		buffer.append(AseStrings.SPACE);
		buffer.append(CATEGORY_NAME);
		buffer.append(AseStrings.SPACE);
		buffer.append(FILE_NAME);
		buffer.append("\r\nWhere\r\n");
		buffer.append(CATEGORY_NAME);
		buffer.append(" include:");
		buffer.append("\r\n\tsize    = Print the size of all the categories listed below");
		buffer.append("\r\n\tall     = Print all the categories listed below");
		for(int i=0;i<this.categories.size();i++){
			Category category = (Category) this.categories.get(i);
			buffer.append("\r\n\t");
			buffer.append(category.type);
			buffer.append("     =");
			buffer.append(category.name);
		}
		buffer.append(AseStrings.NEWLINE_WITH_CR);
		buffer.append(FILE_NAME);
		buffer.append(" specifies the absolute file name to redirect the output.");
		buffer.append("\r\n\t If the file name is missing, the output will be redirected to the console.");
		
		return buffer.toString();
	}
	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#changeState(com.baypackets.bayprocessor.agent.MComponentState)
	 */
	public void changeState(MComponentState state)
		throws UnableToChangeStateException {
		try {
			if(logger.isInfoEnabled()){
				logger.info("Change state called on Printable Info Store :::" + state.getValue());
			}
			if(state.getValue() == MComponentState.LOADED){
				this.initialize();
			} else if(state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
				this.shutdown();
			}
		} catch(Exception e){
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#updateConfiguration(com.baypackets.bayprocessor.slee.common.Pair[], com.baypackets.bayprocessor.agent.OperationType)
	 */
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
		throws UnableToUpdateConfigException {
		//No Op...
	}
	
	public void initialize() throws Exception {
		Properties prop = AseUtils.getProperties(null);
		this.registerConfigCategory(Constants.CTG_ID_CONFIG, Constants.CTG_NAME_CONFIG, prop);

		TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_PRINT_INFO, this);
	}
	
	public void start() throws Exception {}
	
	public void shutdown() throws Exception {}
}
