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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class ScreenHandler {

	private final IOExecutableHandler executable;
	
	public ScreenHandler(File queryScreenExecutable) {
		executable = new IOExecutableHandler(queryScreenExecutable);
	}
	
	public Screen[] queryScreens() {
		try {
			String[] ret = executable.execute(TimeUnit.SECONDS.toMillis(1));
			List<Screen> screens = new ArrayList<Screen>();
			for(String vars : ret) {
				String[] elements = vars.split(",");
				if(elements.length == 4) {
					try {
						screens.add(new Screen(
								Integer.parseInt(elements[0]),
								Integer.parseInt(elements[1]),
								Integer.parseInt(elements[2]),
								Integer.parseInt(elements[3])));
					}catch(NumberFormatException e) {}
				}
			}
			return screens.toArray(new Screen[screens.size()]);
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	@ToString
	@RequiredArgsConstructor
	public class Screen {
		  private static final int screenPadding = 20;
		  
		  private final @Getter int x, y, x2, y2;
		  
		  public Rectangle getRect(boolean padding) {
		    if (padding)
		      return new Rectangle(getX() - screenPadding, getY() - screenPadding, getX2() - getX() + screenPadding*2, getY2() - getY() + screenPadding*2); 
		    return new Rectangle(getX(), getY(), getX2() - getX(), getY2() - getY());
		  }
		}
}
