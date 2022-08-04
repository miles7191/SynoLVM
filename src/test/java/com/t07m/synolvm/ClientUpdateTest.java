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
package com.t07m.synolvm;

import org.junit.jupiter.api.Test;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synolvm.handlers.WindowHandler;
import com.t07m.synolvm.handlers.WindowHandler.Window;
import com.t07m.synolvm.system.ScreenReader;

class ClientUpdateTest {

	private static final Logger logger = LoggerFactory.getLogger(ClientUpdateTest.class);


	@Test
	void test() {
		
		//System.out.println(ScreenReader.findText("account or password is invalid").textLine());
		
		System.out.println(ScreenReader.readLine("Compatible"));
		
//		Window window = WindowHandler.queryWindow(17364);
//		if(window != null) {
//			Region r = Region.create(window.getRect());
//			for(String s : r.textLines()) {
//				System.out.println(s);
//			}
//			ImagePath.add("C:/Users/Matthew/Desktop");
//			try{
//				s.click("UpdateIndicator.png");
//				s.wait("spotlight-input.png");
//				s.click();
//				s.write("hello world#ENTER.");
//			}
//			catch(FindFailed e){
//				e.printStackTrace();
//			}
//		}
	}

}
