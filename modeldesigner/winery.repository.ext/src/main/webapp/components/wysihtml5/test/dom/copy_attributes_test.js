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
module("wysihtml5.dom.copyAttributes", {
  setup: function() {
    this.div        = document.createElement("div");
    this.span       = document.createElement("span");
    this.anotherDiv = document.createElement("div");
    this.iframe     = document.createElement("iframe");
    
    this.iframe.src = "javascript:'<html></html>'";
    document.body.appendChild(this.iframe);
  },
  
  teardown: function() {
    this.iframe.parentNode.removeChild(this.iframe);
  }
});


test("Basic Tests", function() {
  var attributes = { title: "foobar", lang: "en", className: "foo bar" };
  wysihtml5.dom.setAttributes(attributes).on(this.div);
  wysihtml5.dom.copyAttributes(["title", "lang", "className"]).from(this.div).to(this.span);
  
  equal(this.span.title, attributes.title, "Title correctly copied");
  equal(this.span.lang, attributes.lang, "Lang correctly copied");
  equal(this.span.className, attributes.className, "Class correctly copied");
});


asyncTest("Test copying attributes from one element to another element which is in an iframe", function() {
  expect(1);
  
  var that = this;
  
  // Timeout needed to make sure that the iframe is ready
  setTimeout(function() {
    var iframeDocument = that.iframe.contentWindow.document,
        iframeElement = iframeDocument.createElement("div");
    
    iframeDocument.body.appendChild(iframeElement);
    that.span.title = "heya!";
    
    wysihtml5.dom
      .copyAttributes(["title"])
      .from(that.span)
      .to(iframeElement);
    
    equal(iframeElement.title, "heya!", "Element in iframe correctly got attributes copied over");
    
    start();
  }, 1000);
});