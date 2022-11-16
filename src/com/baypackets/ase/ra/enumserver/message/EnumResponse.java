package com.baypackets.ase.ra.enumserver.message;

import org.xbill.DNS.RRset;

import com.baypackets.ase.resource.Response;

public interface EnumResponse extends Response {

	public String getData();

	public RRset[] getRecords();

}
