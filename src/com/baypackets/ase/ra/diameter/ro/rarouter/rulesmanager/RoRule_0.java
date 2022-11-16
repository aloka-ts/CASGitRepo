import com.baypackets.ase.ra.diameter.ro.rarouter.rulesmanager.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RoRule_0  {
	
	
	private static void main(String[] args){
		
		String params[] ={"cas7702.agnity.com","seagullRealm"};
		
		boolean value=_evaluate(params,null);
		
		System.out.println(" evaluate ....."+value);
		
		}
	
	public static boolean _evaluate(String param[], ArrayList list) {
		int k = 0;
		return ((((param[RequestHelper.request_orig_host] != null) && param[RequestHelper.request_orig_host]
				.equals("cas7702.agnity.com"))
				|| ((param[RequestHelper.request_orig_realm] != null) && param[RequestHelper.request_orig_realm]
						.equals("seagullRealm")) || (false))
				|| (((param[RequestHelper.request_orig_host] != null) && param[RequestHelper.request_orig_host]
						.equals("cas7802.agnity.com"))
						|| ((param[RequestHelper.request_orig_realm] != null) && param[RequestHelper.request_orig_realm]
								.equals("seagullRealm")) || (false)) || (false));
	}
}
