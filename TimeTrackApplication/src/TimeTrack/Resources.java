package TimeTrack;

import java.util.ArrayList;

public class Resources {
	static ArrayList<ResourceItem> projects = new ArrayList<ResourceItem>();

	static {
		projects.add(new ResourceItem("ru", "Проекты"));
		projects.add(new ResourceItem("en", "Projects"));
	}

	public Resources() {
	};

	public static String getProjects(String p_lang) {
		String val = "";
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getLang().equals(p_lang)) {
				val = projects.get(i).getValue();
			}
		}
		return val;
	}
}

class ResourceItem {
	String lang;
	String value;

	public ResourceItem() {
	};

	public ResourceItem(String p_lang, String p_value) {
		this.lang = p_lang;
		this.value = p_value;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String p_lang) {
		this.lang = p_lang;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String p_value) {
		this.value = p_value;
	}
}