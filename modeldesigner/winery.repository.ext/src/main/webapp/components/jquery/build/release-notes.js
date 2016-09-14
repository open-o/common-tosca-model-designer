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
#!/usr/bin/env node
/*
 * jQuery Release Note Generator
 */

var fs = require("fs"),
	http = require("http"),
	extract = /<a href="\/ticket\/(\d+)" title="View ticket">(.*?)<[^"]+"component">\s*(\S+)/g,
	categories = [],
	version = process.argv[2];

if ( !/^\d+\.\d+/.test( version ) ) {
	console.error( "Invalid version number: " + version );
	process.exit( 1 );
}

http.request({
	host: "bugs.jquery.com",
	port: 80,
	method: "GET",
	path: "/query?status=closed&resolution=fixed&max=400&component=!web&order=component&milestone=" + version
}, function (res) {
	var data = [];

	res.on( "data", function( chunk ) {
		data.push( chunk );
	});

	res.on( "end", function() {
		var match,
			file = data.join(""),
			cur;

		while ( (match = extract.exec( file )) ) {
			if ( "#" + match[1] !== match[2] ) {
				var cat = match[3];

				if ( !cur || cur !== cat ) {
					if ( cur ) {
						console.log("</ul>");
					}
					cur = cat;
					console.log( "<h3>" + cat.charAt(0).toUpperCase() + cat.slice(1) + "</h3>" );
					console.log("<ul>");
				}

				console.log(
					"  <li><a href=\"http://bugs.jquery.com/ticket/" + match[1] + "\">#" +
					match[1] + ": " + match[2] + "</a></li>"
				);
			}
		}
		if ( cur ) {
			console.log("</ul>");
		}

	});
}).end();

