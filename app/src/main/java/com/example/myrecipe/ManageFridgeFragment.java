package com.example.myrecipe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageFridgeFragment extends Fragment {

    private static final String TAG = "ManageFridgeFragment";

    private RecyclerView recyclerView;
    private IngredientAdapter adapter;
    private ArrayList<Ingredient> ingredientsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_fridge, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientsList = new ArrayList<>();
        adapter = new IngredientAdapter(
                ingredientsList,
                requireContext(),
                new ManageFridgeFragment.UpdateStatusListener() {
                    @Override
                    public void onUpdateSuccess() {
                        Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                        // Refresh data using existing loadIngredients()
                        loadIngredients();
                    }

                    @Override
                    public void onUpdateFailure() {
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                },
                IngredientAdapter.ListType.MANAGE
        );
        recyclerView.setAdapter(adapter);

        // Load data for Manage Fridge
        loadIngredients();
        return view;
    }


    public interface UpdateStatusListener {
        void onUpdateSuccess();
        void onUpdateFailure();
    }

    private void loadIngredients() {
        String url = "http://192.168.5.102:8080/api/getIngredients.php"; // Your API endpoint

        Log.d(TAG, "Starting API request to " + url);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "API response received. Length: " + response.length());
                    ingredientsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            String name = obj.getString("IngName");
                            String tag = obj.optString("Tag", "");
                            String description = obj.optString("Description", "");
                            boolean foundOrNot = obj.getInt("FoundOrNot") == 1;
                            boolean favorite = obj.getInt("favorite") == 1;

                            Ingredient ingredient = new Ingredient(name, tag, description, foundOrNot, favorite);
                            ingredientsList.add(ingredient);

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error at index " + i, e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Ingredients list updated and adapter notified.");
                },
                error -> {
                    Log.e(TAG, "Volley error during API call", error);
                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Failed to load ingredients. Please check your Internet connection.",
                                Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);
    }
}
