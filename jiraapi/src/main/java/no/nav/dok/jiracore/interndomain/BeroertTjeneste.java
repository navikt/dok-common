package no.nav.dok.jiracore.interndomain;

public class BeroertTjeneste extends CustomField {
	public static final String BEROERT_TJENESTE_CUSTOMFIELD_KEY = "customfield_20768";
	public static final String DOKUMENTLOESNINGER_CMDB_KEY = "CMDB-274838";

	public BeroertTjeneste(String cmdbId) {
		super(BEROERT_TJENESTE_CUSTOMFIELD_KEY, cmdbId);
	}

	public static BeroertTjeneste dokumentloesninger() {
		return new BeroertTjeneste(DOKUMENTLOESNINGER_CMDB_KEY);
	}
}
