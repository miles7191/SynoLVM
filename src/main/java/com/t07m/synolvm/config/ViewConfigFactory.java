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
package com.t07m.synolvm.config;

import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.process.RegistryHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewConfigFactory {

	private final @NonNull LVMConfig config;
	private final @NonNull RegistryHandler registryHandler;

	public ViewConfig loadNewViewConfig() {
		ViewConfig vc = config.newViewConfig();
		if(registryHandler.exportRegistryTo(vc.getRegistry())) {
			return vc;
		}
		return null;
	}
}
