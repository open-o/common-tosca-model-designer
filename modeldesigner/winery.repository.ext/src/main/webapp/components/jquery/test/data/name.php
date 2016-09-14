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
$wait = $_REQUEST['wait'];
if($wait) {
	sleep($wait);
}
$xml = $_REQUEST['xml'];
if($xml) {
	header("Content-type: text/xml");
	$result = ($xml == "5-2") ? "3" : "?";
	echo "<math><calculation>$xml</calculation><result>$result</result></math>";
	die();
}
$name = $_REQUEST['name'];
if($name == 'foo') {
	echo "bar";
	die();
} else if($name == 'peter') {
	echo "pan";
	die();
}

echo 'ERROR <script type="text/javascript">ok( true, "name.php executed" );</script>';
?>