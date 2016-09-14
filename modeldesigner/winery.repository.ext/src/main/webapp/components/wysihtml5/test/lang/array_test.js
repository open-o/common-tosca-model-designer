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
module("wysihtml5.lang.array");

test("contains()", function() {
  var arr = [1, "2", "foo"];
  ok(wysihtml5.lang.array(arr).contains(1));
  ok(!wysihtml5.lang.array(arr).contains(2));
  ok(wysihtml5.lang.array(arr).contains("2"));
  ok(wysihtml5.lang.array(arr).contains("foo"));
});

test("without()", function() {
  var arr = [1, 2, 3];
  deepEqual(wysihtml5.lang.array(arr).without([1]), [2, 3]);
  deepEqual(wysihtml5.lang.array(arr).without([4]), [1, 2, 3]);
});

test("get()", function() {
  var nodeList = document.getElementsByTagName("*"),
      arr      = wysihtml5.lang.array(nodeList).get();
  equal(arr.length, nodeList.length);
  ok(arr instanceof Array);
});