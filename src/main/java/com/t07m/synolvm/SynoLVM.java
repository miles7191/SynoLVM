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

import com.t07m.application.Application;
import com.t07m.swing.console.ConsoleWindow;

public class SynoLVM extends Application{

	public static void main(String[] args) {
		new SynoLVM();
	}

	private ConsoleWindow console;

	public void init() {
		this.console = new ConsoleWindow("SynoLVM") {
			public void closeRequested() {
				stop();
			}
		};
		this.console.setup();
		this.console.setLocationRelativeTo(null);
	    this.console.setVisible(true);
	}
}
