package com.kangtech.tauonstream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kangtech.tauonstream.util.SharedPreferencesUtils;

import java.util.Objects;

public class AddStream extends AppCompatActivity {

    private SharedPreferences.Editor editor;

    private TextInputEditText tieIP, tiePORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stream);

        tieIP = findViewById(R.id.tie_ip);
        tiePORT = findViewById(R.id.tie_port);
        Button btnGoStream = findViewById(R.id.btn_go_stream);

        if (SharedPreferencesUtils.getBoolean("is_stream", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnGoStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(tieIP.getText()).length() == 0 || Objects.requireNonNull(tiePORT.getText()).length() == 0) {
                    Toast.makeText(AddStream.this, "IP or PORT not Valid", Toast.LENGTH_SHORT).show();
                } else {
                    initStream();

                    finish();
                }
            }
        });
    }

    private void initStream() {
        String getIP = String.valueOf(tieIP.getText());
        String getPORT = String.valueOf(tiePORT.getText());

        editor = getSharedPreferences("tauon_stream", MODE_PRIVATE).edit();
        editor.putString("ip", getIP);
        editor.putString("port", getPORT);
        editor.putBoolean("is_stream", true);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}