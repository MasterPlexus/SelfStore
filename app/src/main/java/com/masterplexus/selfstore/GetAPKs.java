package com.masterplexus.selfstore;

import android.os.AsyncTask;
import android.util.Log;

import com.masterplexus.selfstore.ui.DownloadFileFromURL;
import com.masterplexus.selfstore.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetAPKs extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GetAPKs";

    String[] ListSources;
    Integer actualProcess = 0;

    @Override
    protected String doInBackground(Void... voids) {

        InputStream is = null;
        try {
            is = new URL(ListSources[actualProcess]).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String PageText = readAll(rd);

            return PageText;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    protected void onPostExecute(String Page) {

        String apkdownload = "";
        // Link
        apkdownload = GetHost() + new RegExSnipped().GetSearch(Page,
                    "<a[^>]+href=['\"]?([^'\">]+apk)['\"]?[^>]*>");

        // Description später
        //apkdownload = apkdownload + "\n" + new RegExSnipped().GetSearch(Page,
        //            "<div[^>]+class=['\"]commit-desc['\"][^>]*>(.*?)</div>");

        Log.i(TAG,"matches: "+apkdownload);
        HomeFragment.setText(apkdownload + " found.\ntry to load");

        if (apkdownload != "") {
            DownloadFileFromURL installAPK = (DownloadFileFromURL) new DownloadFileFromURL().execute(apkdownload);

            actualProcess++;
            if (actualProcess < ListSources.length ) {
                selfRestart();
            }
        }
    }

    public void selfRestart() {
        GetAPKs dotask = new GetAPKs();
        dotask.ListSources = this.ListSources;
        dotask.actualProcess = this.actualProcess;
        dotask.execute();

    }


    private String GetHost () {
        return new RegExSnipped().GetSearch(ListSources[actualProcess],
                "(htt.*?//.*?)/");
    }

}