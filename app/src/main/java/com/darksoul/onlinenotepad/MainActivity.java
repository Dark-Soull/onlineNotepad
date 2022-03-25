package com.darksoul.onlinenotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final int PERMISSION_REQUEST_CODE = 100;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private DrawerLayout mdrawerLayout;
    ActionBarDrawerToggle toggleMenu;
    Toolbar toolbar;
    private FloatingActionButton addButton;
    private Button createOffline, createOnline, Join,joinBtn;
    private EditText idField;

    //socket initialization......
    private Boolean isConnected = true;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://onlinenotepad.guwache.com/");
        } catch (URISyntaxException e) {
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        // checking storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()) {
                createSaveDir();
            } else {
                requestPermission();
            }
        } else {
            createSaveDir();
        }

        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mdrawerLayout = findViewById(R.id.mDrawer);
        toolbar = findViewById(R.id.tool);
        addButton = findViewById(R.id.add_icon);


        setSupportActionBar(toolbar);
        toggleMenu = new ActionBarDrawerToggle(this, mdrawerLayout, toolbar, R.string.toggle_open, R.string.toggle_close);
        mdrawerLayout.addDrawerListener(toggleMenu);
        toggleMenu.syncState();

        //menu event listener....
        navigationView.setNavigationItemSelectedListener(this);

        //add button event listener.....
        addButton.setOnClickListener(this);


    }

    //add button click event handling...........
    @Override
    public void onClick(View v) {

        //create buttons display and their events handling............
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.create_option_buttons, null);
        final PopupWindow popup = new PopupWindow(view, 800, 800, true);
        popup.showAtLocation(v, Gravity.CENTER, 0, 0);
        createOffline = view.findViewById(R.id.offline_create);
        createOnline = view.findViewById(R.id.online_create);
        Join = view.findViewById(R.id.join);
        createOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OfflineNotepadActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                popup.dismiss();
            }
        });
        createOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = getRandomNumberString();
                String MyId=getRandomNumberString();
                    Intent i = new Intent(MainActivity.this, OnlineNotepadActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("id",ID);
                    i.putExtra("MyId",MyId);
                    startActivity(i);
                finish();
                    popup.dismiss();
            }
        });
        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.join_note, null);
                final PopupWindow pop = new PopupWindow(view, 800, 800, true);
                pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                joinBtn=view.findViewById(R.id.join_btn);
                idField=view.findViewById(R.id.join_online);
                joinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String note=idField.getText().toString();
                        String MyID=getRandomNumberString();
                        if(note.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Enter Code", Toast.LENGTH_SHORT).show();
                        }else if(note.length()<6){
                            Toast.makeText(getApplicationContext(), "Enter correct Code", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent i = new Intent(MainActivity.this, OnlineNotepadActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("id", note);
                            i.putExtra("MyId", MyID);
                            startActivity(i);
                            finish();
                            pop.dismiss();
                        }
                    }
                });
            }
        });
    }

    //generate random unique id
    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }


    //menu item select events handling
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                mdrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.donation:
                mdrawerLayout.closeDrawer(GravityCompat.START);
                Intent i = new Intent(MainActivity.this, DonationActivity.class);
                startActivity(i);
                finish();
                break;
//            case R.id.setting:
//                mdrawerLayout.closeDrawer(GravityCompat.START);
//                Toast.makeText(getApplicationContext(), "setting key", Toast.LENGTH_LONG).show();
//                break;
            case R.id.about:
                mdrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.share:
                mdrawerLayout.closeDrawer(GravityCompat.START);
                share(MainActivity.this, "");
                break;
            case R.id.rate:
                mdrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mdrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mdrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    public void CreateOffline() {
        Intent i = new Intent(MainActivity.this, OfflineNotepadActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    ;

    //after share button clicked.....
    public static void share(Context context, String body) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sharingIntent.setType("text/plain");
        String shareBody = body + "\n\n" + context.getString(R.string.share_body) + BuildConfig.APPLICATION_ID;
        String shareSub = "Share app";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }


    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    // creating download folder
    public void createSaveDir() {
        File file = new File(getSaveDir(MainActivity.this), "Online_Notepad");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getSaveDir(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory() + "Online_Notepad");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath();
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store Data. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

}