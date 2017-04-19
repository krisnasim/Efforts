package com.android.efforts.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.efforts.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.code_text_value) TextView code_text_value;

    private String url_Code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        getBundleData();
    }

    private void getBundleData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d("url_code", bundle.getString("url_code"));
        url_Code = bundle.getString("url_code");
        code_text_value.setText(url_Code);
    }
}
