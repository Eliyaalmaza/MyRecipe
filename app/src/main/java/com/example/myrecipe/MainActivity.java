package com.example.myrecipe;
import com.example.myrecipe.R;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.example.myrecipe.IngredientsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Set up the DrawerLayout and NavigationView
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        // Set up the ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment on launch
        if (savedInstanceState == null) {
            navView.setCheckedItem(R.id.nav_ingredients);
            openIngredientsSection();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //mni7 zabatit R.id
        if (item.getItemId() == R.id.nav_ingredients){openIngredientsSection();} else if (item.getItemId() == R.id.nav_recipes){openRecipesSection();}
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void openIngredientsSection() {
        // Replace fragment for Ingredients
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new IngredientsFragment())
                .commit();
    }

    void openRecipesSection() {
        // Replace fragment for Recipes
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new com.example.myrecipe.RecipesFragment())
                .commit();
    }
}
   