package com.example.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Random rand = new Random();
    ///frame
    private FrameLayout gameFrame;
    private int frameHeight , frameWidth , initialFrameWidth;
    private LinearLayout startLayout;

    ///pictures
    private ImageView box,black,pink,orange;
    private Drawable imageRight , imageLeft;

    ///size
    private int boxSize;//sq box so width=height

    ///coordinates
    private float boxX , boxY , pinkX , pinkY , orangeX , orangeY , blackX , blackY;

    ///score
    private TextView scoreLabel , highScoreLabel;
    private int score , highScore , timeCount;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    /*private SoundPlayer soundPlayer;*/

    // Status
    private boolean start_flag = false; ///checking game start
    private boolean action_flag = false;///controlling box movement
    private boolean pink_flag = false;///whether pink ball will emerge
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);
        imageLeft = getResources().getDrawable(R.drawable.box_left);
        imageRight = getResources().getDrawable(R.drawable.box_right);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public void changePosition(){

        ///timecount
        timeCount+=20;
        ///orange
        orangeY += 12;
        if(isBallCaptured(orange)){
            score+=10;
            orangeY = 0;
            orangeX = rand.nextInt(frameWidth);
        }
        if(orangeY>=frameHeight){
            orangeY = 0;
            if(frameWidth-(int)orangeX>0) orangeX = rand.nextInt(frameWidth-(int)orangeX);
            else orangeX = rand.nextInt(frameWidth);

        }
        orange.setY(orangeY);
        orange.setX(orangeX);

        ///pink
        if(timeCount%10000==0 && !pink_flag){
            pink_flag = true;
        }
        if(pink_flag)pinkY+=15;
        if(isBallCaptured(pink)){
            score += 30;
            pinkY = -100;
            if(frameWidth-(int)pinkX>0)pinkX = rand.nextInt(frameWidth-(int)pinkX);
            else pinkX = rand.nextInt(frameWidth);
            pink_flag = false;
        }
        if(pinkY>=frameHeight){
            pinkY = -100;
            if(frameWidth-(int)pinkX>0)pinkX = rand.nextInt(frameWidth-(int)pinkX);
            else pinkX = rand.nextInt(frameWidth);
            pink_flag = false;
        }
        pink.setY(pinkY);
        pink.setX(pinkX);

        ///black
        blackY+=18;
        if(isBallCaptured(black)){
            blackY = 0;
            frameWidth = frameWidth*80/100;
            if(frameWidth<=boxSize){
                gameOver();
            }
            if(frameWidth-(int)blackX>0)blackX = rand.nextInt(frameWidth-(int)blackX);
            else blackX = rand.nextInt(frameWidth);
            changeWidth(frameWidth);
        }
        if(blackY>=frameHeight){
            blackY = 0;
            if(frameWidth-(int)blackX>0)blackX = rand.nextInt(frameWidth-(int)blackX);
            else blackX = rand.nextInt(frameWidth);
        }
        black.setX(blackX);
        black.setY(blackY);
        ///score
        scoreLabel.setText("Score : " + score);
    }

    public void changeWidth(int width){
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = width;
        gameFrame.setLayoutParams(params);
        frameWidth = width;
    }

    public void gameOver(){
        timer.cancel();
        timer = null;
        start_flag = false;

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeWidth(initialFrameWidth);
        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        pink.setVisibility(View.INVISIBLE);
        ///update High Score
        if(score>highScore){
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);
        }

    }
    public boolean isBallCaptured(ImageView ball){
        float radi =  ball.getWidth()/2;
        float centerX = ball.getX()+ radi;
        float centerY = ball.getY()+ radi;
        if(centerX>=boxX && centerX<=boxX+boxSize && centerY>=boxY)return true;
        else return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int remWall = (getScreenWidth()-frameWidth)/2;
        int remUpWall = (getScreenHeight()-frameHeight);
        if (start_flag) {
            int X = (int) event.getX() - remWall;
            int Y = (int) event.getY() - remUpWall;
            if (X>=boxX && Y>=(frameHeight-boxSize*2) &&  event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flag = true;
                boxX+=50;
                if(frameWidth - boxSize <= boxX){
                    boxX = frameWidth - boxSize;
                }
                box.setX(boxX);
            } else if (X<=boxX  &&  Y>=(frameHeight-boxSize*2) && event.getAction() == MotionEvent.ACTION_UP) {
                action_flag = false;
                boxX -=50;
                if(boxX<=0){
                    boxX = 0;
                }
                box.setX(boxX);
            }

        }
        return true;
    }

    public void startGame(View view){
        start_flag = true;
        startLayout.setVisibility(View.INVISIBLE);
        if(frameHeight==0){
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();

        }
        frameWidth = initialFrameWidth;

        box.setX(0);

        ///ball's Y coordinate
        black.setY(-100);
        orange.setY(-100);
        pink.setY(-100);

        blackY = black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();

        ///set random X coordinate to balls
        black.setX(rand.nextInt(frameWidth));
        orange.setX(rand.nextInt(frameWidth));
        pink.setX(rand.nextInt(frameWidth));

        blackX = black.getX();
        pinkX = pink.getX();
        orangeX = orange.getX();

        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : " + score);

        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (start_flag) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    changePosition();
                                }
                            });
                        }
                    }
                },0,20
        );


    }
    public void quitGame(View view){

    }
}
