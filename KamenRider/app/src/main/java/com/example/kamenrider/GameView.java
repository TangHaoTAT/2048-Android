package com.example.kamenrider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class GameView extends GridLayout {
    private static ObjectAnimator objectAnimatorX;
    private static ObjectAnimator objectAnimatorY;//声明ObjectAimator类
    private static Card[][] cardsBox=new Card[4][4];//卡片集合
    private static List<Point> emptyPoint=new ArrayList<>();//空卡片集合
    public GameView(Context context) {
        super(context);
        initGameView();
    }
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    private void initGameView(){
        setRowCount(4);//设置行数
        setColumnCount(4);//设置列数
        setBackgroundColor(0xFFBBADA0);//getResources().getColor(R.color.GameBackGround)
        addCards(getCardSize());//向布局中加入卡片
        startGame();//因为会比MainActivity中的loadingGame（）先执行，所以暂时不需要处理
        setOnTouchListener(new OnTouchListener() {
            private float startX,startY;//落手位置
            private float endX,endY;//抬手位置
            private float offX,offY;//偏移值
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX=motionEvent.getX();
                        startY=motionEvent.getY();
//                        System.out.println("startX:"+startX);
//                        System.out.println("startY:"+startY);
                        break;
                    case MotionEvent.ACTION_UP:
                        endX=motionEvent.getX();
                        endY=motionEvent.getY();
//                        System.out.println("endX:"+endX);
//                        System.out.println("endY:"+endY);
                        offX=endX-startX;
                        offY=endY-startY;
                        if (Math.abs(offX)>Math.abs(offY)){
//                            水平方向
                            if(offX<-5){
//                                Log.d("滑动方向","左");
                                slipLeft();
                            }else if(offX>5){
//                                Log.d("滑动方向","右");
                                slipRight();
                            }
                        }else {
//                            垂直方向
                            if(offY<-5){
//                                Log.d("滑动方向","上");
                                slipUp();
                            }else if(offY>5){
//                                Log.d("滑动方向","下");
                                slipDown();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void slipLeft(){
//        判断是否操作，操作后在产生一个新卡片
        boolean Act=false;
//        向左滑动
//        从左往右查询
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                for(int k=j+1;k<4;k++){
                    if(cardsBox[i][k].getNumber()>0){
                        if(cardsBox[i][j].getNumber()==0){
                            cardsBox[i][j].setNumber(cardsBox[i][k].getNumber());//左移
                            cardsBox[i][k].setNumber(0);//消值
                            Act=true;
                        }else if(cardsBox[i][j].equals(cardsBox[i][k])){
                            cardsBox[i][j].setNumber(cardsBox[i][j].getNumber()*2);//合并
                            objectAnimatorX=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleX",1f,1.1f,1f);
                            objectAnimatorY=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleY",1f,1.1f,1f);
                            AnimatorSet animatorSet=new AnimatorSet();
                            animatorSet.setDuration(500);
                            animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
                            animatorSet.start();
                            MainActivity.getMainActivity().addScore(cardsBox[i][j].getNumber());//加分
                            addScoreSuccess_musicPlay(1,0,MainActivity.MenuMusic_state);//加分后播放得分音效
                            cardsBox[i][k].setNumber(0);//消值
                            Act=true;
                            break;
                        }else {
                            break;
                        }
                    }
                }
            }
        }
        if(Act){
//            添加新的卡片
            setRandomNumber();
            int[] numberList=new int[16];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    numberList[k++]=cardsBox[i][j].getNumber();
                }
            }
            History history=new History(MainActivity.getMainActivity().getScore(),numberList);
            if (MainActivity.getHistoryStack().size()>=2){
                //入栈前先判断已有多少栈，将栈顶取出，保证至多只有2个栈，完成最多撤销一步操作
                History tempHistory=MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().push(tempHistory);
            }
            MainActivity.getHistoryStack().push(history);
//            判断游戏是否已经结束
            checkGame();
        }
    }
    private void slipRight(){
//        判断是否操作，操作后在产生一个新卡片
        boolean Act=false;
//        向右滑动
//        从右往左查询
        for(int i=0;i<4;i++){
            for(int j=3;j>=0;j--){
                for(int k=j-1;k>=0;k--){
                    if(cardsBox[i][k].getNumber()>0){
                        if(cardsBox[i][j].getNumber()==0){
                            cardsBox[i][j].setNumber(cardsBox[i][k].getNumber());//右移
                            cardsBox[i][k].setNumber(0);//消值
                            Act=true;
                        }else if(cardsBox[i][j].equals(cardsBox[i][k])){
                            cardsBox[i][j].setNumber(cardsBox[i][j].getNumber()*2);//合并
                            objectAnimatorX=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleX",1f,1.1f,1f);
                            objectAnimatorY=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleY",1f,1.1f,1f);
                            AnimatorSet animatorSet=new AnimatorSet();
                            animatorSet.setDuration(500);
                            animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
                            animatorSet.start();
                            MainActivity.getMainActivity().addScore(cardsBox[i][j].getNumber());//加分
                            addScoreSuccess_musicPlay(1,0,MainActivity.MenuMusic_state);//加分后播放得分音效
                            cardsBox[i][k].setNumber(0);//消值
                            Act=true;
                            break;
                        }else {
                            break;
                        }
                    }
                }
            }
        }
        if(Act){
//            添加新的卡片
            setRandomNumber();
            int[] numberList=new int[16];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    numberList[k++]=cardsBox[i][j].getNumber();
                }
            }
            History history=new History(MainActivity.getMainActivity().getScore(),numberList);
            if (MainActivity.getHistoryStack().size()>=2){
                //入栈前先判断已有多少栈，将栈顶取出，保证至多只有2个栈，完成最多撤销一步操作
                History tempHistory=MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().push(tempHistory);
            }
            MainActivity.getHistoryStack().push(history);
//            判断游戏是否已经结束
            checkGame();
        }
    }
    private void slipUp(){
//        判断是否操作，操作后在产生一个新卡片
        boolean Act=false;
//        向上滑动
//        从上往下查询
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                for(int k=i+1;k<4;k++){
                    if(cardsBox[k][j].getNumber()>0){
                        if(cardsBox[i][j].getNumber()==0){
                            cardsBox[i][j].setNumber(cardsBox[k][j].getNumber());//上移
                            cardsBox[k][j].setNumber(0);//消值
                            Act=true;
                        }else if(cardsBox[i][j].equals(cardsBox[k][j])){
                            cardsBox[i][j].setNumber(cardsBox[i][j].getNumber()*2);//合并
                            objectAnimatorX=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleX",1f,1.1f,1f);
                            objectAnimatorY=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleY",1f,1.1f,1f);
                            AnimatorSet animatorSet=new AnimatorSet();
                            animatorSet.setDuration(500);
                            animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
                            animatorSet.start();
                            MainActivity.getMainActivity().addScore(cardsBox[i][j].getNumber());//加分
                            addScoreSuccess_musicPlay(1,0,MainActivity.MenuMusic_state);//加分后播放得分音效
                            cardsBox[k][j].setNumber(0);//消值
                            Act=true;
                            break;
                        }else {
                            break;
                        }
                    }
                }
            }
        }
        if(Act){
//            添加新的卡片
            setRandomNumber();
            int[] numberList=new int[16];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    numberList[k++]=cardsBox[i][j].getNumber();
                }
            }
            History history=new History(MainActivity.getMainActivity().getScore(),numberList);
            if (MainActivity.getHistoryStack().size()>=2){
                //入栈前先判断已有多少栈，将栈顶取出，保证至多只有2个栈，完成最多撤销一步操作
                History tempHistory=MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().push(tempHistory);
            }
            MainActivity.getHistoryStack().push(history);
//            判断游戏是否已经结束
            checkGame();
        }
    }
    private void slipDown(){
//        判断是否操作，操作后在产生一个新卡片
        boolean Act=false;
//        向下滑动
//        从下往上查询
        for(int i=3;i>=0;i--){
            for(int j=0;j<4;j++){
                for(int k=i-1;k>=0;k--){
                    if(cardsBox[k][j].getNumber()>0){
                        if(cardsBox[i][j].getNumber()==0){
                            cardsBox[i][j].setNumber(cardsBox[k][j].getNumber());//下移
                            cardsBox[k][j].setNumber(0);//消值
                            Act=true;
                        }else if(cardsBox[i][j].equals(cardsBox[k][j])){
                            cardsBox[i][j].setNumber(cardsBox[i][j].getNumber()*2);//合并
                            objectAnimatorX=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleX",1f,1.1f,1f);
                            objectAnimatorY=ObjectAnimator.ofFloat(cardsBox[i][j].getTextViewNumber(),"scaleY",1f,1.1f,1f);
                            AnimatorSet animatorSet=new AnimatorSet();
                            animatorSet.setDuration(500);
                            animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
                            animatorSet.start();
                            MainActivity.getMainActivity().addScore(cardsBox[i][j].getNumber());//加分
                            addScoreSuccess_musicPlay(1,0,MainActivity.MenuMusic_state);//加分后播放得分音效
                            cardsBox[k][j].setNumber(0);//消值
                            Act=true;
                            break;
                        }else {
                            break;
                        }
                    }
                }
            }
        }
        if(Act){
//            添加新的卡片
            setRandomNumber();
            int[] numberList=new int[16];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    numberList[k++]=cardsBox[i][j].getNumber();
                }
            }
            History history=new History(MainActivity.getMainActivity().getScore(),numberList);
            if (MainActivity.getHistoryStack().size()>=2){
                //入栈前先判断已有多少栈，将栈顶取出，保证至多只有2个栈，完成最多撤销一步操作
                History tempHistory=MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().push(tempHistory);
            }
            MainActivity.getHistoryStack().push(history);
//            判断游戏是否已经结束
            checkGame();
        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        System.out.println("w:"+w+",h:"+h+",oldw:"+oldw+",oldh:"+oldh);
//        int cardSize=(Math.min(w,h)-8)/4;//获得卡片大小
//        System.out.println("cardSize:"+cardSize);
//        addCards(getCardSize());
//        startGame();
//    }

    private void addCards(int cardSize){
        Card card;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
//                System.out.println(i+","+j);
                card=new Card(getContext());
                LayoutParams glp=new LayoutParams();
                glp.width=cardSize;
                glp.height=cardSize;
                if(i==0&&j==0){
                    glp.topMargin=15;
                    glp.leftMargin=15;
                }else if(i==0){
                    glp.topMargin=15;
                }else if(j==0){
                    glp.leftMargin=15;
                }
                card.setNumber(0);
                addView(card,glp);
                cardsBox[i][j]=card;
            }
        }
    }

    private int getCardSize(){
        int cardSize;
        //屏幕信息的对象
        DisplayMetrics displayMetrics;
        //获取屏幕信息
        displayMetrics=getResources().getDisplayMetrics();
        cardSize=(displayMetrics.widthPixels-96)/4;
        return cardSize;
    }

    public static void startGame(){
        System.out.println("---------startGame()");
        //清空分数
        try {
            MainActivity.getMainActivity().cleanScore();
        }catch (Exception ee){}

        //清空界面
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                cardsBox[i][j].setNumber(0);
            }
        }
        //初始化2张卡片
        setRandomNumber();//加入动画效果的卡片生成
        setRandomNumber();
//        初始化后入栈
            int[] numberList=new int[16];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    numberList[k++]=cardsBox[i][j].getNumber();
                }
            }
            History history=new History(0,numberList);
            if (MainActivity.getHistoryStack().size()>=2){
                //入栈前先判断已有多少栈，将栈顶取出，保证至多只有2个栈，完成最多撤销一步操作
                History tempHistory=MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().pop();
                MainActivity.getHistoryStack().push(tempHistory);
            }
            MainActivity.getHistoryStack().push(history);
    }

    private static void setRandomNumber(){
        emptyPoint.clear();
        for (int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(cardsBox[i][j].getNumber()==0){
                    emptyPoint.add(new Point(i,j));
                }
            }
        }
        Point point=emptyPoint.get((int)(Math.random()*emptyPoint.size()));
        cardsBox[point.x][point.y].setNumber(Math.random()>0.1?2:4);
        objectAnimatorX=ObjectAnimator.ofFloat(cardsBox[point.x][point.y].getTextViewNumber(),"scaleX",0.2f,1f);
        objectAnimatorY=ObjectAnimator.ofFloat(cardsBox[point.x][point.y].getTextViewNumber(),"scaleY",0.2f,1f);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }

    private void checkGame(){
        boolean gameOver=true;//默认游戏已经结束
        firstFor:
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
//            逐个卡片查询游戏是否还能产生新卡片
                if(cardsBox[i][j].getNumber()==0//还存在空位
                        ||(i>0&&cardsBox[i][j].equals(cardsBox[i-1][j]))//除第一行外，其余行有与上一行可合并的
                        ||(i<3&&cardsBox[i][j].equals(cardsBox[i+1][j]))//除最后一行外，其余行有与下一行可合并的
                        ||(j>0&&cardsBox[i][j].equals(cardsBox[i][j-1]))//除第一列外，其余列有可与前一列可合并的
                        ||(j<3&&cardsBox[i][j].equals(cardsBox[i][j+1]))//除最后一列外，其余列有可与后一列可合并的
                ){
                    gameOver=false;
                    break firstFor;
                }
            }
        }
        if(gameOver){
//            判定结束时，弹出对话框，判定玩家是否选择重新开始游戏
            AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
            adb.setTitle("2048");
            adb.setMessage("游戏结束!");
            adb.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    重新开始
                    startGame();
                }
            });
            adb.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //退出游戏
                    MainActivity.getMainActivity().finish();
                }
            });
            adb.setNeutralButton("上传分数", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //上传分数至排行榜，并重新开始
                    Toast.makeText(getContext(),"还没有实现!",Toast.LENGTH_LONG).show();
                    startGame();
                }
            });
            AlertDialog ad=adb.create();
            ad.show();
        }
    }

    public static Card[][] getCardsBox() {
        return cardsBox;
    }

    public static List<Point> getEmptyPoint() {
        return emptyPoint;
    }

    private void addScoreSuccess_musicPlay(int sound,int loop,boolean GameMusic_state){
        if(GameMusic_state){
            MainActivity.getMainActivity().playSound(sound,loop);
        }
    }



}
