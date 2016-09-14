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
var script      = process.argv[2],
    http        = require("http"),
    queryString = require("querystring"),
    fs          = require("fs");

if (!script) {
  throw "No script url given";
}

function post(code, callback) {
  // Build the post string from an object
  var postData = queryString.stringify({
    compilation_level:  "SIMPLE_OPTIMIZATIONS",
    output_format:      "text",
    output_info:        "compiled_code",
    warning_level:      "QUIET",
    js_code:            code
  });

  // An object of options to indicate where to post to
  var postOptions = {
    host: "closure-compiler.appspot.com",
    port: "80",
    path: "/compile",
    method: "POST",
    headers: {
      "Content-Type":   "application/x-www-form-urlencoded",
      "Content-Length": postData.length
    }
  };
  
  // Set up the request
  var request = http.request(postOptions, function(response) {
    var responseText = [];
    response.setEncoding("utf8");
    response.on("data", function(data) {
      responseText.push(data);
    });
    response.on("end", function() {
      callback(responseText.join(""));
    });
  });
  
  // Post the data
  request.write(postData);
  request.end();
}

function readFile(filePath, callback) {
  // This is an async file read
  fs.readFile(filePath, "utf-8", function (err, data) {
    if (err) {
      // If this were just a small part of the application, you would
      // want to handle this differently, maybe throwing an exception
      // for the caller to handle. Since the file is absolutely essential
      // to the program's functionality, we're going to exit with a fatal
      // error instead.
      console.log("FATAL An error occurred trying to read in the file: " + err);
      process.exit(-2);
    }
    // Make sure there's data before we post it
    if (data) {
      callback(data);
    } else {
      console.log("No data to post");
      process.exit(-1);
    }
  });
}

function writeFile(filePath, data, callback) {
  fs.writeFile(filePath, data, "utf-8", callback);
}


// Ok GO!
readFile(script, function(code) {
  post(code, function(code) {
    var output = script.replace(/\.js/, ".min.js");
    writeFile(output, code);
  });
});
