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
package com.t07m.synolvm.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig.RegistryConfig;
import com.t07m.synolvm.handlers.RegistryHandler;
import com.t07m.synolvm.handlers.WindowHandler.Window;
import com.t07m.synolvm.view.monitors.ViewMonitor;

public class ViewManager extends Service<SynoLVM>{

	private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);

	private ArrayList<View> views;

	private RegistryConfig registryCache;
	private View lastLaunch = null;

	public ViewManager(SynoLVM app) {
		super(app, TimeUnit.SECONDS.toMillis(1));
	}

	public void init() {
		views = new ArrayList<View>();
	}

	public void process() {
		synchronized(getApp().getConfig()) {
			List<ViewConfig> viewConfigs = new ArrayList<ViewConfig>(Arrays.asList(getApp().getConfig().getViewConfigurations()));
			synchronized(views) {
				Iterator<View> itr = views.iterator();
				cleanupUnusedViewConfigs(viewConfigs, itr);
				for(ViewConfig vc : viewConfigs) {
					addView(vc);
				}
			}
		}
		synchronized(views) {
			sortViews();
			boolean foundInvalid = false;
			for(View view : views) {
				if(foundInvalid) {
					stopViewForPriorityView(view);
				}
				if(!foundInvalid){
					if(view.getSurveillanceStationClient().isRunning()) {
						foundInvalid = !validateView(view);
					}
					if(!view.getSurveillanceStationClient().isRunning() && view.getViewConfig().isEnabled() && view.getSurveillanceStationClient().screenAvailable(view.getViewConfig().getMonitor())) {
						if(lastLaunchCheck()) {
							cacheRegistry();
							foundInvalid = !launchView(view);
						}
					}
				}
			}
		}
		if(registryCache != null && lastLaunch != null && !lastLaunch.withinGracePeriod()) {
			restoreRegistryCache();
		}
	}

	private void cleanupUnusedViewConfigs(List<ViewConfig> viewConfigs, Iterator<View> itr) {
		while(itr.hasNext()) {
			View v = itr.next();
			boolean found = false;
			for(ViewConfig vc : viewConfigs) {
				if(vc.getName().equalsIgnoreCase(v.getViewConfig().getName())) {
					if(!v.getViewConfig().equals(vc)) {
						v.setViewConfig(vc);
					}
					viewConfigs.remove(vc);
					found = true;
					break;
				}
			}
			if(!found) {
				itr.remove();
				cleanupRemovedView(v);
				logger.info("Unloaded View: " + v.getViewConfig().getName());
			}
		}
	}

	private void sortViews() {
		views.sort(new Comparator<View>() {
			public int compare(View o1, View o2) {
				return Integer.compare(o2.getViewConfig().getPriority(), o1.getViewConfig().getPriority());
			}
		});
	}

	private boolean lastLaunchCheck() {
		if(lastLaunch != null) {
			if(lastLaunch.getSurveillanceStationClient().isRunning() && lastLaunch.withinGracePeriod()) {
				if((lastLaunch.getWindowTitleWatcher() != null && !lastLaunch.getWindowTitleWatcher().validate()) ||
						(lastLaunch.getWindowLocationWatcher() != null && !lastLaunch.getWindowLocationWatcher().validate())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateView(View view) {
		if(!view.getViewConfig().isEnabled()) {
			logger.info("View No Longer Enabled. Killing View: " + view.getViewConfig().getName());
			view.stop();
		}
		if(!view.isValid() && !view.withinGracePeriod()) {
			logger.info("View Invalidated. Killing View: " + view.getViewConfig().getName());
			view.stop();
			if(view.getSurveillanceStationClient().screenAvailable(view.getViewConfig().getMonitor())) {
				return false;
			}
		}
		return true;
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
		if(view.launch(getApp())) {
			lastLaunch = view;
			for(ViewMonitor vw : view.getViewWatchers()) {
				if(vw != null) {
					getApp().registerService(vw);
				}
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

	private void addView(ViewConfig vc) {
		synchronized(views) {
			for(View v : views) {
				if(v.getViewConfig().equals(vc)) {
					return;
				}else if(v.getViewConfig().getName().equalsIgnoreCase(vc.getName())) {
					logger.info("Attempted to load view with duplicate name! " + vc.getName() + " : " + v.getViewConfig().getName());
					return;
				}
			}
			views.add(new View(vc, getApp().getSurveillanceStationFactory().newSurveillanceStationClient()));
			logger.info("Loaded View: " + vc.getName());
		}
	}

	private void cleanupRemovedView(View view) {
		synchronized(views) {
			for(ViewMonitor vw : view.getViewWatchers()) {
				if(vw != null) {
					getApp().removeService(vw);
					view.getSurveillanceStationClient().stop();
				}
			}
		}
	}

	public void cleanup() {
		synchronized(views) {
			Iterator<View> itr = views.iterator();
			while(itr.hasNext()) {
				View v = itr.next();
				itr.remove();
				v.stop();
			}
		}
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
