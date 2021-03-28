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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.console.Command;
import com.t07m.console.Console;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ViewSetCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(ViewSetCommand.class);
	
	private final SynoLVM lvm;

	public ViewSetCommand(SynoLVM lvm) {
		super("View Set");
		this.lvm = lvm;
		OptionParser op = new OptionParser();
		String[] viewOptions = { "v", 
		"view" };
		op.acceptsAll(Arrays.asList(viewOptions), "View Name").withRequiredArg().ofType(String.class).required();
		String[] prioritOptions = { "p", 
		"priority" };
		op.acceptsAll(Arrays.asList(prioritOptions), "View Priority (Higher Number = Higher Priority)").withRequiredArg().ofType(Integer.class);
		String[] monitorOptions = { "m", 
		"monitor" };
		op.acceptsAll(Arrays.asList(monitorOptions), "View Monitor").withRequiredArg().ofType(Integer.class);
		String[] enabledOptions = { "e", 
		"enable" };
		op.acceptsAll(Arrays.asList(enabledOptions), "Enable View");
		String[] disableOptions = { "d", 
		"disable" };
		op.acceptsAll(Arrays.asList(disableOptions), "Disable View").availableUnless("enable");
		setOptionParser(op);
	}

	public void process(OptionSet optionSet, Console console) {
		if(optionSet.has("view")) {
			String name = (String)optionSet.valueOf("view");
			LVMConfig config = lvm.getConfig();
			synchronized(config) {
				for(ViewConfig vc : lvm.getConfig().getViewConfigurations()) {
					if(name.equalsIgnoreCase(vc.getName())) {
						setValues(optionSet, config, vc);
						return;
					}
				}
			}
			logger.warn("Unable to find view.");
		}
	}

	private void setValues(OptionSet optionSet, LVMConfig config, ViewConfig vc) {
		if(optionSet.has("enable")) {
			vc.setEnabled(true);
			logger.info(vc.getName() + ": Enabled");
		}else if(optionSet.has("disable")) {
			vc.setEnabled(false);
			logger.info(vc.getName() + ": Disabled");
		}
		if(optionSet.has("monitor")) {
			vc.setMonitor(((Integer)optionSet.valueOf("monitor")).intValue());
			logger.info(vc.getName() + " Monitor: " + vc.getMonitor());
		}
		if(optionSet.has("priority")) {
			vc.setPriority(((Integer)optionSet.valueOf("priority")).intValue());
			logger.info(vc.getName() + " Priority: " + vc.getPriority());
		}
		try {
			config.save();
			logger.info("Successfully modified view: " + vc.getName());
		} catch (InvalidConfigurationException e) {
			logger.error("View Set was unable to save the configuration to disk. Changes will not persist through restart!");
		}
	}

}
