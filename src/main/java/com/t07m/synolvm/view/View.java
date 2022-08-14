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

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.t07m.synolvm.SurveillanceStationClient;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.handlers.ClientApplicationHandler;
import com.t07m.synolvm.handlers.ClientApplicationHandler.ClientApplication;
import com.t07m.synolvm.view.monitors.ScreenPixelMonitor;
import com.t07m.synolvm.view.monitors.ViewMonitor;
import com.t07m.synolvm.view.monitors.WindowLocationMonitor;
import com.t07m.synolvm.view.monitors.WindowTitleMonitor;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class View {

	private static final long PROCESS_GRACE_PERIOD = TimeUnit.SECONDS.toMillis(60);

	private @Getter @Setter ViewConfig viewConfig;
	private @Getter SurveillanceStationClient surveillanceStationClient;
	private @Getter boolean valid;

	private @Getter ScreenPixelMonitor screenPixelWatcher;
	private @Getter WindowLocationMonitor windowLocationWatcher;
	private @Getter WindowTitleMonitor windowTitleWatcher;

	public View(ViewConfig config, SurveillanceStationClient client) {
		this.viewConfig = config;
		this.surveillanceStationClient = client;
	}

	public boolean launch(SynoLVM lvm) {
		ClientApplication app = null;
		if(viewConfig.getClientVersion() == null || viewConfig.getClientVersion().equals("")) {
			app = ClientApplicationHandler.getLatestClient();
		}else {
			app = ClientApplicationHandler.getClient(viewConfig.getClientVersion());
		}
		boolean completed = false;
		if(app != null) {
			completed = surveillanceStationClient.launch(
					TimeUnit.SECONDS.toMillis(10), 
					viewConfig.getMonitor(), 
					new File(app.getDirectory() + File.separator + app.getExecutable()), 
					viewConfig.getRegistry());
			if(completed) {
				if(screenPixelWatcher == null)
					screenPixelWatcher = new ScreenPixelMonitor(lvm, this);
				if(windowLocationWatcher == null)
					windowLocationWatcher = new WindowLocationMonitor(lvm, this);
				if(windowTitleWatcher == null)
					windowTitleWatcher = new WindowTitleMonitor(lvm, this);
				if(viewConfig.getClientVersion() == null || viewConfig.getClientVersion().equals("")) {
					viewConfig.setClientVersion(app.getVersion());
					try {
						lvm.getConfig().save();
					} catch (InvalidConfigurationException e) {}
				}
			}
		}
		return valid = completed;
	}

	public void stop() {
		surveillanceStationClient.stop();
	}

	public ViewMonitor[] getViewWatchers() {
		return new ViewMonitor[] {screenPixelWatcher, windowLocationWatcher, windowTitleWatcher};
	}

	public boolean withinGracePeriod() {
		return surveillanceStationClient.getProcessRuntime() < PROCESS_GRACE_PERIOD;
	}

	public void inValidate() {
		valid = false;
	}

}
