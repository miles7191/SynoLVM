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
package com.t07m.synolvm.startup;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synolvm.system.hardware.DisplayHandler;
import com.t07m.synolvm.system.hardware.DisplayHandler.Display;

public class ScreenCheck implements StartupCheck{

	private static final Logger logger = LoggerFactory.getLogger(ScreenCheck.class);

	private final int SecondsToReboot = (int) TimeUnit.MINUTES.toSeconds(3);

	public boolean check() {
		if(GraphicsEnvironment.isHeadless()) {
			return false;
		}
		logger.info("Checking system for available screens.");
		Display[] displays = DisplayHandler.queryDisplays();
		if(displays.length > 0) {
			for(int i = 0; i < displays.length; i++) {
				warnDisplayScale(displays, i);
			}
			return true;
		}
		logger.error("ScreenHandler did not find any screens!");
		return false;
	}

	private void warnDisplayScale(Display[] displays, int i) {
		Display display = displays[i];
		if(display.getScale() != 1.0) {
			logger.warn("Display scaling is set to " + (int) (display.getScale()*100) + "% for Screen: " + i);
		}
	}

	public void performCorrectiveAction() {
		logger.warn("System running in headless mode. Attempting to reboot the system in 30 seconds.");
		long start = System.currentTimeMillis();
		try {
			Runtime.getRuntime().exec("shutdown -r -t " + SecondsToReboot);
			Thread.sleep(1000);
		} catch (IOException | InterruptedException e) {}
		while(withinGracePeriod(start)) {
			if(!systemHeadless()) {
				CancelShutdown();
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		System.exit(0);
	}

	private boolean withinGracePeriod(long start) {
		return System.currentTimeMillis() - start < TimeUnit.SECONDS.toMillis(SecondsToReboot-1);
	}

	private void CancelShutdown() {
		try {
			logger.info("System is no longer headless. Attempting to cancel reboot.");
			Runtime.getRuntime().exec("shutdown -a");
		} catch (IOException e) {}
	}

	private boolean systemHeadless() {
		return GraphicsEnvironment.isHeadless() || DisplayHandler.queryDisplays().length == 0;
	}

}
