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
package com.t07m.synolvm.system.monitors;

import java.awt.Point;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.system.Mouse;

public class UserMonitor extends SystemMonitor{

	private static final Logger logger = LoggerFactory.getLogger(UserMonitor.class);
	
	private final long userGracePeriod = TimeUnit.MINUTES.toMillis(2);
	private long lastFound = 0;

	private int lastX, lastY;
	
	private Thread pauseThread;

	public UserMonitor(SynoLVM app) {
		super(app, TimeUnit.SECONDS.toMillis(5));
	}

	public void process() {
		Point p = Mouse.getMouseLocation();
		if(p.x != lastX || p.y != lastY) {
			lastFound = System.currentTimeMillis();
			lastX = p.x;
			lastY = p.y;
			if(pauseThread == null) {
				pauseThread = createNewPauseThread();
				pauseThread.start();
			}
		}
	}

	private Thread createNewPauseThread() {
		return new Thread() {
			public void run() {
				logger.info("User detected. Pausing application.");
				getApp().pauseExecution();
				while(userPresent()) {
					process();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
				logger.info("Resuming application.");
				Mouse.setPosition(0, 0);
				lastX = 0;
				lastY = 0;
				getApp().resumeExecution();
				pauseThread = null;
			}
		};
	}

	public boolean userPresent() {
		return System.currentTimeMillis() - lastFound < userGracePeriod;
	}

}
