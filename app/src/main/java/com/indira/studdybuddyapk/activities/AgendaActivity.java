package com.indira.studdybuddyapk.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.indira.studdybuddyapk.R;

public class AgendaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        
        if (findViewById(R.id.btn_star_agenda) != null) {
            findViewById(R.id.btn_star_agenda).setOnClickListener(v -> finish());
        }
    }
}