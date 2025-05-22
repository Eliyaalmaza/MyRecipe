package com.example.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private Context context;

    public RecipeAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe r = recipes.get(position);
        holder.tvName.setText(r.getName());
        holder.star.setVisibility(r.isFavorite() ? View.VISIBLE : View.GONE);

        // Gray out and dim if recipe cannot be made
        if (!r.isCanBeDone()) {
            holder.tvName.setTextColor(Color.GRAY);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.tvName.setTextColor(Color.BLACK);
            holder.itemView.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!r.isCanBeDone()) {
                // Fetch current ingredient statuses to determine missing items
                String ingUrl = "http://192.168.5.102:8080/api/getIngredients.php";
                RequestQueue queue = Volley.newRequestQueue(context);
                JsonArrayRequest ingReq = new JsonArrayRequest(
                        Request.Method.GET,
                        ingUrl,
                        null,
                        ingResp -> {
                            ArrayList<String> missing = new ArrayList<>();
                            try {
                                for (String ingName : r.getItemsJson().split(",")) {
                                    String nameTrim = ingName.trim();
                                    for (int j = 0; j < ingResp.length(); j++) {
                                        JSONObject obj = ingResp.getJSONObject(j);
                                        if (obj.optString("IngName").equals(nameTrim)
                                                && obj.optInt("FoundOrNot", 0) == 0) {
                                            missing.add(nameTrim);
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context,
                                        "Error parsing ingredient data", Toast.LENGTH_SHORT).show();
                            }

                            if (missing.isEmpty()) {
                                Toast.makeText(context,
                                        "Cannot make this recipe: missing ingredients",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        "Missing: " + TextUtils.join(", ", missing),
                                        Toast.LENGTH_LONG).show();
                            }
                            // Open detail after toast
                            Intent i = new Intent(context, RecipeDetailActivity.class);
                            i.putExtra("RecipName", r.getName());
                            context.startActivity(i);
                        },
                        error -> {
                            Toast.makeText(context,
                                    "Error checking missing ingredients",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context, RecipeDetailActivity.class);
                            i.putExtra("RecipName", r.getName());
                            context.startActivity(i);
                        }
                );
                ingReq.setShouldCache(false);
                queue.add(ingReq);

            } else {
                // Recipe can be made: open detail directly
                Intent i = new Intent(context, RecipeDetailActivity.class);
                i.putExtra("RecipName", r.getName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView star;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            star   = itemView.findViewById(R.id.imgFavRecipe);
        }
    }
}
