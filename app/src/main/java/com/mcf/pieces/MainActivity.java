package com.mcf.pieces;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvTrapCount,tvStepCount,tvTrapXY;
    private Button btnStart,btnReSet;
    private Random random;
    //陷阱的个数
    private int mTrap = -1;
    private MyHandler mHandler;
    //陷阱集合
    private List<String> trap = new ArrayList<>();
    private final int UP = 0;
    private final int LEFT = 1;
    private final int DOWN = 2;
    private final int RIGHT = 3;
    //棋子的当前X坐标
    private int currentX = 0;
    //棋子的当前Y坐标
    private int currentY = 0;
    //棋子移动的动作
    private int mChessAction = -1;
    //是否踩中陷阱
    private boolean isDel;
    //陷阱的X坐标
    private int[] coordX;
    //陷阱的Y坐标
    private int[] coordY;
    //步数
    private int mStepCount = 0;


    public static class MyHandler extends Handler{
        WeakReference<Activity> weakReference;
        public MyHandler(Activity activity){
             this.weakReference = new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = (MainActivity) weakReference.get();
            if(activity == null || activity.isFinishing())
                return;
            switch (msg.what){
                case 0:
                    activity.initEnd();
                    break;
                case 101:
                    activity.route();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        randomTrap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void randomTrap() {
        //随机产生陷阱数N个
        mTrap = random.nextInt(9);
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int count=0;
                while (count<mTrap+1){
                    //随机产生陷阱的X Y坐标
                    int x = random.nextInt(3);
                    int y = random.nextInt(3);
                    if(x == 0 && y==0)
                        continue;
                    if(count == 0){
                        trap.add("("+String.valueOf(x)+"，"+String.valueOf(y)+")");
                        count++;
                    }else{
                        int size = trap.size();
                        boolean isAdded = false;
                        for (int i=0;i<size;i++){
                             if(trap.get(i).equals("("+String.valueOf(x)+"，"+String.valueOf(y)+")")){
                                 isAdded = true;
                             }
                        }
                        if(!isAdded){
                            trap.add("("+String.valueOf(x)+"，"+String.valueOf(y)+")");
                            count++;
                        }
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private void initEnd() {
        tvTrapCount.setText("陷阱的个数:"+String.valueOf(mTrap+1));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("3*3棋盘陷阱的坐标有："+"\n");
        for(int i=0;i<trap.size();i++){
            stringBuffer.append(trap.get(i));
        }
        tvTrapXY.setText(stringBuffer);
        if(coordX == null)
            coordX = new int[mTrap+1];
        if(coordY == null)
            coordY = new int[mTrap+1];
        for(int i=0;i<trap.size();i++){
            coordX[i] = Integer.parseInt(trap.get(i).substring(1, trap.get(i).length() - 1).split("，")[0]);
            coordY[i] = Integer.parseInt(trap.get(i).substring(1,trap.get(i).length()-1).split("，")[1]);
        }
    }

    private void initView() {
        tvTrapXY = (TextView) findViewById(R.id.tv_trap_xy);
        tvTrapCount = (TextView) findViewById(R.id.tv_trap_count);
        tvStepCount = (TextView) findViewById(R.id.tv_step_count);
        btnReSet = (Button) findViewById(R.id.btn_reset);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnReSet.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        random = new Random();
        mHandler = new MyHandler(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.btn_reset:
//                if(trap !=null)
//                    trap.clear();
//                mHandler.removeMessages(101);
//                currentX = 0;
//                currentY = 0;
//                randomTrap();
//                break;
            case R.id.btn_start:
                calculateStep();
                break;
        }
    }

    private void route() {
        //棋子的随机动作
        mChessAction = random.nextInt(4);
        if(currentY == 0 && mChessAction == UP){
            mHandler.sendEmptyMessage(101);
            return;
        }
        if(currentX == 2 && mChessAction == RIGHT){
            mHandler.sendEmptyMessage(101);
            return;
        }
        if(currentX == 0 && mChessAction == LEFT){
            mHandler.sendEmptyMessage(101);
            return;
        }
        if(currentY == 2 && mChessAction == DOWN){
            mHandler.sendEmptyMessage(101);
            return;
        }
        Toast.makeText(MainActivity.this,"目前的坐标是"+currentX+"，"+currentY,Toast.LENGTH_SHORT).show();
        switch (mChessAction){
            case UP:
                Toast.makeText(MainActivity.this,"棋子将往上移动",Toast.LENGTH_SHORT).show();
                currentY = currentY-1;
                break;
            case LEFT:
                Toast.makeText(MainActivity.this,"棋子将往左移动",Toast.LENGTH_SHORT).show();
                currentX = currentX-1;
                break;
            case DOWN:
                Toast.makeText(MainActivity.this,"棋子将往下移动",Toast.LENGTH_SHORT).show();
                currentY = currentY+1;
                break;
            case RIGHT:
                Toast.makeText(MainActivity.this,"棋子将往右移动",Toast.LENGTH_SHORT).show();
                currentX = currentX+1;
                break;
        }
        for(int i=0;i<coordX.length;i++){
            if(currentX == coordX[i]){
                for(int j=0;j<coordY.length;j++){
                    if(currentY == coordY[j]){
                        isDel = true;
                    }
                }
            }
        }
        mHandler.removeMessages(101);
        if(isDel){
            Toast.makeText(MainActivity.this,"在"+currentX+"，"+currentY+"踩入陷阱了,走了"+String.valueOf(mStepCount)+"步",Toast.LENGTH_SHORT).show();
            tvStepCount.setText("走了"+String.valueOf(mStepCount)+"步");
        }else{
            mHandler.sendEmptyMessageDelayed(101,1000);
            mStepCount++;
        }
    }

    //棋子开始移动
    private void calculateStep() {
        mHandler.sendEmptyMessage(101);
    }
}
