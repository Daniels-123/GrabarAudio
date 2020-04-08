package com.dybcatering.grabarvoz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btniniciargrabacion, btndetenergrabacion, btnreproducir, btndetener;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!checkPermissionFromDevice())
            requestPermission();



        btniniciargrabacion = findViewById(R.id.btnstartrecord);
        btndetenergrabacion = findViewById(R.id.btnstoprecord);
        btnreproducir = findViewById(R.id.btnstartplay);
        btndetener = findViewById(R.id.btnstopplay);



            btniniciargrabacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkPermissionFromDevice()){




                    pathSave  = Environment.getExternalStorageDirectory()
                            .getAbsolutePath()+"/"
                            + UUID.randomUUID().toString()+"_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnreproducir.setEnabled(false);
                    btndetener.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Grabando...", Toast.LENGTH_SHORT).show();
                    }else{
                        requestPermission();

                    }

                }
            });


            btndetenergrabacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    btndetenergrabacion.setEnabled(false);
                    btnreproducir.setEnabled(true);
                    btniniciargrabacion.setEnabled(true);
                    btndetener.setEnabled(false);
                }
            });


            btnreproducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btndetener.setEnabled(true);
                    btndetenergrabacion.setEnabled(false);
                    btniniciargrabacion.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Reproduciendo...", Toast.LENGTH_SHORT).show();
                }
            });

            btndetener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btndetenergrabacion.setEnabled(false);
                    btniniciargrabacion.setEnabled(true);
                    btndetener.setEnabled(false);
                    btnreproducir.setEnabled(true);

                    if (mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }

                }
            });

    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permiso Concedido", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
                break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
