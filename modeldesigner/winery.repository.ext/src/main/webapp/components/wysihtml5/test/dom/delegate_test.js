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
module("wysihtml5.dom.delegate", {
  setup: function() {
    this.container    = document.createElement("div");
    this.link1        = document.createElement("a");
    this.link2        = document.createElement("a");
    this.nestedSpan   = document.createElement("span");
    
    this.link2.appendChild(this.nestedSpan);
    this.container.appendChild(this.link1);
    this.container.appendChild(this.link2);
    
    document.body.appendChild(this.container);
  },
  
  teardown: function() {
    this.container.parentNode.removeChild(this.container);
  }
});

test("Basic test", function() {
  expect(3);
  
  var that = this;
  
  wysihtml5.dom.delegate(this.container, "a", "click", function(event) {
    ok(true, "Callback handler executed");
    equal(this, that.link1, "Callback handler executed in correct scope");
    ok(event.stopPropagation && event.preventDefault, "Parameter passed into callback handler is a proper event object");
  });
  
  QUnit.triggerEvent(this.link1, "click");
});

test("Click on nested element works as well", function() {
  expect(3);
  
  var that = this;
  
  wysihtml5.dom.delegate(this.container, "a", "click", function(event) {
    ok(true, "Callback handler executed");
    equal(this, that.link2, "Callback handler executed in correct scope");
    ok(event.stopPropagation && event.preventDefault, "Parameter passed into callback handler is a proper event object");
  });
  
  QUnit.triggerEvent(this.nestedSpan, "click");
});

test("Delegation on the body", function() {
  expect(1);
  
  var delegater = wysihtml5.dom.delegate(document.body, ".delegation-test", "mousedown", function() {
    ok(true, "Callback handler executed");
  });
  
  this.link1.className = "delegation-test another-class";
  
  QUnit.triggerEvent(this.link1, "mousedown");
  
  delegater.stop();
  
  QUnit.triggerEvent(this.link1, "mousedown");
});