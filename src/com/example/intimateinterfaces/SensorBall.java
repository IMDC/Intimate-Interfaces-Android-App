package com.example.intimateinterfaces;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class SensorBall extends View {
	
	private Context context;
  
    //Create a paint for the stroke  
    private Paint circlePaint;
  
    //Two floats to store the touch position  
    private float radius = 200; //default radius
    private final int MIN_RADIUS = 200;
    private float viewWidth;
    private float viewHeight;
    private float posX = 0;
    private float posY = 0;
    private float centreX = 0;
    private float centreY = 0;
    
    //Animation Variables
    private int xVelocity;
    private int yVelocity;
    private Handler h;
    private final int FRAME_RATE = 10;
    private boolean rePosition;
    
    //Scale variables
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    
    //Timer Variables
    private final double UPDATE_TIME = 100; //100 milliseconds
    private float prevPosX = -1;
    private float prevPosY = -1;

	public SensorBall(Context context) {
        super(context);
        init(context);
    }

    public SensorBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SensorBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    /**
     * Thread that runs the animation
     */
    private Runnable r = new Runnable() {
        @Override
        public void run() {
        	invalidate(); //calls the onDraw Method
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(posX, posY, radius, circlePaint);
        
        if(rePosition) {
        	if(withinCircle(centreX, centreY, 10)) {
        		posX = centreX;
        		posY = centreY;
        		rePosition = false;
        	}
        	else {
        		xVelocity = (int) Math.ceil((centreX - posX) * 0.1);
        		yVelocity = (int) Math.ceil((centreY - posY) * 0.1);
        		posX += xVelocity;
        		posY += yVelocity;
        	}
        	h.postDelayed(r, FRAME_RATE);
        }
        else {
        	h.removeCallbacks(r);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	// Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(event);
        
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {   
        	//Store the position of the touch event at 'posX' and 'posY'  
        	case MotionEvent.ACTION_MOVE: {
        		if( !mScaleDetector.isInProgress() &&
        				withinCircle(event.getX(), event.getY(), radius)) { //make sure the user is touching within the sensor ball
            		posX = event.getX();
                    posY = event.getY();
                    rePosition = false;
                    invalidate();
            	}
        		break;
        	}
        	
        	case MotionEvent.ACTION_UP: {
        		rePosition = true;
            	invalidate();
            	break;
        	}
        }
        return true;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        viewWidth = widthSize;
        viewHeight = heightSize;
        posX = viewWidth / 2; //centre of the screen
        centreX = posX;
        
        posY = viewHeight / 2; //centre of the screen plus the radius
        centreY = posY;

        //MUST CALL THIS
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
        
//        Log.d("Screen: onMeasure", "Width: " + viewWidth + " Height: " + viewHeight +
//        		" New Positions {X:" + posX + " Y: " + posY + "}");
    }
    
    /**
     * Initialise the view when it is created
     */
    protected void init(Context context) {
    	
    	h = new Handler();
    	
    	this.context = context;
    	
    	mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    	
    	//This View can receive focus, so it can react to touch events.
        this.setFocusable(true);
  
        //Initialize the strokePaint object and define some of its parameters  
        circlePaint = new Paint();
        circlePaint.setDither(true);
        circlePaint.setColor(0xFF8AAAC7);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(3);
        
        //timer for finding position and calculate the velocity
        Timer timer = new Timer();
        timer.schedule(new PositionTimer(), 0, (int) UPDATE_TIME);

    }
    
    /**
     * Using Pythagoras to measure the distance between
     * the new point and the center to see if it's lower than the radius.
     * Used to see if the user has touched the sensor ball or not.
     */
    private boolean withinCircle(float x, float y, float rad) {
    	float dist = (float) Math.sqrt(Math.pow(posX - x, 2) + Math.pow(posY - y, 2));
    	return dist < rad;
    }
    
    /**
     * Used to calculate the velocity of the sensor ball
     */
    private double calculateVelocity(float x1, float x2, float y1, float y2) {
    	float xDist = Math.abs(x2 - x1);
    	float yDist = Math.abs(y2 - y1);
    	double distance = (float) Math.sqrt(Math.pow(xDist,2) + Math.pow(yDist, 2));
    	double time = UPDATE_TIME / 1000;
    	
    	return distance / time;
    }
    
    /**
     * Used to calculate the velocity of the sensor ball.
     * This data is then sent to a converter to indicate whether 
     * the combination of velocity and scale will be classified as "SLOW"
     * or "FAST". The converter will be sent to the data sender which sends this
     * data to the II server 
     */
    private class PositionTimer extends TimerTask {
    	DataSender dataSender = new DataSender(context);
    	DataConverter dataConverter = new DataConverter(); 
		
    	@Override
        public void run() {
    		if(prevPosX != -1 && prevPosY != -1) {
    			double velocity = calculateVelocity(prevPosX, posX, prevPosY, posY);
    			//Log.d("Velocity", "Vel: " + velocity + "Scale: " + mScaleFactor + " RawVal: " + velocity * mScaleFactor);
    			prevPosX = -1;
    			prevPosY = -1;
    			
    			dataConverter.convert(mScaleFactor, velocity);
    			dataSender.sendData(dataConverter);
    		}
    		else {
    			prevPosX = posX;
    			prevPosY = posY;
    		}
        }
     }
    
    
    /**
     * Listens to the Scale events (radius of the sensor ball)
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            
            float span = detector.getCurrentSpan() / 2;
            posX = detector.getFocusX();
            posY = detector.getFocusY();
            
            if(span > (Math.min(viewWidth, viewHeight) / 2))
            	radius = Math.min(viewWidth, viewHeight) / 2;
            else if (span < MIN_RADIUS)
            	radius = MIN_RADIUS;
            else
            	radius = span;
            
            invalidate();
            return true;
        }
    }
    
}
