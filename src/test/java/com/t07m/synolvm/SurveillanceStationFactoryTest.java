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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.config.ViewConfigFactory;
import com.t07m.synolvm.process.LaunchHandler;
import com.t07m.synolvm.process.RegistryHandler;
import com.t07m.synolvm.process.ScreenHandler;
import com.t07m.synolvm.process.SurveillanceStationFactory;
import com.t07m.synolvm.process.WindowHandler;
import com.t07m.synolvm.process.SurveillanceStationFactory.SurveillanceStationClient;

class SurveillanceStationFactoryTest {

	@Test
	void test() {
		File ss = new File("C:\\Program Files\\Synology\\SynologySurveillanceStationClient\\bin\\SynologySurveillanceStationClient.exe");
		RegistryHandler rh = new RegistryHandler(new File("lib/WindowsRegistry.exe"));
		LaunchHandler lh = new LaunchHandler(new File("lib/Launch.exe"));
		ScreenHandler sh = new ScreenHandler(new File("lib/QueryScreen.exe"));
		WindowHandler wh = new WindowHandler(new File("lib/QueryWindow.exe"));
		LVMConfig config = new LVMConfig();
		RegistryHandler handler = new RegistryHandler(new File("lib/WindowsRegistry.exe"));
		ViewConfigFactory vcf = new ViewConfigFactory(config, handler);
		ViewConfig vc = vcf.loadNewViewConfig();

		SurveillanceStationFactory ssf = new SurveillanceStationFactory(ss, rh, lh, sh, wh);
		ExecutorService es = Executors.newFixedThreadPool(4);
		for(int i = 0; i < 0; i++) {
			Thread t = new Thread() {				
				public void run() {
					SurveillanceStationClient ssc = ssf.newSurveillanceStationClient();
					if(vc != null && ssc.launch(TimeUnit.SECONDS.toMillis(10), 0, vc.getRegistry())) {
						System.out.println(ssc.getTitle());
						assert(ssc.getWindow() != null);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
						ssc.stop();
					}
				}
			};
			es.submit(t);
		}
		try {
			es.shutdown();
			es.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SurveillanceStationClient ssc = ssf.newSurveillanceStationClient();
		if(vc != null && ssc.launch(TimeUnit.SECONDS.toMillis(10), 0, vc.getRegistry())) {
			System.out.println(ssc.getTitle());
			System.out.println(ssc.getBounds());
			System.out.println(ssc.isCorrectScreen());
			assert(ssc.getWindow() != null);
			ssc.stop();
		}else {
			fail();
		}
	}

}
