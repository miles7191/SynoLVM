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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Manager;
import com.t07m.synolvm.SurveillanceStationClient;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.view.monitors.ViewMonitor;

public class ViewManager extends Manager<SynoLVM, View>{

	private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);

	public ViewManager(SynoLVM app) {
		super(app, new View[0]);
	}

	public void init() {
		logger.debug("Initializing ViewManager");
		refreshConfigs();
	}

	public View[] getViews() {
		return getAllChildren();
	}
	
	public void refreshConfigs() {
		List<ViewConfig> viewConfigs = null;
		synchronized(getApp().getConfig()) {
			viewConfigs = new ArrayList<ViewConfig>(Arrays.asList(getApp().getConfig().getViewConfigurations()));
		}
		cleanupUnusedViewConfigs(viewConfigs);
		for(ViewConfig vc : viewConfigs) {
			addView(vc);
		}
		sortViews();
	}

	private void cleanupRemovedView(View view) {
		lockChildren();
		for(ViewMonitor vw : view.getViewWatchers()) {
			if(vw != null) {
				getApp().removeService(vw);
				view.getSurveillanceStationClient().stop();
			}
		}
		unlockChildren();
	}

	private void cleanupUnusedViewConfigs(List<ViewConfig> viewConfigs) {
		for(View v : getAllChildren()) {
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
				removeChild(v);
				cleanupRemovedView(v);
				logger.info("Unloaded View: " + v.getViewConfig().getName());
			}
		}
	}

	private void addView(ViewConfig vc) {
		for(View v : getAllChildren()) {
			if(v.getViewConfig().equals(vc)) {
				return;
			}else if(v.getViewConfig().getName().equalsIgnoreCase(vc.getName())) {
				logger.info("Attempted to load view with duplicate name! " + vc.getName() + " : " + v.getViewConfig().getName());
				return;
			}
		}
		addChild(new View(vc, new SurveillanceStationClient()));
		logger.info("Loaded View: " + vc.getName());
	}

	private void sortViews() {
		this.lockChildren();
		ArrayList<View> list = new ArrayList<View>(Arrays.asList(this.getAllChildren()));
		list.sort(new Comparator<View>() {
			public int compare(View o1, View o2) {
				return Integer.compare(o2.getViewConfig().getPriority(), o1.getViewConfig().getPriority());
			}
		});
		this.replaceChildren(list);
		this.unlockChildren();
	}

	public void cleanup() {
		this.lockChildren();
		View[] views = getAllChildren();
		clearChildren();
		this.unlockChildren();
		for(View v : views) {
			v.stop();
		}
	}

}
