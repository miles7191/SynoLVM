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

import java.io.File;

import com.t07m.application.Application;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synolvm.command.StopCommand;
import com.t07m.synolvm.command.ViewDeleteCommand;
import com.t07m.synolvm.command.ViewExportCommand;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.ViewConfigFactory;
import com.t07m.synolvm.process.LaunchHandler;
import com.t07m.synolvm.process.RegistryHandler;
import com.t07m.synolvm.process.ScreenHandler;
import com.t07m.synolvm.process.WindowHandler;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class SynoLVM extends Application{

	public static void main(String[] args) {
		new SynoLVM();
	}

	private @Getter LVMConfig config;
	private @Getter ViewConfigFactory viewConfigFactory;
	private @Getter SurveillanceStationFactory surveillanceStationFactory;

	private ConsoleWindow console;

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
		this.console = new ConsoleWindow("SynoLVM") {
			public void closeRequested() {
				stop();
			}
		};
		this.console.setup();
		this.console.registerCommand(new StopCommand());
		this.console.registerCommand(new ViewExportCommand(this));
		this.console.registerCommand(new ViewDeleteCommand(this));
		this.console.setLocationRelativeTo(null);
		this.console.setVisible(true);
		RegistryHandler registryHandler = new RegistryHandler(new File("lib/WindowsRegistry.exe"));
		this.viewConfigFactory = new ViewConfigFactory(this.config, registryHandler);
		this.surveillanceStationFactory = new SurveillanceStationFactory(new File(this.config.getSurveillanceStationPath()), registryHandler, new LaunchHandler(new File("lib/Launch.exe")), new ScreenHandler(new File("lib/QueryScreen.exe")), new WindowHandler(new File("QueryWindow.exe")));
	}
}
