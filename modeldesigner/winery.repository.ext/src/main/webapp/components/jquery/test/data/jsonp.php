<?php
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
error_reporting(0);
$callback = $_REQUEST['callback'];
if ( ! $callback ) {
	$callback = explode("?",end(explode("/",$_SERVER['REQUEST_URI'])));
	$callback = $callback[0];
}
$json = $_REQUEST['json'];
if($json) {
	echo $callback . '([ {"name": "John", "age": 21}, {"name": "Peter", "age": 25 } ])';
} else {
	echo $callback . '({ "data": {"lang": "en", "length": 25} })';
}
?>
