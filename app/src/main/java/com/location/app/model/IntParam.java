package com.location.app.model;

public class IntParam {

	int maxValue;
	int minValue;

	int value;

	public IntParam(int minValue, int maxValue, int defValue) {
		if(minValue > maxValue)
			throw new IllegalArgumentException("minValue > maxValue");
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.value = defValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	public void set(int value) {
		checkValue(value);
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	public int checkValue(int value){
		if(value < minValue ||  value > maxValue)
			throw new IllegalStateException("value < " + minValue + " ||  value > " + maxValue);
		return value;
	}

	public int getTotal(){
		return maxValue - minValue;
	}


}
