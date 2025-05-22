package com.example.myrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class IngredientDetailActivity extends AppCompatActivity {
    private static final String TAG = "IngredientDetail";

    private SwitchMaterial favSwitch;
    private MaterialButton deleteBtn;
    private String ingredientName;
    private boolean currentFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        favSwitch = findViewById(R.id.favorite_switch);
        deleteBtn = findViewById(R.id.btn_delete);

        ingredientName = getIntent().getStringExtra("IngName");
        loadIngredientDetails();

        favSwitch.setOnCheckedChangeListener((btn, isChecked) -> updateFavorite(isChecked));
        deleteBtn.setOnClickListener(v -> deleteIngredient());
    }

    private void loadIngredientDetails() {
        try {
            String url = "http://192.168.5.102:8080/api/getIngredientDetails.php?IngName=" +
                    URLEncoder.encode(ingredientName, "UTF-8");
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.getCache().remove(url);
            queue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // parse and set UI...
                        currentFav = response.optInt("favorite", 0) == 1;
                        favSwitch.setChecked(currentFav);
                    },
                    error -> Toast.makeText(this, "Load failed", Toast.LENGTH_SHORT).show()
            ) {{ setShouldCache(false); }});
        } catch (Exception e) {
            Log.e(TAG, "Encoding error", e);
        }
    }

    private void updateFavorite(boolean isFav) {
        String url = "http://192.168.5.102:8080/api/updateIngredient.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.POST, url,
                resp -> {
                    Toast.makeText(this, isFav?"Added to favorites":"Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                    currentFav = isFav;
                    LocalBroadcastManager.getInstance(this)
                            .sendBroadcast(new Intent("ingredient_changed"));
                },
                err -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("IngName", ingredientName);
                p.put("favorite", isFav?"1":"0");
                return p;
            }
        };
        queue.add(req);
    }

    private void deleteIngredient() {
        String url = "http://192.168.5.102:8080/api/deleteIngredient.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.POST, url,
                resp -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(this)
                            .sendBroadcast(new Intent("ingredient_changed"));
                    finish();
                },
                err -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("IngName", ingredientName);
                return p;
            }
        };
        queue.add(req);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); return true;
    }
}