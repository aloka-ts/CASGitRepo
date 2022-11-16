
package com.baypackets.ase.ra.ro;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public final class CustomAVPMapImpl implements CustomAVPMap {
	private Map _map = new HashMap();

	public CustomAVPMapImpl() {
	}

	public int[] getCustomAVPCodes() {
		int[] codeList = new int[_map.keySet().size()];

		Iterator iter = _map.keySet().iterator();
		int i = 0;
		while(iter.hasNext()) {
			codeList[i++] = ((Integer)iter.next()).intValue();
		}

		return codeList;
	}

	public CustomAVP getCustomAVP(int code) {
		return (CustomAVP)_map.get(new Integer(code));
	}

	public CustomAVP setCustomAVP(int code, CustomAVP value) {
		return (CustomAVP)_map.put(new Integer(code), value);
	}

	public CustomAVP removeCustomAVP(int code) {
		return (CustomAVP)_map.remove(new Integer(code));
	}
}

