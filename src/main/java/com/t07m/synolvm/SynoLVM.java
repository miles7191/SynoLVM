/*
 - * Copyright (C) 2020 Matthew Rosato
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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zafarkhaja.semver.Version;
import com.t07m.application.Application;
import com.t07m.console.swing.ConsoleWindow;
import com.t07m.synolvm.command.DisplaysCommand;
import com.t07m.synolvm.command.PauseCommand;
import com.t07m.synolvm.command.ReloadCommand;
import com.t07m.synolvm.command.ResumeCommand;
import com.t07m.synolvm.command.ViewDeleteCommand;
import com.t07m.synolvm.command.ViewExportCommand;
import com.t07m.synolvm.command.ViewListCommand;
import com.t07m.synolvm.command.ViewSetCommand;
import com.t07m.synolvm.config.LVMConfig;
import com.t07m.synolvm.config.LVMConfig.ViewConfig;
import com.t07m.synolvm.config.ViewConfigFactory;
import com.t07m.synolvm.handlers.RegistryHandler;
import com.t07m.synolvm.startup.GracePeriodCheck;
import com.t07m.synolvm.startup.MouseLocationCheck;
import com.t07m.synolvm.startup.RougeClientCheck;
import com.t07m.synolvm.startup.ScreenCheck;
import com.t07m.synolvm.startup.StartupCheck;
import com.t07m.synolvm.system.monitors.UserMonitor;
import com.t07m.synolvm.view.ViewController;
import com.t07m.synolvm.view.ViewManager;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class SynoLVM extends Application{

	public static final Version VERSION = Version.valueOf("1.1.2");
	
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

	private static final Logger logger = LoggerFactory.getLogger(SynoLVM.class);
	
	private @Getter LVMConfig config;
	private @Getter ViewConfigFactory viewConfigFactory;
	private @Getter UserMonitor userMonitor;
	
	private @Getter ViewManager viewManager;
	private @Getter ViewController viewController;

	public SynoLVM(boolean gui) {
		super(gui, "SynoLVM - " + VERSION.toString());
	}
	
	public void init() {
		this.config = new LVMConfig();
		try {
			this.config.init();
			for(ViewConfig vc : this.config.getViewConfigurations()) {
				RegistryHandler.trimLoginHistory(vc.getRegistry());
			}
			this.config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		logger.info("Launching Application.");
		StartupCheck[] startupChecks = new StartupCheck[] {
				new ScreenCheck(),
				new GracePeriodCheck(TimeUnit.SECONDS.toMillis(this.config.getLaunchGracePeriod())),
				new RougeClientCheck(),
				new MouseLocationCheck()};
		this.getConsole().registerCommands(
				new ReloadCommand(this),
				new ViewExportCommand(this),
				new ViewDeleteCommand(this),
				new ViewListCommand(this),
				new ViewSetCommand(this),
				new PauseCommand(this),
				new ResumeCommand(this),
				new DisplaysCommand());
		this.viewConfigFactory = new ViewConfigFactory(this.config);
		this.viewManager = new ViewManager(this);
		this.viewController = new ViewController(this);
		this.userMonitor = new UserMonitor(this);
		for(StartupCheck check : startupChecks) {
			if(!check.check()) {
				check.performCorrectiveAction();
			}
		}
		this.registerHandler(viewManager);
		this.registerService(userMonitor);
		this.registerService(viewController);
		if(this.getConsole() instanceof ConsoleWindow) {
			((ConsoleWindow)(this.getConsole())).setState(Frame.ICONIFIED);
		}
	}
}
