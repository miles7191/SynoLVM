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

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import com.t07m.synolvm.handlers.WindowHandler;
import com.t07m.synolvm.handlers.WindowHandler.Window;

public class Mouse {

	private static Robot robot = null;

	public static Point getMouseLocation() {
		try {
			return MouseInfo.getPointerInfo().getLocation();
		}catch(Exception e) {
			return new Point(0,0);
		}
	}

	public static void setPosition(int x, int y) {
		Robot r = getRobot();
		if(r!= null) {
			r.mouseMove(x, y);
		}
	}

	private static Robot getRobot() {
		if(robot == null) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
			return robot;
		}else {
			return robot;
		}
	}

}
