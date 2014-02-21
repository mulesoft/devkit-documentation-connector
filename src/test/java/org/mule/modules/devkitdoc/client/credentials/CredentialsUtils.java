/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client.credentials;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class CredentialsUtils {
	
	static final public String DEFAULT_CREDENTIALS_PATH = "credentials.properties";
	
	public Credentials readCredentials() throws IOException, IllegalArgumentException, IllegalAccessException  {
		return readCredentials(DEFAULT_CREDENTIALS_PATH);
	}
	
	public Credentials readCredentials(String credentialsPath) throws IOException, IllegalArgumentException, IllegalAccessException {
		
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(credentialsPath);
		prop.load(stream);
		
		Credentials c = new Credentials();
		
		Field[] declaredFields = Credentials.class.getDeclaredFields();
		
		for (int x = 0, xMax = declaredFields.length ; x < xMax ; x++ ) {
			Field field = declaredFields[x];
			String value = prop.getProperty(field.getName(), null);
			
			if (value != null) {
				boolean accessible = field.isAccessible();
				// If it's a private field we can: get declared getter method and use it, or modify the accessibility of the field and edit it
				field.setAccessible(true);
				field.set(c, value);
				field.setAccessible(accessible);
			}
		}
		
		return c;
	}
}
