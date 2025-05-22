package com.example.myrecipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyFridgeFragment extends Fragment {

    private RecyclerView recyclerView;
    private IngredientAdapter adapter;
    private ArrayList<Ingredient> ingredientsList;

    // Listen for changes to found-status so we reload automatically
    private final BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadIngredients();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_fridge, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientsList = new ArrayList<>();
        adapter = new IngredientAdapter(
                ingredientsList,
                requireContext(),
                /* no update listener here */ null,
                IngredientAdapter.ListType.MYFRIDGE
        );
        recyclerView.setAdapter(adapter);

        // initial load
        loadIngredients();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(refreshReceiver, new IntentFilter("ingredient_status_updated"));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(refreshReceiver);
        super.onStop();
    }

    private void loadIngredients() {
        String url = "http://192.168.5.102:8080/api/getIngredients.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    ingredientsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            // only include those marked FoundOrNot == 1
                            if (obj.optInt("FoundOrNot", 0) == 1) {
                                String name        = obj.optString("IngName", "");
                                String tag         = obj.optString("tag", "");
                                String description = obj.optString("description", "");
                                boolean favorite   = obj.optInt("favorite", 0) == 1;
                                ingredientsList.add(new Ingredient(
                                        name, tag, description, true, favorite
                                ));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireContext(),
                                    "Error parsing ingredient at index " + i,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(requireContext(),
                        "Failed to load My Fridge: " + error.getMessage(),
                        Toast.LENGTH_LONG).show()
        );

        req.setShouldCache(false);
        queue.add(req);
    }
}
