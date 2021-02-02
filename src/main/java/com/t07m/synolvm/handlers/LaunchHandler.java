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
package com.t07m.synolvm.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synolvm.view.ViewManager;

public class LaunchHandler {

	private static final Logger logger = LoggerFactory.getLogger(LaunchHandler.class);
	
	public static ProcessHandle executeHandler(File file) {
		ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath());
		try {
			pb.redirectError(Redirect.DISCARD);
			pb.redirectOutput(Redirect.DISCARD);
			Process proc = pb.start();
			if(proc != null) {
				logger.debug("Started new process with PID: " + proc.pid() + " File: " + file.getAbsolutePath());
				return proc.toHandle();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
