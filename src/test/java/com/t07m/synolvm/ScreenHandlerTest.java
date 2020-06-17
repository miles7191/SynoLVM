/*
 * Copyright (C) 2020 Matthew Rosato
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

import java.io.File;

import org.junit.jupiter.api.Test;

import com.t07m.synolvm.process.ScreenHandler;
import com.t07m.synolvm.process.ScreenHandler.Screen;

class ScreenHandlerTest {

	@Test
	void test() {
		ScreenHandler sh = new ScreenHandler(new File("lib/QueryScreen.exe"));
		Screen[] screens = sh.queryScreens();
		for(Screen s : screens) {
			System.out.println(s.toString());
		}
		assert(screens.length > 0);
	}

}
