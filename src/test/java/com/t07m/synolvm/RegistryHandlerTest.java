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
package com.t07m.synolvm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.ViewConfigFactory;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.process.RegistryHandler;

class RegistryHandlerTest {

	@Test
	void test() {
		RegistryHandler rh = new RegistryHandler();
		LVMConfig config = new LVMConfig();
		ViewConfigFactory vcf = new ViewConfigFactory(config, rh);
		ViewConfig vc = vcf.loadNewViewConfig();
		assert(rh.importRegistry(vc.getRegistry()));
	}

}
