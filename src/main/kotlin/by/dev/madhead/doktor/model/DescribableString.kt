package by.dev.madhead.doktor.model

import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import org.kohsuke.stapler.DataBoundConstructor

class DescribableString
@DataBoundConstructor
constructor(
	val value: String
) : AbstractDescribableImpl<DescribableString>() {
	@Extension
	class DescribableStringDescriptor : Descriptor<DescribableString>()
}
