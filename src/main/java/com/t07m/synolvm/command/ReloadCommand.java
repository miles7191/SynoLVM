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
package com.t07m.synolvm.command;

import com.t07m.swing.console.Command;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synolvm.SynoLVM;

import joptsimple.OptionSet;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ReloadCommand extends Command {

	private final SynoLVM lvm;

	public ReloadCommand(SynoLVM lvm) {
		super("Reload");
		this.lvm = lvm;
	}


	public void process(OptionSet optionSet, ConsoleWindow console) {
		try {
			lvm.getConfig().reload();
			console.getLogger().info("Configuration Reloaded");
		} catch (InvalidConfigurationException e) {
			console.getLogger().info(e.getMessage());
			e.printStackTrace();
		}
	}
}