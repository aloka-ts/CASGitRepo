package com.baypackets.ase.ftjdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;


public class ProxyCallStatement implements java.lang.reflect.InvocationHandler {
    private java.sql.CallableStatement[] callstmts;
    
    private static Class[] INTERFACES = new Class[] {CallableStatement.class}; 

    public static Object newInstance(Object obj) {
            return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                INTERFACES,
                new ProxyCallStatement(obj));
    }

    private ProxyCallStatement(Object obj) {
            this.callstmts = (java.sql.CallableStatement[]) obj;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result = null;
        try {
                System.out.println("before method " + m.getName());
                for (int i=0; i<callstmts.length; i++){
                    result = m.invoke(callstmts[i], args);
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
