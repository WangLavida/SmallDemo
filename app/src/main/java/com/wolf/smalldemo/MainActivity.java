package com.wolf.smalldemo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.wequick.small.Small;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {
    private String updateUrl = "http://192.168.253.15:8080/updatebundle.json";
    private List<UpdateInfo> infos;
    private int downloadIndex = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.start);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        });
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    showDialog();
                } else {
                    Toast.makeText(MainActivity.this, "请打开权限", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        progressDialog = new ProgressDialog(MainActivity.this);
    }

    private void showDialog() {
        AlertDialog.Builder progressDialog = new AlertDialog.Builder(this);
        progressDialog.setMessage("是否更新");
        progressDialog.setNegativeButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUpdateJson();
            }
        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void getUpdateJson() {
        OkGo.<String>get(updateUrl).execute(new StringCallback() {

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Toast.makeText(MainActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(Response<String> response) {
                Log.i("请求返回", response.body());
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response.body().toString());
                    JSONObject mf = jo.has("manifest") ? jo.getJSONObject("manifest") : null;
                    JSONArray updates = jo.getJSONArray("updates");
                    infos = new ArrayList<UpdateInfo>();
                    for (int i = 0; i < updates.length(); i++) {
                        JSONObject o = updates.getJSONObject(i);
                        UpdateInfo info = new UpdateInfo();
                        info.pkg = o.getString("pkg");
                        info.url = o.getString("url");
                        infos.add(info);
                    }
                    JSONObject manifest = mf;
                    boolean isS = Small.updateManifest(manifest, false);
                    download(downloadIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void download(final int index) {
        UpdateInfo updateInfo = infos.get(index);


        final net.wequick.small.Bundle bundle = Small.getBundle(updateInfo.pkg);
        File file = bundle.getPatchFile();
        Log.i("下载packageName", updateInfo.pkg);
        Log.i("下载保存地址", file.getPath());
        String filePath = file.getPath();
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        progressDialog.setMessage("正在下载第" + (index + 1) + "个文件");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        OkGo.<File>get(updateInfo.getUrl()).execute(new FileCallback(file.getPath(), null) {
            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                progressDialog.setProgress((int) progress.fraction * 100);
            }

            @Override
            public void onSuccess(Response<File> response) {
                Log.i("下载地址", response.body().getPath());
                bundle.upgrade();
                if (index == (infos.size() - 1)) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                    //重启app代码
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 退出程序
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                    System.exit(0);
                } else {
                    downloadIndex = downloadIndex + 1;
                    download(downloadIndex);
                }


            }
        });
        progressDialog.show();
    }

}
