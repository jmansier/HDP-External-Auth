package fakeAuth;

import java.util.HashMap;
import java.util.logging.Logger;

import com.ddtek.cloudservice.plugins.auth.javaplugin.JavaAuthPluginException;

public class fakeAuth implements com.ddtek.cloudservice.plugins.auth.javaplugin.JavaAuthPluginInterface {

	@Override
	public boolean authenticate(String arg0, String arg1, String arg2) throws JavaAuthPluginException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(HashMap<String, Object> arg0, Logger arg1) {
		// TODO Auto-generated method stub
		
	}

}
