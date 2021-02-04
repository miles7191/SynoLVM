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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracePeriodCheck implements StartupCheck{

	private static final Logger logger = LoggerFactory.getLogger(GracePeriodCheck.class);
	
	private final long gracePeriod;
	private long initTime;
	
	public GracePeriodCheck(long gracePeriod) {
		this.gracePeriod = gracePeriod;
		this.initTime = System.currentTimeMillis();
	}
	
	public boolean check() {
		return System.currentTimeMillis() - initTime > gracePeriod ;
	}

	public void performCorrectiveAction() {
		logger.info("Sleeping for grace period");
		while(!check()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("Resuming application");
	}
}
