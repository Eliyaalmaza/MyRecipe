package com.example.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    public enum ListType {
        MANAGE,
        MYFRIDGE,
        FAVORITE
    }

    private ArrayList<Ingredient> ingredients;
    private Context context;
    private ManageFridgeFragment.UpdateStatusListener updateStatusListener;
    private ListType listType;

    public IngredientAdapter(ArrayList<Ingredient> ingredients,
                             Context context,
                             ManageFridgeFragment.UpdateStatusListener listener,
                             ListType listType) {
        this.ingredients = ingredients;
        this.context = context;
        this.updateStatusListener = listener;
        this.listType = listType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);

        holder.nameTextView.setText(ingredient.getName());
        holder.tagTextView.setText(ingredient.getTag());

        // Only show the "Found?" checkbox in MANAGE list
        holder.foundCheckBox.setOnCheckedChangeListener(null);
        if (listType == ListType.MANAGE) {
            holder.foundCheckBox.setVisibility(View.VISIBLE);
            holder.foundCheckBox.setChecked(ingredient.isFoundOrNot());
            holder.foundCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ingredient.setFoundOrNot(isChecked);
                updateIngredientFoundStatus(ingredient.getName(), isChecked);
            });
        } else {
            holder.foundCheckBox.setVisibility(View.GONE);
        }

        // Hide favorite switch in the list items; favorites managed only in detail view
        holder.favoriteSwitch.setVisibility(View.GONE);
        holder.favoriteSwitch.setOnCheckedChangeListener(null);

        // Item click opens detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, IngredientDetailActivity.class);
            intent.putExtra("IngName", ingredient.getName());
            context.startActivity(intent);
        });
    }

    private void updateIngredientFoundStatus(String ingName, boolean foundOrNot) {
        String url = "http://192.168.5.102:8080/api/updateIngredient.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(context, "Ingredient status updated.", Toast.LENGTH_SHORT).show();
                    if (updateStatusListener != null) {
                        updateStatusListener.onUpdateSuccess();
                    }
                    // ▶▶ Broadcast so MyFridgeFragment (and any other) can reload:
                    Intent i = new Intent("ingredient_status_updated");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                },
                error -> {
                    Toast.makeText(context, "Failed to update ingredient.", Toast.LENGTH_SHORT).show();
                    if (updateStatusListener != null) updateStatusListener.onUpdateFailure();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("IngName", ingName);
                params.put("FoundOrNot", foundOrNot ? "1" : "0");
                return params;
            }
        };

        queue.add(postRequest);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView tagTextView;
        CheckBox foundCheckBox;
        // favoriteSwitch kept for compatibility but hidden
        Switch favoriteSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView   = itemView.findViewById(R.id.ingredient_name);
            tagTextView    = itemView.findViewById(R.id.ingredient_tag);
            foundCheckBox  = itemView.findViewById(R.id.found_checkbox);
            favoriteSwitch = itemView.findViewById(R.id.favorite_switch);
        }
    }
}
