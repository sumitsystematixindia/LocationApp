package com.location.app;

import android.widget.SeekBar;

import com.location.app.model.IntParam;

//import objects.IntParam;

public class SeekBarHelper {

	public static void setupSeekBar(SeekBar seekBar, IntParam param){
		seekBar.setMax(param.getTotal());
		seekBar.setProgress(param.getValue() - param.getMinValue());
	}

	public static int getValueFromSeekBar(IntParam param, SeekBar seekBar){
		return param.getMinValue() + seekBar.getProgress();
	}

}
