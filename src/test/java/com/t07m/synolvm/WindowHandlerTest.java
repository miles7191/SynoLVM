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

import java.awt.Rectangle;

import org.junit.jupiter.api.Test;

import com.t07m.synolvm.handlers.ScreenHandler;
import com.t07m.synolvm.handlers.WindowHandler;
import com.t07m.synolvm.handlers.ScreenHandler.Screen;
import com.t07m.synolvm.handlers.WindowHandler.Window;


class WindowHandlerTest {

	@Test
	void test() {
		Window w = WindowHandler.queryWindow(9936);
		System.out.println(w.getRect());
		Screen[] screens = ScreenHandler.queryScreens();
		System.out.println(screens[1].getScaledRect(false));
		assert(w != null);
	}

}
