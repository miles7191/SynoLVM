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

import java.awt.Frame;
import java.io.File;

import com.t07m.application.Application;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synolvm.command.ReloadCommand;
import com.t07m.synolvm.command.ViewDeleteCommand;
import com.t07m.synolvm.command.ViewExportCommand;
import com.t07m.synolvm.command.ViewListCommand;
import com.t07m.synolvm.command.ViewSetCommand;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.ViewConfigFactory;
import com.t07m.synolvm.process.LaunchHandler;
import com.t07m.synolvm.process.RegistryHandler;
import com.t07m.synolvm.process.SurveillanceStationFactory;
import com.t07m.synolvm.system.WindowHandler;
import com.t07m.synolvm.view.ViewManager;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class SynoLVM extends Application{

	public static void main(String[] args) {
		boolean gui = true;
		if(args.length > 0) {
			for(String arg : args) {
				if(arg.equalsIgnoreCase("-nogui")) {
					gui = false;
				}
			}
		}
		new SynoLVM(gui).start();
	}

	private @Getter LVMConfig config;
	private @Getter ViewConfigFactory viewConfigFactory;
	private @Getter SurveillanceStationFactory surveillanceStationFactory;
	
	private @Getter ViewManager viewManager;

	public SynoLVM(boolean gui) {
		super(gui, "SynoLVM");
	}
	
	@SuppressWarnings("serial")
	public void init() {
		this.config = new LVMConfig();
		try {
			this.config.init();
			this.config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		this.getConsole().registerCommands(
				new ReloadCommand(this),
				new ViewExportCommand(this),
				new ViewDeleteCommand(this),
				new ViewListCommand(this),
				new ViewSetCommand(this));
		if(this.getConsole() instanceof ConsoleWindow) {
			((ConsoleWindow)(this.getConsole())).setState(Frame.ICONIFIED);
		}
		RegistryHandler registryHandler = new RegistryHandler();
		this.viewConfigFactory = new ViewConfigFactory(this.config, registryHandler);
		this.surveillanceStationFactory = new SurveillanceStationFactory(new File(this.config.getSurveillanceStationPath()), registryHandler, new LaunchHandler(new File("lib/Launch.exe")));
		this.viewManager = new ViewManager(this);
		this.registerService(viewManager);
	}
}
