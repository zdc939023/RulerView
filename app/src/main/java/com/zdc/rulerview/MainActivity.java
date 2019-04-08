package com.zdc.rulerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RulerView rulerView;
    private TextView tv_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rulerView=findViewById(R.id.rv_rulerView);
        tv_value=findViewById(R.id.tv_value);
        //设置初始值
        rulerView.setValue(60);
        rulerView.initViewParam(60,200,RulerView.MOD_TYPE_HALF);
        rulerView.setOnValueChangeListener(new RulerView.onValueChangeListener() {
            @Override
            public void valueChange(final float value) {
                //在这里得到滑动值的变化
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_value.setText("当前值："+value);
                    }
                });
            }
        });
    }

}
