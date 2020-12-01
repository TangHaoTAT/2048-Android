package com.example.kamenrider;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private static List<Map<String,Object>> list;
    private String[] from={"showMenu","state"};
    private int[] to={R.id.showMenu,R.id.checkBox};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initDate();
        listView=findViewById(R.id.listView);
        simpleAdapter=new SimpleAdapter(getApplicationContext(),list,R.layout.list_item,from,to);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //实现设置中描述的功能
                switch (i){
                    case 0:
                        //背景音效开关功能
                        CheckBox checkBox0=view.findViewById(R.id.checkBox);
                        if(checkBox0.isChecked()){
                            checkBox0.setChecked(false);
                            MainActivity.BackgroundMusic_state=false;
                        }else {
                            checkBox0.setChecked(true);
                            MainActivity.BackgroundMusic_state=true;
                        }
                        break;
                    case 1:
                       //游戏音效开关功能
                        CheckBox checkBox1=view.findViewById(R.id.checkBox);
                        if(checkBox1.isChecked()){
                            checkBox1.setChecked(false);
                            MainActivity.MenuMusic_state=false;
                        }else {
                            checkBox1.setChecked(true);
                            MainActivity.MenuMusic_state=true;
                        }
                        break;
                }
            }
        });
    }

    private void initDate(){
        String[] showMenu={"背景音效","游戏音效"};
        boolean[] Menu_state={MainActivity.BackgroundMusic_state,MainActivity.MenuMusic_state};
        list=new ArrayList<>();
        for(int i=0;i<showMenu.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("showMenu",showMenu[i]);
            map.put("state",Menu_state[i]);
            list.add(map);
        }
    }
}
