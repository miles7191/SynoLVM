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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.t07m.application.Service;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;

public class ViewManager extends Service<SynoLVM>{

	private ArrayList<View> views;

	public ViewManager(SynoLVM app) {
		super(app, TimeUnit.SECONDS.toMillis(1));
	}

	public void init() {
		views = new ArrayList<View>();
	}

	public void process() {
		synchronized(app.getConfig()) {
			List<ViewConfig> viewConfigs = new ArrayList<ViewConfig>(Arrays.asList(app.getConfig().getViewConfigurations()));
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
						app.getConsole().log("Unloaded View: " + v.getViewConfig().getName());
					}
				}
				for(ViewConfig vc : viewConfigs) {
					addView(vc);
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
					app.getConsole().log("Attempted to load view with duplicate name! " + vc.getName() + " : " + v.getViewConfig().getName());
					return;
				}
			}
			views.add(new View(vc, app.getSurveillanceStationFactory().newSurveillanceStationClient()));
			app.getConsole().log("Loaded View: " + vc.getName());
		}
	}
	
	private void cleanupRemovedView(View view) {
		synchronized(views) {
			for(ViewWatcher vw : view.getViewWatchers()) {
				if(vw != null) {
					app.removeService(vw);
				}
			}
		}
	}
}