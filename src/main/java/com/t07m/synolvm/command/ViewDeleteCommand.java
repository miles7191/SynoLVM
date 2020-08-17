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

import com.t07m.swing.console.Command;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ViewDeleteCommand extends Command {

	private final SynoLVM lvm;
	
	public ViewDeleteCommand(SynoLVM lvm) {
		super("View Delete");
		this.lvm = lvm;
		OptionParser op = new OptionParser();
		String[] viewOptions = { "v", 
		"view" };
		op.acceptsAll(Arrays.asList(viewOptions), "View Name").withRequiredArg().ofType(String.class).required();
		setOptionParser(op);
	}

	public void process(OptionSet optionSet, ConsoleWindow console) {
		String name = (String)optionSet.valueOf("view");
		LVMConfig config = lvm.getConfig();
		synchronized(config) {
			List<ViewConfig> currentViews = new ArrayList<ViewConfig>(Arrays.asList(lvm.getConfig().getViewConfigurations()));
			for(ViewConfig vc : currentViews) {
				if(name.equalsIgnoreCase(vc.getName())) {
					currentViews.remove(vc);
					config.setViewConfigurations(currentViews.toArray(new ViewConfig[currentViews.size()]));
					try {
						config.save();
						console.getLogger().info("Successfully deleted view: " + vc.getName());
					} catch (InvalidConfigurationException e) {
						console.getLogger().severe("View Export was unable to save the configuration to disk. Changes will not persist through restart!");
					}
					return;
				}
			}
			console.getLogger().warning("Unable to find view: " + name);
		}
	}
	
}
