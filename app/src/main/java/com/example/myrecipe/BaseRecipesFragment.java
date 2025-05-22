package com.example.myrecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseRecipesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AutoCompleteTextView searchView;
    private RecipeAdapter adapter;

    private List<Recipe> master = new ArrayList<>();
    private List<Recipe> display = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_recipes, container, false);
        searchView = view.findViewById(R.id.search_recipe);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecipeAdapter(display, requireContext());
        recyclerView.setAdapter(adapter);

        setupSearch();
        loadRecipes();
        return view;
    }

    private void setupSearch() {
        searchView.setThreshold(1);
        searchView.setOnItemClickListener((parent, v, pos, id) -> applyFilter());
        searchView.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { applyFilter(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void loadRecipes() {
        String url = "http://192.168.5.102:8080/api/getRecipes.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    master.clear();
                    Set<String> names = new HashSet<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject o = response.getJSONObject(i);
                            Recipe r = new Recipe(
                                    o.getString("RecipName"),
                                    o.getString("tag"),
                                    o.getString("Items"),
                                    o.getString("Process"),
                                    o.getInt("CanbeDone")==1,
                                    o.getInt("favorite")==1
                            );
                            master.add(r);
                            names.add(r.getName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> sugAdapter = new ArrayAdapter<>(
                            requireContext(), android.R.layout.simple_dropdown_item_1line,
                            new ArrayList<>(names)
                    );
                    searchView.setAdapter(sugAdapter);
                    applyFilter();
                },
                error -> Toast.makeText(getContext(),"Load recipes failed",Toast.LENGTH_SHORT).show()
        );
        req.setShouldCache(false);
        queue.add(req);
    }

    private void applyFilter() {
        String q = searchView.getText().toString().toLowerCase();
        display.clear();
        for (Recipe r: master) {
            boolean match = r.getName().toLowerCase().contains(q);
            if (match && shouldInclude(r)) display.add(r);
        }
        adapter.notifyDataSetChanged();
    }

    protected abstract boolean shouldInclude(Recipe recipe);
}