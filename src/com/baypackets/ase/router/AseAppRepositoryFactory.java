package com.baypackets.ase.router;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * 
 * Factory class for AR repository Only one instance of every repository is
 * maintained based on singleton pattern
 * 
 * @author averma
 * 
 */
public class AseAppRepositoryFactory {
	private static AseAppRepositoryProp aseAppRepositoryProp;

	private static AseAppRepositoryDB aseAppRepositoryDB;

	private static AseAppRepositoryXML aseAppRepositoryXML;

	private static final String DATABASE = "database";
	private static final String XML = "xml";

	private AseAppRepositoryFactory() {

	}

	public static synchronized AseAppRepository getAppRepository() {

		ConfigRepository cr = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);
		AseAppRepository aseAppRepository = null;

		if (cr != null) {
			String repositoryType = (String) cr
					.getValue(Constants.PROP_APP_ROUTER_REPO_TYPE);
			// if no repository defined or repository as properties is defined
			// than see
			// return repo based on properties file
			if (repositoryType.equalsIgnoreCase(XML)) {
				if (aseAppRepositoryXML == null) {
					aseAppRepositoryXML = new AseAppRepositoryXML();

				}
				aseAppRepository = aseAppRepositoryXML;
			} else if (repositoryType.equalsIgnoreCase(DATABASE)) {
				if (aseAppRepositoryDB == null) {
					aseAppRepositoryDB = new AseAppRepositoryDB();

				}
				aseAppRepository = aseAppRepositoryDB;
			} else {
				// default repository
				if (aseAppRepositoryProp == null) {
					aseAppRepositoryProp = new AseAppRepositoryProp();

				}
				aseAppRepository = aseAppRepositoryProp;
			}

		}

		return aseAppRepository;
	}
}