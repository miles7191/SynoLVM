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
import com.t07m.synolvm.handlers.WindowHandler.Window;

public class ViewManager extends Service<SynoLVM>{

	private static Logger logger = LoggerFactory.getLogger(ViewManager.class);
	
	private ArrayList<View> views;

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
				for(ViewConfig vc : viewConfigs) {
					addView(vc);
				}
			}
		}
		synchronized(views) {
			views.sort(new Comparator<View>() {
				public int compare(View o1, View o2) {
					return Integer.compare(o2.getViewConfig().getPriority(), o1.getViewConfig().getPriority());
				}
			});
			boolean foundInvalid = false;
			for(View v : views) {
				if(foundInvalid) {
					if(v.getSurveillanceStationClient().isRunning()) {
						logger.info("Killing View For Priority View: " + v.getViewConfig().getName());
						v.stop();
					}
				}else {
					if(v.getSurveillanceStationClient().isRunning()) {
						if(!v.getViewConfig().isEnabled()) {
							logger.info("View No Longer Enabled. Killing View: " + v.getViewConfig().getName());
							v.stop();
						}
						if(!v.isValid() && !v.withinGracePeriod()) {
							logger.info("View Invalidated. Killing View: " + v.getViewConfig().getName());
							v.stop();
							if(v.getSurveillanceStationClient().screenAvailable(v.getViewConfig().getMonitor())) {
								foundInvalid = true;
							}
						}
					}else if(v.getViewConfig().isEnabled() && v.getSurveillanceStationClient().screenAvailable(v.getViewConfig().getMonitor())) {
						if(lastLaunch != null) {
							if(lastLaunch.getSurveillanceStationClient().isRunning() && lastLaunch.withinGracePeriod()) {
								Window w = lastLaunch.getSurveillanceStationClient().getWindow();
								if(w == null || (w != null && w.getTitle().equals("Synology Surveillance Station Client"))) {
									return;
								}
							}
						}
						logger.info("Launching View: " + v.getViewConfig().getName());
						if(v.launch(getApp())) {
							lastLaunch = v;
							for(ViewWatcher vw : v.getViewWatchers()) {
								if(vw != null) {
									getApp().registerService(vw);
								}
							}
							foundInvalid = true;
						}
					}
				}
			}
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
			for(ViewWatcher vw : view.getViewWatchers()) {
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
				cleanupRemovedView(v);
			}
		}
	}

}
