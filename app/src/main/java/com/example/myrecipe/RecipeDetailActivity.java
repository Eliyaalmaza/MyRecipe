package com.example.myrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = "RecipeDetailActivity";

    private TextView nameTv, tagTv, itemsTv, processTv, statusTv;
    private SwitchMaterial favSwitch;
    private String recipeName;
    private boolean currentFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameTv    = findViewById(R.id.recipe_name);
        tagTv     = findViewById(R.id.recipe_tag);
        itemsTv   = findViewById(R.id.recipe_items);
        processTv = findViewById(R.id.recipe_process);
        statusTv  = findViewById(R.id.recipe_status);
        favSwitch = findViewById(R.id.favorite_switch);

        recipeName = getIntent().getStringExtra("RecipName");
        if (recipeName == null) {
            Toast.makeText(this, "No recipe specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(recipeName);
        }

        loadRecipeDetails();
    }

    private void loadRecipeDetails() {
        try {
            String encoded = URLEncoder.encode(recipeName, "UTF-8");
            String url = "http://192.168.5.102:8080/api/getRecipeDetails.php?RecipName=" + encoded;

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.getCache().remove(url);

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    resp -> {
                        try {
                            if (resp.has("error")) {
                                Toast.makeText(this, resp.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String name    = resp.getString("RecipName");
                            String tag     = resp.getString("tag");
                            String items   = resp.getString("Items");
                            String process = resp.getString("Process");
                            boolean canDo  = resp.getInt("CanbeDone") == 1;
                            currentFav     = resp.getInt("favorite") == 1;

                            nameTv.setText(name);
                            tagTv.setText(tag);
                            itemsTv.setText(items);
                            processTv.setText(process);
                            statusTv.setText(canDo ? "You can make this recipe" : "Missing ingredients");

                            favSwitch.setOnCheckedChangeListener(null);
                            favSwitch.setChecked(currentFav);
                            favSwitch.setOnCheckedChangeListener((btn, isChecked) -> updateFavorite(isChecked));
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error", e);
                            Toast.makeText(this, "Data error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    err -> {
                        Log.e(TAG, "Load error", err);
                        Toast.makeText(this, "Load failed", Toast.LENGTH_SHORT).show();
                    }
            );
            req.setShouldCache(false);
            queue.add(req);
        } catch (Exception e) {
            Log.e(TAG, "URL encode error", e);
            Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavorite(boolean isFav) {
        String url = "http://192.168.5.102:8080/api/updateRecipe.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest req = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    Toast.makeText(this,
                            isFav ? "Added to favorites!" : "Removed from favorites!",
                            Toast.LENGTH_SHORT).show();
                    currentFav = isFav;
                },
                error -> {
                    Log.e(TAG, "Update error", error);
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    favSwitch.setOnCheckedChangeListener(null);
                    favSwitch.setChecked(currentFav);
                    favSwitch.setOnCheckedChangeListener((btn, checked) -> updateFavorite(checked));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("RecipName", recipeName);
                params.put("favorite", isFav ? "1" : "0");
                return params;
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