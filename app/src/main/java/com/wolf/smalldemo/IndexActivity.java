package com.wolf.smalldemo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.wequick.small.Small;

import static android.os.Build.VERSION_CODES.M;
import static net.wequick.small.Small.openUri;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        TextView a = (TextView) findViewById(R.id.a);
        TextView b = (TextView) findViewById(R.id.b);
        TextView c = (TextView) findViewById(R.id.c);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Small.setUp(IndexActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        boolean isLoad = Small.openUri("a", IndexActivity.this);
                        if (!isLoad) {
                            Toast.makeText(IndexActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Small.setUp(IndexActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        boolean isLoad = Small.openUri("b", IndexActivity.this);
                        if (!isLoad) {
                            Toast.makeText(IndexActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Small.setUp(IndexActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        boolean isLoad = Small.openUri("c", IndexActivity.this);
                        if (!isLoad) {
                            Toast.makeText(IndexActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
