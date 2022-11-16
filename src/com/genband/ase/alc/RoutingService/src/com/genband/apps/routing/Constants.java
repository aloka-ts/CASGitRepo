package com.genband.apps.routing;

public interface Constants {


	public static final String ATTR_SBB = "SBB".intern();
	public static final String ATTR_ROUTING_DIRECTIVE = "ROUTING_DIRECTIVE".intern();
	public static final String ATTR_ROUTING_Disconnect_Handler = "ROUTING_Disconnect_Handler".intern();
	public static final String ATTR_ROUTING_Connect_Handler = "ROUTING_CONNECT_HANDLER".intern();
	public static final String ATTR_URIS = "URIS".intern();

	public static final String ATTR_OKAY = "OK".intern();
	public static final String ATTR_FAILED = "FAILED".intern();
	public static final String ROUTE_FAIL_REASON="ROUTE_FAIL_REASON".intern();
	public static final String ROUTE_STATUS="ROUTE_STATUS".intern();
	public static final String ROUTE_CALL_ID="ROUTE_CALL_ID".intern();
	

	public static final String ATTRIBUTE_DIALOG_STATE = "DIALOG_STATE";
    public static final int STATE_INITIAL=0;
    public static final int STATE_UNDEFINED=0;
    public static final int STATE_EARLY=1;
    public static final int STATE_CONFIRMED=2;
    public static final int STATE_TERMINATED=3;
}
