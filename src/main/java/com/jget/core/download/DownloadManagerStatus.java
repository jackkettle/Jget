package com.jget.core.download;


public class DownloadManagerStatus {

	private int activeThreads;
	
	private int frontierSize;
	
	private int linkMapSize;
	
	private int processLinks;
	
	 private int elapsedTimeSeconds;
	
	public int getActiveThreads () {
		return activeThreads;
	}

	
	public void setActiveThreads (int activeThreads) {
		this.activeThreads = activeThreads;
	}

	
	public int getFrontierSize () {
		return frontierSize;
	}

	
	public void setFrontierSize (int frontierSize) {
		this.frontierSize = frontierSize;
	}

	
	public int getLinkMapSize () {
		return linkMapSize;
	}

	
	public void setLinkMapSize (int linkMapSize) {
		this.linkMapSize = linkMapSize;
	}

	
	public int getProcessLinks () {
		return processLinks;
	}

	
	public void setProcessLinks (int processLinks) {
		this.processLinks = processLinks;
	}


	public int getElapsedTimeSeconds () {
		return elapsedTimeSeconds;
	}


	public void setElapsedTimeSeconds (int elapsedTimeSeconds) {
		this.elapsedTimeSeconds = elapsedTimeSeconds;
	}

	@Override
	public String toString () {
		return "DownloadManagerStatus [activeThreads=" + activeThreads + ", frontierSize=" + frontierSize + ", linkMapSize=" + linkMapSize
				+ ", processLinks=" + processLinks + ", elapsedTimeSeconds=" + elapsedTimeSeconds + "]";
	}
	
}
