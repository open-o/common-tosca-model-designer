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
	# Support: Firefox
	header("X-Content-Security-Policy: default-src 'self';");

	# Support: Webkit, Safari 5
	# http://stackoverflow.com/questions/13663302/why-does-my-content-security-policy-work-everywhere-but-safari
	header("X-WebKit-CSP: script-src " . $_SERVER["HTTP_HOST"] . " 'self'");

	header("Content-Security-Policy: default-src 'self'");
?>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>CSP Test Page</title>
	<script src="../../jquery.js"></script>
	<script src="csp.js"></script>
</head>
<body>
	<p>CSP Test Page</p>
</body>
</html>
