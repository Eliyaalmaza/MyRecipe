package com.example.myrecipe;

import android.os.Bundle;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoriteItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private IngredientAdapter adapter;
    private ArrayList<Ingredient> ingredientsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_items, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ingredientsList = new ArrayList<>();
        adapter = new IngredientAdapter(
                ingredientsList,
                requireContext(),
                null, // No update listener for Favorites
                IngredientAdapter.ListType.FAVORITE
        );
        recyclerView.setAdapter(adapter);

        // Load data for Favorites
        loadIngredients();
        return view;
    }


    private void loadIngredients() {
        String url = "http://192.168.5.102:8080/api/getIngredients.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.length() == 0) {
                        Toast.makeText(getContext(), "No favorite items found", Toast.LENGTH_SHORT).show();
                    }
                    ingredientsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            if (obj.getInt("favorite") == 1) {
                                String name = obj.getString("IngName");
                                String tag = obj.optString("Tag", "");
                                String description = obj.optString("Description", "");
                                boolean foundOrNot = obj.getInt("FoundOrNot") == 1;
                                boolean favorite = obj.getInt("favorite") == 1;
                                ingredientsList.add(new Ingredient(name, tag, description, foundOrNot, favorite));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "JSON parsing error at index " + i, Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Favorite items loaded successfully", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(getContext(), "Failed to load favorite items", Toast.LENGTH_LONG).show()
        );
        queue.add(request);
    }
}

