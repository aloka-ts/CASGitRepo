package com.sas.util;

public class TestSASUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean sasStatus=false;
		String host = "10.32.8.252";		// update in application.properties also
		int  port=14000;
		String serviceName="atb";
		String serviceVersion="1.2";
		String resourceName="ro-ra";
		String resourceVersion="1.0";
		String servicePriority="1";
		String pathSAR="D:\\SAS\\Applications\\uas-war-1.0.0.sar";
		String pathSbbJAR="D:\\SAS\\SBB-Upgrade\\sbb-impl-4.0.jar";
		String pathResourceJAR="D:\\SAS\\Resources\\ro-ra.jar";

		SASUtils utils= new SASUtilsImpl();

		try {
			sasStatus = utils.statusSAS(host,port);
		} catch (SASUtilException e1) {			
			e1.printStackTrace();
		}

		if(sasStatus) {
			System.out.println("The status of SAS is " + sasStatus);			
			boolean result=false;
			try {
				utils.triggerActivityTest();
				//result=utils.deployAndActivateService(serviceName, serviceVersion, servicePriority, pathSAR);
				//result=utils.deployAndActivateResource(resourceName, resourceVersion, pathResourceJAR);
				System.out.println("Operation Result = " + result);	
			} catch (SASUtilException e) {
				System.err.println("Operation Failed !! " + result);
				e.printStackTrace();
			}	
		}
	}
}
