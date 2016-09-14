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
// Simple script loader that uses jQuery.readyWait via jQuery.holdReady()

//Hold on jQuery!
jQuery.holdReady(true);

var readyRegExp = /^(complete|loaded)$/;

function assetLoaded( evt ){
	var node = evt.currentTarget || evt.srcElement;
	if ( evt.type === "load" || readyRegExp.test(node.readyState) ) {
		jQuery.holdReady(false);
	}
}

setTimeout( function() {
	var script = document.createElement("script");
	script.type = "text/javascript";
	if ( script.addEventListener ) {
		script.addEventListener( "load", assetLoaded, false );
	} else {
		script.attachEvent( "onreadystatechange", assetLoaded );
	}
	script.src = "data/readywaitasset.js";
	document.getElementsByTagName("head")[0].appendChild(script);
}, 2000 );
