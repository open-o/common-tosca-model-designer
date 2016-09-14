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
module("wysihtml5.dom.resolveList", {
  equal: function(actual, expected, message) {
    return wysihtml5.assert.htmlEqual(actual, expected, message);
  },
  
  resolveList: function(html) {
    var container = wysihtml5.dom.getAsDom(html);
    document.body.appendChild(container);
    wysihtml5.dom.resolveList(container.firstChild);
    var innerHTML = container.innerHTML;
    container.parentNode.removeChild(container);
    return innerHTML;
  }
});

test("Basic tests", function() {
  this.equal(
    this.resolveList("<ul><li>foo</li></ul>"),
    "foo<br>"
  );
  
  this.equal(
    this.resolveList("<ul><li>foo</li><li>bar</li></ul>"),
    "foo<br>bar<br>"
  );
  
  this.equal(
    this.resolveList("<ol><li>foo</li><li>bar</li></ol>"),
    "foo<br>bar<br>"
  );
  
  this.equal(
    this.resolveList("<ol><li></li><li>bar</li></ol>"),
    "bar<br>"
  );
  
  this.equal(
    this.resolveList("<ol><li>foo<br></li><li>bar</li></ol>"),
    "foo<br>bar<br>"
  );
  
  this.equal(
    this.resolveList("<ul><li><h1>foo</h1></li><li><div>bar</div></li><li>baz</li></ul>"),
    "<h1>foo</h1><div>bar</div>baz<br>"
  );
});
