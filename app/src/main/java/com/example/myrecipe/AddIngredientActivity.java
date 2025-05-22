package com.example.myrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class AddIngredientActivity extends AppCompatActivity {
    private TextInputEditText nameEt, tagEt, descEt;
    private CheckBox foundCb, favCb;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        // Toolbar setup (if you have one in your layout)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Ingredient");
        }

        // Bind views
        nameEt  = findViewById(R.id.input_name);
        tagEt   = findViewById(R.id.input_tag);
        descEt  = findViewById(R.id.input_desc);
        foundCb = findViewById(R.id.check_found);
        favCb   = findViewById(R.id.check_fav);
        saveBtn = findViewById(R.id.btn_save);   // ← this now resolves

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            postNewIngredient(name,
                    tagEt.getText().toString().trim(),
                    descEt.getText().toString().trim(),
                    foundCb.isChecked(),
                    favCb.isChecked()
            );
        });
    }

    private void postNewIngredient(String name, String tag, String desc, boolean found, boolean fav) {
        String url = "http://192.168.5.102:8080/api/addIngredient.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
                    // Notify the list to refresh
                    LocalBroadcastManager.getInstance(this)
                            .sendBroadcast(new Intent("ingredient_changed"));
                    finish();
                },
                error -> Toast.makeText(this, "Add failed: "+error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("IngName",     name);
                p.put("tag",         tag);
                p.put("description", desc);
                p.put("FoundOrNot",  found ? "1" : "0");
                p.put("favorite",    fav   ? "1" : "0");
                return p;
            }
        };

        queue.add(req);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
