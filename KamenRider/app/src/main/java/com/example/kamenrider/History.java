package com.example.kamenrider;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class History {
    private int score=0;//当前分数
    private int[] numberList=new int[16];//卡片数值集合

    History(int score,int[] numberList){
        this.score=score;
        this.numberList=numberList;
    }

    public int getScore() {
        return score;
    }

    public int[] getNumberList() {
        return numberList;
    }

}
