package com.suhao.butterknife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.suhao.butterknife.butterknife_annotations.BindView;

public class SecondActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    public TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);

        tv.setText("谢谢你在代码中给我设置内容");
    }
}
