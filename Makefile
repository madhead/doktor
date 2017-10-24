
.DEFAULT: help

help:
	@echo "Available commands:\n\n\\tlint      Performs linting using ktlint and shellcheck if available.\n"

lint: $(which shellcheck) $(which ktlint)
	shellcheck gradlew
	ktlint -F .
