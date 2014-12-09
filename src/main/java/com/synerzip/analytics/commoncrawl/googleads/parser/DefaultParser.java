/**
 * Copyright (C) 2004-2014 Synerzip. 
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.synerzip.analytics.commoncrawl.googleads.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * Parser to read the Google Ad JavaScript and infer configuration attributes
 * for the same.
 * 
 * @author Rohit Ghatol
 *
 */
public class DefaultParser implements GoogleAdParser {

	private static final Map<String, String> map = new HashMap<String, String>();
	private static final Logger LOG = Logger.getLogger(DefaultParser.class);
	private static final Pattern pattern = Pattern
			.compile("(?://.*)|(/\\*(?:.|[\\n\\r])*?\\*/)");

	/**
	 * Create a Parser from given googleAdScript
	 */
	public DefaultParser(String googleAdScript) {

		// FIXME - Instead of regular expression, lets us JavaScript Interpreter
		// and pass some predefined values for window.width, window.height etc

		LOG.debug("Parsing Advt");

		int googleAdScriptLength = googleAdScript.length();

		//FIXME - This is marginal better, fails at later stage but still fails
		/*String scriptCode = googleAdScript.substring(5,
				googleAdScriptLength - 5);
		
		Matcher match = pattern.matcher(scriptCode);
		StringBuilder sb = new StringBuilder();
		while(match.find()){
			sb.append(match.replaceAll(""));
		}
		String substr = sb.toString();*/
		
		//FIXME - temporary solution to find rougue data that was crashing MR job
		String substr="";
		try {
			substr = googleAdScript.substring(5, googleAdScriptLength - 5)
					.replaceAll("(?://.*)|(/\\*(?:.|[\\n\\r])*?\\*/)", "");
		} catch (StackOverflowError e) {
			LOG.error("StackOverflow** "+googleAdScript,e);
		//	e.printStackTrace();
		}

		StringTokenizer tokenizer = new StringTokenizer(substr, ";");
		while (tokenizer.hasMoreTokens()) {
			String subToken = tokenizer.nextToken().replaceAll("(\\r|\\n)", "")
					.replaceAll("\"", "");

			StringTokenizer subTokenizer = new StringTokenizer(subToken, "=");

			String key = null;
			String value = null;
			if (subTokenizer.hasMoreTokens()) {
				key = subTokenizer.nextToken().trim();
			}
			if (subTokenizer.hasMoreTokens()) {
				value = subTokenizer.nextToken().trim();
			}

			map.put(key, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synerzip.analytics.commoncrawl.googleads.parser.GoogleAdParser#
	 * getAttribute(java.lang.String)
	 */
	@Override
	public String getAttribute(String attribute) {

		return map.get(attribute);
	}

}
