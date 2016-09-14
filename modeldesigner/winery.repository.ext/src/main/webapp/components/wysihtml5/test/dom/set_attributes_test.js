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
module("wysihtml5.dom.setAttributes", {
  setup: function() {
    this.element = document.createElement("div");
  }
});

test("Basic test", function() {
  wysihtml5.dom.setAttributes({
    id:       "foo",
    "class":  "bar"
  }).on(this.element);
  
  equal(this.element.id, "foo");
  equal(this.element.className, "bar");
});