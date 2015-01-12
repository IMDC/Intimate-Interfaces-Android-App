package com.example.intimateinterfaces;

/**
 * Responsible for converting draw data into definitive values
 * 
 * @author IMDC
 *
 */
public class DataConverter {

	private AnimationSpeed animationSpeed;
	private static final int THRESHHOLD = 500;
	
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
		
		if(rawValue < THRESHHOLD)
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
