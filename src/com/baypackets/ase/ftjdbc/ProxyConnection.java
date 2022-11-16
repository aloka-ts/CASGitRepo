package com.baypackets.ase.ftjdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;


public class ProxyConnection implements java.lang.reflect.InvocationHandler {
    private java.sql.Connection[] cons;
    
    private static Class[] INTERFACES = new Class[] {Connection.class}; 

    public static Object newInstance(Object obj) {
            return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                INTERFACES,
                new ProxyConnection(obj));
    }

    private ProxyConnection(Object obj) {
            this.cons = (java.sql.Connection[]) obj;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
    	java.sql.Statement[] resultStmt = null;
        Statement retStmt = null;
    	java.sql.PreparedStatement[] resultPrepStmt = null;
        Statement retPrepStmt = null;
    	java.sql.CallableStatement[] resultCallStmt = null;
        Statement retCallStmt = null;
        Object resultObj = null;
    	
    	if (m.getName().equalsIgnoreCase("createStatement")) {
    		resultStmt = new Statement[this.cons.length];
            try {
                System.out.println("before method " + m.getName());
                for (int i=0; i<cons.length; i++){
                  	resultStmt[i] = (java.sql.Statement)m.invoke(cons[i], args);
                }
                retStmt = (Statement)ProxyStatement.newInstance(resultStmt);
            } catch (InvocationTargetException e) {
                    throw e.getTargetException();
            } catch (Exception e) {
            		e.printStackTrace();
                    throw new RuntimeException("unexpected invocation exception: " +
                                                           e.getMessage());
            } finally {
                  System.out.println("after method " + m.getName());
            }
            return retStmt;
    	}
    	else if (m.getName().equalsIgnoreCase("prepareStatement")) {
    		resultPrepStmt = new PreparedStatement[this.cons.length];
            try {
                System.out.println("before method " + m.getName());
                for (int i=0; i<cons.length; i++){
                  	resultPrepStmt[i] = (java.sql.PreparedStatement)m.invoke(cons[i], args);
                }
                retPrepStmt = (PreparedStatement)ProxyPrepStatement.newInstance(resultPrepStmt);
            } catch (InvocationTargetException e) {
                    throw e.getTargetException();
            } catch (Exception e) {
            		e.printStackTrace();
                    throw new RuntimeException("unexpected invocation exception: " +
                                                           e.getMessage());
            } finally {
                  System.out.println("after method " + m.getName());
            }
            return retPrepStmt;
    	}
    	else if (m.getName().equalsIgnoreCase("prepareCall")) {
    		resultCallStmt = new CallableStatement[this.cons.length];
            try {
                System.out.println("before method " + m.getName());
                for (int i=0; i<cons.length; i++){
                  	resultCallStmt[i] = (java.sql.CallableStatement)m.invoke(cons[i], args);
                }
                retCallStmt = (CallableStatement)ProxyCallStatement.newInstance(resultCallStmt);
            } catch (InvocationTargetException e) {
                    throw e.getTargetException();
            } catch (Exception e) {
            		e.printStackTrace();
                    throw new RuntimeException("unexpected invocation exception: " +
                                                           e.getMessage());
            } finally {
                  System.out.println("after method " + m.getName());
            }
            return retCallStmt;
    	}
    	else {
	        try {
	            System.out.println("before method " + m.getName());
	            for (int i=0; i<cons.length; i++){
	              	resultObj = m.invoke(cons[i], args);
	            }
	        } catch (InvocationTargetException e) {
	                throw e.getTargetException();
	        } catch (Exception e) {
	        		e.printStackTrace();
	                throw new RuntimeException("unexpected invocation exception: " +
	                                                       e.getMessage());
	        } finally {
	              System.out.println("after method " + m.getName());
	        }
	        return resultObj;
    	}
    }
}
