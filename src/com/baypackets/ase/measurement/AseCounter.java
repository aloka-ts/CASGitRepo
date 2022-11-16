/*
 * Created on Sep 8, 2004
 *
 */
package com.baypackets.ase.measurement;

import java.util.concurrent.atomic.AtomicLong;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.meascounters.SleeMeasurementCounter;

/**
 * @author Ravi
 */
public class AseCounter implements MeasurementCounter, SleeMeasurementCounter {

	static final String INDENTED_NEW_LINE = "\r\n    ";

	public AtomicLong count = new AtomicLong();
	private long startTime;
	private String name;
	private short type;
	private int index = -1;
	private int serviceId;
	private String serviceName;

	private String oid;
	private String perfOid; 

	AseCounter(String name){
		this.name = name;
		this.startTime = System.currentTimeMillis();
	}

	public long getCount() {
		return count.get();
	}

	public String getName() {
		return name;
	}

	public void setCount(int count) {
		this.count.set(count);
		//No need to add work to the queue for setting. Fixed as a part of Level3 Performance
		//AseMeasurementManager.instance().setCount(this, count);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void increment(){
		this.change(1);
	}

	public void increment(int offset){
		this.change(offset);
	}

	public void change(int offset){
		if(this.getType()==TYPE_USAGE) {
			this.count.addAndGet(offset);//bug 8659
		}
		if(offset > 0) {
			AseMeasurementManager.instance().increment(this, offset);
		} else if(offset < 0){
			AseMeasurementManager.instance().decrement(this, Math.abs(offset));
		}
	}

	public void decrement(int offset){
		if(this.count.get() > 0) {
			this.change(offset * -1);
		}
	}

	public void decrement(){
		if(this.count.get() > 0){
			this.change(-1);
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public short getType() {
		return type;
	}

	void setStartTime(long l) {
		startTime = l;
	}

	void setType(short s) {
		type = s;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int i) {
		index = i;
	}

	public String getOid() {
		return oid;
	}

	public String getPerfOid() {
		return perfOid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setPerfOid(String oid) {
		this.perfOid = oid;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int i) {
		serviceId = i;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getSleeMeasurementCounterName() { return getName(); }

	@Override
	public long getSleeMeasurementCounterValue() {
		return getCount();
	}

	@Override
	public String getSleeMeasurementCounterServiceName() { return getServiceName(); }

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(INDENTED_NEW_LINE);
		buffer.append(this.name);
		buffer.append(AseStrings.EQUALS);
		buffer.append(this.count);
		return buffer.toString();
	}

}
