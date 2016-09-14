/*
 * Copyright 2016 [ZTE] and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
module("wysihtml5.lang.string");

test("trim()", function() {
  equal(wysihtml5.lang.string("   foo   \n").trim(), "foo");
});

test("interpolate()", function() {
  equal(
    wysihtml5.lang.string("Hello #{name}, I LOVE YOUR NAME. IT'S VERY GERMAN AND SOUNDS STRONG.").interpolate({ name: "Reinhold" }),
    "Hello Reinhold, I LOVE YOUR NAME. IT'S VERY GERMAN AND SOUNDS STRONG."
  );
});

test("replace()", function() {
  equal(
    wysihtml5.lang.string("I LOVE CAKE").replace("CAKE").by("BOOBS"),
    "I LOVE BOOBS"
  );
});