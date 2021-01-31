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
package com.t07m.synolvm.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.t07m.synolvm.config.LVMConfig.ViewConfig.Registry;

public class RegistryHandler {

	private static final String REGISTRY_HIVE = "HKEY_CURRENT_USER\\SOFTWARE\\Synology\\Surveillance Station Client";
	private static final Pattern KEY_PATTERN = Pattern.compile("\"([A-Z])\\w+\"");

	public boolean importRegistry(Registry registry) {
		if(registry != null) {
			File tf = newTempFile();
			if(tf != null) {
				if(saveRegistryToFile(registry, tf)) {
					try {
						Process p = Runtime.getRuntime().exec("REG IMPORT \"" + tf.getAbsolutePath() + "\"");
						if (p.waitFor(10L, TimeUnit.SECONDS)) {
							tf.delete();
							return true;
						} 
						p.destroyForcibly();
					} catch (IOException|InterruptedException e) {} 
					tf.delete();
				}
			}
		}
		return false;
	}

	public boolean exportRegistryTo(Registry registry) {
		if(registry != null) {
			File file = newTempFile();
			if(exportRegistryToFile(REGISTRY_HIVE, file)) {
				try(Scanner scanner = new Scanner(file, Charset.forName("UTF-16"))){
					String header="";
					String values="";
					int count = 0;
					while(scanner.hasNextLine()) {
						if(count < 3) {
							header+=scanner.nextLine()+System.lineSeparator();
						}else{
							values+=scanner.nextLine();
						}
						count++;
					}
					registry.setHeader(header);
					Map<String, Object> valueMap = parseValuesToMap(values);
					file.delete();
					if(valueMap != null) {
						setValues(registry, valueMap);
						setRecommendedValues(registry);
						return true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			file.delete();
		}		
		return false;
	}

	private void setValues(Registry reg, Map<String, Object> registryValues) {
		reg.setAlwaysAskDownloadLocation(registryValues.get("AlwaysAskDownloadLocation") != null ? ((String) registryValues.get("AlwaysAskDownloadLocation")) : null);
		reg.setAutoBalance(registryValues.get("AutoBalance") != null ? ((String) registryValues.get("AutoBalance")) : null);
		reg.setAutoLogin(registryValues.get("AutoLogin") != null ? ((String) registryValues.get("AutoLogin")) : null);
		reg.setClearDownload(registryValues.get("ClearDownload") != null ? ((String) registryValues.get("ClearDownload")) : null);
		reg.setDebugConfig(registryValues.get("DebugConfig") != null ? ((String) registryValues.get("DebugConfig")) : null);
		reg.setDisplayZoom(registryValues.get("DisplayZoom") != null ? ((String) registryValues.get("DisplayZoom")) : null);
		reg.setDownloadHistory(registryValues.get("DownloadHistory") != null ? ((String) registryValues.get("DownloadHistory")) : null);
		reg.setDownloadLocation(registryValues.get("DownloadLocation") != null ? ((String) registryValues.get("DownloadLocation")) : null);
		reg.setEnableGpuDecoder(registryValues.get("EnableGpuDecoder") != null ? ((String) registryValues.get("EnableGpuDecoder")) : null);
		reg.setHideSvsIcon(registryValues.get("HideSvsIcon") != null ? ((String) registryValues.get("HideSvsIcon")) : null);
		reg.setInstallerLang(registryValues.get("InstallerLang") != null ? ((String) registryValues.get("InstallerLang")) : null);
		reg.setLoggingConfig(registryValues.get("LoggingConfig") != null ? ((String) registryValues.get("LoggingConfig")) : null);
		reg.setLoginHistory(registryValues.get("LoginHistory") != null ? ((String) registryValues.get("LoginHistory")) : null);
		reg.setLoginLang(registryValues.get("LoginLang") != null ? ((String) registryValues.get("LoginLang")) : null);
		reg.setLowestResolution(registryValues.get("LowestResolution") != null ? ((Integer) registryValues.get("LowestResolution")) : 0);
		reg.setMaxGpuDecoderNum(registryValues.get("MaxGpuDecoderNum") != null ? ((Integer) registryValues.get("MaxGpuDecoderNum")) : 0);
		reg.setName(registryValues.get("Name") != null ? ((String) registryValues.get("Name")) : null);
		reg.setProxyAuthEnabled(registryValues.get("ProxyAuthEnabled") != null ? ((String) registryValues.get("ProxyAuthEnabled")) : null);
		reg.setProxyAuthPasswd(registryValues.get("ProxyAuthPasswd") != null ? ((String) registryValues.get("ProxyAuthPasswd")) : null);
		reg.setProxyAuthUserName(registryValues.get("ProxyAuthUserName") != null ? ((String) registryValues.get("ProxyAuthUserName")) : null);
		reg.setProxyIP(registryValues.get("ProxyIP") != null ? ((String) registryValues.get("ProxyIP")) : null);
		reg.setProxyPort(registryValues.get("ProxyPort") != null ? ((Integer)registryValues.get("ProxyPort")) : 0);
		reg.setProxyType(registryValues.get("ProxyType") != null ? ((Integer)registryValues.get("ProxyType")) : 0);
		reg.setRememberPassword(registryValues.get("RememberPassword") != null ? ((String) registryValues.get("RememberPassword")) : null);
		reg.setRunOnStartup(registryValues.get("RunOnStartup") != null ? ((String) registryValues.get("RunOnStartup")) : null);
		reg.setSessionCookie(registryValues.get("SessionCookie") != null ? ((String) registryValues.get("SessionCookie")) : null);
		reg.setShowHwLabel(registryValues.get("ShowHwLabel") != null ? ((String) registryValues.get("ShowHwLabel")) : null);
		reg.setUpgrader(registryValues.get("Upgrader") != null ? ((Integer)registryValues.get("Upgrader")) : 0);
		reg.setUseTestUpdateServer(registryValues.get("UseTestUpdateServer") != null ? ((String) registryValues.get("UseTestUpdateServer")) : null);
		reg.setWinGeometry(registryValues.get("WinGeometry") != null ? ((String) registryValues.get("WinGeometry")) : null);
		reg.setWinStates(registryValues.get("WinStates") != null ? ((Integer)registryValues.get("WinStates")) : 0);
	}

	private void setRecommendedValues(Registry registry) {
		registry.setAutoBalance("true");
		registry.setAutoLogin("true");
		registry.setEnableGpuDecoder("true");
		registry.setAutoLogin("true");
		registry.setHideSvsIcon("true");
		registry.setRememberPassword("true");
		registry.setShowHwLabel("false");
	}

	private Map<String, Object> parseValuesToMap(String values){
		if(values != null && values.length() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			Matcher matcher = KEY_PATTERN.matcher(values);
			int lastStart = 0, lastEnd = 0;
			while (matcher.find()) {
				if (lastEnd != 0)
					addValue(values.substring(lastStart, matcher.start()), map); 
				lastEnd = matcher.end();
				lastStart = matcher.start();
			} 
			addValue(values.substring(lastStart), map);
			return map;
		}
		return null;
	}

	private void addValue(String s, Map<String, Object> map) {
		String[] elements = s.split("=");
		String key = elements[0];
		key = key.substring(1, key.length() - 1);
		String preValue = s.substring(key.length() + 3);
		if (preValue.startsWith("dword")) {
			try {
				int value = Integer.parseInt(preValue.substring(6), 16);
				map.put(key, value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} 
		} else if (preValue.startsWith("\"") && preValue.endsWith("\"")) {
			String value = preValue.substring(1, preValue.length() - 1);
			map.put(key, value);
		} 
	}

	private boolean saveRegistryToFile(Registry registry, File file) {
		if(!file.exists() || file.canWrite()) {
			try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_16LE)){
				writer.write("\ufeff");
				writer.write(registry.getHeader());
				writer.write("\"AlwaysAskDownloadLocation\"=\"" + registry.getAlwaysAskDownloadLocation() + "\"" + System.lineSeparator());
				writer.write("\"AutoBalance\"=\"" + registry.getAutoBalance() + "\"" + System.lineSeparator());
				writer.write("\"AutoLogin\"=\"" + registry.getAutoLogin() + "\"" + System.lineSeparator());
				writer.write("\"ClearDownload\"=\"" + registry.getClearDownload() + "\"" + System.lineSeparator());
				writer.write("\"DebugConfig\"=\"" + registry.getDebugConfig() + "\"" + System.lineSeparator());
				writer.write("\"DisplayZoom\"=\"" + registry.getDisplayZoom() + "\"" + System.lineSeparator());
				writer.write("\"DownloadHistory\"=\"" + registry.getDownloadHistory() + "\"" + System.lineSeparator());
				writer.write("\"DownloadLocation\"=\"" + registry.getDownloadLocation() + "\"" + System.lineSeparator());
				writer.write("\"EnableGpuDecoder\"=\"" + registry.getEnableGpuDecoder() + "\"" + System.lineSeparator());
				writer.write("\"HideSvsIcon\"=\"" + registry.getHideSvsIcon() + "\"" + System.lineSeparator());
				writer.write("\"InstallerLang\"=\"" + registry.getInstallerLang() + "\"" + System.lineSeparator());
				writer.write("\"LoggingConfig\"=\"" + registry.getLoggingConfig() + "\"" + System.lineSeparator());
				writer.write("\"LoginHistory\"=\"" + registry.getLoginHistory() + "\"" + System.lineSeparator());
				writer.write("\"LoginLang\"=\"" + registry.getLoginLang() + "\"" + System.lineSeparator());
				writer.write("\"LowestResolution\"=dword:" + String.format("%08X", registry.getLowestResolution()) + System.lineSeparator());
				writer.write("\"MaxGpuDecoderNum\"=dword:" + String.format("%08X", registry.getMaxGpuDecoderNum()) + System.lineSeparator());
				writer.write("\"Name\"=\"" + registry.getName() + "\"" + System.lineSeparator());
				writer.write("\"ProxyAuthEnabled\"=\"" + registry.getProxyAuthEnabled() + "\"" + System.lineSeparator());
				writer.write("\"ProxyAuthPasswd\"=\"" + registry.getProxyAuthPasswd() + "\"" + System.lineSeparator());
				writer.write("\"ProxyAuthUserName\"=\"" + registry.getProxyAuthUserName() + "\"" + System.lineSeparator());
				writer.write("\"ProxyIP\"=\"" + registry.getProxyIP() + "\"" + System.lineSeparator());
				writer.write("\"ProxyPort\"=dword:" + String.format("%08X", registry.getProxyPort()) + System.lineSeparator());
				writer.write("\"ProxyType\"=dword:" + String.format("%08X", registry.getProxyType()) + System.lineSeparator());
				writer.write("\"RememberPassword\"=\"" + registry.getRememberPassword() + "\"" + System.lineSeparator());
				writer.write("\"RunOnStartup\"=\"" + registry.getRunOnStartup() + "\"" + System.lineSeparator());
				writer.write("\"SessionCookie\"=\"" + registry.getSessionCookie() + "\"" + System.lineSeparator());
				writer.write("\"ShowHwLabel\"=\"" + registry.getShowHwLabel() + "\"" + System.lineSeparator());
				writer.write("\"Upgrader\"=dword:" + String.format("%08X", registry.getUpgrader()) + System.lineSeparator());
				writer.write("\"UseTestUpdateServer\"=\"" + registry.getUseTestUpdateServer() + "\"" + System.lineSeparator());
				writer.write("\"WinGeometry\"=\"" + registry.getWinGeometry() + "\"" + System.lineSeparator());
				writer.write("\"WinStates\"=dword:" + String.format("%08X", registry.getWinStates()) + System.lineSeparator());
				writer.write(System.lineSeparator());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean exportRegistryToFile(String path, File file) {
		if (file != null) {
			try {
				Process p = Runtime.getRuntime().exec("REG EXPORT \"" + path + "\" \"" + file.getAbsolutePath() + "\" /y");
				if (p.waitFor(10L, TimeUnit.SECONDS)) {
					return true;
				} 
				p.destroyForcibly();
			} catch (IOException|InterruptedException e) {
				e.printStackTrace();
			} 
		} 
		return false;
	}

	private File newTempFile() {
		try {
			File f = Files.createTempFile("LV", null, (FileAttribute<?>[])new FileAttribute[0]).toFile();
			if (f.exists() && f.canWrite()) {
				f.deleteOnExit();
				return f;
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
