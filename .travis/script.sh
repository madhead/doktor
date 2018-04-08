#!/usr/bin/env bash

set -ex

if  [[ ${TRAVIS_REPO_SLUG} == 'jenkinsci/doktor-plugin' ]] && \
	[[ ${TRAVIS_BRANCH} == 'master' ]] && \
	[[ ${TRAVIS_PULL_REQUEST} == 'false' ]] && \
	[[ $(git cat-file -p ${TRAVIS_COMMIT} | grep -o 'parent' | wc -l) -gt 1 ]]
then
	SECRETS=$(mktemp -d)

	openssl aes-256-cbc -K $encrypted_6ae40059dfe6_key -iv $encrypted_6ae40059dfe6_iv -in .travis/secrets.tar.enc -out ${SECRETS}/secrets.tar -d
	tar xvf ${SECRETS}/secrets.tar -C ${SECRETS}
	mv ${SECRETS}/deploy_key ${HOME}/.ssh/id_rsa
	cp -f .travis/config ${HOME}/.ssh/config
	chmod 600 ${HOME}/.ssh/id_rsa

	git checkout ${TRAVIS_BRANCH}
	git remote set-url origin git@github.com:jenkinsci/doktor-plugin.git
	git config user.name "Travis"
	git config user.email "builds@travis-ci.org"

	./gradlew clean release -Prelease.useAutomaticVersion=true
else
	./gradlew clean assemble check jacocoTestReport
	bash <(curl -s https://codecov.io/bash)
fi
