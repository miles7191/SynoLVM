/*
 * Copyright (C) 2022 Matthew Rosato
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
package com.t07m.synolvm.view;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig.RegistryConfig;
import com.t07m.synolvm.handlers.ClientDownloadHandler;
import com.t07m.synolvm.handlers.RegistryHandler;
import com.t07m.synolvm.system.ScreenReader;
import com.t07m.synolvm.view.monitors.ViewMonitor;

import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ViewController extends Service<SynoLVM>{

	private SynoLVM app;

	public ViewController(SynoLVM app) {
		super(TimeUnit.SECONDS.toMillis(1));
		this.app = app;
	}

	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

	private RegistryConfig registryCache;
	private View lastLaunch = null;

	public void init() {

	}

	public void process() {
		View[] views = app.getViewManager().getViews();
		boolean foundInvalid = false;
		for(View view : views) {
			if(foundInvalid) {
				stopViewForPriorityView(view);
			}
			if(!foundInvalid){
				if(view.getSurveillanceStationClient().isRunning()) {
					foundInvalid = !validateView(view);
				}
				if(!view.getSurveillanceStationClient().isRunning() && view.getViewConfig().isEnabled() && view.getSurveillanceStationClient().displayAvailable(view.getViewConfig().getMonitor())) {
					if(lastLaunchCheck()) {
						cacheRegistry();
						foundInvalid = !launchView(view);
					}
				}
			}
		}
		if(registryCache != null && lastLaunch != null && !lastLaunch.withinGracePeriod()) {
			restoreRegistryCache();
		}
	}

	private boolean lastLaunchCheck() {
		if(lastLaunch != null) {
			if(lastLaunch.getSurveillanceStationClient().isRunning()) {
				if((lastLaunch.getWindowTitleWatcher() != null && !lastLaunch.getWindowTitleWatcher().validate()) ||
						(lastLaunch.getWindowLocationWatcher() != null && !lastLaunch.getWindowLocationWatcher().validate())) {
					if(ScreenReader.readLine("Loading") == null) {
						String compat = ScreenReader.readLine("Compatible version");
						if(compat != null) {
							for(String ver : ClientDownloadHandler.getAvailableVersions()) {
								if(compat.contains(ver)) {
									lastLaunch.getViewConfig().setClientVersion(ver);
									stopView(lastLaunch);
									try {
										app.getConfig().save();
									} catch (InvalidConfigurationException e) {}
								}
							}
						}else if(ScreenReader.readLine("The account or password is invalid") != null) {
							stopView(lastLaunch);
						}else if(ScreenReader.readLine("Unable to connect") != null) {
							stopView(lastLaunch);
						}else if(ScreenReader.readLine("Theservice is disabled now") != null) {
							stopView(lastLaunch);
						}
					}
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateView(View view) {
		if(!view.getViewConfig().isEnabled()) {
			logger.info("View No Longer Enabled. Killing View: " + view.getViewConfig().getName());
			stopView(view);
		}
		if(!view.isValid() && !view.withinGracePeriod()) {
			logger.info("View Invalidated. Killing View: " + view.getViewConfig().getName());
			stopView(view);
			if(view.getSurveillanceStationClient().displayAvailable(view.getViewConfig().getMonitor())) {
				return false;
			}
		}
		return true;
	}
	
	private void stopView(View view) {
		view.stop();
		for(ViewMonitor vm : view.getViewWatchers()) {
			app.removeService(vm);
		}
	}

	private void cacheRegistry() {
		if(registryCache == null) {
			registryCache = RegistryHandler.exportRegistry();
			logger.debug("Caching existing system registry.");
		}
	}

	private void restoreRegistryCache() {
		if(RegistryHandler.importRegistryToSystem(registryCache)) {
			logger.debug("Restored cached system registry.");
			registryCache = null;
			return;
		}
		logger.debug("Failed to restore cached system registry.");
	}

	private boolean launchView(View view) {
		logger.info("Launching View: " + view.getViewConfig().getName());
		if(view.launch(app)) {
			lastLaunch = view;
			for(ViewMonitor vw : view.getViewWatchers()) {
				app.registerService(vw);
			}
			return false;
		}
		return true;
	}

	private void stopViewForPriorityView(View view) {
		if(view.getSurveillanceStationClient().isRunning()) {
			logger.info("Killing View For Priority View: " + view.getViewConfig().getName());
			view.stop();
		}
	}

	public void cleanup() {
		if(registryCache != null) {
			if(RegistryHandler.importRegistryToSystem(registryCache)) {
				logger.debug("Restored cached system registry.");
				registryCache = null;
			}else {
				logger.debug("Failed to restore cached system registry.");
			}
		}
	}
}
