package com.darksoul.onlinenotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.client.IO;

import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class OnlineNotepadActivity extends AppCompatActivity {
    private EditText typeArea, typeTitle;
    private ImageView back, save, share;
    private String NoteID,MyID;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://onlinenotepad.guwache.com/");
        } catch (URISyntaxException e) {
        }
    }

    private Boolean isConnected = true;
    private Boolean mine=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_notepad);
        //init......................
        typeArea = findViewById(R.id.typearea);
        typeTitle = findViewById(R.id.typetitle);
        back = findViewById(R.id.note_back);
        save = findViewById(R.id.note_save);
        share = findViewById(R.id.note_share);
        save.setVisibility(View.INVISIBLE);

        //getting note id.........
        NoteID=getIntent().getStringExtra("id");
        MyID=getIntent().getStringExtra("MyId");

        setListening();

        //initialize websocket.......
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.connect();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Asize = typeArea.length();
                int Tsize = typeTitle.length();
                if (Asize >= 1 && Tsize >= 1) {
                    Toast.makeText(getBaseContext(), "save button", Toast.LENGTH_LONG).show();
                } else {
                    if (Asize == 0) {
                        Toast.makeText(getBaseContext(), "Empty Context", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Enter Title", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        //share button event listener.....
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = typeArea.getText().toString();
                share(OnlineNotepadActivity.this, text);
            }
        });

        //back button event........
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnlineNotepadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //edit text view listener...........
        typeArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mSocket.connected()){return;}
                String dat=s.toString();
                Log.e("text",dat);
                JSONObject data=new JSONObject();
                try{
                    data.put("myid",MyID);
                    data.put("ID",NoteID);
                    data.put("text",dat);
                    mine=true;
                    mSocket.emit("CHAT",data);
                } catch (JSONException e) {
                    Log.e("me", "error send message " + e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                int size = s.length();
                if (size >= 1) {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        //titel box event..........
        typeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int size = s.length();
                if (size >= 1) {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
       // mSocket.off("new Text", onTextSend);
    }

    //share event handle...........
    public static void share(Context context, String body) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sharingIntent.setType("text/plain");
        String shareBody = body;
        String shareSub = "Share Text";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OnlineNotepadActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //all socket emmit listeners..............................................

    private Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                Log.e("Socket", "connected...");
                Toast.makeText(OnlineNotepadActivity.this,
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();
            });
        }
    };


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                    Log.i("Socket", "disconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),"Socket Disconnected!", Toast.LENGTH_LONG).show();
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext().getApplicationContext(),
                            "Socket connecting error", Toast.LENGTH_LONG).show();
            });
        }
    };

//    private Emitter.Listener onTextSend = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(()-> {
//                    JSONObject data = (JSONObject) args[0];
//                    int ID;
//                    String Text;
//                    try {
//                        ID=data.getInt("ID");
//                        Text = data.getString("text");
//                        Log.e("recived",String.valueOf(ID));
//                        Log.e("recieved",Text);
//                    } catch (JSONException e) {
//                        Log.e("Socket", e.getMessage());
//                        return;
//                    }
//                    //addMessage(username, message);
//            });
//        }
//    };

    private void setListening() {
        mSocket.on("CHAT", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//                try {
//                    JSONObject messageJson = new JSONObject(args[0].toString());
//                    String message = messageJson.getString("message");
//                    runOnUiThread(()-> {
//                        Log.e("recived",message);
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                JSONObject data = (JSONObject) args[0];
                String myid;
                String Text;
                try {
                    myid=data.getString("myid");
                    Text = data.getString("text");
                } catch (JSONException e) {
                    Log.e("Socket", e.getMessage());
                    return;
                }
                if(myid.equals(MyID)){
                    return;
                }else{
                    typeArea.setText(Text);
                }
            }
        });
    }



}