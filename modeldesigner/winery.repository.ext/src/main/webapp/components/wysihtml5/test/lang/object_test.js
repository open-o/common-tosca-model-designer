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
module("wysihtml5.lang.object");

test("merge()", function() {
  var obj         = { foo: 1, bar: 1 },
      returnValue = wysihtml5.lang.object(obj).merge({ bar: 2, baz: 3 }).get();
  equal(returnValue, obj);
  deepEqual(obj, { foo: 1, bar: 2, baz: 3 });
});

test("clone()", function() {
  var obj = { foo: true },
      returnValue = wysihtml5.lang.object(obj).clone();
  ok(obj != returnValue);
  deepEqual(obj, returnValue);
});

test("isArray()", function() {
  ok(wysihtml5.lang.object([]).isArray());
  ok(!wysihtml5.lang.object({}).isArray());
  ok(!wysihtml5.lang.object(document.body.childNodes).isArray());
  ok(!wysihtml5.lang.object("1,2,3").isArray());
});