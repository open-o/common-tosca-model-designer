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
module("wysihtml5.dom.getAsDom", {
  teardown: function() {
    var iframe;
    while (iframe = document.querySelector("iframe.wysihtml5-sandbox")) {
      iframe.parentNode.removeChild(iframe);
    }
  }
});

test("Basic test", function() {
  var result;
  
  result = wysihtml5.dom.getAsDom('<span id="get-in-dom-element-test">foo</span>');
  equal(result.nodeName, "DIV");
  equal(result.ownerDocument, document);
  equal(result.firstChild.nodeName, "SPAN");
  equal(result.childNodes.length , 1);
  equal(result.firstChild.innerHTML, "foo");
  ok(!document.getElementById("get-in-dom-element-test"));
  
  result = wysihtml5.dom.getAsDom("<i>1</i> <b>2</b>");
  equal(result.childNodes.length, 3);
  
  result = wysihtml5.dom.getAsDom(document.createElement("div"));
  equal(result.innerHTML.toLowerCase(), "<div></div>");
});


test("HTML5 elements", function() {
  var result;
  
  result = wysihtml5.dom.getAsDom("<article><span>foo</span></article>");
  equal(result.firstChild.nodeName.toLowerCase(), "article");
  equal(result.firstChild.innerHTML.toLowerCase(), "<span>foo</span>");
  
  result = wysihtml5.dom.getAsDom("<output>foo</output>");
  equal(result.innerHTML.toLowerCase(), "<output>foo</output>");
});


asyncTest("Different document context", function() {
  expect(2);
  
  new wysihtml5.dom.Sandbox(function(sandbox) {
    var result;
    
    result = wysihtml5.dom.getAsDom("<div>hello</div>", sandbox.getDocument());
    equal(result.firstChild.ownerDocument, sandbox.getDocument());
    
    result = wysihtml5.dom.getAsDom("<header>hello</header>", sandbox.getDocument());
    equal(result.innerHTML.toLowerCase(), "<header>hello</header>");
    
    start();
  }).insertInto(document.body);
});