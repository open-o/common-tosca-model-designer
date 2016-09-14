/**
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
package org.eclipse.winery.repository;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Copyright 2016 openo <br>
 * <br>
 * Customized version of original ContainerClient by Markus to be used in a WAR
 * Frontend for winery.
 * 
 * @author Markus Fischer - donghu@raisecom.com
 * 
 */
public class CSARDeployClient {

	private static final XLogger logger = XLoggerFactory.getXLogger(CSARDeployClient.class);
	
	public static URI BASEURI;
	private Client client;

	// Singleton Pattern
	private static final CSARDeployClient INSTANCE = new CSARDeployClient();

	public static CSARDeployClient getInstance() {
		return CSARDeployClient.INSTANCE;
	}

	/**
	 * To decode a encoded URL back to original
	 * 
	 * @param encodedURL
	 *            as String
	 * @return decoded URL
	 */
	public static String URLdecode(String encodedURL) {

		try {
			return URLDecoder.decode(encodedURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	/**
	 * To Encode a URL
	 * 
	 * @param url
	 *            as String
	 * @return encoded URL
	 */
	public static String URLencode(String url) {

		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructor
	 */
	private CSARDeployClient() {

		ClientConfig config = new DefaultClientConfig();
		this.client = Client.create(config);
		this.client.setChunkedEncodingSize(1024);
	}
	
	/**
	 * Constructor
	 */
	public CSARDeployClient(String host, String port) {

		ClientConfig config = new DefaultClientConfig();
		this.client = Client.create(config);
		this.client.setChunkedEncodingSize(1024);

		// We assume that the OpenTOSC container is running on port 1337 on the
		// same machine as the GUI Backend.
		// TODO Move Container API port and path information to e.g. external
		// properties file, should not be fixed in the code.
		CSARDeployClient.BASEURI = UriBuilder.fromUri(
				"http://" + host + ":"+ port +"/api/nsoc/v1/").build();

	}


	public void destroy() {

		this.client.destroy();
	}


	/**
	 * Provides a WebResource to send requests to the ContainerBaseUri
	 * 
	 * @return WebResource
	 */
	private WebResource getBaseService() {

		return this.client.resource(CSARDeployClient.BASEURI);
	}

	/**
	 * Returns the String between to Quotes. Input: "anything", Output:anything
	 * 
	 * @param s
	 * @return String
	 */
	public String getBetweenQuotes(String s) {

		int first = s.indexOf("\"") + 1;
		int last = s.lastIndexOf("\"");
		return s.substring(first, last);
	}


	/**
	 * DebugMethod to print a Array of Strings
	 * 
	 * @param subStrings
	 */
	public void printStringArray(String[] subStrings) {

		for (String sub : subStrings) {
			CSARDeployClient.logger.trace(sub);
		}
	}

	public List<String> deployCSAR(String absoluteFilePath) {

		List<String> result = new ArrayList<String>();

		CSARDeployClient.logger.trace("Trying to upload ThorFile from: "
				+ absoluteFilePath);

		File file = new File(absoluteFilePath);

		if (!file.exists()) {
			CSARDeployClient.logger.trace("Error: file does not exist.");
			result.add("Error: file does not exist.");
			return result;
		}

		CSARDeployClient.logger.trace("Size of the file to upload: "
				+ file.getTotalSpace());

		ClientResponse resp = null;
		FormDataMultiPart multiPart = new FormDataMultiPart();

		FormDataContentDisposition.FormDataContentDispositionBuilder dispositionBuilder = FormDataContentDisposition
				.name("file");
		dispositionBuilder.fileName(file.getName());
		dispositionBuilder.size(file.getTotalSpace());

		FormDataContentDisposition formDataContentDisposition = dispositionBuilder
				.build();

		multiPart.bodyPart(new FormDataBodyPart("file", file,
				MediaType.APPLICATION_OCTET_STREAM_TYPE)
				.contentDisposition(formDataContentDisposition));

		resp = this.getBaseService().path("csars")
				.type(MediaType.MULTIPART_FORM_DATA_TYPE)
				.post(ClientResponse.class, multiPart);
		
		CSARDeployClient.logger.trace("ClientResponse string: "
				+ resp.getClientResponseStatus().toString());
		
		// when deploy finish,delete temp file
		file.delete();

		result.add(resp.getClientResponseStatus().toString());

		return result;
	}

	public List<String> uploadCSARDueURL(String urlToUpload) {
		
		CSARDeployClient.logger.trace("Try to send the URL to the ContainerAPI: " + CSARDeployClient.URLencode(urlToUpload));
		
		ArrayList<String> result = new ArrayList<String>();

		ClientResponse resp = this.getBaseService().path("csars").queryParam("url", urlToUpload)
				.post(ClientResponse.class);

		if (!resp.getClientResponseStatus().equals(Status.OK)) {
			CSARDeployClient.logger.trace("Error occurred while uploading CSAR from URL "
					+ urlToUpload + ", Server returned: "
					+ resp.getClientResponseStatus());
			result.add("Invocation Error, Server returned: "
					+ resp.getClientResponseStatus());
		} else {
			result.add("Created");
		}
		return result;
	}
}
