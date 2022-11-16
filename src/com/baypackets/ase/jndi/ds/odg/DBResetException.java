package com.baypackets.ase.jndi.ds.odg;
/**
  * The SleeException class which has a protected error code and an addiional method to get error code */


public class DBResetException extends Exception {

  protected int errorCode;
  
  public DBResetException(String message,int errorCode){
    super(message);
    this.errorCode = errorCode;
  }

  public DBResetException(String message){
    super(message);
  } 

  public int getErrorCode(){
    return errorCode;
  }
}
