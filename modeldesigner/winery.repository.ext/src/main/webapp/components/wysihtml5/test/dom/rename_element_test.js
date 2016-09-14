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
module("wysihtml5.dom.renameElement", {
  equal: function(actual, expected, message) {
    return wysihtml5.assert.htmlEqual(actual, expected, message);
  },
  
  renameElement: function(html, newNodeName) {
    var container = wysihtml5.dom.getAsDom(html);
    wysihtml5.dom.renameElement(container.firstChild, newNodeName);
    return container.innerHTML;
  }
});

test("Basic tests", function() {
  this.equal(
    this.renameElement("<p>foo</p>", "div"),
    "<div>foo</div>"
  );
  
  this.equal(
    this.renameElement("<ul><li>foo</li></ul>", "ol"),
    "<ol><li>foo</li></ol>"
  );
  
  this.equal(
    this.renameElement('<p align="left" class="foo"></p>', "h2"),
    '<h2 align="left" class="foo"></h2>'
  );
});