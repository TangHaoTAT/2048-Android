package com.example.kamenrider;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class Card extends FrameLayout {
    private int number;//保存数值
    private TextView textViewNumber;//显示数值
    private TextView textViewNumber2;//作为显示卡片的新背景，优化动画效果
    public Card(@NonNull Context context) {
        super(context);
//        System.out.println("Card()构造函数");
        textViewNumber2=new TextView(getContext());//作为显示卡片的新背景，优化动画效果
        textViewNumber2.setGravity(Gravity.CENTER);//作为显示卡片的新背景，优化动画效果
        textViewNumber2.setBackground(getResources().getDrawable(R.drawable.cards0));//作为显示卡片的新背景，优化动画效果
        textViewNumber=new TextView(getContext());
        textViewNumber.setTextSize(32);
        textViewNumber.setGravity(Gravity.CENTER);
        textViewNumber.setBackground(getResources().getDrawable(R.drawable.fore_cards0));//最上面颜色选择透明，避免颜色叠加
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(15,15,15,15);
        addView(textViewNumber2,lp);//作为显示卡片的新背景，优化动画效果
        addView(textViewNumber,lp);
        setNumber(0);
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
        if(number>0){
            textViewNumber.setText(number+"");
        }else{
            textViewNumber.setText("");
        }
        switch (number){
            case 0:
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.fore_cards0));
                break;
            case 2:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_2));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards2));
                break;
            case 4:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_4));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards4));
                break;
            case 8:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_8));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards8));
                break;
            case 16:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_16));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards16));
                break;
            case 32:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_32));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards32));
                break;
            case 64:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_64));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards64));
                break;
            case 128:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_128));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards128));
                break;
            case 256:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_256));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards256));
                break;
            case 512:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_512));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards512));
                break;
            case 1024:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_1024));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards1024));
                break;
            case 2048:
                textViewNumber.setTextColor(getResources().getColor(R.color.textView_2048));
                textViewNumber.setBackground(getResources().getDrawable(R.drawable.cards2048));
                break;
             default:
                 break;
        }
    }
    public boolean equals(Card card) {
        return this.getNumber()==card.getNumber();
    }

    public TextView getTextViewNumber() {
        return textViewNumber;
    }

    public void setTextViewNumber(TextView textViewNumber) {
        this.textViewNumber = textViewNumber;
    }
}
