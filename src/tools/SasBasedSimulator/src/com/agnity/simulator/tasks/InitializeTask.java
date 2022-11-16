package com.agnity.simulator.tasks;

import com.agnity.simulator.InapIsupSimServlet;

public class InitializeTask implements Runnable {
	
	private InapIsupSimServlet servlet;

	public InitializeTask(InapIsupSimServlet servlet) {
		this.servlet= servlet;
	}

	@Override
	public void run() {
		initialize();

	}

	private void initialize() {
		servlet.initializeAndStartFlow();
		
	}

}
