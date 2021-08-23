package com.example.kingqi.paykeep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.SpeakConfig;
import com.example.robotactivitylibrsry.RobotActivity;
import com.asus.robotframework.API.DialogSystem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.sql.StatementEvent;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends RobotActivity{

    static final String SERVER_IP = "140.134.26.196";
    private Button fab;
    private Button studentbtn1, studentbtn2, studentbtn3, studentbtn4, studentbtn5, studentbtn6;
    private Button Pdetail, Gdetail, videobtn;
    private String content = null;
    private String pointcontent = null, nowstudentid = null, nowpart = null;
    public String[] parts = new String[]{"vp1","foc1","vr1","vp2","foc2","vr2"};
    public String[] groups = new String[]{"Group 1", "Group 2", "Group 3", "Group 4", "Group 5", "Group 6"};
    public String[] completeparts = new String[]{"Reading 1 Vocabulary Preview","Reading 1 Focus on Content","Reading 1 Vocabulary Review","Reading 2 Vocabulary Preview","Reading 2 Focus on Content","Reading 2 Vocabulary Review"};
    public String[] bundlescanresult = new String[]{"student 1", "student 2", "student 3", "student 4", "student 5", "student 6"};
    private Handler handler = null;
    public SpeakConfig config = new SpeakConfig();
    private String week = "2";
    public int opentimes = 0;
    public String[] vocabularylist = new String[24];
    public String[] vocabularyparts;
    private boolean stop = false;
    private boolean jumptoscan = true, jumptoct = true;
    private Intent studentdetail;
    private int[] sectionrobottalk = new int[]{0,0,0,0,0,0,0};
    private String zenbo = "";

    public MainActivity() {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //設定顯示介面
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final TextView groupview = (TextView)findViewById(R.id.groupnum);
        AlertDialog.Builder GrouppartBuilder = new AlertDialog.Builder(MainActivity.this);
        GrouppartBuilder.setTitle("choose the group");
        GrouppartBuilder.setIcon(R.mipmap.ic_launcher);
        GrouppartBuilder.setSingleChoiceItems(groups, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                zenbo = groups[i].substring(groups[i].length()-1);
                groupview.setText(groups[i]);
                setstudentsid();
                dialogInterface.dismiss();
            }
        });
        GrouppartBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog GrouppartDialog = GrouppartBuilder.create();
        GrouppartDialog.show();
        config.languageId(2);
        config.readMode(SpeakConfig.READ_MODE_SENTENCE);
        config.pitch(80);
        config.volume(50);
        init();
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
        handler = new Handler();
        //相機權限android 6.0版本要詢問
        getCameraPermission();
        checksection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
    }

    public void setstudentsid(){

        final String[] studentsid = new String[]{"student 1", "student 2", "student 3", "student 4", "student 5", "student 6"};
        final Runnable UIRunnable = new  Runnable(){
            @Override
            public void run() {
            studentbtn1.setText(studentsid[0]);
            studentbtn2.setText(studentsid[1]);
            studentbtn3.setText(studentsid[2]);
            studentbtn4.setText(studentsid[3]);
            studentbtn5.setText(studentsid[4]);
            studentbtn6.setText(studentsid[5]);
            }
        };

        new Thread(new Runnable() {
            OkHttpClient client = new OkHttpClient();
            Request request;

            @Override
            public void run() {

                request = new Request.Builder()
                        .url("http://"+SERVER_IP+"/getgroupstudents/?group="+zenbo)
                        .get()
                        .build();
                Response response = null;
                String resStr = null;
                try {
                    response = client.newCall(request).execute();
                    resStr = response.body().string();
                    JSONObject json = new JSONObject(resStr);
                    studentsid[0] = json.getString("student1");
                    studentsid[1] = json.getString("student2");
                    studentsid[2] = json.getString("student3");
                    studentsid[3] = json.getString("student4");
                    studentsid[4] = json.getString("student5");
                    studentsid[5] = json.getString("student6");
                    handler.post(UIRunnable);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void init(){

        fab = (Button) findViewById(R.id.SC);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivity.this, qrcode_scanner.class);
                intent.putExtra("group",zenbo);
                startActivityForResult(intent,1);
            }
        });

        videobtn = (Button) findViewById(R.id.videobtn);
        videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoRecorderActivity.class);
                intent.putExtra("Student1",studentbtn1.getText().toString());
                intent.putExtra("Student2",studentbtn2.getText().toString());
                intent.putExtra("Student3",studentbtn3.getText().toString());
                intent.putExtra("Student4",studentbtn4.getText().toString());
                intent.putExtra("Student5",studentbtn5.getText().toString());
                intent.putExtra("Student6",studentbtn6.getText().toString());
                startActivityForResult(intent,2);
            }
        });

        studentbtn1 = (Button) findViewById(R.id.student1);
        studentbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn1.getText().toString());
                        nowstudentid = studentbtn1.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        studentbtn2 = (Button) findViewById(R.id.student2);
        studentbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn2.getText().toString());
                        nowstudentid = studentbtn2.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        studentbtn3 = (Button) findViewById(R.id.student3);
        studentbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn3.getText().toString());
                        nowstudentid = studentbtn3.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        studentbtn4 = (Button) findViewById(R.id.student4);
        studentbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn4.getText().toString());
                        nowstudentid = studentbtn4.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        studentbtn5 = (Button) findViewById(R.id.student5);
        studentbtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn5.getText().toString());
                        nowstudentid = studentbtn5.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        studentbtn6 = (Button) findViewById(R.id.student6);
        studentbtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder partBuilder = new AlertDialog.Builder(MainActivity.this);
                partBuilder.setTitle("choose the section");
                partBuilder.setIcon(R.mipmap.ic_launcher);
                partBuilder.setSingleChoiceItems(completeparts, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkpoint(parts[i],studentbtn6.getText().toString());
                        nowstudentid = studentbtn6.getText().toString();
                        nowpart = parts[i];
                        dialogInterface.dismiss();
                    }
                });
                partBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog partDialog = partBuilder.create();
                partDialog.show();
            }
        });

        Pdetail = (Button) findViewById(R.id.pdetail);
        Pdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nowpart.equals("vp1")){
                    studentdetail = new Intent(MainActivity.this, VocabularyPreviewDetail.class);
                    studentdetail.putExtra("id",nowstudentid);
                    studentdetail.putExtra("reading","1");
                }else if(nowpart.equals("vp2")){
                    studentdetail = new Intent(MainActivity.this, VocabularyPreviewDetail.class);
                    studentdetail.putExtra("id",nowstudentid);
                    studentdetail.putExtra("reading","2");
                }else if(nowpart.equals("vr1")){
                    studentdetail = new Intent(MainActivity.this, VocabularyReviewDetail.class);
                    studentdetail.putExtra("id",nowstudentid);
                    studentdetail.putExtra("reading","1");
                }else if(nowpart.equals("vr2")){
                    studentdetail = new Intent(MainActivity.this, VocabularyReviewDetail.class);
                    studentdetail.putExtra("id",nowstudentid);
                    studentdetail.putExtra("reading","2");
                }
                startActivity(studentdetail);
            }
        });

        Gdetail = (Button) findViewById(R.id.gdetail);
        Gdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Runnable UIRunnable = new  Runnable(){
                    @Override
                    public void run() {
                        vocabularyparts = new String[]{vocabularylist[0],vocabularylist[3],vocabularylist[6],vocabularylist[9],vocabularylist[12],vocabularylist[15],vocabularylist[18],vocabularylist[21]};
//                        LayoutInflater detaillayout = LayoutInflater.from(MainActivity.this);
//                        View detailview = detaillayout.inflate(R.layout.vpdetail,null);
//                        settextdetailtest(detailview);
                        AlertDialog.Builder detailBuilder = new AlertDialog.Builder(MainActivity.this);
                        detailBuilder.setTitle("Which vocabulary you want to know");
                        detailBuilder.setIcon(R.mipmap.ic_launcher);
//                        detailBuilder.setView(detailview);
                        detailBuilder.setSingleChoiceItems(vocabularyparts, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder vocabularyBuilder = new AlertDialog.Builder(MainActivity.this);
                                for (int j=0; j<vocabularylist.length; j+=3){
                                    System.out.println("here "+ vocabularylist[j]);
                                    if (vocabularyparts[i].equals(vocabularylist[j])){
                                        vocabularyBuilder.setTitle(vocabularylist[j]);
                                        vocabularyBuilder.setMessage(vocabularylist[j+1] + "      " + vocabularylist[j+2]);
                                    }
                                }
                                vocabularyBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog vocabularyDialog = vocabularyBuilder.create();
                                vocabularyDialog.show();
                                //dialogInterface.dismiss();
                            }
                        });
                        detailBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog detailDialog = detailBuilder.create();
                        detailDialog.show();
                    }
                };

                new Thread(new Runnable() {
                    OkHttpClient client = new OkHttpClient();
                    Request request;

                    @Override
                    public void run() {
                        System.out.println("jjjjjjjjjjjjjjjjjjjjjj"+nowpart);
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getvocabularydetail/?unit=10&reading="+nowpart.substring(nowpart.length()-1))
                                .get()
                                .build();
                        Response response = null;
                        String resStr = null;
                        try {
                            response = client.newCall(request).execute();
                            resStr = response.body().string();
                            JSONObject json = new JSONObject(resStr);
                            System.out.println(json.toString());
                            testmessagerecord(json);
                            handler.post(UIRunnable);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    public void testmessagerecord(@NonNull JSONObject json){
        String[] parts;
        try {
            for(int i=0; i<24; i+=3){
                switch(i){
                    case 0:
                        parts = json.getString("v1").split("/");
                        break;
                    case 3:
                        parts = json.getString("v2").split("/");
                        break;
                    case 6:
                        parts = json.getString("v3").split("/");
                        break;
                    case 9:
                        parts = json.getString("v4").split("/");
                        break;
                    case 12:
                        parts = json.getString("v5").split("/");
                        break;
                    case 15:
                        parts = json.getString("v6").split("/");
                        break;
                    case 18:
                        parts = json.getString("v7").split("/");
                        break;
                    default:
                        parts = json.getString("v8").split("/");
                        break;
                }
                vocabularylist[i] = parts[0];
                vocabularylist[i+1] = parts[1];
                vocabularylist[i+2] = parts[2];
                System.out.println(vocabularylist[i] + vocabularylist[i+1] + vocabularylist[i+2]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checksection(){

        final ImageView qrcodeimage = (ImageView) findViewById(R.id.qrcodeimage);
        final TextView section = (TextView) findViewById(R.id.textView);

        final Runnable UIRunnable = new  Runnable(){
            @Override
            public void run() {
                if(content.equals("section")){
                    section.setText("Connected !");
                    stop = false;
                }
                else if(content.equals("studentcheck")){
                    if(jumptoscan){
                        Intent intent = new Intent(MainActivity.this, qrcode_scanner.class);
                        intent.putExtra("group",zenbo);
                        startActivityForResult(intent,1);
                        jumptoscan = false;
                    }
                }else if(content.equals("beforeyouread")){
                    qrcodeimage.setImageResource(R.drawable.beforeyouread);
                    section.setText("Unit 10 Before You Read");
                    if(sectionrobottalk[0]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[0] += 1;
                    }
                }else if(content.equals("U10R1vocabularypreview")){
                    qrcodeimage.setImageResource(R.drawable.u10r1vp);
                    section.setText("Unit 10 Reading 1 Vocabulary Preview");
                    if(sectionrobottalk[1]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[1] += 1;
                    }
                }else if(content.equals("U10R1focusoncontent")){
                    qrcodeimage.setImageResource(R.drawable.u10r1foc);
                    section.setText("Unit 10 Reading 1 Focus on Content");
                    if(sectionrobottalk[2]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[2] += 1;
                    }
                }else if(content.equals("U10R1vocabularyreview")){
                    qrcodeimage.setImageResource(R.drawable.u10r1vr);
                    section.setText("Unit 10 Reading 1 Vocabulary Review");
                    if(sectionrobottalk[3]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[3] += 1;
                    }
                }else if(content.equals("U10R2vocabularypreview")){
                    qrcodeimage.setImageResource(R.drawable.u10r2vp);
                    section.setText("Unit 10 Reading 2 Vocabulary Preview");
                    if(sectionrobottalk[4]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[4] += 1;
                    }
                }else if(content.equals("U10R2focusoncontent")){
                    qrcodeimage.setImageResource(R.drawable.u10r2foc);
                    section.setText("Unit 10 Reading 2 Focus on Content");
                    if(sectionrobottalk[5]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[5] += 1;
                    }
                }else if(content.equals("U10R2vocabularyreview")){
                    qrcodeimage.setImageResource(R.drawable.u10r2vr);
                    section.setText("Unit 10 Reading 2 Vocabulary Review");
                    if(sectionrobottalk[6]==0){
                        robotAPI.robot.speak("Please scan the Q R code to test", config);
                        for(int i=0; i<7; i++){
                            sectionrobottalk[i]=0;
                        }
                        sectionrobottalk[6] += 1;
                    }
                }else if(content.equals("criticalthinking")){
                    if(jumptoct){
                        Intent intent = new Intent(MainActivity.this, VideoRecorderActivity.class);
                        startActivityForResult(intent,1);
                        jumptoct = false;
                    }
                }else{
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stop){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://"+SERVER_IP+"/checksection/?zenbo="+zenbo)
                            .get()
                            .build();
                    Response response = null;
                    String resStr = null;
                    try {
                        response = client.newCall(request).execute();
                        resStr = response.body().string();
                        JSONObject json = new JSONObject(resStr);
                        System.out.println(json.getString("section"));
                        content = json.getString("section");
                        handler.post(UIRunnable);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void checkpoint(final String part, final String cId){

        Pdetail = (Button) findViewById(R.id.pdetail);
        Gdetail = (Button) findViewById(R.id.gdetail);
        final TextView studentpoint = (TextView) findViewById(R.id.studentpoint);
        int randomnum = 0;
        randomnum = (int)(Math.random()*10);

        final int finalRandomnum = randomnum;
        final Runnable UIRunnable = new  Runnable(){
            @Override
            public void run() {
                switch (finalRandomnum){
                    case 0:
                        robotAPI.robot.setExpression(RobotFace.ACTIVE);
                        break;
                    case 1:
                        robotAPI.robot.setExpression(RobotFace.INTERESTED);
                        break;
                    case 2:
                        robotAPI.robot.setExpression(RobotFace.CONFIDENT);
                        break;
                    case 3:
                        robotAPI.robot.setExpression(RobotFace.PLEASED);
                        break;
                    case 4:
                        robotAPI.robot.setExpression(RobotFace.SERIOUS);
                        break;
                    case 5:
                        robotAPI.robot.setExpression(RobotFace.SHOCKED);
                        break;
                    case 6:
                        robotAPI.robot.setExpression(RobotFace.EXPECTING);
                        break;
                    case 7:
                        robotAPI.robot.setExpression(RobotFace.QUESTIONING);
                        break;
                    case 8:
                        robotAPI.robot.setExpression(RobotFace.SHY);
                        break;
                    case 9:
                        robotAPI.robot.setExpression(RobotFace.HAPPY);
                        break;
                }
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                studentpoint.setText(pointcontent.subSequence(5,10));
                Pdetail.setVisibility(View.VISIBLE);
                Gdetail.setVisibility(View.VISIBLE);
                if(nowpart.equals("foc1")||nowpart.equals("foc2")){
                    Pdetail.setVisibility(View.INVISIBLE);
                    Gdetail.setVisibility(View.INVISIBLE);
                }
                robotAPI.robot.speak(pointcontent.substring(5,10),config);
                robotAPI.robot.setExpression(RobotFace.HIDEFACE);
                //onResume();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request;
                switch(part){
                    case "vp1":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getvocabularypreviewscore/?reading=1&cId="+cId)
                                .get()
                                .build();
                        break;
                    case "vp2":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getvocabularypreviewscore/?reading=2&cId="+cId)
                                .get()
                                .build();
                        break;
                    case "foc1":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getfocusoncontentscore/?reading=1&cId="+cId)
                                .get()
                                .build();
                        break;
                    case "foc2":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getfocusoncontentscore/?reading=2&cId="+cId)
                                .get()
                                .build();
                        break;
                    case "vr1":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getvocabularyreviewscore/?reading=1&cId="+cId)
                                .get()
                                .build();
                        break;
                    case "vr2":
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/getvocabularyreviewscore/?reading=2&cId="+cId)
                                .get()
                                .build();
                        break;
                    default:
                        request = new Request.Builder()
                                .url("http://"+SERVER_IP+"/checksection/?reading=2&cId="+cId)
                                .get()
                                .build();
                        break;
                }
                Response response = null;
                String resStr = null;
                try {
                    //Thread.sleep(5000);
                    response = client.newCall(request).execute();
                    resStr = response.body().string();
                    JSONObject json = new JSONObject(resStr);
                    System.out.println(json.toString());
                    pointcontent = json.toString();
                    handler.post(UIRunnable);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                //robotAPI.robot.speak(result.getContents());
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("--------------------------------------------------------------------------------------");
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
        opentimes++;
        System.out.println("open"+opentimes+"Thread"+Thread.currentThread().getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Bundle bundleresult = data.getExtras();
                bundlescanresult[0] = bundleresult.getString("resultcId1");
                bundlescanresult[1] = bundleresult.getString("resultcId2");
                bundlescanresult[2] = bundleresult.getString("resultcId3");
                bundlescanresult[3] = bundleresult.getString("resultcId4");
                bundlescanresult[4] = bundleresult.getString("resultcId5");
                bundlescanresult[5] = bundleresult.getString("resultcId6");
                for(int i=0; i<6; i++){
                    if (studentbtn1.getText().toString().equals(bundlescanresult[i])) studentbtn1.setBackgroundResource(R.drawable.button_shape3);
                    else if (studentbtn2.getText().toString().equals(bundlescanresult[i])) studentbtn2.setBackgroundResource(R.drawable.button_shape3);
                    else if (studentbtn3.getText().toString().equals(bundlescanresult[i])) studentbtn3.setBackgroundResource(R.drawable.button_shape3);
                    else if (studentbtn4.getText().toString().equals(bundlescanresult[i])) studentbtn4.setBackgroundResource(R.drawable.button_shape3);
                    else if (studentbtn5.getText().toString().equals(bundlescanresult[i])) studentbtn5.setBackgroundResource(R.drawable.button_shape3);
                    else if (studentbtn6.getText().toString().equals(bundlescanresult[i])) studentbtn6.setBackgroundResource(R.drawable.button_shape3);
                }
                jumptoscan = true;
            }
        }else if(requestCode == 2){
            System.out.println("yeeeeeeeeee");
            System.out.println("yaaaaaaaaaa");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("I'm pause");
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }
    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    //相機權限詢問
    public void getCameraPermission(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
    }

}

