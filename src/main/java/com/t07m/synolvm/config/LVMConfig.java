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
package com.t07m.synolvm.config;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

@ToString
public class LVMConfig extends YamlConfig {

	@Comment("Path To Synology Surveillance Station Executable")
	private @Getter @Setter String SurveillanceStationPath = "C:\\Program Files\\Synology\\SynologySurveillanceStationClient\\bin\\SynologySurveillanceStationClient.exe";
	private @Getter @Setter ViewConfig[] ViewConfigurations = new ViewConfig[0];

	public LVMConfig() {
		CONFIG_HEADER = new String[]{"SynoLVM Configuration Data"};
		CONFIG_FILE = new File("config.yml");
	}

	public ViewConfig newViewConfig() {
		return new ViewConfig();
	}
	
	@ToString
	public class ViewConfig extends YamlConfig{

		private @Getter @Setter String Name;
		private @Getter @Setter boolean Enabled;
		private @Getter @Setter int Priority;
		private @Getter @Setter int Monitor;
		private @Getter @Setter Registry Registry = new Registry();

		@ToString
		public class Registry extends YamlConfig{

			private @Getter @Setter String AutoBalance;
			private @Getter @Setter String AutoLogin;
			private @Getter @Setter String DisplayZoom;
			private @Getter @Setter String EnableGpuDecoder;
			private @Getter @Setter String HideSvsIcon;
			private @Getter @Setter String InstallerLang;
			private @Getter @Setter String LoggingConfig;
			private @Getter @Setter String LoginHistory;
			private @Getter @Setter String LoginLang;
			private @Getter @Setter int LowestResolution;
			private @Getter @Setter int MaxGpuDecoderNum;
			private @Getter @Setter String Name;
			private @Getter @Setter String ProxyAuthEnabled;
			private @Getter @Setter String ProxyAuthPasswd;
			private @Getter @Setter String ProxyAuthUserName;
			private @Getter @Setter String ProxyIP;
			private @Getter @Setter int ProxyPort;
			private @Getter @Setter int ProxyType;
			private @Getter @Setter String RememberPassword;
			private @Getter @Setter String SessionCookie;
			private @Getter @Setter String ShowHwLabel;
			private @Getter @Setter int Upgrader;
			private @Getter @Setter String UseTestUpdateServer;
			private @Getter @Setter String WinGeometry;
			private @Getter @Setter int WinStates;			
		}
	}
}
