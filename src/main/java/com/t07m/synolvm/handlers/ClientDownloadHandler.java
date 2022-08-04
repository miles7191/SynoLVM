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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import net.lingala.zip4j.ZipFile;

public class ClientDownloadHandler {

	private static final Logger logger = LoggerFactory.getLogger(ClientDownloadHandler.class);

	private static String getDownloadURL(String version) {
		return "https://global.download.synology.com/download/Utility/SurveillanceStationClient/" + version + "/Windows/x86_64/Synology Surveillance Station Client-" + version + "_x64.zip";
	}

	private static String getArchiveName(String version, String clientDirectory) {
		return clientDirectory + File.separator + "Synology Surveillance Station Client-" + version + "_x64";
	}

	static boolean downloadClient(String version, String clientDirectory) {
		if(version != null && version.length() > 0) {
			FileUtils.deleteQuietly(new File(getArchiveName(version, clientDirectory) + ".zip"));
			FileUtils.deleteQuietly(new File(getArchiveName(version, clientDirectory)));
			try {
				logger.debug("Downloading Client Version " + version);
				FileUtils.copyURLToFile(
						new URL(getDownloadURL(version)), 
						new File(getArchiveName(version, clientDirectory) + ".zip"));
				new ZipFile(new File(getArchiveName(version, clientDirectory) + ".zip"))
				.extractAll(getArchiveName(version, clientDirectory));
				Files.deleteIfExists(Path.of(getArchiveName(version, clientDirectory) + ".zip"));
				return true;
			} catch (IOException e) {
				logger.error(e.getMessage());
				FileUtils.deleteQuietly(new File(getArchiveName(version, clientDirectory) + ".zip"));
				FileUtils.deleteQuietly(new File(getArchiveName(version, clientDirectory)));
			}
		}		
		return false;
	}

	public static String[] getAvailableVersions() {
		try {
			URL url = new URL("https://archive.synology.com/download/Utility/SurveillanceStationClient");
			Document doc = Jsoup.parse(url, 3000);
			ArrayList<String> versions = new ArrayList<>();
			Element table = doc.select("table").get(0);
			Elements rows = table.select("tr");

			for (int i = 2; i < rows.size(); i++) {
				Element row = rows.get(i);
				Element link = row.select("a").get(0);
				versions.add(link.text());
			}
			return versions.toArray(new String[versions.size()]);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new String[0];
		}
	}	
}
