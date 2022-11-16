package com.baypackets.ase.ftjdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;


public class ProxyStatement implements java.lang.reflect.InvocationHandler {
    private java.sql.Statement[] stmts;
    
    private static Class[] INTERFACES = new Class[] {Statement.class}; 

    public static Object newInstance(Object obj) {
            return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                INTERFACES,
                new ProxyStatement(obj));
    }

    private ProxyStatement(Object obj) {
            this.stmts = (java.sql.Statement[]) obj;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result = null;
        try {
                System.out.println("before method " + m.getName());
                
                // Execute on only one db if read operation
                if (m.getName().equalsIgnoreCase("executeQuery")) {
                	result = m.invoke(stmts[0], args);
                }
                else {
                	for (int i=0; i<stmts.length; i++){
                        result = m.invoke(stmts[i], args);
                    }	
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
            return result;
    }
}
