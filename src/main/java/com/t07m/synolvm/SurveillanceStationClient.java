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
package com.t07m.synolvm;

import java.awt.Rectangle;
import java.io.File;
import java.util.concurrent.TimeUnit;

import com.t07m.synolvm.config.LVMConfig.ViewConfig.RegistryConfig;
import com.t07m.synolvm.handlers.LaunchHandler;
import com.t07m.synolvm.handlers.RegistryHandler;
import com.t07m.synolvm.handlers.WindowHandler;
import com.t07m.synolvm.handlers.WindowHandler.Window;
import com.t07m.synolvm.system.hardware.DisplayHandler;
import com.t07m.synolvm.system.hardware.DisplayHandler.Display;

import lombok.Getter;

public class SurveillanceStationClient {

	private boolean running = false;
	private ProcessHandle process;
	private @Getter int monitor = Integer.MAX_VALUE;

	private final Object processLock = new Object();

	public Rectangle getBounds() {
		synchronized(processLock) {
			if(process != null) {
				Window w = getWindow();
				if(w != null)
					return w.getRect();
			}
			return null;
		}
	}

	public String getTitle() {
		synchronized(processLock) {
			if(process != null) {
				Window w = getWindow();
				if(w != null)
					return w.getTitle();
			}
			return null;
		}
	}

	public Window getWindow() {
		synchronized(processLock) {
			if(process != null) {
				return WindowHandler.queryWindow(process.pid());
			}
			return null;
		}
	}

	public Display getDisplay() {
		Display[] displays = DisplayHandler.queryDisplays();
		if(displays != null && displays.length > monitor) {
			return displays[monitor];
		}
		return null;
	}

	public boolean displayAvailable(int id) {
		Display[] displays = DisplayHandler.queryDisplays();
		if(displays != null && displays.length > id) {
			return displays[id] != null;
		}
		return false;
	}

	public boolean isCorrectDisplay() {
		Display display = getDisplay();
		Window window = getWindow();
		if(display != null && window != null) {
			return display.getScaledRect(true).contains(window.getRect());
		}
		return false;
	}

	public boolean isFullScreen() {
		Display display = getDisplay();
		Window window = getWindow();
		if(display != null && window != null) {
			Rectangle sr = display.getScaledRect(false);
			Rectangle wr = window.getRect();
			return sr.x == wr.x && sr.y == wr.y && sr.width == wr.width && sr.height == wr.height;
		}
		return false;
	}

	public long getProcessRuntime() {
		synchronized(processLock) {
			if(process != null && process.isAlive()) {
				return System.currentTimeMillis() - process.info().startInstant().get().toEpochMilli();
			}
		}
		return -1;
	}

	public boolean isRunning() {
		synchronized(processLock) {
			if(running) {
				if(process != null) {
					running = process.isAlive();
				}else{
					running = false;
				}
			}
			return running;
		}
	}

	public boolean launch(long timeout, int monitor, File executable, RegistryConfig registry) {
		synchronized(processLock) {
			if(!running) {
				synchronized(RegistryHandler.getRegistryLock()) {
					this.monitor = monitor;
					Display display = getDisplay();
					Rectangle displayRect = display.getScaledRect(false);
					if(display != null) {
						registry.setWinGeometry(((int) displayRect.getX())+","+((int) (displayRect.getY()+1))+",1280,720");
						if(RegistryHandler.importRegistryToSystem(registry)) {
							process = LaunchHandler.executeHandle(executable);
							long start = System.currentTimeMillis();
							while(System.currentTimeMillis() - start < timeout && getWindow() == null) {
								try {
									Thread.sleep(Math.min(100, System.currentTimeMillis()-start+timeout));
								} catch (InterruptedException e) {}
							}
							if(getWindow() == null) {
								stop();
							}
							return running = process != null;
						}
					}
				}
			}
			return false;
		}
	}

	public void stop() {
		synchronized(processLock) {
			if(process != null) {
				try {
					process.destroy();
					long start = System.currentTimeMillis();
					while(process.isAlive() && System.currentTimeMillis() - start < TimeUnit.SECONDS.toMillis(1));						
				}catch(IllegalStateException e) {
				}finally {
					if(process.isAlive())
						try {
							process.destroyForcibly();
						}catch(IllegalStateException e) {}
					process = null;
				}
				this.monitor = Integer.MAX_VALUE;
				this.running = false;
			}
		}

	}

}
