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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.t07m.console.Command;
import com.t07m.console.Console;
import com.t07m.synolvm.SynoLVM;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ViewListCommand extends Command {

	private static Logger logger = LoggerFactory.getLogger(ViewListCommand.class);
	
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

	public void process(OptionSet optionSet, Console console) {
		LVMConfig config = lvm.getConfig();
		synchronized(config) {
			ViewConfig[] views = config.getViewConfigurations();
			if(optionSet.has("view")) {
				String name = (String)optionSet.valueOf("view");
				for(ViewConfig vc : views) {
					if(name.equalsIgnoreCase(vc.getName())) {
						logger.info("Name: " + vc.getName() + System.lineSeparator() +
								"Enabled: " + vc.isEnabled() + System.lineSeparator() +
								"Priority: " + vc.getPriority() + System.lineSeparator() +
								"Monitor: " + vc.getMonitor());
						if(vc.getRegistry().getLoginHistory() != null) {
							try {
								Gson gson = new Gson();
								JsonArray jsonArray = gson.fromJson(vc.getRegistry().getLoginHistory().replace("\\\"", "\""), JsonArray.class);
								JsonObject json = jsonArray.get(0).getAsJsonObject();
								for(Entry<String, JsonElement> entry : json.entrySet()) {
									if(entry.getValue() != null && entry.getValue().getAsString().length() > 0) {
										logger.info(entry.getKey() + ": " + entry.getValue().getAsString());
									}
								}
							}catch(JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
						return;
					}
				}
				logger.warn("Unable to find view: " + name);
			}else {
				if(views.length > 0) {
					for(ViewConfig vc : views) {
						logger.info(vc.getName() + " - " + (vc.isEnabled() ? "Enabled" : "Disabled") + " P:" +vc.getPriority());
					}
				}else {
					logger.info("No views loaded.");
				}
			}
		}
	}
}
