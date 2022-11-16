package com.genband.m5.maps.ide.model;

 

import java.util.HashMap;
import java.util.Map;

import com.genband.m5.maps.common.CPFConstants;



public class RoleMappingHandler {

 

            /**

             * Say, for modify operation roles R1 and R2 can modify all the attributes

             * while role R3 can modify a subset. A map with data like -

             * <100, {R1, R2}, 101, {R3}> would be sent. Here, 100 and 101 are the unique

             * operation id assigned to the two variants of the modify operation.

             * @param o

             * @param screen

             * @return the map

             */

            public Map<Integer, String[]> getOperationRoleMap (CPFConstants.OperationType o, CPFScreen screen) {

            	Map<Integer, String[]> temp = new HashMap<Integer, String[]> ();
            	String[] t = new String [2];
            	t [0] = "Role1";
            	t [1] = "Role2";
            	temp.put(100, t);
            	return temp;
                        //return null; //TODO

            }

}
