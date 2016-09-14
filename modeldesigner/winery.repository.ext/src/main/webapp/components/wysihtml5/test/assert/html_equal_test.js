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
module("wysihtml5.assert.htmlEqual");

test("Basic tests", function() {
  wysihtml5.assert.htmlEqual("<span>foo</span>", "<span>foo</span>");
  wysihtml5.assert.htmlEqual("<SPAN>foo</SPAN>", "<span>foo</span>");
  wysihtml5.assert.htmlEqual("<IMG SRC=foo.gif>", '<img src="foo.gif">');
  
  var container = document.createElement("div"),
      image     = document.createElement("img");
  image.setAttribute("alt", "foo");
  image.setAttribute("border", 0);
  image.setAttribute("src", "foo.gif");
  image.setAttribute("width", 25);
  image.setAttribute("height", 25);
  container.appendChild(image);
  
  wysihtml5.assert.htmlEqual(container.innerHTML, '<img alt="foo" border="0" src="foo.gif" width="25" height="25">');
  
  var inlineElement = document.createElement("span");
  inlineElement.innerHTML = "<p>foo</p>";
  container.innerHTML = "";
  container.appendChild(inlineElement);
  wysihtml5.assert.htmlEqual(container.innerHTML, '<span><p>foo</p></span>');
  
  wysihtml5.assert.htmlEqual("<p>foo     bar</p>", '<p>foo bar</p>', "", {
    normalizeWhiteSpace: true
  });
  
  wysihtml5.assert.htmlEqual("<div><pre><p>foo  bar</p></pre></div>", '<div><pre><p>foo  bar</p></pre></div>', "", {
    normalizeWhiteSpace: true
  });
});