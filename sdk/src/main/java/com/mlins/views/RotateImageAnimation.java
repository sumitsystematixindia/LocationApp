package com.mlins.views;


import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;


public class RotateImageAnimation extends RotateAnimation {

    private float toAngle;
    private float fromAngle;
    private TouchImageView view = null;
    private float newAngle = 0;
    private float delta = 0;


    public RotateImageAnimation(TouchImageView view, float fromDegrees, float toDegrees) {

        super(fromDegrees, toDegrees);
        this.fromAngle = fromDegrees;
        this.toAngle = toDegrees;
        this.view = view;

        float delta1 = Math.abs(toAngle - fromAngle);

        float delta2 = 360.0f - delta1;


        delta = Math.min(delta1, delta2);

        int direction = calculateDirection(toAngle, fromAngle);
        delta = delta * direction;


    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
//		//super.applyTransformation(interpolatedTime, t);
//		float fangle = fromAngle;
//		float tangle = toAngle;
//		
//		System.out.println("f= "+ fangle + " " + "t = " + tangle);
//		
////		if(fangle > tangle){
////			return;
////		}
//		
//		if(fangle<= 360 && fangle > 180 && tangle < 180){
//			tangle = 360;
//			System.out.println("cond 1");
//		}
//		
//		if((fangle>=358 || fangle<=1) && tangle > 180){
//			fangle = 360;
//			System.out.println("cond 2");
//		}
//		
//		if(Math.abs(fangle-tangle)>=270){
//			return;
//		}
//		
//		newAngle =  (fangle  + (tangle - fangle )*interpolatedTime); 
//		view.setImageRotation(newAngle);


        newAngle = fromAngle + delta * interpolatedTime;
        view.setImageRotation(newAngle);


    }

    private int calculateDirection(float to, float from) {
        if (Math.abs(to - from) < 180) {
            if ((to - from) < 0) {
                return -1;
            }
            return 1;
        } else if (Math.abs(to - from) > 180 && (to - from) < 0) {
            return 1;
        }
        return -1;
    }


}

/*
import com.com.mlins.utils.FacilityConf;
import com.com.mlins.utils.FacilityContainer;
import com.com.mlins.utils.PropertyHolder;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

public class RotateImageAnimation extends Animation {


	private float toAngle;
	private float fromAngle;
	private TouchImageView view = null;
	float newAngle = 0;
	
	
	public RotateImageAnimation(float toangle, TouchImageView tiv) {
		super();
		
		//normelize values
		toAngle = (toangle+ 360)%360;
		view = tiv;
		if (PropertyHolder.getInstance().isLocationPlayer()) {
			fromAngle = ((view.getImageRotation() - FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360)%360;
		} else {
			FacilityConf facConf= FacilityContainer.getInstance().getCurrent();
			if(facConf!=null){
				fromAngle = ((view.getImageRotation() - facConf.getFloorRotation()) + 360)%360;
			}
		}
		
		
//		if (Math.abs(toAngle - fromAngle ) > 180)
//		{
//			float tmp = toAngle;
//			toangle = fromAngle;
//			fromAngle = tmp;
//			
//		} 
		
		if (Math.abs(toAngle - fromAngle ) > 180){
			toangle -= 180;
		}
		
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		
		newAngle =  (fromAngle  + (toAngle - fromAngle )*interpolatedTime); 
		view.setImageRotation(newAngle);
		
	}
}
*/
