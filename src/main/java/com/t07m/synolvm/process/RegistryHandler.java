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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class RegistryHandler {

	private static final String REGISTRY_HIVE = "\"HKEY_CURRENT_USER\\SOFTWARE\\Synology\\Surveillance Station Client\"";

	private enum Task{SET, GET};

	private final IOExecutableHandler executable;

	public RegistryHandler(File registryExecutable) {
		executable = new IOExecutableHandler(registryExecutable);
	}

	public boolean setValues(Map<String, Object> values) {
		if(values != null && values.size() > 0) {
			Map<String, Object> retValues = executeHandler(Task.SET, values);
			if(retValues != null) {
				for(Entry<String, Object> entry : values.entrySet()) {
					if(retValues.get(entry.getKey()).equals(entry.getValue())) {
						continue;
					}else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public Map<String, Object> getValues(List<String> keys){
		if(keys != null && keys.size() > 0) {
			Map<String, Object> values = new HashMap<String, Object>();
			for(String key : keys)
				values.put(key, null);
			return executeHandler(Task.GET, values);
		}
		return null;
	}

	private Map<String, Object> executeHandler(Task task, Map<String, Object> values){
		if(values != null ) {
			try {
				for(Entry<String, Object> entry : values.entrySet()) {
					String[] ret = null;
					if(task == Task.GET) {
						ret = executable.execute(TimeUnit.SECONDS.toMillis(10), "get", REGISTRY_HIVE, entry.getKey());
					}else if(task == Task.SET) {
						ret = executable.execute(TimeUnit.SECONDS.toMillis(10), "set", REGISTRY_HIVE, entry.getKey(), entry.getValue().toString());
					}
					if(ret != null && ret.length > 0) {
						if(entry.getValue() instanceof Integer) {
							values.put(entry.getKey(), Integer.parseInt(ret[0]));
						}else {
							values.put(entry.getKey(), ret[0]);
						}
					}else {
						values.put(entry.getKey(), null);
					}
				}
				return values;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
