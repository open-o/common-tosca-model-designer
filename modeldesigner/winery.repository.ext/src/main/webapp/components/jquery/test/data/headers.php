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

header( "Sample-Header: Hello World" );
header( "Empty-Header: " );
header( "Sample-Header2: Hello World 2" );

$headers = array();

foreach( $_SERVER as $key => $value ) {

	$key = str_replace( "_" , "-" , substr( $key , 0 , 5 ) == "HTTP_" ? substr( $key , 5 ) : $key );
	$headers[ $key ] = $value;

}

foreach( explode( "_" , $_GET[ "keys" ] ) as $key ) {
	echo "$key: " . @$headers[ strtoupper( $key ) ] . "\n";
}
