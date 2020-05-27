package com.dajoonee.finalconnectingmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText id_txt, pw_txt, name_txt;
    Button join_btn,chec_same;
    Socket socket;
    OutputRunnable outputRunnable = new OutputRunnable();
    boolean check_id = false;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            if(msg.what==2){

                Toast.makeText(getApplicationContext(), "존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
            }else if(msg.what==3){
                Toast.makeText(getApplicationContext(), "서버오류로 저장하지 못했음..",Toast.LENGTH_SHORT).show();
            }else if(msg.what==1){
                Toast.makeText(getApplicationContext(), "아이디 사용 가능",Toast.LENGTH_SHORT).show();
                check_id = true;
            }else if(msg.what==4){
                Toast.makeText(getApplicationContext(), "저장 완료!",Toast.LENGTH_SHORT).show();
            }


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_txt = findViewById(R.id.id_txt);
        pw_txt = findViewById(R.id.pw_txt);
        name_txt = findViewById(R.id.name_txt);
        join_btn = findViewById(R.id.join_btn);
        chec_same = findViewById(R.id.chec_same);

    }

    @Override
    protected void onResume() {
        super.onResume();

        join_btn.setOnClickListener((v)->{

            if(check_id){
                if(!pw_txt.getText().toString().matches("")){
                    outputRunnable.setData(socket,json(id_txt.getText().toString(),pw_txt.getText().toString(),name_txt.getText().toString()));
                    Thread thread = new Thread(outputRunnable);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        check_id = false;
                    }
                    handler.sendEmptyMessage(outputRunnable.getResult());
                    check_id = false;

                }else {
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하시오!",Toast.LENGTH_SHORT).show();
                }//비밀번호 입력 체크

            }else{
                Toast.makeText(getApplicationContext(),"아이디 중복 확인하십시오!",Toast.LENGTH_SHORT).show();
            }//아이디 중복 체크

        });

        chec_same.setOnClickListener((v)->{

            if(!id_txt.getText().toString().matches("")) {

                outputRunnable.setData(socket, json(id_txt.getText().toString()));
                Thread thread = new Thread(outputRunnable);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
                handler.sendEmptyMessage(outputRunnable.getResult());

            }else{
                Toast.makeText(getApplicationContext(),"아이디를 입력하세요!",Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    protected void onStart() {
        String serverIp = "10.10.21.121";
        int port = 9999;
        super.onStart();
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    socket = new Socket(serverIp,port);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"소켓 생성 오류",Toast.LENGTH_SHORT).show();
                }
            }

        };
       thread.start();

    }
    JSONObject json(String id){
        System.out.println("Json(id) 들옴");
        return this.json(id,"null","null");
    }
    JSONObject json(String id, String pw, String name){
        System.out.println("Json() 들옴");
        JSONObject data = new JSONObject();
        if(pw.matches("null")){
            try {
                data.put("kind","id");
                data.put("id",id);
            } catch (JSONException e) {
                e.printStackTrace();

                runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(),"JSON 생성 오류!",Toast.LENGTH_SHORT).show();
                });

            }
        }else{
            try {
                data.put("kind","join");
                data.put("id",id);
                data.put("pw",pw);
                if(name.matches(""))
                    data.put("name",null);
                else
                    data.put("name",name);
            } catch (JSONException e) {
                e.printStackTrace();

                runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(),"JSON 생성 오류!",Toast.LENGTH_SHORT).show();
                });

            }
        }
        return data;
    }
}
