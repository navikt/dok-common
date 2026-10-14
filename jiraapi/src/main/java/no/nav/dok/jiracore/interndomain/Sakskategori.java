package no.nav.dok.jiracore.interndomain;

public class Sakskategori extends CustomField {
	public static final String SAKSKATEGORI_CUSTOMFIELD_KEY = "customfield_20813";
	public static final String TJENESTE_UTILGJENGELIG_CMDB_KEY = "CMDB-314138";

	public Sakskategori(String cmdbId) {
		super(SAKSKATEGORI_CUSTOMFIELD_KEY, cmdbId);
	}
}
