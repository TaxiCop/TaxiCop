package com.taxicop.authenticator;

public class StopWatch2 {

	public long startTimeOne, startTimeTwo;
	public long stopTimeOne, stopTimeTwo;
	public long elapsedTimeOne, elapsedTimeTwo;
	public boolean runningTimeOne, runningTimeTwo;
	public long accumTimeOne, accumTimeTwo;
	public long N;

	public StopWatch2() {
		startTimeOne = 0;
		stopTimeOne = 0;
		elapsedTimeOne = 0;
		runningTimeOne = false;
		accumTimeOne = 0;
		startTimeTwo = 0;
		stopTimeTwo = 0;
		elapsedTimeTwo = 0;
		runningTimeTwo = false;
		accumTimeTwo = 0;
		N = 0;
	}

	public void resetCount() {
		N = 0;
	}

	public void IncrementCount() {
		N++;
	}

	public long getN() {
		return N;
	}

	public void setN(long n) {
		N = n;
	}

	public void startTimeOne() {
		this.startTimeOne = System.nanoTime();
		this.runningTimeOne = true;
	}

	public long getElapsedTimeOne() {
		if (runningTimeOne) {
			elapsedTimeOne = ((System.nanoTime() - startTimeOne) / 1000000);
		} else {
			elapsedTimeOne = ((stopTimeOne - startTimeOne) / 1000000);
		}
		return elapsedTimeOne;
	}

	public void stopTimeOne() {
		this.accumTimeOne = getElapsedTimeOne() + this.accumTimeOne;
		this.stopTimeOne = System.nanoTime();
		this.runningTimeOne = false;
	}

	public void startTimeTwo() {
		this.startTimeTwo = System.nanoTime();
		this.runningTimeTwo = true;
	}

	public long getElapsedTimeTwo() {
		if (runningTimeTwo) {
			elapsedTimeTwo = ((System.nanoTime() - startTimeTwo) / 1000000);
		} else {
			elapsedTimeTwo = ((stopTimeTwo - startTimeTwo) / 1000000);
		}
		return elapsedTimeTwo;
	}

	public void stopTimeTwo() {
		this.accumTimeTwo += getElapsedTimeTwo();
		this.stopTimeTwo = System.nanoTime();
		this.runningTimeTwo = false;
	}

	public void reset() {
		this.startTimeOne = 0;
		this.startTimeTwo = 0;
		this.stopTimeOne = 0;
		this.stopTimeTwo = 0;
		this.accumTimeOne = 0;
		this.accumTimeTwo = 0;
		this.runningTimeOne = false;
		this.runningTimeTwo = false;

	}
}