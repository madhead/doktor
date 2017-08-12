package by.dev.madhead.doktor.config

import hudson.Extension
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest

@Extension(ordinal = -100.0)
class ConfluenceServers : GlobalConfiguration() {
	var servers: List<ConfluenceServer> = emptyList()

	init {
		load()
	}

	override fun configure(req: StaplerRequest, json: JSONObject): Boolean {
		servers = emptyList()
		req.bindJSON(this, json)

		return true
	}
}
