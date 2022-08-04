/*
 * Copyright (C) 2022 Matthew Rosato
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
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.ToString;

public class ClientApplicationHandler {

	private static final Logger logger = LoggerFactory.getLogger(ClientApplicationHandler.class);

	private static final String clientDirectory = "Clients";

	public static ClientApplication getLatestClient() {
		String[] versions = ClientDownloadHandler.getAvailableVersions();
		if(versions.length > 0) {
			return getClient(versions[0]);
		}else {
			return getAvailableClients()[0];
		}
	}

	public static ClientApplication getClient(String version) {
		ClientApplication app = buildClientFromVersion(version);
		if(app == null) {
			ClientDownloadHandler.downloadClient(version, clientDirectory);
		}
		return buildClientFromVersion(version);
	}

	public static ClientApplication[] getAvailableClients() {
		ArrayList<ClientApplication> clients = new ArrayList<ClientApplication>();
		String[] dirs = listClientDirectories();
		for(String dir : dirs) {
			ClientApplication c = buildClientFromDirectory(dir);
			if(c != null)
				clients.add(c);
		}
		return clients.toArray(new ClientApplication[clients.size()]);
	}

	private static String[] listClientDirectories() {
		try {
			File file = new File(clientDirectory);
			String[] directories = file.list(new FilenameFilter() {
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			return directories;
		}catch (Exception e) {}
		return new String[0];
	}

	private static ClientApplication buildClientFromVersion(String version) {
		String directory = "Synology Surveillance Station Client-" + version + "_x64";
		File dir = new File(clientDirectory + File.separator + directory + File.separator + "bin");
		String exe = findClientExecutable(dir);
		if(exe != null) {
			return new ClientApplication(version, dir.getAbsolutePath(), exe);
		}
		return null;
	}

	private static ClientApplication buildClientFromDirectory(String directory) {
		String version = directory.substring(37, directory.length()-4);
		File dir = new File(clientDirectory + File.separator + directory + File.separator + "bin");
		String exe = findClientExecutable(dir);
		if(exe != null) {
			return new ClientApplication(version, dir.getAbsolutePath(), exe);
		}
		return null;
	}

	private static String findClientExecutable(File dir) {
		if(dir.exists()) {
			String[] exes = dir.list(new FilenameFilter() {
				public boolean accept(File current, String name) {
					if(name.toLowerCase().endsWith(".exe")) {
						return name.toLowerCase().contains("client");
					}
					return false;
				}
			});
			if(exes != null && exes.length == 1) {
				return exes[0];
			}
			logger.warn("Unable to determine executable: " + dir.getAbsolutePath());
		}
		return null;
	}

	@ToString
	public static class ClientApplication{

		private final @Getter String version;
		private final @Getter String directory;
		private final @Getter String executable;

		ClientApplication(String version, String directory, String executable) {
			this.version = version;
			this.directory = directory;
			this.executable = executable;
		}

	}

}
