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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.t07m.synolvm.config.LVMConfig.ViewConfig.Registry;
import com.t07m.synolvm.process.ScreenHandler.Screen;
import com.t07m.synolvm.process.WindowHandler.Window;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SurveillanceStationFactory {

	private final File surveillanceStation;
	private final RegistryHandler registryHandler;
	private final LaunchHandler launchHandler;
	private final ScreenHandler screenHandler;
	private final WindowHandler windowHandler;

	public SurveillanceStationClient newSurveillanceStationClient() {
		return new SurveillanceStationClient();
	}
	
	public class SurveillanceStationClient{

		private boolean running = false;
		private ProcessHandle process;
		private int monitor = Integer.MAX_VALUE;

		private Object processLock = new Object();

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
					return windowHandler.queryWindow(process.pid());
				}
				return null;
			}
		}
		
		public Screen getScreen() {
			Screen[] screens = screenHandler.queryScreens();
			if(screens != null && screens.length > monitor) {
				return screens[monitor];
			}
			return null;
		}

		public boolean isCorrectScreen() {
			Screen screen = getScreen();
			Window window = getWindow();
			if(screen != null && window != null) {
				return screen.getRect(true).contains(window.getRect());
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

		private boolean importRegistry(Registry registry) {
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("AutoBalance", registry.getAutoBalance());
			values.put("AutoLogin", registry.getAutoLogin());
			values.put("DisplayZoom", registry.getDisplayZoom());
			values.put("EnableGpuDecoder", registry.getEnableGpuDecoder());
			values.put("HideSvsIcon", registry.getHideSvsIcon());
			values.put("InstallerLang", registry.getInstallerLang());
			values.put("LoggingConfig", registry.getLoggingConfig());
			values.put("LoginHistory", registry.getLoginHistory());
			values.put("LoginLang", registry.getLoginLang());
			values.put("LowestResolution", registry.getLowestResolution());
			values.put("MaxGpuDecoderNum", registry.getMaxGpuDecoderNum());
			values.put("Name", registry.getName());
			values.put("ProxyAuthEnabled", registry.getProxyAuthEnabled());
			values.put("ProxyAuthPasswd", registry.getProxyAuthPasswd());
			values.put("ProxyAuthUserName", registry.getProxyAuthUserName());
			values.put("ProxyIP", registry.getProxyIP());
			values.put("ProxyPort", registry.getProxyPort());
			values.put("ProxyType", registry.getProxyType());
			values.put("RememberPassword", registry.getRememberPassword());
			values.put("SessionCookie", registry.getSessionCookie());
			values.put("ShowHwLabel", registry.getShowHwLabel());
			values.put("Upgrader", registry.getUpgrader());
			values.put("UseTestUpdateServer", registry.getUseTestUpdateServer());
			values.put("WinGeometry", registry.getWinGeometry());
			values.put("WinStates", registry.getWinStates());
			return registryHandler.setValues(values);
		}

		public boolean launch(long timeout, int monitor, Registry registry) {
			synchronized(processLock) {
				synchronized(registryHandler) {
					if(!running && importRegistry(registry)) {
						this.monitor = monitor;
						Screen screen = getScreen();
						if(screen != null) {
							registry.setWinGeometry(screen.getX()+","+screen.getY()+",1280,660");
							process = launchHandler.executeHandler(surveillanceStation);
							long start = System.currentTimeMillis();
							while(System.currentTimeMillis() - start < timeout && getWindow() == null) {
								try {
									Thread.sleep(Math.min(100, System.currentTimeMillis()-start));
								} catch (InterruptedException e) {}
							}
							if(getWindow() == null) {
								stop();
							}
							return running = process != null;
						}
					}
					return false;
				}
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

}
