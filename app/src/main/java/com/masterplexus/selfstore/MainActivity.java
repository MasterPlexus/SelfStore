package com.masterplexus.selfstore;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.webkit.MimeTypeMap;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.masterplexus.selfstore.databinding.ActivityMainBinding;
import com.masterplexus.selfstore.ui.home.HomeFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private String TAG ="MainActivity";

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

                GetAPKs dotask = new GetAPKs();
                dotask.ListSources = new String[] {
                        "https://github.com/MasterPlexus/SelfStore/releases",
                        "https://github.com/julian-klode/dns66/releases",
                        "https://github.com/nextcloud/news-android/releases" };

                dotask.execute();
                
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

    private Context context = SelfStoreApplication.getAppContext();

    int MYINSTALLACTIVITY =4711;

    public void installAPK (String file_url) {
        Log.i(TAG, "Start the intend of " + file_url);
        Intent intent = new Intent(Intent.ACTION_VIEW); //ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", new File(file_url));
        intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        ActivityCompat.startActivityForResult(this,intent, MYINSTALLACTIVITY, null);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MYINSTALLACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Log.i (TAG, "Result string: " + result);
                String nextApp= HomeFragment.getNextApptoInstall();
                if (!nextApp.isEmpty()) {
                    Log.i (TAG, "Start next app " + nextApp);
                    installAPK(nextApp);
                } else {
                    Log.i (TAG, "it was the last app, stopping!");
                    HomeFragment.unsetRunning();
                }
            } else if (resultCode == Activity.RESULT_CANCELED ) {
                Log.i (TAG, "Canceled Result, try next app");
                String nextApp= HomeFragment.getNextApptoInstall();
                if (!nextApp.isEmpty()) {
                    Log.i (TAG, "Start next app " + nextApp);
                    installAPK(nextApp);
                } else {
                    Log.i (TAG, "it was the last app, stopping!");
                    HomeFragment.unsetRunning();
                }
            } else {
                Log.i (TAG, "no Result, try next app");
                String nextApp= HomeFragment.getNextApptoInstall();
                if (!nextApp.isEmpty()) {
                    Log.i (TAG, "Start next app " + nextApp);
                    installAPK(nextApp);
                } else {
                    Log.i (TAG, "it was the last app, stopping!");
                    HomeFragment.unsetRunning();
                }
            }
        }
    }

    public void installLos() {
        HomeFragment.setRunning();
        String nextApp= HomeFragment.getNextApptoInstall();
        if (!nextApp.isEmpty()) {
            Log.i (TAG, "Start next app " + nextApp);
            installAPK(nextApp);
        } else {
            Log.e (TAG, "it was the last app, but the first run - stopping!");
            HomeFragment.unsetRunning();
        }
    }

}
