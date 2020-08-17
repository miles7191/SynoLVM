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
package com.t07m.synolvm.view.watcher;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.process.ScreenHandler.Screen;
import com.t07m.synolvm.view.View;
import com.t07m.synolvm.view.ViewWatcher;

public class ScreenPixelWatcher extends ViewWatcher {

	private BufferedImage lastPass = null;

	public ScreenPixelWatcher(SynoLVM app, View view) {
		super(app, TimeUnit.SECONDS.toMillis(30), view);
	}

	public void process() {
		if(!getView().withinGracePeriod()) {
			synchronized(getView().getSurveillanceStationClient()) {
				Screen screen = getView().getSurveillanceStationClient().getScreen();
				if(screen != null) {
					try {
						Robot robot = new Robot();
						if(lastPass == null) {
							lastPass = robot.createScreenCapture(screen.getRect(false));
						}else {
							BufferedImage current = robot.createScreenCapture(screen.getRect(false));
							if (lastPass.getWidth() == current.getWidth() && lastPass.getHeight() == current.getHeight()) {
								for (int x = 0; x < lastPass.getWidth(); x++) {
									for (int y = 0; y < lastPass.getHeight(); y++) {
										if (lastPass.getRGB(x, y) != current.getRGB(x, y)) {
											lastPass = current;
											return; 
										}
									} 
								}
							}
							lastPass = null;
							getView().inValidate();
							app.getConsole().getLogger().info("View failed ScreenPixelWatcher: " + getView().getViewConfig().getName());
							
						}
					} catch (AWTException e) {}
				}
			}
		}
	}
}
