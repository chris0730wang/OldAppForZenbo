package com.example.kingqi.paykeep;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class socketconnector extends AppCompatActivity {

    Button connectPythonBut;
    static TextView replyMessageTextView;
    EditText data2SendEditText;
    static final String SERVER_IP = "140.134.26.196"; // The SERVER_IP must be the same in server and client
    static final int PORT = 1234; // You can put any arbitrary PORT value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socketconnect);

        connectPythonBut = (Button) findViewById(R.id.connectPythonBut);
        replyMessageTextView = (TextView) findViewById(R.id.replyMessageTextView);
        data2SendEditText = (EditText) findViewById(R.id.data2SendEditText);

        connectPythonBut.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                replyMessageTextView.setText("Waiting python reply");

                ConnectPyTask task = new ConnectPyTask();
                ConnectPyTask.context = getApplicationContext();
                System.out.println("start");
                System.out.println(ConnectPyTask.context);
                System.out.println("end");
                task.execute(data2SendEditText.getText().toString());
            }
        });
    }
    static class ConnectPyTask extends AsyncTask<String, Void, String>
    {
        static Context context = null;
        static float startTime = 0, endTime = 0;
        @Override
        protected String doInBackground(String... data) {
            try {
                startTime = System.currentTimeMillis();
                Socket socket = new Socket(SERVER_IP, PORT); //Server IP and PORT
                Scanner sc = new Scanner(socket.getInputStream());
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                printWriter.write(data[0]); // Send Data
                printWriter.flush();

                replyMessageTextView.setText(sc.next()); // Receive data and edit the text view

            }catch (Exception e){
                Log.d("Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            endTime = System.currentTimeMillis();
            String execTime = String.valueOf((endTime - startTime)/1000.0f);
            Toast.makeText(context, "Time execution is: " + execTime + "s", Toast.LENGTH_SHORT).show();
        }
    }
}

