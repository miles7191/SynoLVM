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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class ViewExportCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ViewExportCommand.class);

	private final SynoLVM lvm;

	public ViewExportCommand(SynoLVM lvm) {
		super("View Export");
		this.lvm = lvm;
		OptionParser op = new OptionParser();
		String[] viewOptions = { "v", 
		"view" };
		op.acceptsAll(Arrays.asList(viewOptions), "View Name").withRequiredArg().ofType(String.class).required();
		String[] monitorOptions = { "m", 
		"monitor" };
		op.acceptsAll(Arrays.asList(monitorOptions), "Display Monitor").withRequiredArg().ofType(Integer.class).required();
		String[] priorityOptions = { "p",
		"priority"};
		op.acceptsAll(Arrays.asList(priorityOptions), "View Priortiy").withRequiredArg().ofType(Integer.class);
		String[] enabledOptions = { "e", 
		"enable" };
		op.acceptsAll(Arrays.asList(enabledOptions), "Enable View");
		setOptionParser(op);
	}

	public void process(OptionSet optionSet, Console console) {
		String name = (String)optionSet.valueOf("view");
		int monitor = ((Integer)optionSet.valueOf("monitor")).intValue();
		int priority = 0;
		if(optionSet.has("priority")) {
			priority = ((Integer)optionSet.valueOf("priority")).intValue();
		}
		boolean enabled = optionSet.has("enable");
		LVMConfig config = lvm.getConfig();
		synchronized(config) {
			List<ViewConfig> currentViews = new ArrayList<ViewConfig>(Arrays.asList(lvm.getConfig().getViewConfigurations()));
			for(ViewConfig vc : currentViews) {
				if(name.equalsIgnoreCase(vc.getName())) {
					logger.warn("Found existing view with same name. Please delete existing view before exporting view with same name.");
					return;
				}
			}
			ViewConfig vc = lvm.getViewConfigFactory().loadNewViewConfig();
			if(vc != null) {
				saveView(name, monitor, priority, enabled, config, currentViews, vc);
				lvm.getViewManager().refreshConfigs();
				return;
			}
			logger.warn("Unable to export view!");
		}
	}

	private void saveView(String name, int monitor, int priority, boolean enabled, LVMConfig config,
			List<ViewConfig> currentViews, ViewConfig vc) {
		vc.setName(name);
		vc.setMonitor(monitor);
		vc.setEnabled(enabled);
		vc.setPriority(priority);
		currentViews.add(vc);
		config.setViewConfigurations(currentViews.toArray(new ViewConfig[currentViews.size()]));
		try {
			config.save();
			logger.info("Successfully exported view: " + vc.getName());
		} catch (InvalidConfigurationException e) {
			logger.error("Warning! View Export was unable to save the configuration to disk. Changes will not persist through restart!");
		}
	}
}
