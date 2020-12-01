package com.example.kamenrider;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import com.example.kamenrider.db.db;

public class MainActivity extends AppCompatActivity {
    public static boolean MenuMusic_state=true;//控制游戏音效(菜单点击和游戏得分音效)
    public static boolean BackgroundMusic_state=true;//控制背景音乐音效
    MediaPlayer mediaPlayer;//声明MediaPlayer的引用
    SoundPool soundPool;//声明SoundPoold的引用
    HashMap<Integer,Integer> hashMap;//声明一个HashMap来存放声音文件
    int currStreamId;//当前正在播放的stream
    public static Stack<History> historyStack=new Stack<>();//保存历史记录,用于撤销操作
    private CountDownTimer timer;
    private static int flag=0;
    ImageButton restart;//重新开始按钮
    private int score=0;//存储卡片的值
    private TextView tvScore;//显示卡片的值
    private int bestScore=0;//存储最高分的值
    private TextView tvBestscore;//显示历史最高分
    private static MainActivity mainActivity=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvScore=findViewById(R.id.tvScore);
        tvBestscore=findViewById(R.id.tvBestscore);
        mainActivity=this;
        loadingGame();//判断是否需要加载本地数据，实现载入本地游戏数据存档
        initSound();//初始化声音
        restart=findViewById(R.id.reStart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                双击重新开始游戏
                if(flag==0){
                    //第一次单击的触发事件
                    flag=1;
                    Toast.makeText(getApplicationContext(),"请10秒内再按一次重新开始游戏!",Toast.LENGTH_SHORT).show();
                    if (!isFinishing()){
                        timer=new CountDownTimer(1000*10,1000) {
                            @Override
                            public void onTick(long l) { }
                            @Override
                            public void onFinish() {
                                flag=0;
//                                回收内存,避免内存泄漏
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                            }
                        }.start();
                    }
                }else {
                    //第二次单击的触发事件
                    flag=0;
//                    重新开始菜单音效
                    menuClick_musicPlay(2,0,MenuMusic_state);
                    GameView.startGame();
                    Toast.makeText(getApplicationContext(),"游戏愉快(｀・ω・´)",Toast.LENGTH_SHORT).show();

                }
            }
        });
        /* 2019年9月29日21:04:25-------BUG
            RelativeLayout.LayoutParams rllp=new RelativeLayout.LayoutParams(restart.getLayoutParams());
            rllp.width=displayMetrics.widthPixels/10;
            rllp.height=displayMetrics.widthPixels/10;
            restart.setLayoutParams(rllp);
            */
        //2019-9-26 13:36:25
        try {
            //屏幕信息的对象
            DisplayMetrics displayMetrics;
            //获取屏幕信息
            displayMetrics=getResources().getDisplayMetrics();
//            System.out.println("---------屏幕宽高的最小值：,"+displayMetrics.widthPixels);
            GridLayout glt=findViewById(R.id.gameView);
            LinearLayout.LayoutParams lllp=new LinearLayout.LayoutParams(glt.getLayoutParams());
            lllp.gravity= Gravity.CENTER_HORIZONTAL;
            lllp.width=displayMetrics.widthPixels-66;
            lllp.height=displayMetrics.widthPixels-66;
            glt.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.score_corners_rectangle));
            glt.setLayoutParams(lllp);
        }catch (Exception e){}
        //实现动态设置GridLayout的宽高
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("---------onPause()");
        try{
            mediaPlayer.start();
            mediaPlayer.pause();
        }catch (Exception e){ }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("---------onStop()");
        //1.获取游戏数据score(得分)、bestScore(最高分)、cardsBox[][](卡片集合)->获取卡片数值集合numberList[16]->ArrayList<String> list
        int tempScore=score;
        int tempBestScore=bestScore;
        int[] numberList=new int[16];
        int k=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                numberList[k++]=GameView.getCardsBox()[i][j].getNumber();
            }
        }
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<numberList.length;i++){
            String str=numberList[i]+"";
            list.add(str);
        }
        String tempList="";
        for (int i=0;i<list.size();i++){
            tempList+=list.get(i)+"#";
        }
        //2.将游戏数据保存至本地数据库中
        System.out.println(score+","+bestScore+","+tempList);
        db.setContext(getApplicationContext());
        db dataBases=new db();
        SQLiteDatabase db=dataBases.getConnection();
        String[] columns={"id","score","best_score","card_set"};
        Cursor cursor=db.query("local_data",columns,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String id=cursor.getString(cursor.getColumnIndex("id"));
            String sql="update local_data set score='"+score+"',best_score='"+bestScore+"',card_set='"+tempList+"' where id='"+id+"'";
            db.execSQL(sql);
            dataBases.close(db);
        }else{
            String sql="insert into local_data values(null,'"+score+"','"+bestScore+"','"+tempList+"')";
            db.execSQL(sql);
            dataBases.close(db);
        }
        //将设置参数保存至sharedPreference中
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences spf=getSharedPreferences("Music",Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = spf.edit();
        //步骤3：将获取过来的值放入文件
        editor.putBoolean("BackgroundMusic_state",BackgroundMusic_state);
        editor.putBoolean("MenuMusic_state",MenuMusic_state);
        //步骤4：提交
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("---------onResume()");
        if(BackgroundMusic_state==true){
            playBackgroundMusic(true);
        }else {
            playBackgroundMusic(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "反悔").setEnabled(false);
        menu.add(0,2,0,"设置");
        menu.add(0,6,0,"关于");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (historyStack.size()>=2){
            menu.findItem(1).setEnabled(true);
        }else {
            menu.findItem(1).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                menuClick_musicPlay(2,0,MenuMusic_state);
                goBack();
                break;
            case 2:
                menuClick_musicPlay(2,0,MenuMusic_state);
                goSetting();
                break;
            case 3:
                //预留菜单项
                break;
            case 4:
                //预留菜单项
                break;
            case 5:
                //预留菜单项
                break;
            case 6:
                menuClick_musicPlay(2,0,MenuMusic_state);
                Toast.makeText(getApplicationContext(),"微博:小二来碗七分熟红烧牛肉面",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void cleanScore(){
        score=0;
        showScore();
    }

    public void showScore(){
        tvScore.setText(score+"");
        tvBestscore.setText(bestScore+"");
    }

    public void addScore(int score){
        this.score+=score;
        bestScore=Integer.parseInt(tvBestscore.getText().toString());
        if(bestScore<this.score){
            bestScore=this.score;
        }
        showScore();
    }

    public int getScore() {
        return score;
    }

    private void goBack(){
//        Toast.makeText(getApplicationContext(),"施工中ing......",Toast.LENGTH_SHORT).show();
            if(historyStack.size()>=2){
                historyStack.pop();
               /* History temp1=historyStack.pop();
                for(int i=0;i<16;i++){
                        System.out.println(temp1.getNumberList()[i]);
                        遍历输出测试栈顶里的值
                        2019-10-3 07:20:11
                }*/
                History temp=historyStack.peek();
                /*for(int i=0;i<16;i++){
                    System.out.println(temp.getNumberList()[i]);
                    遍历输出测试第一个栈里的值
                }*/
//                恢复原有分数
                score=temp.getScore();
                tvScore.setText(temp.getScore()+"");
//                恢复原有卡片的值
                int k=0;
                for (int i=0;i<4;i++){
                    for(int j=0;j<4;j++){
                        GameView.getCardsBox()[i][j].setNumber(temp.getNumberList()[k++]);
                    }
                }
//                恢复原有空卡片
                GameView.getEmptyPoint().clear();
                for (int i=0;i<4;i++){
                    for(int j=0;j<4;j++){
                        if(GameView.getCardsBox()[i][j].getNumber()==0){
                            GameView.getEmptyPoint().add(new Point(i,j));
                        }
                    }
                }

            }
    }

    public static Stack<History> getHistoryStack() {
        return historyStack;
    }

    public void initSound(){
        SharedPreferences spf=getSharedPreferences("Music",Context.MODE_PRIVATE);
        MenuMusic_state=spf.getBoolean("MenuMusic_state",true);
        BackgroundMusic_state=spf.getBoolean("BackgroundMusic_state",true);
        initSoundPool();//初始化音效声音池
        initMediaPlayer();//初始化背景音乐声音池
    }

    public void initSoundPool(){
        soundPool=new SoundPool(4, AudioManager.STREAM_MUSIC,0);//创建SoundPool对象
        hashMap=new HashMap<>();//创建hashMap对象
        hashMap.put(1,soundPool.load(getApplicationContext(),R.raw.add_score,1));//加载声音文件addScore并且设置为1号声音加入hashMap中
        hashMap.put(2,soundPool.load(getApplicationContext(),R.raw.menu_click,2));//点击菜单音效
    }
    public void initMediaPlayer(){
        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.background_music);
    }

    public void playBackgroundMusic(boolean BackgroundMusic_state){
        if(BackgroundMusic_state){
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }else {
            mediaPlayer.stop();
            try { mediaPlayer.prepare(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void playSound(int sound,int loop){//播放声音
//        获取AudioManager引用
        AudioManager audioManager=(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //获取当前音量
        float streamVolumeCurrent=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取系统最大音量
        float streamVolumeMax=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //计算得到播放音量
        float volume=streamVolumeCurrent/streamVolumeMax;
        //调用SoundPool的play方法来播放声音文件
        currStreamId=soundPool.play(hashMap.get(sound),volume,volume,1,loop,1.0f);
    }

    private void menuClick_musicPlay(int sound,int loop,boolean MenuMusic_state){
        if(MenuMusic_state){
            playSound(sound,loop);
        }
    }
    private void goSetting(){
        Intent intent=new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
    }

    public void loadingGame(){//根据情况选择是否载入本地游戏数据存档
        System.out.println("---------loadingGame()");
        db.setContext(getApplicationContext());
        db dataBases=new db();
        SQLiteDatabase db=dataBases.getConnection();
        String[] columns={"id","score","best_score","card_set"};
        Cursor cursor=db.query("local_data",columns,null,null,null,null,null);
        if(cursor.moveToFirst()){//载入存档
//            恢复分数
            score=Integer.parseInt(cursor.getString(cursor.getColumnIndex("score")));
            bestScore=Integer.parseInt(cursor.getString(cursor.getColumnIndex("best_score")));
            showScore();
//            恢复原有卡片的值
            String[] tempCard=cursor.getString(cursor.getColumnIndex("card_set")).split("#");
            ArrayList<Integer> numberList=new ArrayList<>();
            for(int i=0;i<tempCard.length;i++){
                numberList.add(Integer.parseInt(tempCard[i]));
            }
            int k=0;
            for (int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    GameView.getCardsBox()[i][j].setNumber(numberList.get(k++));
                }
            }
//              恢复原有空卡片
            GameView.getEmptyPoint().clear();
            for (int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    if(GameView.getCardsBox()[i][j].getNumber()==0){
                        GameView.getEmptyPoint().add(new Point(i,j));
                    }
                }
            }
        }
        dataBases.close(db);
    }
}
