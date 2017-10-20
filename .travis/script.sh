#!/usr/bin/env bash

set -x

if [[ ${TRAVIS_BRANCH} == 'master' ]] && [[ ${TRAVIS_PULL_REQUEST} == 'false' ]] &&  [[ $(git cat-file -p ${TRAVIS_COMMIT} | grep -o 'parent' | wc -l) -gt 1 ]]; then
	openssl aes-256-cbc -K ${encrypted_6ae40059dfe6_key} -iv ${encrypted_6ae40059dfe6_iv} -in .travis/github_deploy_key.enc -out ${HOME}/.ssh/id_rsa -d
	mv -f .travis/config ${HOME}/.ssh/config
	git checkout ${TRAVIS_BRANCH}

	./gradlew clean release -Prelease.useAutomaticVersion=true
else
	./gradlew clean assemble check
fi
