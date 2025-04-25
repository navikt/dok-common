package no.nav.dok.jiracore.interndomain;

public class AnsvarligTeam extends CustomField {
	public static final String ANSVARLIG_TEAM_CUSTOMFIELD_KEY = "customfield_20730";
	public static final String TEAM_DOKUMENTLOESNINGER_CMDB_KEY = "CMDB-725";
	public static final String YTELSESLINJEN_FAGPOST_CMDB_KEY = "CMDB-825";

	public AnsvarligTeam(String cmdbId) {
		super(ANSVARLIG_TEAM_CUSTOMFIELD_KEY, cmdbId);
	}

}
