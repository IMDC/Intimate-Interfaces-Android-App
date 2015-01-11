package com.example.intimateinterfaces;

/**
 * Responsible for converting draw data into definitive values
 * 
 * @author IMDC
 *
 */
public class DataConverter {

	AnimationSpeed animationSpeed;
	
	public DataConverter() {
		//set a default value of 0 which will yield 
		//a slow animation speed 
		this.convert(0.0, 0.0); 
	}
	
	/**
	 * Based on the velocity and scale, a raw value is calculated
	 * and based on a threshold the animation speed is set.
	 * 
	 * @param scale
	 * @param velocity
	 */
	public void convert(double scale, double velocity) {
		double rawValue = scale * velocity;
		
		if(rawValue < 500)
			animationSpeed = AnimationSpeed.SLOW;
		else
			animationSpeed = AnimationSpeed.FAST;
		
		return;
	}

	/**
	 * Getter for the animationSpeed
	 * @return
	 */
	public AnimationSpeed getAnimationSpeed() {
		return animationSpeed;
	}
}
