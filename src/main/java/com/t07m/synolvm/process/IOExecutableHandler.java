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
package com.t07m.synolvm.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IOExecutableHandler {

	private final File executable;

	public String[] execute(long timeout, String... vars) throws IOException, InterruptedException {
		if(executable.exists() && executable.canExecute()) {
			ProcessBuilder pb = new ProcessBuilder();
			ArrayList<String> command = new ArrayList<String>();
			command.add(executable.getAbsolutePath());
			command.addAll(Arrays.asList(vars));
			pb.command(command);
			Process p = pb.start();
			if(p != null && p.waitFor(timeout, TimeUnit.MILLISECONDS)) {
				List<String> returnValue = new ArrayList<String>();
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while((line = in.readLine()) != null) {
					returnValue.add(line);
				}
				
				return returnValue.toArray(new String[returnValue.size()]);
			}		
		}
		return null;
	}
}
