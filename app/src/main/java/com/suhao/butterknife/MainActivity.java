package com.suhao.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.suhao.butterknife.butterknife_annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn)
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "手写ButterKnife成功了！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
