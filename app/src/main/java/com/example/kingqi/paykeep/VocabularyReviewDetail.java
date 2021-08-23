package com.example.kingqi.paykeep;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VocabularyReviewDetail extends AppCompatActivity {

    private String SERVER_IP = "140.134.26.196", content = null;
    private Handler handler = null;
    TextView Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8;
    TextView A1, A2, A3, A4, A5, A6, A7, A8;
    TextView C1, C2, C3, C4, C5, C6, C7, C8;
    private String[] vocabularyreviewquestionset = new String[8];
    private String[] vocabularyreviewstudentans = new String[8];
    private String[] vocabularyreviewcorrectans = new String[8];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vocabulary_review_detail);
        init();
        Intent nowstudentid = getIntent();
        Bundle studentbundle = nowstudentid.getExtras();
        getstudentdetail(studentbundle);
        handler = new Handler();
    }

    private void init(){
        Q1 = (TextView)findViewById(R.id.Q1);
        Q2 = (TextView)findViewById(R.id.Q2);
        Q3 = (TextView)findViewById(R.id.Q3);
        Q4 = (TextView)findViewById(R.id.Q4);
        Q5 = (TextView)findViewById(R.id.Q5);
        Q6 = (TextView)findViewById(R.id.Q6);
        Q7 = (TextView)findViewById(R.id.Q7);
        Q8 = (TextView)findViewById(R.id.Q8);
        A1 = (TextView)findViewById(R.id.A1);
        A2 = (TextView)findViewById(R.id.A2);
        A3 = (TextView)findViewById(R.id.A3);
        A4 = (TextView)findViewById(R.id.A4);
        A5 = (TextView)findViewById(R.id.A5);
        A6 = (TextView)findViewById(R.id.A6);
        A7 = (TextView)findViewById(R.id.A7);
        A8 = (TextView)findViewById(R.id.A8);
        C1 = (TextView)findViewById(R.id.C1);
        C2 = (TextView)findViewById(R.id.C2);
        C3 = (TextView)findViewById(R.id.C3);
        C4 = (TextView)findViewById(R.id.C4);
        C5 = (TextView)findViewById(R.id.C5);
        C6 = (TextView)findViewById(R.id.C6);
        C7 = (TextView)findViewById(R.id.C7);
        C8 = (TextView)findViewById(R.id.C8);
    }

    private void collectdata(JSONObject jsondata){
        try {
            vocabularyreviewquestionset[0] = jsondata.getString("Q1");
            vocabularyreviewquestionset[1] = jsondata.getString("Q2");
            vocabularyreviewquestionset[2] = jsondata.getString("Q3");
            vocabularyreviewquestionset[3] = jsondata.getString("Q4");
            vocabularyreviewquestionset[4] = jsondata.getString("Q5");
            vocabularyreviewquestionset[5] = jsondata.getString("Q6");
            vocabularyreviewquestionset[6] = jsondata.getString("Q7");
            vocabularyreviewquestionset[7] = jsondata.getString("Q8");
            vocabularyreviewcorrectans[0] = jsondata.getString("q1coranswer");
            vocabularyreviewcorrectans[1] = jsondata.getString("q2coranswer");
            vocabularyreviewcorrectans[2] = jsondata.getString("q3coranswer");
            vocabularyreviewcorrectans[3] = jsondata.getString("q4coranswer");
            vocabularyreviewcorrectans[4] = jsondata.getString("q5coranswer");
            vocabularyreviewcorrectans[5] = jsondata.getString("q6coranswer");
            vocabularyreviewcorrectans[6] = jsondata.getString("q7coranswer");
            vocabularyreviewcorrectans[7] = jsondata.getString("q8coranswer");
            vocabularyreviewstudentans[0] = jsondata.getString("q1answer");
            vocabularyreviewstudentans[1] = jsondata.getString("q2answer");
            vocabularyreviewstudentans[2] = jsondata.getString("q3answer");
            vocabularyreviewstudentans[3] = jsondata.getString("q4answer");
            vocabularyreviewstudentans[4] = jsondata.getString("q5answer");
            vocabularyreviewstudentans[5] = jsondata.getString("q6answer");
            vocabularyreviewstudentans[6] = jsondata.getString("q7answer");
            vocabularyreviewstudentans[7] = jsondata.getString("q8answer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getstudentdetail(Bundle studentbundle){
        if(studentbundle != null){
            final String studentid = (String)studentbundle.get("id");
            final String reading = (String)studentbundle.get("reading");


            final Runnable UIRunnable = new  Runnable(){
                @Override
                public void run() {
                    Q1.setText(vocabularyreviewquestionset[0]);
                    Q2.setText(vocabularyreviewquestionset[1]);
                    Q3.setText(vocabularyreviewquestionset[2]);
                    Q4.setText(vocabularyreviewquestionset[3]);
                    Q5.setText(vocabularyreviewquestionset[4]);
                    Q6.setText(vocabularyreviewquestionset[5]);
                    Q7.setText(vocabularyreviewquestionset[6]);
                    Q8.setText(vocabularyreviewquestionset[7]);
                    A1.setText(vocabularyreviewcorrectans[0]);
                    A2.setText(vocabularyreviewcorrectans[1]);
                    A3.setText(vocabularyreviewcorrectans[2]);
                    A4.setText(vocabularyreviewcorrectans[3]);
                    A5.setText(vocabularyreviewcorrectans[4]);
                    A6.setText(vocabularyreviewcorrectans[5]);
                    A7.setText(vocabularyreviewcorrectans[6]);
                    A8.setText(vocabularyreviewcorrectans[7]);
                    C1.setText(vocabularyreviewstudentans[0]);
                    if (!C1.getText().equals(A1.getText())) C1.setTextColor(Color.rgb(255,0,0));
                    C2.setText(vocabularyreviewstudentans[1]);
                    if (!C2.getText().equals(A2.getText())) C2.setTextColor(Color.rgb(255,0,0));
                    C3.setText(vocabularyreviewstudentans[2]);
                    if (!C3.getText().equals(A3.getText())) C3.setTextColor(Color.rgb(255,0,0));
                    C4.setText(vocabularyreviewstudentans[3]);
                    if (!C4.getText().equals(A4.getText())) C4.setTextColor(Color.rgb(255,0,0));
                    C5.setText(vocabularyreviewstudentans[4]);
                    if (!C5.getText().equals(A5.getText())) C5.setTextColor(Color.rgb(255,0,0));
                    C6.setText(vocabularyreviewstudentans[5]);
                    if (!C6.getText().equals(A6.getText())) C6.setTextColor(Color.rgb(255,0,0));
                    C7.setText(vocabularyreviewstudentans[6]);
                    if (!C7.getText().equals(A7.getText())) C7.setTextColor(Color.rgb(255,0,0));
                    C8.setText(vocabularyreviewstudentans[7]);
                    if (!C8.getText().equals(A8.getText())) C8.setTextColor(Color.rgb(255,0,0));
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://"+SERVER_IP+"/getvocabularyreviewscoredetail/?reading="+reading+"&cId="+studentid)
                            .get()
                            .build();
                    Response response = null;
                    String resStr = null;
                    try {
                        response = client.newCall(request).execute();
                        resStr = response.body().string();
                        JSONObject json = new JSONObject(resStr);
                        collectdata(json);
                        handler.post(UIRunnable);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}