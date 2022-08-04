/*
 * Copyright (C) 2021 Matthew Rosato
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.t07m.console.Command;
import com.t07m.console.Console;
import com.t07m.synolvm.system.hardware.DisplayHandler;
import com.t07m.synolvm.system.hardware.DisplayHandler.Display;

import joptsimple.OptionSet;

public class DisplaysCommand extends Command{

	private final long displayTime = TimeUnit.SECONDS.toMillis(10);

	private final Object displaySync = new Object();
	private boolean displaying = false;

	public DisplaysCommand() {
		super("Displays");
	}

	public void process(OptionSet optionSet, Console console) {
		synchronized(displaySync) {
			if(!displaying) {
				displayFrames();
			}
		}
	}

	private void displayFrames() {
		JFrame[] frames = createFrames(DisplayHandler.queryDisplays());
		if(frames.length > 0) {
			displaying = true;
			for(JFrame frame : frames) {
				frame.setVisible(true);
			}
			createDisposeThread(frames).start();
		}
	}

	private Thread createDisposeThread(JFrame[] frames) {
		return new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				while(System.currentTimeMillis() - start < displayTime) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
				for(JFrame frame : frames) {
					frame.dispose();
				}
				synchronized(displaySync) {
					displaying = false;
				}
			}
		};
	}

	private JFrame[] createFrames(Display[] displays) {
		ArrayList<JFrame> frames = new ArrayList<JFrame>();
		for(int i = 0; i < displays.length; i++) {
			Display display = displays[i];
			JFrame frame = new JFrame("Screen: " + i);  
			JPanel panel = new JPanel();  
			Rectangle dims = display.getRect(false);
			panel.setBackground(Color.BLACK);
			panel.setLayout(new GridLayout(0, 1));  
			JLabel label = new JLabel("<html><center>" + display.getNumber() + "<br>" +
					display.getResolution().width + "x" + display.getResolution().height + "<br>" + 
					"</center></html>", SwingConstants.CENTER);
			Font font = new Font(Font.SANS_SERIF, Font.BOLD, 120);
			label.setForeground(Color.WHITE);
			label.setFont(font);
			panel.add(label, BorderLayout.CENTER);  
			JButton jButton = new JButton("click to exit");
		    jButton.setForeground(Color.gray);
		    jButton.setOpaque(false);
		    jButton.setContentAreaFilled(false);
		    jButton.setBorder(BorderFactory.createEmptyBorder());
		    jButton.addActionListener(new ActionListener() {
		          public void actionPerformed(ActionEvent param1ActionEvent) {
		        	 frame.dispose();
		          }
		        });
		    panel.add(jButton, BorderLayout.CENTER);
			frame.add(panel);  
			panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			frame.setUndecorated(true);
			frame.setVisible(true);
			frame.setSize(dims.width, dims.height);
			frame.setVisible(false);
			frame.setLocation(display.getX(), display.getY());
			frame.setAlwaysOnTop(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frames.add(frame);
		}
		return frames.toArray(new JFrame[frames.size()]);
	}


}
