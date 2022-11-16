package com.baypackets.ase.container;

public final class WebContainerState {

	public static final WebContainerState STOPPED = new WebContainerState(new Short((short)0));
	public static final WebContainerState INITIALIZED = new WebContainerState(new Short((short)1));
	public static final WebContainerState RUNNING = new WebContainerState(new Short((short)2));

	private Short state;

	private WebContainerState(Short state) {
		this.state = state;
	}

	public String toString() {
		switch (state.shortValue()) {
			case 0 : return "STOPPED";
			case 1 : return "INITIALIZED";
			case 2 : return "RUNNING";
			default : return null;
		}
	}

	public boolean equals(Object obj) {
		return obj instanceof WebContainerState && ((WebContainerState)obj).state.equals(this.state);
	}

	public int hashCode() {
		return this.state.hashCode();
	}

}

