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

import com.t07m.application.Service;
import com.t07m.synolvm.SynoLVM;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class ViewWatcher extends Service<SynoLVM>{

	private final @Getter(AccessLevel.PROTECTED) View view;
	
	public ViewWatcher(SynoLVM app, long updateFrequency, View view) {
		super(app, updateFrequency);
		this.view = view;
	}
}
