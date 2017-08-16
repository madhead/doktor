package by.dev.madhead.doktor.config

import by.dev.madhead.doktor.Messages
import hudson.Extension
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest

@Extension(ordinal = -100.0)
class ConfluenceServers : GlobalConfiguration() {
	var servers: List<ConfluenceServer> = emptyList()
		set(value) {
			field = value
			save()
		}

	init {
		load()
	}

	override fun configure(req: StaplerRequest, json: JSONObject): Boolean {
		validate(json)
		req.bindJSON(this, json)

		return true
	}

	private fun validate(json: JSONObject) {
		// Single Confluence server, no validation needed
		if (json["servers"] is JSONObject) {
			return
		}
		if ((json["servers"] as JSONArray).groupBy { (it as JSONObject)["name"] }.any { it.value.size > 1 }) {
			throw FormException(Messages.doktor_config_ConfluenceServers_validation_name_dublicate(), "")
		}
	}
}
