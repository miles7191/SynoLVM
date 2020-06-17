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

import java.util.Arrays;
import java.util.Map;

import com.t07m.synolvm.LVMConfig.ViewConfig;
import com.t07m.synolvm.LVMConfig.ViewConfig.Registry;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewConfigFactory {

	private final @NonNull LVMConfig config;
	private final @NonNull RegistryHandler registryHandler;

	public ViewConfig loadNewViewConfig() {
		ViewConfig vc = config.newViewConfig();
		Map<String, Object> registryValues = registryHandler.getValues(Arrays.asList(new String[] {
				"AutoBalance",
				"AutoLogin",
				"DisplayZoom",
				"EnableGpuDecoder",
				"HideSvsIcon",
				"InstallerLang",
				"LoggingConfig",
				"LoginHistory",
				"LoginLang",
				"LowestResolution",
				"MaxGpuDecoderNum",
				"Name",
				"ProxyAuthEnabled",
				"ProxyAuthPasswd",
				"ProxyAuthUserName",
				"ProxyIP",
				"ProxyPort",
				"ProxyType",
				"RememberPassword",
				"SessionCookie",
				"ShowHwLabel",
				"Upgrader",
				"UseTestUpdateServer",
				"WinGeometry",
				"WinStates"
		}));
		Registry reg = vc.getRegistry();
		reg.setAutoBalance(registryValues.get("AutoBalance") != null ? ((String) registryValues.get("AutoBalance")) : null);
		reg.setAutoLogin(registryValues.get("AutoLogin") != null ? ((String) registryValues.get("AutoLogin")) : null);
		reg.setDisplayZoom(registryValues.get("DisplayZoom") != null ? ((String) registryValues.get("DisplayZoom")) : null);
		reg.setEnableGpuDecoder(registryValues.get("EnableGpuDecoder") != null ? ((String) registryValues.get("EnableGpuDecoder")) : null);
		reg.setHideSvsIcon(registryValues.get("HideSvsIcon") != null ? ((String) registryValues.get("HideSvsIcon")) : null);
		reg.setInstallerLang(registryValues.get("InstallerLang") != null ? ((String) registryValues.get("InstallerLang")) : null);
		reg.setLoggingConfig(registryValues.get("LoggingConfig") != null ? ((String) registryValues.get("LoggingConfig")) : null);
		reg.setLoginHistory(registryValues.get("LoginHistory") != null ? ((String) registryValues.get("LoginHistory")) : null);
		reg.setLoginLang(registryValues.get("LoginLang") != null ? ((String) registryValues.get("LoginLang")) : null);
		try { reg.setLowestResolution(registryValues.get("LowestResolution") != null ? Integer.parseInt((String)registryValues.get("LowestResolution")) : 0); }catch(NumberFormatException e) {}
		try { reg.setMaxGpuDecoderNum(registryValues.get("MaxGpuDecoderNum") != null ? Integer.parseInt((String)registryValues.get("MaxGpuDecoderNum")) : 0); }catch(NumberFormatException e) {}
		reg.setName(registryValues.get("Name") != null ? ((String) registryValues.get("Name")) : null);
		reg.setProxyAuthEnabled(registryValues.get("ProxyAuthEnabled") != null ? ((String) registryValues.get("ProxyAuthEnabled")) : null);
		reg.setProxyAuthPasswd(registryValues.get("ProxyAuthPasswd") != null ? ((String) registryValues.get("ProxyAuthPasswd")) : null);
		reg.setProxyAuthUserName(registryValues.get("ProxyAuthUserName") != null ? ((String) registryValues.get("ProxyAuthUserName")) : null);
		reg.setProxyIP(registryValues.get("ProxyIP") != null ? ((String) registryValues.get("ProxyIP")) : null);
		try { reg.setProxyPort(registryValues.get("ProxyPort") != null ? Integer.parseInt((String)registryValues.get("ProxyPort")) : 0); }catch(NumberFormatException e) {}
		try { reg.setProxyType(registryValues.get("ProxyType") != null ? Integer.parseInt((String)registryValues.get("ProxyType")) : 0); }catch(NumberFormatException e) {}
		reg.setRememberPassword(registryValues.get("RememberPassword") != null ? ((String) registryValues.get("RememberPassword")) : null);
		reg.setSessionCookie(registryValues.get("SessionCookie") != null ? ((String) registryValues.get("SessionCookie")) : null);
		reg.setShowHwLabel(registryValues.get("ShowHwLabel") != null ? ((String) registryValues.get("ShowHwLabel")) : null);
		try { reg.setUpgrader(registryValues.get("Upgrader") != null ? Integer.parseInt((String)registryValues.get("Upgrader")) : 0); }catch(NumberFormatException e) {}
		reg.setUseTestUpdateServer(registryValues.get("UseTestUpdateServer") != null ? ((String) registryValues.get("UseTestUpdateServer")) : null);
		reg.setWinGeometry(registryValues.get("WinGeometry") != null ? ((String) registryValues.get("WinGeometry")) : null);
		try { reg.setWinStates(registryValues.get("WinStates") != null ? Integer.parseInt((String)registryValues.get("WinStates")) : 0); }catch(NumberFormatException e) {}
		return vc;
	}

}
