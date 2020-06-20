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

import java.util.concurrent.TimeUnit;

import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.process.SurveillanceStationFactory.SurveillanceStationClient;
import com.t07m.synolvm.view.watcher.ScreenPixelWatcher;
import com.t07m.synolvm.view.watcher.WindowLocationWatcher;
import com.t07m.synolvm.view.watcher.WindowTitleWatcher;

import lombok.Getter;
import lombok.Setter;

public class View {
	
	private static final long PROCESS_GRACE_PERIOD = TimeUnit.SECONDS.toMillis(30);

	private @Getter @Setter ViewConfig viewConfig;
	private @Getter SurveillanceStationClient surveillanceStationClient;
	private @Getter boolean valid;
	
	private ScreenPixelWatcher screenPixelWatcher;
	private WindowLocationWatcher windowLocationWatcher;
	private WindowTitleWatcher windowTitleWatcher;
	
	public View(ViewConfig config, SurveillanceStationClient client) {
		this.viewConfig = config;
		this.surveillanceStationClient = client;
	}
	
	public boolean launch(SynoLVM lvm) {
		boolean completed = surveillanceStationClient.launch(TimeUnit.SECONDS.toMillis(10), viewConfig.getMonitor(), viewConfig.getRegistry());
		if(completed) {
			if(screenPixelWatcher == null)
				screenPixelWatcher = new ScreenPixelWatcher(lvm, this);
			if(windowLocationWatcher == null)
				windowLocationWatcher = new WindowLocationWatcher(lvm, this);
			if(windowTitleWatcher == null)
				windowTitleWatcher = new WindowTitleWatcher(lvm, this);
		}
		return valid = completed;
	}
	
	public void stop() {
		surveillanceStationClient.stop();
	}
	
	public ViewWatcher[] getViewWatchers() {
		return new ViewWatcher[] {screenPixelWatcher, windowLocationWatcher, windowTitleWatcher};
	}
	
	public boolean withinGracePeriod() {
		return surveillanceStationClient.getProcessRuntime() != -1 ? surveillanceStationClient.getProcessRuntime() < PROCESS_GRACE_PERIOD : false;
	}
	
	public void inValidate() {
		valid = false;
	}
	
}
