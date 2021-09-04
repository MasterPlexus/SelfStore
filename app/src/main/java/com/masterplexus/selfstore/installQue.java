package com.masterplexus.selfstore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.masterplexus.selfstore.ui.home.HomeFragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.os.Build;


import java.io.File;

public class installQue extends FragmentActivity {

    private String TAG ="installQue";
    private Context context = SelfStoreApplication.getAppContext();

    int MYINSTALLACTIVITY =4711;

    static final String ACTION_INSTALL_PACKAGE = "INSTALL_PACKAGE";
    static final String ACTION_UNINSTALL_PACKAGE = "UNINSTALL_PACKAGE";

    private static final int REQUEST_CODE_INSTALL = 0;
    private static final int REQUEST_CODE_UNINSTALL = 1;

    private Uri downloadUri;
    private String uninstallPackageName;

    private String file_url;

    // for the broadcasts
    //private DefaultInstaller installer;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //installer = new DefaultInstaller(this);

            Intent intent = getIntent();
            String action = intent.getAction();
            if (ACTION_INSTALL_PACKAGE.equals(action)) {
                Uri localApkUri = intent.getData();
                downloadUri = intent.getParcelableExtra(file_url);
                installPackage(localApkUri);
            } else if (ACTION_UNINSTALL_PACKAGE.equals(action)) {
                uninstallPackageName = intent.getStringExtra(file_url);

                uninstallPackage(uninstallPackageName);
            } else {
                throw new IllegalStateException("Intent action not specified!");
            }
        }

        @SuppressLint("InlinedApi")
        private void installPackage(Uri uri) {
            if (uri == null) {
                throw new RuntimeException("Set the data uri to point to an apk location!");
            }
            // https://code.google.com/p/android/issues/detail?id=205827
            if ((Build.VERSION.SDK_INT < 24) && (!uri.getScheme().equals("file"))) {
                throw new RuntimeException("PackageInstaller < Android N only supports file scheme!");
            }
            if ((Build.VERSION.SDK_INT >= 24) && (!uri.getScheme().equals("content"))) {
                throw new RuntimeException("PackageInstaller >= Android N only supports content scheme!");
            }

            Intent intent = new Intent();

            // Note regarding EXTRA_NOT_UNKNOWN_SOURCE:
            // works only when being installed as system-app
            // https://code.google.com/p/android/issues/detail?id=42253

            if (Build.VERSION.SDK_INT < 14) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else if (Build.VERSION.SDK_INT < 16) {
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
            } else if (Build.VERSION.SDK_INT < 24) {
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(uri);
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            } else { // Android N
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(uri);
                // grant READ permission for this content Uri
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            }

            try {
                startActivityForResult(intent, REQUEST_CODE_INSTALL);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "ActivityNotFoundException", e);
                HomeFragment.unsetRunning();
                finish();
            }
            //installer.sendBroadcastInstall(downloadUri, Installer.ACTION_INSTALL_STARTED);
        }

        private void uninstallPackage(String packageName) {
            // check that the package is installed
            try {
                getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "NameNotFoundException", e);
                //installer.sendBroadcastUninstall(packageName, Installer.ACTION_UNINSTALL_INTERRUPTED,
                //        "Package that is scheduled for uninstall is not installed!");
                finish();
                return;
            }

            Uri uri = Uri.fromParts("package", packageName, null);
            Intent intent = new Intent();
            intent.setData(uri);

            if (Build.VERSION.SDK_INT < 14) {
                intent.setAction(Intent.ACTION_DELETE);
            } else {
                intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            }

            try {
                startActivityForResult(intent, REQUEST_CODE_UNINSTALL);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "ActivityNotFoundException", e);
                //installer.sendBroadcastUninstall(packageName, Installer.ACTION_UNINSTALL_INTERRUPTED,
                //        "This Android rom does not support ACTION_UNINSTALL_PACKAGE!");
                finish();
            }
        }

        /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case REQUEST_CODE_INSTALL:
                    /**
                     * resultCode is always 0 on Android < 4.0. See
                     * com.android.packageinstaller.PackageInstallerActivity: setResult is
                     * never executed on Androids < 4.0

                    if (Build.VERSION.SDK_INT < 14) {
                        //installer.sendBroadcastInstall(downloadUri, Installer.ACTION_INSTALL_COMPLETE);
                        break;
                    }

                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            //installer.sendBroadcastInstall(downloadUri, Installer.ACTION_INSTALL_COMPLETE);
                            break;
                        case Activity.RESULT_CANCELED:
                            //installer.sendBroadcastInstall(downloadUri, Installer.ACTION_INSTALL_INTERRUPTED);
                            break;
                        case Activity.RESULT_FIRST_USER:
                        default:
                            // AOSP returns Activity.RESULT_FIRST_USER on error
                            //installer.sendBroadcastInstall(downloadUri, Installer.ACTION_INSTALL_INTERRUPTED,
                            //        getString(R.string.install_error_unknown));
                            break;
                    }

                    break;
                case REQUEST_CODE_UNINSTALL:
                    // resultCode is always 0 on Android < 4.0.
                    if (Build.VERSION.SDK_INT < 14) {
                        //installer.sendBroadcastUninstall(uninstallPackageName, Installer.ACTION_UNINSTALL_COMPLETE);
                        Log.i(TAG,"sdk <14");
                        break;
                    }

                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            //installer.sendBroadcastUninstall(uninstallPackageName, Installer.ACTION_UNINSTALL_COMPLETE);
                            Log.i(TAG,"result ok");
                            break;
                        case Activity.RESULT_CANCELED:
                            Log.i(TAG,"Activity.RESULT_CANCELED");
                            break;
                        case Activity.RESULT_FIRST_USER:
                        default:
                            // AOSP UninstallAppProgress returns RESULT_FIRST_USER on error
                            //installer.sendBroadcastUninstall(uninstallPackageName, Installer.ACTION_UNINSTALL_INTERRUPTED,
                            //        getString(R.string.uninstall_error_unknown));
                            Log.i(TAG,"Activity.RESULT_FIRST_USER");
                            break;
                    }

                    break;
                default:
                    throw new RuntimeException("Invalid request code!");
            }

            // after doing the broadcasts, finish this transparent wrapper activity
            finish();
        }
    */

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MYINSTALLACTIVITY) {
            String nextApp= HomeFragment.getNextApptoInstall();
            if (!nextApp.isEmpty()) {
                Uri apkURI = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", new File(nextApp));
                Log.i(TAG, "Start next app " + nextApp);
                installPackage(apkURI);

                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    Log.i(TAG, "Result string: " + result);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "Canceled Result, try next app");
                } else {
                    Log.i(TAG, "no Result, try next app");

                }
            } else {
                Log.i (TAG, "it was the last app, stopping!");
                HomeFragment.unsetRunning();
            }
            finish();

        }
    }


    //-------
    public void installAPK (String file_url) {
        Log.i(TAG, "Start the intend of " + file_url);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE); //ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", new File(file_url));
        intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        //context.startActivityforResult(intent);
        //startActivityforResult(intent, MYINSTALLACTIVITY);
        try {
            Log.i(TAG,"just");
        } catch (Exception e) {
            Log.e (TAG,"error by Start with Result" + e.getStackTrace().toString());
            HomeFragment.unsetRunning();
            context.startActivity(intent);

        }
    }

    public void installLos() {
        HomeFragment.setRunning();
        String nextApp= HomeFragment.getNextApptoInstall();
        if (!nextApp.isEmpty()) {
            Log.i (TAG, "Start next app " + nextApp);
            Uri apkURI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext()
                            .getPackageName() + ".provider", new File(nextApp));
            Log.i(TAG, "Start next app " + nextApp);
            installPackage(apkURI);
        } else {
            Log.e (TAG, "it was the last app, but the first run - stopping!");
            HomeFragment.unsetRunning();
        }
    }


}
