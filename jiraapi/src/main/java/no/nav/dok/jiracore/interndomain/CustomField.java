package no.nav.dok.jiracore.interndomain;

import java.util.List;
import java.util.Map;

public abstract class CustomField {
	public static final String CMDB_OBJECT_KEY_KEY = "key";
	protected final String customFieldKey;
	protected final String cmdbId;

	protected CustomField(String customFieldKey, String cmdbId) {
		this.customFieldKey = customFieldKey;
		this.cmdbId = cmdbId;
	}

	public final String getCustomFieldKey() {
		return customFieldKey;
	}

	public List<Map<String, String>> asRawInput() {
		return List.of(Map.of(CMDB_OBJECT_KEY_KEY, cmdbId));
	}
}
