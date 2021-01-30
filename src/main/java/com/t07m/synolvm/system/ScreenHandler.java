/*
 * Copyright (C) 2021 Matthew Rosato
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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.util.ArrayList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class ScreenHandler {

	public static Screen[] queryScreens() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ArrayList<Screen> screens = new ArrayList<Screen>();
		try {
			for(GraphicsDevice device : env.getScreenDevices()) {
				Rectangle ret = device.getDefaultConfiguration().getBounds();
				screens.add(new Screen(
						(int) (ret.getX()),
						(int) (ret.getY()),
						(int) (ret.getX() + ret.getWidth()),
						(int) (ret.getY() + ret.getHeight())));
			}
		}catch (HeadlessException e) {}
		return screens.toArray(new Screen[screens.size()]);
	}

	@ToString
	@RequiredArgsConstructor
	public static class Screen {
		private static final int screenPadding = 20;

		private final @Getter int x, y, x2, y2;

		public Rectangle getRect(boolean padding) {
			if (padding)
				return new Rectangle(getX() - screenPadding, getY() - screenPadding, getX2() - getX() + screenPadding*2, getY2() - getY() + screenPadding*2); 
			return new Rectangle(getX(), getY(), getX2() - getX(), getY2() - getY());
		}
	}
}
