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
package com.t07m.synolvm.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.console.Command;
import com.t07m.console.Console;
import com.t07m.synolvm.SynoLVM;

import joptsimple.OptionSet;

public class ResumeCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(ResumeCommand.class);
	
	private final SynoLVM lvm;
	
	public ResumeCommand(SynoLVM lvm) {
		super("Resume");
		this.lvm = lvm;
	}

	public void process(OptionSet optionSet, Console console) {
		logger.info("Resuming application");
		lvm.resumeExecution();
	}
	
}
