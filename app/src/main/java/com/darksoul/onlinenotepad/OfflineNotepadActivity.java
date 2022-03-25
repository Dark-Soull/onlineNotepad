package com.darksoul.onlinenotepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class OfflineNotepadActivity extends AppCompatActivity {
    private EditText typeArea,typeTitle;
    private ImageView back,save,share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_notepad);

        //init......................
        typeArea=findViewById(R.id.typearea);
        typeTitle=findViewById(R.id.typetitle);
        back=findViewById(R.id.note_back);
        save=findViewById(R.id.note_save);
        share=findViewById(R.id.note_share);
        save.setVisibility(View.INVISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Asize=typeArea.length();
                int Tsize=typeTitle.length();
                if(Asize>=1 && Tsize>=1){
                    Toast.makeText(getBaseContext(), "save button", Toast.LENGTH_LONG).show();
                }else{
                    if(Asize==0){
                        Toast.makeText(getBaseContext(), "Empty Context", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(), "Enter Title", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        //share button event listener.....
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=typeArea.getText().toString();
                share(OfflineNotepadActivity.this,text);
            }
        });

        //back button event........
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(OfflineNotepadActivity.this, MainActivity.class);
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
                int size=s.length();
                if(size>=1){
                    save.setVisibility(View.VISIBLE);
                }
            }
        });









    }

    //share event handle...........
    public static void share(Context context, String body) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sharingIntent.setType("text/plain");
        String shareBody =body;
        String shareSub = "Share Text";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OfflineNotepadActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}