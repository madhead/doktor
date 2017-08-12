package by.dev.madhead.doktor.config

import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import org.kohsuke.stapler.DataBoundConstructor


class ConfluenceServer
@DataBoundConstructor
constructor(
	val name: String
) : AbstractDescribableImpl<ConfluenceServer>() {
	@Extension
	class ConfluenceServerDescriptor : Descriptor<ConfluenceServer>()
}
