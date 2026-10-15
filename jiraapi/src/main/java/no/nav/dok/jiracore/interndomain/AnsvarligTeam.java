package no.nav.dok.jiracore.interndomain;

public class AnsvarligTeam extends CustomField {
	public static final String ANSVARLIG_TEAM_CUSTOMFIELD_KEY = "customfield_20730";

	public AnsvarligTeam(String cmdbId) {
		super(ANSVARLIG_TEAM_CUSTOMFIELD_KEY, cmdbId);
	}

}
