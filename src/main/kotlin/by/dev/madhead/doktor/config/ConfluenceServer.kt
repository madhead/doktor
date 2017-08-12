package by.dev.madhead.doktor.config

import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.security.ACL
import hudson.util.ListBoxModel
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundConstructor


class ConfluenceServer
@DataBoundConstructor
constructor(
	val name: String,
	val url: String,
	val credentials: String
) : AbstractDescribableImpl<ConfluenceServer>() {
	@Extension
	class ConfluenceServerDescriptor : Descriptor<ConfluenceServer>() {
		fun doFillCredentialsItems(): ListBoxModel {
			return StandardListBoxModel()
				.includeEmptyValue()
				.includeMatchingAs(ACL.SYSTEM, Jenkins.getInstance(), StandardUsernamePasswordCredentials::class.java, emptyList(), CredentialsMatchers.always())
		}
	}
}
