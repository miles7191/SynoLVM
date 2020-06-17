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

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LaunchHandler {

	private final IOExecutableHandler executable;
	
	public LaunchHandler(File launchxecutable) {
		executable = new IOExecutableHandler(launchxecutable);
	}

	public ProcessHandle executeHandler(File file) {
		if(file != null && file.exists() && file.canExecute()) {
			try {
				String[] ret = executable.execute(TimeUnit.SECONDS.toMillis(5), file.getAbsolutePath());
				if(ret != null && ret.length > 0){
					try {
						Optional<ProcessHandle> opt = ProcessHandle.of(Long.parseLong(ret[0]));
						if(opt.isPresent())
							return opt.get();
					} catch(NumberFormatException e) {}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
