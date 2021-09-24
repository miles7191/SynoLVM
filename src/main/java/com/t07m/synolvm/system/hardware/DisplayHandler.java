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
package com.t07m.synolvm.system.hardware;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.util.ArrayList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class DisplayHandler {

	public static Display[] queryDisplays() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ArrayList<Display> screens = new ArrayList<Display>();
		try {
			int i = 0;
			for(GraphicsDevice device : env.getScreenDevices()) {
				Rectangle ret = device.getDefaultConfiguration().getBounds();
				double scale = device.getDefaultConfiguration().getDefaultTransform().getScaleX();
				screens.add(new Display(
						i,
						(int) (ret.getX()),
						(int) (ret.getY()),
						(int) (ret.getX() + ret.getWidth()),
						(int) (ret.getY() + ret.getHeight()),
						scale));
				i++;
			}
		}catch (HeadlessException e) {}
		return screens.toArray(new Display[screens.size()]);
	}

	@ToString
	@RequiredArgsConstructor
	public static class Display{

		private static final int sp = 20;

		private final @Getter int number;
		private final @Getter int x, y, x2, y2;
		private final @Getter double scale;

		public Rectangle getRect(boolean padding) {
			if (padding)
				return new Rectangle(x-sp, y-sp, x2-x+sp*2, y2-y+sp*2); 
			return new Rectangle(x, y, x2-x, y2-y);
		}

		public Rectangle getScaledRect(boolean padding) {
			int sx = (int) Math.ceil(x*scale);
			int sy = (int) Math.ceil(y*scale);
			int sx2 = (int) Math.ceil(x2*scale);
			int sy2 = (int) Math.ceil(y2*scale);
			if (padding)
				return new Rectangle(sx-sp, sy-sp, sx2-sx+sp*2, sy2-sy+sp*2); 
			return new Rectangle(sx, sy, sx2-sx, sy2-sy);
		}

		public Dimension getResolution() {
			return new Dimension((int)((x2-x) * scale), (int)((y2-y) * scale));
		}

	}

}
