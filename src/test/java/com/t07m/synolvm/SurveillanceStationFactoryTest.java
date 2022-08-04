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

import org.junit.jupiter.api.Test;

class SurveillanceStationFactoryTest {

	@Test
	void test() {
//		File ss = new File("C:\\Program Files\\Synology\\SynologySurveillanceStationClient\\bin\\SynologySurveillanceStationClient.exe");
//		LVMConfig config = new LVMConfig();
//		ViewConfigFactory vcf = new ViewConfigFactory(config);
//		ViewConfig vc = vcf.loadNewViewConfig();
//
//		SurveillanceStationClient ssf = new SurveillanceStationClient(ss);
//		ExecutorService es = Executors.newWorkStealingPool();
//		for(int i = 0; i < 1; i++) {
//			Thread t = new Thread() {				
//				public void run() {
//					SurveillanceStationClient ssc = ssf.newSurveillanceStationClient();
//					if(vc != null && ssc.launch(TimeUnit.SECONDS.toMillis(10), 0, vc.getRegistry())) {
//						System.out.println(ssc.getTitle());
//						assert(ssc.getWindow() != null);
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						ssc.stop();
//					}
//				}
//			};
//			es.submit(t);
//		}
//		try {
//			es.shutdown();
//			es.awaitTermination(1, TimeUnit.HOURS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		SurveillanceStationClient ssc = ssf.newSurveillanceStationClient();
//		if(vc != null && ssc.launch(TimeUnit.SECONDS.toMillis(10), 0, vc.getRegistry())) {
//			System.out.println(ssc.getTitle());
//			System.out.println(ssc.getBounds());
//			System.out.println(ssc.isCorrectDisplay());
//			assert(ssc.getWindow() != null);
//			ssc.stop();
//		}else {
//			fail();
//		}
	}

}
