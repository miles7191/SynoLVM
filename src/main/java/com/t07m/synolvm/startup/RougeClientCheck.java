/*
 * Copyright (C) 2021 Matthew Rosato
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
package com.t07m.synolvm.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RougeClientCheck implements StartupCheck{

	private static final Logger logger = LoggerFactory.getLogger(RougeClientCheck.class);

	private List<ProcessHandle> rouges = new ArrayList<ProcessHandle>();

	public boolean check() {
		logger.info("Checking for rouge Surveillance Station Clients...");
		Iterator<ProcessHandle> itr = ProcessHandle.allProcesses().iterator();
		while(itr.hasNext()) {
			ProcessHandle processHandle = itr.next();
			if(isSurveillanceStation(processHandle)) {
				rouges.add(processHandle);
			}
		}
		return rouges.size() == 0;
	}

	private boolean isSurveillanceStation(ProcessHandle processHandle) {
		if(processHandle.info().command().isPresent()) {
			String command = processHandle.info().command().get();
			if(command != null && command.toLowerCase().endsWith("synologysurveillancestationclient.exe")) {
				return true;
			}
		}
		return false;
	}	

	public void performCorrectiveAction() {
		for(ProcessHandle ph : rouges) {
			boolean killed = ph.destroyForcibly();
			if(killed) {
				logger.info("Successfully killed rouge client PID: " + ph.pid());
			}else {
				logger.warn("Failed to kill rouge client PID: " + ph.pid());
			}
		}
	}
}
