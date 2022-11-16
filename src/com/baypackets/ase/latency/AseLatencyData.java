package com.baypackets.ase.latency;


import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.latency.AseLatencyLogger.LatencyDataProvider;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.util.AseStrings;



/**
 * 
 * This class is not Thread safe
 * 
 *
 */
public class AseLatencyData {


	public static int getMethodType(final String methodString){
		for(int i=0; i<AseLatencyLogger.MessagesToBeCaptured.size(); i++){
			if(AseLatencyLogger.MessagesToBeCaptured.get(i).charAt(0)==(methodString.charAt(0))
					&&AseLatencyLogger.MessagesToBeCaptured.get(i).toLowerCase().contains("xx"))
				return i;
			else
				if(AseLatencyLogger.MessagesToBeCaptured.get(i).equals(methodString))
					return i;
		}
		return -1;
	}

	public final MethodSpecificLatencyData[] methodSpecificData = 
		new MethodSpecificLatencyData[AseLatencyLogger.MessagesToBeCaptured.size()];

	public AseLatencyData() {
		for(int i=0; i<AseLatencyLogger.MessagesToBeCaptured.size(); i++) {
			methodSpecificData[i]=new MethodSpecificLatencyData(i);
		}

		singleMethod = false;
		msgType = -1;
	}

	private AseLatencyData(final int METHOD, final int counter) {
		methodSpecificData[METHOD] = new MethodSpecificLatencyData(METHOD);
		methodSpecificData[METHOD].counter = counter;

		this.singleMethod = true;
		this.msgType = METHOD;
	}

	/**
	 * For Use in Thread Local's stored data
	 * @param stage
	 */
	public void noteStageBeginTime(int stage, final AseSipServletMessage msg){

		if(msg == null){
			return;
		}

		if( AseLatencyLogger.getInstance().getLatencyLoggingLevel()== 0){
			return;
		}

		int msgType = getMsgType(msg);

		if(msgType == -1)
			return;

		this.methodSpecificData[msgType].componentTimes.stageBeginTime = System.nanoTime();
	}

	/**
	 * @param stage
	 * @param msg
	 */
	public void addStageTime(int stage, final AseSipServletMessage msg){
		if(msg == null){
			return;
		}

		if( AseLatencyLogger.getInstance().getLatencyLoggingLevel() != 2){
			return;
		}

		int msgType = getMsgType(msg);

		if(msgType == -1)
			return;

		this.methodSpecificData[msgType].componentTimes.stageTimes[stage] += 
			(System.nanoTime() - this.methodSpecificData[msgType].componentTimes.stageBeginTime)/1000;

		this.methodSpecificData[msgType].counter++;
	}

	private final boolean singleMethod;
	private final int msgType;// if single method
	private static Logger _logger = Logger.getLogger(AseLatencyData.class);


	/**
	 * This method shows how to create and use this class.
	 * @return
	 */
	public static AseLatencyData createSample(){
		AseLatencyData ld= new AseLatencyData();

		for(MethodSpecificLatencyData m:ld.methodSpecificData){
			m.componentTimes.stageTimes[ComponentTimes.STACK] = 203;
			m.componentTimes.stageTimes[ComponentTimes.CONNECTOR] = 113;
			m.componentTimes.stageTimes[ComponentTimes.QUEUE] = 1203;
			m.componentTimes.stageTimes[ComponentTimes.CONTAINER] = 1503;
			m.componentTimes.stageTimes[ComponentTimes.APPLICATION] = 2001;
			m.componentTimes.stageTimes[ComponentTimes.RETURN] = 1126;
		}

		return ld;
	}


	public String toString(){
		return super.toString() + " methodspecificdata:"+ methodSpecificData.toString();
	}

	public String printLatencyData() {
		StringBuilder sb = new StringBuilder();
		sb.append(print());
		for(int i=0; i<AseLatencyLogger.MessagesToBeCaptured.size(); i++) {
			MethodSpecificLatencyData msd = methodSpecificData[i];
			if(msd!=null){
				sb.append(msd.componentTimes.toStringLevel());
			}
			else{
				sb.append(MethodSpecificLatencyData.nullString(i));
			}
			sb.append(AseStrings.NEWLINE);
		}
		sb.append(AseStrings.NEWLINE);
		return sb.toString();
	}

	public String printOutLatencyData() {
		StringBuilder sb = new StringBuilder();
		sb.append(printOut());
		for(int i=0; i<AseLatencyLogger.MessagesToBeCaptured.size(); i++) {
			MethodSpecificLatencyData msd = methodSpecificData[i];
			if(msd!=null){
				sb.append(msd.componentTimes.toStringOutLevel());
			}
			else{
				sb.append(MethodSpecificLatencyData.nullString(i));
			}
			sb.append(AseStrings.NEWLINE);
		}
		sb.append(AseStrings.NEWLINE);
		return sb.toString();
	}

	public AseLatencyData add(AseLatencyData ld) {
		for(int i=0; i<AseLatencyLogger.MessagesToBeCaptured.size();i++) {
			if(ld.methodSpecificData[i] == null){
				continue;
			}

			if(this.methodSpecificData[i] == null){
				this.methodSpecificData[i] = new MethodSpecificLatencyData(i);
			}

			this.methodSpecificData[i].addMethodSpecificLatencyData(
					ld.methodSpecificData[i]);
		}
		return this;
	}

	/**
	 * This method puts the stage time in the AseSipServletMessage 
	 * @param msg
	 * @param stage
	 * @param createLatencyData
	 */
	public static void noteLatencyData(final AseSipServletMessage msg, int stage, boolean createLatencyData){		

		if(msg == null){
			return;
		}
		
		if(msg.getAppChaining()){
			return;
		}
		
			
		if( AseLatencyLogger.getInstance().getLatencyLoggingLevel()== 0){
			return;
		}

		int msgType = getMsgType(msg);

		if(msgType == -1)
			return;
		if (_logger.isDebugEnabled()) {
			_logger.debug("noteLatencyData called");
		}
		if(msg.aseLatencyData == null && createLatencyData){
			msg.aseLatencyData = new AseLatencyData(msgType, 1);

			msg.aseLatencyData.methodSpecificData[msgType]
			                                      .componentTimes.stageBeginTime=msg.getBeginTimeStamp();
		}

		MethodSpecificLatencyData msd = 
			msg.aseLatencyData.methodSpecificData[msgType];

		if(AseLatencyLogger.getInstance().getLatencyLoggingLevel()==1 &&
				!(stage == ComponentTimes.CONTAINER || stage == ComponentTimes.APPLICATION)){
			return;
		}

		long time = System.nanoTime();

		msd.componentTimes.stageTimes[stage]
		                              = (time
		                            		  - msd.componentTimes.stageBeginTime ) / 1000;

		msd.componentTimes.stageBeginTime = time;

		if(stage == ComponentTimes.APPLICATION){
			ThreadLocalLatencyContainer.addLatencyData(msg.aseLatencyData);
		}

	}

	private static int getMsgType(final AseSipServletMessage msg){

		if(msg.aseLatencyData !=null && msg.aseLatencyData.singleMethod)
			return msg.aseLatencyData.msgType;

		if(msg instanceof AseSipServletRequest){
			return getMethodType(msg.getMethod());
		}else if(msg instanceof AseSipServletResponse){
			return getMethodType( ""+ ((AseSipServletResponse)msg).getStatus() );
		}

		return -1;
	}

	public String print() {
		if(AseLatencyLogger.getInstance().getLatencyLoggingLevel()==1)
			return "METHOD,\t"+"Counter,\t"+"SAS_Time,\t"+"Application_Time,\t"+"\n";

		else
			return "METHOD,\t"+"Counter,\t"+"Stack_Time,\t"+"Connector_Time,\t"+	
			"Queue_Time,\t"+"Container_Time,\t"+"Application_Time,\t"+"\n";
	}

	public String printOut() {
		return "METHOD,\t"+"Counter,\t"+"Outgoing_Time,\t"+"\n";
	}



	public static class MethodSpecificLatencyData{

		public long counter=0; 

		public final int METHOD;

		public MethodSpecificLatencyData(int method){
			METHOD=method;
		}

		public final ComponentTimes componentTimes = new ComponentTimes(this);

		public MethodSpecificLatencyData addMethodSpecificLatencyData(MethodSpecificLatencyData methodSpecificLatencyData) {


			if(methodSpecificLatencyData==null)
				return this;

			if(methodSpecificLatencyData.METHOD!=this.METHOD)
				throw new IllegalArgumentException("parameter's METHOD"+methodSpecificLatencyData.METHOD
						+" does not match this.METHOD"+this.METHOD);


			this.componentTimes.addComponentTimes(methodSpecificLatencyData.componentTimes);
			this.counter+= methodSpecificLatencyData.counter;

			return this;
		}

		@Override
		public String toString(){
			return super.toString()+" METHOD:"+AseLatencyLogger.MessagesToBeCaptured.get(METHOD)+" counter:"+counter;
		}

		

		public static String nullString(int method) {
			return "METHOD:\t"+AseLatencyLogger.MessagesToBeCaptured.get(method)
			+"\nCounter:\t"+0
			+"\n"+ComponentTimes.nullString()+"\n";
		}



	}

	public static class ComponentTimes{

		public final MethodSpecificLatencyData methodSpecificLatencyData;

		public static final int NUM_STAGES = 6;
		public static final int STACK = 0;
		public static final int CONNECTOR = 1;
		public static final int QUEUE = 2;
		public static final int CONTAINER = 3;// this includes the queue time as well
		public static final int APPLICATION = 4;
		public static final int RETURN = 5;
		public final long[] stageTimes = new long[NUM_STAGES];
		public static final String[] STAGE_NAME = new String[] {
			"STACK", "CONNECTOR","QUEUE","CONTAINER","APPLICATION","RETURN"};

		//init stage times to 0;
		{
			for(int i=0; i < NUM_STAGES; i++){
				stageTimes[i]=0;
			}
		}

		//this is temporary storage used while calculating each of the stage times;
		public transient long stageBeginTime;


		public ComponentTimes(MethodSpecificLatencyData msd,int stack_time, int connector_time,
				int queue_time, int container_time, int app_time,int outgoing_time) {
			methodSpecificLatencyData=msd;			

			setTimes(stack_time, connector_time, queue_time, container_time, app_time,outgoing_time);	
		}

		public void setTimes(int stack_time, int connector_time,
				int queue_time, int container_time, int app_time,int outgoing_time){

			stageTimes[STACK] = stack_time;
			stageTimes[CONNECTOR] = connector_time;
			stageTimes[QUEUE] = queue_time;
			stageTimes[CONTAINER] = container_time;
			stageTimes[APPLICATION] = app_time;
			stageTimes[RETURN] = outgoing_time;
		}

		public ComponentTimes(MethodSpecificLatencyData msd) {
			methodSpecificLatencyData=msd;
		}

		public ComponentTimes (final ComponentTimes componentTimes){
			this.methodSpecificLatencyData=componentTimes.methodSpecificLatencyData;
			stageTimes[STACK] = componentTimes.stageTimes[STACK];
			stageTimes[CONNECTOR] = componentTimes.stageTimes[CONNECTOR];
			stageTimes[QUEUE] = componentTimes.stageTimes[QUEUE];
			stageTimes[CONTAINER] = componentTimes.stageTimes[CONTAINER];
			stageTimes[APPLICATION] = componentTimes.stageTimes[APPLICATION];
			stageTimes[RETURN] = componentTimes.stageTimes[RETURN];
		}


		public ComponentTimes addComponentTimes(final ComponentTimes componentTimes) {

			this.stageTimes[STACK] += componentTimes.stageTimes[STACK];
			this.stageTimes[CONNECTOR] += componentTimes.stageTimes[CONNECTOR];
			this.stageTimes[QUEUE] += componentTimes.stageTimes[QUEUE];
			this.stageTimes[CONTAINER] += componentTimes.stageTimes[CONTAINER];
			this.stageTimes[APPLICATION] += componentTimes.stageTimes[APPLICATION];
			this.stageTimes[RETURN] += componentTimes.stageTimes[RETURN];

			return this;
		}

		public String toStringLevel() {
			if(methodSpecificLatencyData.counter==0)
				return "";

			if(AseLatencyLogger.getInstance().getLatencyLoggingLevel()== 1)
				return AseLatencyLogger.MessagesToBeCaptured.get(methodSpecificLatencyData.METHOD)+ ",\t" +
				methodSpecificLatencyData.counter+ ",\t" +
				stageTimes[CONTAINER]/methodSpecificLatencyData.counter/1000.0   + ",\t" +
				stageTimes[APPLICATION]/methodSpecificLatencyData.counter/1000.0 + ",\t" +"\n";

			else
				return AseLatencyLogger.MessagesToBeCaptured.get(methodSpecificLatencyData.METHOD)+ ",\t" +
				methodSpecificLatencyData.counter                                + ",\t" +
				stageTimes[STACK]/methodSpecificLatencyData.counter/1000.0       + ",\t" +
				stageTimes[CONNECTOR]/methodSpecificLatencyData.counter/1000.0   + ",\t" +
				stageTimes[QUEUE]/methodSpecificLatencyData.counter/1000.0       + ",\t" +
				stageTimes[CONTAINER]/methodSpecificLatencyData.counter/1000.0   + ",\t" +
				stageTimes[APPLICATION]/methodSpecificLatencyData.counter/1000.0 + ",\t" + "\n";
		}


		public String toStringOutLevel() {
			if(methodSpecificLatencyData.counter==0)
				return "";

			return AseLatencyLogger.MessagesToBeCaptured.get(methodSpecificLatencyData.METHOD)+ ",\t" +
			methodSpecificLatencyData.counter+ ",\t" +
			stageTimes[RETURN]/methodSpecificLatencyData.counter/1000.0 +",\t"  + "\n";
		}

		public static String nullString() {
			return "stack_time:\tNA"
			+"\nconnector_time:\tNA"
			+"\nqueue_time:\tNA"
			+"\ncontainer_time:\tNA"
			+"\napp_time:\tNA"
			+"\noutgoing_time:\tNA";
		}
	}

	public static class ThreadLocalLatencyContainer{
		private boolean registeredWithLatencyLogger=false;

		private void registerWithLatencyLogger(){

			final ThreadLocalLatencyContainer tlc =this;

			LatencyDataProvider ldp = new LatencyDataProvider() {

				@Override
				public AseLatencyData getLatencyData() {
					return tlc.getAndClearLatencyData();
				}

				public AseLatencyData getOutLatencyData() {
					return tlc.getAndClearOutLatencyData();
				}

			};
			AseLatencyLogger.getInstance().registerLatencyDataProvider(ldp);
		}


		public static void addLatencyData(final AseLatencyData ld) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("getLatencyData(): enter");
			}
			ThreadLocalLatencyContainer tlc  = AseThreadData.getLatencyContainer();


			if( !tlc.registeredWithLatencyLogger){
				tlc.registeredWithLatencyLogger=true;
				tlc.registerWithLatencyLogger();
			}

			synchronized (tlc.latencyDataMutex) {

				tlc.storedLatencyData.add(ld);
			}
		}

		private final Object latencyDataMutex = new Object();

		public  AseLatencyData getAndClearLatencyData(){
			AseLatencyData tmp=null;

			synchronized (this.latencyDataMutex) {
				tmp = this.storedLatencyData;
				this.storedLatencyData=new AseLatencyData();
			}

			return tmp;
		}

		public  AseLatencyData getAndClearOutLatencyData(){
			AseLatencyData tmp=null;

			synchronized (this.latencyDataMutex) {
				tmp = this.storedOutLatencyData;
				this.storedOutLatencyData=new AseLatencyData();
			}

			return tmp;
		}

		private AseLatencyData storedLatencyData = new AseLatencyData();

		private AseLatencyData storedOutLatencyData = new AseLatencyData();


		public static void handleBeginOut(int stage, final AseSipServletMessage msg){
			ThreadLocalLatencyContainer tlc  = AseThreadData.getLatencyContainer();

			synchronized (tlc.latencyDataMutex) {
				tlc.storedOutLatencyData.noteStageBeginTime(stage, msg);
			}
		}

		public static void handleEndOut(int stage, final AseSipServletMessage msg){
			ThreadLocalLatencyContainer tlc  = AseThreadData.getLatencyContainer();
			synchronized (tlc.latencyDataMutex) {
				tlc.storedOutLatencyData.addStageTime(stage, msg);
			}
		}
	}
}
