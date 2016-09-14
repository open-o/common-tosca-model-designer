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
// Runs a function many times without the function call overhead
function benchmark(fn, times, name){
	fn = fn.toString();
	var s = fn.indexOf('{')+1,
		e = fn.lastIndexOf('}');
	fn = fn.substring(s,e);

	return benchmarkString(fn, times, name);
}

function benchmarkString(fn, times, name) {
	var fn = new Function("i", "var t=new Date; while(i--) {" + fn + "}; return new Date - t")(times)
	fn.displayName = name || "benchmarked";
	return fn;
}
