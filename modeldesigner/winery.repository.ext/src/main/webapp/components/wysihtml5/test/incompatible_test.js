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
module("wysihtml5 - Incompatible", {
  setup: function() {
    this.originalSupportCheck = wysihtml5.browser.supported;
    wysihtml5.browser.supported = function() { return false; };
    
    this.textareaElement = document.createElement("textarea");
    document.body.appendChild(this.textareaElement);
  },
  
  teardown: function() {
    wysihtml5.browser.supported = this.originalSupportCheck;
    this.textareaElement.parentNode.removeChild(this.textareaElement);
  }
});


asyncTest("Basic test", function() {
  expect(12);
  
  var that = this;
  
  var oldIframesLength = document.getElementsByTagName("iframe").length;
  
  var oldInputsLength = document.getElementsByTagName("input").length;
  
  var editor = new wysihtml5.Editor(this.textareaElement);
  editor.observe("load", function() {
    ok(true, "'load' event correctly triggered");
    ok(!wysihtml5.dom.hasClass(document.body, "wysihtml5-supported"), "<body> didn't receive the 'wysihtml5-supported' class");
    ok(!editor.isCompatible(), "isCompatible returns false when rich text editing is not correctly supported in the current browser");
    equal(that.textareaElement.style.display, "", "Textarea is visible");
    ok(!editor.composer, "Composer not initialized");
    
    equal(document.getElementsByTagName("iframe").length, oldIframesLength, "No hidden field has been inserted into the dom");
    equal(document.getElementsByTagName("input").length,  oldInputsLength,  "Composer not initialized");
    
    var html = "foobar<br>";
    editor.setValue(html);
    equal(that.textareaElement.value, html);
    equal(editor.getValue(), html);
    editor.clear();
    equal(that.textareaElement.value, "");
    
    editor.observe("focus", function() {
      ok(true, "Generic 'focus' event fired");
    });
    
    editor.observe("focus:textarea", function() {
      ok(true, "Specific 'focus:textarea' event fired");
    });
    
    editor.observe("focus:composer", function() {
      ok(false, "Specific 'focus:composer' event fired, and that's wrong, there shouldn't be a composer element/view");
    });
    
    QUnit.triggerEvent(that.textareaElement, wysihtml5.browser.supportsEvent("focusin") ? "focusin" : "focus");
    
    start();
  });
});