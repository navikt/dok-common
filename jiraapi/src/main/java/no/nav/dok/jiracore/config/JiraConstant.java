package no.nav.dok.jiracore.config;

import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.Sakskategori;

import java.util.function.Predicate;

public class JiraConstant {
	public static final String JIRA_PATH = "/rest/api/3/";
	public static final String PROJECT_PATH = JIRA_PATH + "project/";
	public static final String ISSUE_PATH = JIRA_PATH + "issue";
	public static final String ISSUE_TYPE_TASK = "Task";
	public static final String ISSUE_TYPE_INCIDENT = "13101";
	public static final String ATTACHMENTS = "/attachments";
	public static final String TRANSITIONS = "/transitions";
	public static final String PROJECT_KEY_TDH = "MMA";
	public static final String PROJECT_KEY_IKT = "IKT";
	public static final String TRANSITION_ID_KLAR_TIL_ARBEID = "121";
	public static final String BROWSE = "/browse/";

	public static final String OK_STATUS_CODE = "OK";
	public static final String NO_CONTENT_STATUS_CODE = "NO_CONTENT";

	public static final Predicate<IssueType> ISSUE_TYPE_MMA_OPPGAVE = issueType -> ISSUE_TYPE_TASK.equals(issueType.name());
	public static final Predicate<IssueType> ISSUE_TYPE_IKT_INCIDENT = issueType -> ISSUE_TYPE_INCIDENT.equals(issueType.id());

	public static final Sakskategori SAKSKATEGORI_TJENESTE_UTILGJENGELIG = new Sakskategori(Sakskategori.TJENESTE_UTILGJENGELIG_CMDB_KEY);
}
