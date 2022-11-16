package com.baypackets.ase.security;

public interface TrustVerifier {
	
	public static final String TRUST_VERIFIER = "TrustVerifier";
	
	public boolean isTrusted(String nodeName);	
}