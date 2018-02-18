package com.gar.checkappupdate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.gar.checkappupdate.models.Update;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PlayMarketVersion extends AsyncTask<Void, Void, Update> {

    public interface CheckUpdateListener {
        void onSuccess(Update update);
        void onFailure(CheckUpdateError error);
    }

    public enum CheckUpdateError { NETWORK_NOT_AVAILABLE, UPDATE_VARIES_BY_DEVICE }

    private WeakReference<Context> mContextReference;
    private CheckUpdateListener mListener;
    private static final String PS_URL = "https://play.google.com/store/apps/details?id=%s&hl=%s";
    private static final String PS_TAG_RELEASE = "itemprop=\"softwareVersion\">";
    private static final String PS_TAG_CHANGES = "recent-change\">";

    public PlayMarketVersion(Context context) {
        this(context, null);
    }

    public PlayMarketVersion(Context context, CheckUpdateListener checkUpdateListener) {
        mContextReference = new WeakReference<>(context);
        mListener = checkUpdateListener;
    }

    /**
     * Method allow to implement interface in separate method
     * @param checkUpdateListener interface object
     */
    public void setListener(CheckUpdateListener checkUpdateListener) {
        mListener = checkUpdateListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mContextReference == null) {
            fail();
        }
        Context context = mContextReference.get();
        if (context == null || mListener == null) {
            fail();
        }

        if(!isNetworkAvailable(context))  {
            mListener.onFailure(CheckUpdateError.NETWORK_NOT_AVAILABLE);
            fail();
        }
    }

    @Override
    @Nullable
    protected Update doInBackground(Void... voids) {
        if (mContextReference == null) {
            return fail();
        }
        Context context = mContextReference.get();
        if (context == null) {
            return fail();
        }
        return getLatestUpdate(context);
    }


    @Override
    protected void onPostExecute(Update update) {
        super.onPostExecute(update);
        if (mListener == null) {
            return;
        }

        if (update != null && isStringAVersion(update.getAppVersion())) {
            mListener.onSuccess(update);
        } else {
            mListener.onFailure(CheckUpdateError.UPDATE_VARIES_BY_DEVICE);
        }
    }

    /**
     * Find and return latest app version info (version, release notes, valid url)
     * @param context application context
     * @return Update object or null
     */
    @Nullable
    private Update getLatestUpdate(Context context) {
        if (context == null) {
            return fail();
        }
        OkHttpClient client = new OkHttpClient();
        URL url = getUpdateURL(context);
        if (url == null) {
            return fail();
        }
        String source = "";
        Request request = (new Request.Builder()).url(url).build();

        try {
            Response response = client.newCall(request).execute();
            if (response == null) {
                return fail();
            }
            ResponseBody body = response.body();
            if (body == null) {
                return fail();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream(), "UTF-8"));
            StringBuilder str = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                if (line.contains(PS_TAG_RELEASE)) {
                    str.append(line);
                }
            }
            source = str.toString();
        } catch (FileNotFoundException e) {
            Log.e("Exception", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("Exception", e.getLocalizedMessage());
        }
        String version = getVersion(source);
        String releaseNotes = getReleaseNotes(source);
        URL updateUrl = getUpdateURL(context);
        return new Update(version, releaseNotes, updateUrl);
    }

    /**
     * Return app version from play store
     * @param source data from response
     * @return String
     */
    @NonNull
    private String getVersion(String source) {
        String version = "0.0.0.0";
        if (source == null) {
            return version;
        }
        String[] splitPlayStore = source.split(PS_TAG_RELEASE);
        if (splitPlayStore.length > 1) {
            splitPlayStore = splitPlayStore[1].split("(<)");
            version = splitPlayStore[0].trim();
        }
        return version;
    }

    /**
     * Return latest release notes from play store
     * @param source data from response
     * @return String
     */
    @NonNull
    private String getReleaseNotes(String source) {
        String recentChanges = "";
        if (source == null) {
            return recentChanges;
        }
        String[] splitPlayStore = source.split(PS_TAG_CHANGES);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < splitPlayStore.length; i++) {
            sb.append(splitPlayStore[i].split("(<)")[0]).append("\n");
        }
        recentChanges = sb.toString();
        return recentChanges;
    }

    /**
     * Consruct valid google play store url
     * @param context application context
     * @return Valid URL or null
     */
    @Nullable
    private URL getUpdateURL(Context context) {
        if (context == null) {
            return null;
        }
        String res = String.format(PS_URL, context.getPackageName(), Locale.getDefault().getLanguage());
        try {
            return new URL(res);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return state of network
     * @param context application context
     * @return true if network is available, false otherwise
     */
    private boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private boolean isStringAVersion(String version) {
        return version != null && version.matches(".*\\d+.*");
    }

    /**
     * Cancel async task and return null
     * @return null
     */
    @Nullable
    private Update fail() {
        this.cancel(true);
        return null;
    }
}
