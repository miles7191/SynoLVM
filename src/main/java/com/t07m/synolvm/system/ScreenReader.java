/*
 * Copyright (C) 2022 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.synolvm.system;

import java.util.List;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenReader {

	private static final Logger logger = LoggerFactory.getLogger(ScreenReader.class);

	public static List<String> readScreen(int id){
		Screen s = new Screen();
		return s.textLines();
	}
	
	public static Match findLine(String text) {
		Screen s = new Screen();
		return s.findLine(text);
	}
	
	public static String readLine(String contains) {
		Match match = findLine(contains);
		if(match != null)
			return match.text();
		return null;
	}
	
}
