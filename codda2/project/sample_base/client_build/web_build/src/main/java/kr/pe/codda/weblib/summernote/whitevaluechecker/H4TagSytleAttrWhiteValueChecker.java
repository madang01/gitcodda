/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;

public class H4TagSytleAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "h4";
	private final String attributeName = "style";
	
	
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			/*
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);
			return false;
			*/
			throw new WhiteParserException(errorMessage);
		}
		
		if ("".equals(attributeValue)) {
			return;
		}
		
		attributeValue = attributeValue.trim();

		String[] items = attributeValue.split(";");

		if (null == items || 0 == items.length) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] is a bad style becase the var items is null or empty").toString();
			throw new WhiteParserException(errorMessage);
		}

		// text-align: right;
		for (String item : items) {
			
			String[] itemSet = item.split(":");

			if (2 != itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				/*
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;
				*/
				throw new WhiteParserException(errorMessage);
			}

			String key = itemSet[0].trim();
			String value = itemSet[1].trim();
			
			
			// text-align: center;
			if ("text-align".equals(key)) {
				if ("left".equals(value) || "center".equals(value) || "right".equals(value)
						|| "justify".equals(value)) {
					continue;
				}
			}

			if ("margin-top".equals(key) || "margin-bottom".equals(key) || "margin-left".equals(key) || "margin-right".equals(key)) {
				int len = value.length();
				if (value.length() < 3) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase number of its character is less than minum 3").toString();
					throw new WhiteParserException(errorMessage);
				}
				
				String prefix = value.substring(0,  len - 2);
				String suffix = value.substring(len - 2);
				
				if (! "px".equals(suffix)) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase its suffix is not 'px'").toString();
					throw new WhiteParserException(errorMessage);
				}
 				
				try {
					Integer.parseInt(prefix);

					continue;
				} catch (NumberFormatException e) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase its prefix is not number").toString();
					throw new WhiteParserException(errorMessage);
				}
			}

			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] has a bad value[").append(attributeValue)
					.append("] becase the var itemSet of the var item[").append(item).append("] is a disallowed value")
					.toString();
			
			throw new WhiteParserException(errorMessage);
		}
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}
	
}
