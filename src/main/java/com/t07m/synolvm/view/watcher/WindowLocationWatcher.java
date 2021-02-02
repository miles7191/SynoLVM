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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.view.View;
import com.t07m.synolvm.view.ViewWatcher;

public class WindowLocationWatcher extends ViewWatcher {

	private static final Logger logger = LoggerFactory.getLogger(WindowLocationWatcher.class);

	public WindowLocationWatcher(SynoLVM app, View view) {
		super(app, TimeUnit.SECONDS.toMillis(10), view);
	}

	public void process() {
		if(!getView().withinGracePeriod()) {
			synchronized(getView().getSurveillanceStationClient()) {
				if(getView().getViewConfig().getMonitor() != getView().getSurveillanceStationClient().getMonitor() || !getView().getSurveillanceStationClient().isCorrectScreen()) {
					if(getView().isValid()) {
						getView().inValidate();
						logger.info("View failed WindowLocationMonitor: " + getView().getViewConfig().getName());
					}
				}
			}
		}
	}	
}
