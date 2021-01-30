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

import java.awt.Rectangle;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class WindowHandler {

	public static Window queryWindow(long pid) {
		for(DesktopWindow dw : WindowUtils.getAllWindows(true)) {
			HWND hWnd = dw.getHWND();
			IntByReference pidPointer = new IntByReference();
			User32.INSTANCE.GetWindowThreadProcessId(hWnd, pidPointer);
			if((int) pid == pidPointer.getValue()) {
				Rectangle window = dw.getLocAndSize();
				String title = dw.getTitle();
				return new Window(
						(int) (window.getX()), 
						(int) (window.getY()), 
						(int) (window.getX() + window.getWidth()), 
						(int) (window.getY() + window.getHeight()), 
						title);
			}
		}
		return null;
	}

	@ToString
	@RequiredArgsConstructor
	public static class Window {
		private final @Getter int x, y, x2, y2;
		private final @Getter String title;

		public Rectangle getRect() {
			return new Rectangle(getX(), getY(), getX2() - getX(), getY2() - getY());
		}

	}
}
