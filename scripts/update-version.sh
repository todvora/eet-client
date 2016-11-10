#!/bin/bash
set -o errexit

# https://blog.codecentric.de/en/2015/04/increment-versions-maven-build-helper-versions-plugin/

if [ $1 = "major" ]; then
    mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.nextMajorVersion}.0.0 versions:commit
elif [ $1 = "minor" ]; then
  mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0 versions:commit
elif [ $1 = "patch" ]; then
  mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit
else
    echo "Unknown component increment: $1"
    exit 1
fi

NEW_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[')

TMPFILE=$(mktemp /tmp/notes.XXXXXX)
RELEASE_NOTES=$(git log `git describe --tags --abbrev=0`..HEAD --format="- %s (%an)")
echo -e "Release $NEW_VERSION\n\n$RELEASE_NOTES" > $TMPFILE
"${EDITOR:-nano}" $TMPFILE
RELEASE_NOTES=$(cat $TMPFILE)
rm $TMPFILE

git add pom.xml

git commit -m "Release $NEW_VERSION"
git tag -fa $NEW_VERSION -m "$RELEASE_NOTES"

git push --follow-tags
