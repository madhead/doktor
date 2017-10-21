#!/usr/bin/env bash

set -x

SECRETS=$(mktemp -d)

openssl aes-256-cbc -K $encrypted_6ae40059dfe6_key -iv $encrypted_6ae40059dfe6_iv -in .travis/secrets.tar.enc -out ${SECRETS}/secrets.tar -d
tar xvf ${SECRETS}/secrets.tar -C ${SECRETS}
mv ${SECRETS}/.jenkins-ci.org ${HOME}/.jenkins-ci.org

./gradlew clean publish

curl \
	-X POST \
	--data-urlencode "payload={\"channel\": \"#petprojects\", \"username\": \"Travis\", \"text\": \"Doktor version ${TRAVIS_TAG} is available\!\", \"icon_emoji\": \":robot_face:\"}" \
	$SLACK_WEBHOOK_URL
