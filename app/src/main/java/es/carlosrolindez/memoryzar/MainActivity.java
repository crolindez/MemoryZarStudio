package es.carlosrolindez.memoryzar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import java.util.Random;



public class MainActivity extends Activity {

	public static final int REPEATING_MODE = 0;
	public static final int TEACHING_MODE = 1;
	
	public static final int MAX_SEQUENCE_SIZE = 8;
	
	public static SoundPool sp;

	protected SoundPool createSoundPool() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			AudioAttributes attributes = new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_GAME)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.build();
			return new SoundPool.Builder()
					.setAudioAttributes(attributes)
					.build();
		} else {
			return new SoundPool(6,AudioManager.STREAM_MUSIC,0);
		}
	}




	public enum ButtonPosition implements Parcelable {
        UpLeft, UpRight, DownLeft, DownRight;

        private static final ButtonPosition[] arrayButton = ButtonPosition.values();

	    @Override
	    public int describeContents() {
	        return 0;
	    }

	    @Override
	    public void writeToParcel(final Parcel dest, final int flags) {
	        dest.writeInt(ordinal());
	    }

	    public static final Creator<ButtonPosition> CREATOR = new Creator<ButtonPosition>() {
	        @Override
	        public ButtonPosition createFromParcel(final Parcel source) {
	            return arrayButton[source.readInt()];
	        }

	        @Override
	        public ButtonPosition[] newArray(final int size) {
	            return new ButtonPosition[size];
	        }
	    };
	}
	

	
	public static ButtonPosition[] buttonSequence = new ButtonPosition[MAX_SEQUENCE_SIZE];
	
	public static int teachingButtonSequenceIndex;
	public static int repeatingButtonSequenceIndex;
	public static int behaviourMode;
	
	public static final String SavedButtonSequence = "SAVED_BUTTON_SEQUENCE";
	public static final String SavedTeachingButtonSequenceIndex = "SAVED_TEACHING_BUTTON_SEQUENCE_INDEX";
	public static final String SavedRepeatingButtonSequenceIndex = "SAVED_REPEATING_BUTTON_SEQUENCE_INDEX";
	
	public static int upLeftSound;
	public static int upRightSound; 
	public static int downRightSound; 
	public static int downLeftSound;
	
	public static int errorSound;
	public static int bobSound;	
	
	public static int playingStream;
	
	public Button upLeftButton;
	public Button upRightButton;
	public Button downRightButton;
	public Button downLeftButton;
    
    public static final Random random = new Random();
    public final SequenceAnimator animator = new SequenceAnimator();
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = createSoundPool();

		upLeftSound    = sp.load(this, R.raw.mi,  1); 
		upRightSound   = sp.load(this, R.raw.la,  1); 
		downRightSound = sp.load(this, R.raw.re_, 1); 
		downLeftSound  = sp.load(this, R.raw.sol_, 1); 

		errorSound = sp.load(this, R.raw.erroooor, 1);
		
		bobSound = sp.load(this, R.raw.bob, 1);
		
	    upLeftButton = (Button) findViewById(R.id.button_upleft);
	    upRightButton = (Button) findViewById(R.id.button_upright);
	    downRightButton = (Button) findViewById(R.id.button_downright);
	    downLeftButton = (Button) findViewById(R.id.button_downleft);
		    
	    behaviourMode = TEACHING_MODE;
	    
     	if ( (savedInstanceState != null) && (teachingButtonSequenceIndex > 0) ) 
      	{

      		buttonSequence = (ButtonPosition[]) savedInstanceState.getParcelableArray(SavedButtonSequence);
    	    teachingButtonSequenceIndex = savedInstanceState.getInt(SavedTeachingButtonSequenceIndex);
    	    repeatingButtonSequenceIndex = savedInstanceState.getInt(SavedRepeatingButtonSequenceIndex);     
    	    animator.showAnimator();
      		
      	}

		
	    upLeftButton.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	if((event.getAction() == MotionEvent.ACTION_DOWN)  && (behaviourMode == REPEATING_MODE))
	        	{
	        		playingStream = sp.play(upLeftSound, 1, 1, 0, 0, 1);

	        	}
	        	else if (event.getAction() == MotionEvent.ACTION_UP) 
	        	{
	    			sp.stop(playingStream);
	        		v.performClick();
	        		if (behaviourMode == REPEATING_MODE)
	        			checkButton(ButtonPosition.UpLeft);	        	   
	        	}
	           return false;
	        }
	     });

	
		upRightButton.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	if ((event.getAction() == MotionEvent.ACTION_DOWN)  && (behaviourMode == REPEATING_MODE)) 
	        	{
	        		playingStream = sp.play(upRightSound, 1, 1, 0, 0, 1);
	        	}
	        	else if (event.getAction() == MotionEvent.ACTION_UP) 
	        	{
	    			sp.stop(playingStream);
	        		v.performClick();
	        		if (behaviourMode == REPEATING_MODE)
	        			checkButton(ButtonPosition.UpRight);	        	   
	        	}
	        	return false;
	        }
	     });
	
		downRightButton.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	if ((event.getAction() == MotionEvent.ACTION_DOWN)  && (behaviourMode == REPEATING_MODE)) 
	        	{
	        		playingStream = sp.play(downRightSound, 1, 1, 0, 0, 1);
	        	}
	        	else if (event.getAction() == MotionEvent.ACTION_UP) 
	        	{
	    			sp.stop(playingStream);
	        		v.performClick();
	        		if (behaviourMode == REPEATING_MODE)
	        			checkButton(ButtonPosition.DownRight);	        	   
	        	}
	        	return false;
	        }
	     });
	
		downLeftButton.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	if ((event.getAction() == MotionEvent.ACTION_DOWN)  && (behaviourMode == REPEATING_MODE)) 
	        	{
	        		playingStream = sp.play(downLeftSound, 1, 1, 0, 0, 1);
	        	}
	        	else if (event.getAction() == MotionEvent.ACTION_UP) 
	        	{
	    			sp.stop(playingStream);
	        		v.performClick();
	        		if (behaviourMode == REPEATING_MODE)
	        			checkButton(ButtonPosition.DownLeft);	        	   
	        	}
	        	return false;
	        }
	     });
		


		
	}
	
	public void start(View v) {
	    teachingButtonSequenceIndex = 0;
	    repeatingButtonSequenceIndex = 0;
	    addNewButton2Animation();
	}
	
	public void playError(){
		playingStream = sp.play(errorSound, 1, 1, 0, 0, 1);	
	}
	
	
	public void playBob(){
		playingStream = sp.play(bobSound, 1, 1, 0, 0, 1);	
	}
    @Override
    public void onSaveInstanceState(Bundle savedState) 
    {
	   	super.onSaveInstanceState(savedState);
        savedState.putParcelableArray(SavedButtonSequence, buttonSequence);
        savedState.putInt(SavedTeachingButtonSequenceIndex, teachingButtonSequenceIndex);
        savedState.putInt(SavedRepeatingButtonSequenceIndex, repeatingButtonSequenceIndex);
	} 

    @Override
    public void onPause()
    {
		animator.stopAnimator();
	   	super.onPause();

	} 
    
    public ButtonPosition randomButton() {
        int pick = random.nextInt(ButtonPosition.values().length);
        return ButtonPosition.values()[pick];
    }
    
	public void addNewButton2Animation() {
		if (teachingButtonSequenceIndex >= MAX_SEQUENCE_SIZE) {
			teachingButtonSequenceIndex = 0;
			playBob();

			Thread thread=  new Thread(){
		        @Override
		        public void run(){
		            try {
		                synchronized(this){
		                    wait(3000);
		                }
		            }
		            catch(InterruptedException ex){                    
		            }
		        }
		    };

		    thread.start();        

		}
		else {
    		buttonSequence[teachingButtonSequenceIndex++] = randomButton();
    		animator.showAnimator();			
		}
		

		
	}
	
    
    public class SequenceAnimator {
    	
   	
    	final int initialPause = 5;
    	final int duration = 8;
    	final int pause = 2;
    	
    	boolean playing;
    	int nextValue;
    	int endValue; 
    	int stepSequence;
    	
    	public ValueAnimator va;

    	
    	public void showAnimator() {

    		
    		endValue = initialPause + teachingButtonSequenceIndex*(duration+pause);
    		nextValue = initialPause;
    		stepSequence = 0;
    		playing = false;
    		
    	    va = ValueAnimator.ofInt(0, endValue);
    	    va.setDuration(endValue*(long)100);
    	    va.addListener(new ValueAnimator.AnimatorListener() {
  
				@Override
				public void onAnimationCancel(Animator arg0) {
    				sp.stop(playingStream);	
            		playing = false;
            		behaviourMode = REPEATING_MODE;
				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					
				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
					
				}

				@Override
				public void onAnimationStart(Animator arg0) {
					
				}

    	    });
    	    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    	        public void onAnimationUpdate(ValueAnimator animation) {
    	            Integer value = (Integer) animation.getAnimatedValue();
//            		Log.e("Value",""+value);
//            		Log.e("NextValue",""+nextValue);
    	            if (value >= nextValue)
    	            {
    	            	if (playing) {
    	            		resetButton(buttonSequence[stepSequence++]);
    	            		nextValue += pause;
    	            		playing = false;
    	            		if (stepSequence >= teachingButtonSequenceIndex) 
    	            		{
    	            			animation.cancel();
    	            			behaviourMode = REPEATING_MODE;
    	            		}
    	            	}
    	            	else 
    	            	{
                			setButton(buttonSequence[stepSequence]);
                			nextValue += duration;	
                			playing = true;	            
    	            	}    	            	
    	            }	            
    	        }
    	    });

    	    va.start();
    	}	
    	
     	public void stopAnimator() {
    	    if (va!=null) va.cancel();
    	}	
   	
    }
	
	
	public void checkButton(ButtonPosition button)
	{
		if (buttonSequence[repeatingButtonSequenceIndex] != button) {
			behaviourMode = TEACHING_MODE;   
			repeatingButtonSequenceIndex = 0;
			playError();
			animator.showAnimator();
		}	
		else
		{
			if (++repeatingButtonSequenceIndex == teachingButtonSequenceIndex)
			{
				behaviourMode = TEACHING_MODE;
				repeatingButtonSequenceIndex = 0;
				addNewButton2Animation();
			}
		}
	}
    

	public void setButton(ButtonPosition position)
	{
		switch (position) 
		{
		case UpLeft:
			upLeftButton.setPressed(true); 
			upLeftButton.invalidate(); 
			playingStream = sp.play(upLeftSound, 1, 1, 0, 0, 1);		
			break;
			
		case UpRight:
			upRightButton.setPressed(true); 
			upRightButton.invalidate(); 
			playingStream = sp.play(upRightSound, 1, 1, 0, 0, 1);		
			break;
			
		case DownLeft:
			downLeftButton.setPressed(true); 
			downLeftButton.invalidate(); 
			playingStream = sp.play(downLeftSound, 1, 1, 0, 0, 1);		
			break;
			
		case DownRight:
			downRightButton.setPressed(true); 
			downRightButton.invalidate(); 
			playingStream = sp.play(downRightSound, 1, 1, 0, 0, 1);		
			break;
			
		}
	}

	public void resetButton(ButtonPosition position)
	{
		switch (position) 
		{
		case UpLeft:
			upLeftButton.setPressed(false); 
			upLeftButton.invalidate(); 
			sp.stop(playingStream);
			break;
			
		case UpRight:
			upRightButton.setPressed(false); 
			upRightButton.invalidate(); 
			sp.stop(playingStream);	
			break;
			
		case DownLeft:
			downLeftButton.setPressed(false); 
			downLeftButton.invalidate(); 
			sp.stop(playingStream);
			break;
			
		case DownRight:
			downRightButton.setPressed(false); 
			downRightButton.invalidate(); 
			sp.stop(playingStream);
			break;
			
		}
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
