#!/bin/bash
# See https://medium.com/@nthgergo/publishing-gh-pages-with-travis-ci-53a8270e87db
set -o errexit

# config
git config --global user.email "${GH_EMAIL}"
git config --global user.name "${GH_USERNAME}"

# build site
mvn site

# deploy
cd target/site
git init
git add .
git commit -m "Deploy to Github Pages"
git push --force --quiet https://${GH_TOKEN}@${GH_REF} master:gh-pages > /dev/null 2>&1

