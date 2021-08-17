package com.masterplexus.selfstore;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.masterplexus.selfstore.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start Load Selfstore", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String sURL = "https://github.com/MasterPlexus/SelfStore/releases";

                GetAPKs dotask = new GetAPKs();
                dotask.sURL = sURL;
                dotask.execute();
                
                Snackbar.make(view, "Start Load DNS66", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                
                sURL = "https://github.com/julian-klode/dns66/releases";
                GetAPKs dotask2 = new GetAPKs();
                dotask2.sURL = sURL;
                dotask2.execute();
                
                Snackbar.make(view, "Start Load News-App", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                
                sURL = "https://github.com/nextcloud/news-android/releases";
                GetAPKs dotask3 = new GetAPKs();
                dotask3.sURL = sURL;
                dotask3.execute();

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
