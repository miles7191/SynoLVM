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
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.t07m.swing.console.Command;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ViewListCommand extends Command {

	private final SynoLVM lvm;

	public ViewListCommand(SynoLVM lvm) {
		super("View List");
		this.lvm = lvm;
		OptionParser op = new OptionParser();
		String[] viewOptions = { "v", 
		"view" };
		op.acceptsAll(Arrays.asList(viewOptions), "View Name").withRequiredArg().ofType(String.class);
		setOptionParser(op);
	}

	public void process(OptionSet optionSet, ConsoleWindow console) {
		LVMConfig config = lvm.getConfig();
		synchronized(config) {
			ViewConfig[] views = config.getViewConfigurations();
			if(optionSet.has("view")) {
				String name = (String)optionSet.valueOf("view");
				for(ViewConfig vc : views) {
					if(name.equalsIgnoreCase(vc.getName())) {
						console.getLogger().info("Name: " + vc.getName());
						console.getLogger().info("Enabled: " + vc.isEnabled());
						console.getLogger().info("Priority: " + vc.getPriority());
						console.getLogger().info("Monitor: " + vc.getMonitor());
						if(vc.getRegistry().getLoginHistory() != null) {
							try {
								Gson gson = new Gson();
								JsonArray jsonArray = gson.fromJson(vc.getRegistry().getLoginHistory().replace("\\\"", "\""), JsonArray.class);
								JsonObject json = jsonArray.get(0).getAsJsonObject();
								for(Entry<String, JsonElement> entry : json.entrySet()) {
									if(entry.getValue() != null && entry.getValue().getAsString().length() > 0) {
										console.getLogger().info(entry.getKey() + ": " + entry.getValue().getAsString());
									}
								}
							}catch(JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						return;
					}
				}
				console.getLogger().warning("Unable to find view: " + name);
			}else {
				if(views.length > 0) {
					for(ViewConfig vc : views) {
						console.getLogger().info(vc.getName() + " - " + (vc.isEnabled() ? "Enabled" : "Disabled") + " P:" +vc.getPriority());
					}
				}else {
					console.getLogger().info("No views loaded.");
				}
			}
		}
	}
}
