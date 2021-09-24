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
package com.t07m.synolvm;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

class MapDisplayTest {

	@Test
	void test() throws InterruptedException {
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setSize(1, 1);
		frame.setUndecorated(true);
		frame.setTitle("123456789");
		frame.setVisible(true);
		frame.setLocation(1920, 0);
		
		short SWP_NOMOVE = 0X2;
        short SWP_NOSIZE = 1;
        short SWP_NOZORDER = 0X4;
        int SWP_SHOWWINDOW = 0x0040;
		
		for(DesktopWindow dw : WindowUtils.getAllWindows(true)) {
			if(dw.getTitle().equals("Synology Surveillance Station Client - Thiara03326")) {
				HWND hWnd = dw.getHWND();
				HMONITOR hMon = User32.INSTANCE.MonitorFromWindow(hWnd, WinUser.MONITOR_DEFAULTTOPRIMARY);
				//User32.INSTANCE.MoveWindow(hWnd, 3840, 0, 1920, 1080, false);
				User32.INSTANCE.SetWindowPos(hWnd, null, 0, 0, 1920, 1080, SWP_NOZORDER | SWP_SHOWWINDOW);
				MONITORINFOEX mInfo = new MONITORINFOEX();
				User32.INSTANCE.GetMonitorInfo(hMon, mInfo);
				
				System.out.println(mInfo.szDevice);
				return;
			}
		}
		
		Thread.sleep(60000);
	}

}
