#!/usr/bin/env bash

set -x

openssl aes-256-cbc -K ${encrypted_6ae40059dfe6_key} -iv ${encrypted_6ae40059dfe6_iv} -in .travis/.jenkins-ci.org.enc -out ${HOME}/.jenkins-ci.org -d

./gradlew clean publish
