#!/usr/bin/env bash

set -x

if [[ ${TRAVIS_BRANCH} == 'master' ]] && [[ ${TRAVIS_PULL_REQUEST} == 'false' ]] &&  [[ $(git cat-file -p ${TRAVIS_COMMIT} | grep -o 'parent' | wc -l) -gt 1 ]]; then
	SSH_FILE="$(mktemp -u $HOME/.ssh/id_rsa)"

	openssl aes-256-cbc -K ${encrypted_6ae40059dfe6_key} -iv ${encrypted_6ae40059dfe6_iv} -in .travis/github_deploy_key.enc -out ${SSH_FILE} -d
	chmod 600 ${SSH_FILE}
	cat <<- EOF >> ${HOME}/.ssh/config
		Host github.com
			IdentityFile ${SSH_FILE}
	EOF

	./gradlew clean release
else
	./gradlew clean assemble check
fi
