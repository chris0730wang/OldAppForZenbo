package com.example.kingqi.paykeep;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.SpeakConfig;
import com.example.robotactivitylibrsry.RobotActivity;
import com.asus.robotframework.API.DialogSystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.kingqi.paykeep.MainActivity.SERVER_IP;
import static com.example.kingqi.paykeep.MainActivity.robotCallback;

public class VideoRecorderActivity extends RobotActivity {

    private static int VIDEO_REQUEST = 101;
    private Uri videoUri;
    private String[] studentids = new String[6];
    private Handler handler;
    private MediaRecorder recorder;
    private CameraDevice mCamera;

    public VideoRecorderActivity() {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        Intent nowstudentids = getIntent();
        Bundle studentbundle = nowstudentids.getExtras();
        getstudentids(studentbundle);
        handler = new Handler();
    }

    private String getDateAndTime(){
        @SuppressLint("SimpleDateFormat") DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        @SuppressLint("SimpleDateFormat") DateFormat dfTime = new SimpleDateFormat("HHmm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + "-" + time;
    }

    public void getstudentids(Bundle studentbundle){
        if(studentbundle!=null){
            studentids[0] = (String) studentbundle.get("Student1");
            studentids[1] = (String) studentbundle.get("Student2");
            studentids[2] = (String) studentbundle.get("Student3");
            studentids[3] = (String) studentbundle.get("Student4");
            studentids[4] = (String) studentbundle.get("Student5");
            studentids[5] = (String) studentbundle.get("Student6");
            if(!studentids[0].equals("student")){
                callstudenttochoose(0, "before");
            }
        }
    }

    public void choosefinish(){
        TextView answerstudent = (TextView)findViewById(R.id.answerstudent);
        TextView invisiblestudent = (TextView)findViewById(R.id.white);
        Button affirmative = (Button)findViewById(R.id.affirmative);
        Button negative = (Button)findViewById(R.id.negative);
        answerstudent.setText("Please discuss the topic and you will be asked to answer the questions individually");
        invisiblestudent.setVisibility(View.INVISIBLE);
        affirmative.setVisibility(View.INVISIBLE);
        negative.setVisibility(View.INVISIBLE);
        //captureVideo(answerstudent);
    }

    public void student_critical_record(final int studentnum, final String affirmativeornegative, final String beforeorafter){

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://"+SERVER_IP+"/criticalthinkingchoose/?cId="+studentids[studentnum]+"&choose="+affirmativeornegative+"&time="+beforeorafter)
                        .get()
                        .build();
                Response response = null;
                String resStr = null;
                try {
                    response = client.newCall(request).execute();
                    resStr = response.body().string();
                    JSONObject json = new JSONObject(resStr);
                    System.out.println(json.getString("result"));
                    robotAPI.robot.speak(json.getString("result"));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void chooseaffirmative(int studentnum, String beforeorafter){
        student_critical_record(studentnum, "affirmative", beforeorafter);
        studentnum+=1;
        if(!studentids[studentnum].equals("student")){
            callstudenttochoose(studentnum, beforeorafter);
        }else{
            choosefinish();
        }
    }
    public void choosenegative(int studentnum, String beforeorafter){
        student_critical_record(studentnum,"negative", beforeorafter);
        studentnum+=1;
        if(!studentids[studentnum].equals("student")){
            callstudenttochoose(studentnum, beforeorafter);
        }else{
            choosefinish();
        }
    }
    public void callstudenttochoose(final int studentnum, final String beforeorafter){

        TextView answerstudent = (TextView)findViewById(R.id.answerstudent);
        TextView invisiblestudent = (TextView)findViewById(R.id.white);
        Button affirmative = (Button)findViewById(R.id.affirmative);
        Button negative = (Button)findViewById(R.id.negative);
        affirmative.setVisibility(View.VISIBLE);
        negative.setVisibility(View.VISIBLE);

        answerstudent.setText(studentids[studentnum]);
        invisiblestudent.setText(studentids[studentnum]);
        robotAPI.robot.speak("學號" + studentids[studentnum] + "請點選螢幕上的選項");
        affirmative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoRecorderActivity.this);
                alertDialog.setTitle("");
                alertDialog.setMessage("You chose affirmative ?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chooseaffirmative(studentnum, beforeorafter);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoRecorderActivity.this);
                alertDialog.setTitle("");
                alertDialog.setMessage("You chose negative ?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choosenegative(studentnum, beforeorafter);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    public void captureVideo(View view) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if(videoIntent.resolveActivity(getPackageManager()) != null){
            videoIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN,true);
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
            startActivityForResult(videoIntent, VIDEO_REQUEST);

        }

//        try {
//
//            String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + getDateAndTime()+".3gpp";
//            recorder = new MediaRecorder();
//            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//            recorder.setOutputFile(filePath);
//            recorder.prepare();
//            Thread.sleep(1000);
//            recorder.start();
//            System.out.println("kjasdlja");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void playVideo(View view) {
        Intent playIntent = new Intent(this,VideoPlayActivity.class);
        playIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true);
        playIntent.putExtra("videoUri", videoUri.toString());
        startActivity(playIntent);
    }

    public void done(View view){
        finish();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            videoUri = data.getData();
        }
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
}