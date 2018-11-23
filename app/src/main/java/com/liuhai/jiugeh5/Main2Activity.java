package com.liuhai.jiugeh5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liuhai.jiugeh5.BaseicActivity;
import com.liuhai.jiugeh5.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Main2Activity extends BaseicActivity {


    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.jump)
    Button jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);
        loadlisten();
    }

    @Override
    public void loadlisten() {
    jump.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main2Activity.this, XYXActivity.class);
            //holder.mItem.getPlayurl()
            intent.putExtra("weburl",editText.getText().toString() );
            intent.putExtra("name","测试");
            intent.putExtra("icon","2");
            startActivity(intent);
        }
    });

    }
}
