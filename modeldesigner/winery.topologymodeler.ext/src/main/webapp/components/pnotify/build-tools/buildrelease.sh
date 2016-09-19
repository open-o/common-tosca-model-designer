#!/bin/sh
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


DIR=$(dirname $0)

echo Setting up directory structure.
mkdir "${DIR}/pnotify"
mkdir "${DIR}/pnotify/use for picon"
echo Importing files.
cp "${DIR}/../jquery.pnotify.default.icons.css" "${DIR}/pnotify/use for picon/"
cp "${DIR}/../jquery.pnotify.default.css" "${DIR}/pnotify/"
cp "${DIR}/../jquery.pnotify.js" "${DIR}/pnotify/"

echo Compressing JavaScript with Google Closure Compiler.
head -n 11 "${DIR}/pnotify/jquery.pnotify.js" > "${DIR}/pnotify/jquery.pnotify.min.js"
java -jar "${DIR}/compiler.jar" --js="${DIR}/pnotify/jquery.pnotify.js" >> "${DIR}/pnotify/jquery.pnotify.min.js"

CURDIR=$(pwd)
cd "${DIR}/"

echo Zipping the whole directory.
zip -r pnotify.zip pnotify

echo Cleaning up.
rm -r pnotify/

cd "${CURDIR}/"

echo Done.