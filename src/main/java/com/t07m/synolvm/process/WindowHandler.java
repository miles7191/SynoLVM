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
package com.t07m.synolvm.process;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class WindowHandler {

	private final IOExecutableHandler executable;
	
	public WindowHandler(File queryWindowExecutable) {
		executable = new IOExecutableHandler(queryWindowExecutable);
	}
	
	public Window queryWindow(long pid) {
		try {
			String[] ret = executable.execute(TimeUnit.SECONDS.toMillis(1), Long.toString(pid));
			if(ret != null && ret.length == 1) {
				String[] elements = ret[0].split(",");
				if(elements.length >= 5) {
					try {
						return new Window(
								Integer.parseInt(elements[0]),
								Integer.parseInt(elements[1]),
								Integer.parseInt(elements[2]),
								Integer.parseInt(elements[3]),
								String.join(",",Arrays.copyOfRange(elements, 4, elements.length)));
						}catch(NumberFormatException e) {}
				}
			}
		} catch (IOException | InterruptedException e) {}
		
		return null;
	}
	
	@ToString
	@RequiredArgsConstructor
	public class Window {
		private final @Getter int x, y, x2, y2;
		private final @Getter String title;

		public Rectangle getRect() {
			return new Rectangle(getX(), getY(), getX2() - getX(), getY2() - getY());
		}
		
	}
}