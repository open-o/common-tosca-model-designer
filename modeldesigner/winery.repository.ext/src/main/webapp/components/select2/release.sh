#!/bin/bash
#
# Copyright 2016 [ZTE] and others.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

echo -n "Enter the version for this release: "

read ver

if [ ! $ver ]; then 
	echo "Invalid version."
	exit
fi

name="select2"
js="$name.js"
mini="$name.min.js"
css="$name.css"
release="$name-$ver"
tag="$ver"
branch="build-$ver"
curbranch=`git branch | grep "*" | sed "s/* //"`
timestamp=$(date)
tokens="s/@@ver@@/$ver/g;s/\@@timestamp@@/$timestamp/g"
remote="github"

echo "Pulling from origin"

git pull

echo "Updating Version Identifiers"

sed -E -e "s/\"version\": \"([0-9\.]+)\",/\"version\": \"$ver\",/g" -i "" bower.json select2.jquery.json
git add bower.json
git add select2.jquery.json
git commit -m "modified version identifiers in descriptors for release $ver"
git push
 
git branch "$branch"
git checkout "$branch"

echo "Tokenizing..."

find . -name "$js" | xargs -I{} sed -e "$tokens" -i "" {} 
find . -name "$css" | xargs -I{} sed -e "$tokens" -i "" {}
sed -e "s/latest/$ver/g" -i "" bower.json

git add "$js"
git add "$css"

echo "Minifying..."

echo "/*" > "$mini"
cat LICENSE | sed "$tokens" >> "$mini"
echo "*/" >> "$mini"

curl -s \
	--data-urlencode "js_code@$js" \
	http://marijnhaverbeke.nl/uglifyjs \
	>> "$mini"

git add "$mini"
	
git commit -m "release $ver"

echo "Tagging..."
git tag -a "$tag" -m "tagged version $ver"
git push "$remote" --tags

echo "Cleaning Up..."

git checkout "$curbranch"
git branch -D "$branch"

echo "Done"
