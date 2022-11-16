package com.baypackets.ase.util;


import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;



public class AseRexecPrintStream extends PrintStream {

	private AseRexecRollover arr=AseRexecRollover.getAseRexecRollover();
	private long maxFileSize=arr.getFileSizeLimit(); 
	private long totalFileSize = 0;
	
	public AseRexecPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
	}
	
	public AseRexecPrintStream(OutputStream out) {
		super(out);
	}
	
	@Override
	public void print(boolean b) {
		totalFileSize = totalFileSize + 1;
		if(isFileSizeExceed()){
			super.print(b);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(b);
	}
	
	@Override
	public void print(char c) {
		totalFileSize = totalFileSize + 2;
		if(isFileSizeExceed()){
			super.print(c);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(c);
	}
	
	@Override
	public void print(char[] s) {
		totalFileSize = totalFileSize + (s.length*2);
		if(isFileSizeExceed()){
			super.print(s);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(s);
	}
	
	@Override
	public void print(double d) {
		totalFileSize = totalFileSize + 8;
		if(isFileSizeExceed()){
			super.print(d);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(d);
	}
	
	@Override
	public void print(float f) {
		totalFileSize = totalFileSize + 4;
		if(isFileSizeExceed()){
			super.print(f);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(f);
	}
	
	@Override
	public void print(int i) {
		totalFileSize = totalFileSize + 4;
		if(isFileSizeExceed()){
			super.print(i);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(i);
	}
	
	@Override
	public void print(long l) {
		totalFileSize = totalFileSize + 8;
		if(isFileSizeExceed()){
			super.print(l);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(l);
	}
	
	@Override
	public void print(String s) {
		totalFileSize = totalFileSize + s.getBytes().length;
		if(isFileSizeExceed()){
			super.print(s);
			arr.rolloverFile();
			totalFileSize = 0;
			return;
		}
		super.print(s);
	}

	private boolean isFileSizeExceed() {
		if(totalFileSize>=maxFileSize){
			return true;
		}
		return false;
	}
}
