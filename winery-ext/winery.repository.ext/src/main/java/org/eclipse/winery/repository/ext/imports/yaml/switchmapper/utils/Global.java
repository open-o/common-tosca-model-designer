/**
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.utils;

import org.eclipse.winery.model.tosca.TDocumentation;

public class Global {
	/**
	 * A counter for creating unique IDs.
	 */
	private static long uniqueID = 0;
	
	/**
	 * Adds a unique number to the prefix.
	 *
	 * @param prefix The prefix
	 * @return A unique String.
	 */
	public static String unique(String prefix) {
		long id = uniqueID++;
		if (id < 0) {
			// Negative IDs are not pretty.
			uniqueID = 0;
			id = uniqueID;
		}
		return prefix + id;
	}
}
