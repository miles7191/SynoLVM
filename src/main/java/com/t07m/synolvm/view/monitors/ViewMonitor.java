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
package com.t07m.synolvm.view.monitors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.view.View;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class ViewMonitor extends Service<SynoLVM>{

	private static final Logger log = LoggerFactory.getLogger(WindowTitleMonitor.class);
	
	private final @Getter(AccessLevel.PROTECTED) View view;
	
	public ViewMonitor(SynoLVM app, long updateFrequency, View view) {
		super(updateFrequency);
		this.view = view;
	}
	
	public void process() {
		if(!getView().withinGracePeriod() && view.isValid()) {
			synchronized(getView().getSurveillanceStationClient()) {
				if(!validate()) {
					if(getView().isValid()) {
						getView().inValidate();
						log.info("View failed " + getClass().getSimpleName() + ": " + getView().getViewConfig().getName());
					}
				}
			}
		}
	}
	
	public abstract boolean validate();
}
