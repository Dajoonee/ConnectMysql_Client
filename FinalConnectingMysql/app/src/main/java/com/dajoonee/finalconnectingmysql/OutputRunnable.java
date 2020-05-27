package com.dajoonee.finalconnectingmysql;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OutputRunnable implements Runnable {

    JSONObject data;
    int result ;
    Socket socket;


    @Override
    public void run() {
        boolean finished = true;

            try {
                if(socket!=null){
                    A : while(true){
                        System.out.println("데이터 스레드 들어옴");
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        output.writeUTF(data.toString());
                        output.flush();
                        if(output.size()==0){
                            output.close();
                        }
                        B: while(true){
                            System.out.println("데이터 아웃 들어옴");
                            DataInputStream input = new DataInputStream(socket.getInputStream());
                            result = input.readInt();
                            System.out.println(result);
                            if(result!=0){
                                System.out.println("데이터 스레드 중지");
                                break A;
                            }
                        }

                    }



                }
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    void setData(Socket socket, JSONObject data){
        this.data = data;
        this.socket = socket;
    }

    int getResult() {
        return result;
    }
}
