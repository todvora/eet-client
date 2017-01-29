#!/bin/bash
# See https://medium.com/@nthgergo/publishing-gh-pages-with-travis-ci-53a8270e87db
set -o errexit

# config
git config --global user.email "${GH_EMAIL}"
git config --global user.name "${GH_USERNAME}"

# build site
mvn site
echo "mvn site finished building documentation"
# get the current version from pom.xml
echo "reading current project version from pom.xml"
CURRENT_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[')
echo -e "Preparing gh-pages for version $CURRENT_VERSION"

# clone current github pages
echo "cloning gh-pages branch"
git clone -b gh-pages https://${GH_TOKEN}@${GH_REF} gh-pages

# prepare a directory for current version pages
echo "preparing directory gh-pages/$CURRENT_VERSION for documentation"
mkdir -p gh-pages/$CURRENT_VERSION

# remove any content of this version, if exists
echo "removing all content of gh-pages/$CURRENT_VERSION if exists"
rm -rf gh-pages/$CURRENT_VERSION/*  2> /dev/null

# copy generated maven site to a subdirectory
echo "copying all files from target/site/* to gh-pages/$CURRENT_VERSION/"
cp -r target/site/* gh-pages/$CURRENT_VERSION/

# disable jekyll build of this page
echo "touching gh-pages/.nojekyll to disable jekyll pages build"
touch gh-pages/.nojekyll

cd gh-pages

# remove any symlink to the latest version, if exists
rm -f latest 2> /dev/null
# create new symlink latest to this version
ln -s $CURRENT_VERSION latest

# create directory listening
# See https://little418.com/2015/04/directory-listings-on-github-pages.html
ls -d */ | perl -e 'print "<html><body><ul>"; while(<>) { chop $_; print "<li><a href=\"./$_\">$_</a></li>";} print "</ul></body></html>"' > index.html

git add -A .
git commit -m "Deploy $CURRENT_VERSION to Github Pages"
git push --quiet https://${GH_TOKEN}@${GH_REF} gh-pages:gh-pages > /dev/null 2>&1

