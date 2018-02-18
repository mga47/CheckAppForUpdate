package com.gar.sampleapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gar.checkappupdate.InstalledVersion;
import com.gar.checkappupdate.PlayMarketVersion;
import com.gar.checkappupdate.UtilsLib;
import com.gar.checkappupdate.models.Update;

public class MainActivity extends AppCompatActivity implements PlayMarketVersion.CheckUpdateListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlayMarketVersion playMarketVersion = new PlayMarketVersion(this);
        playMarketVersion.setListener(this);
        playMarketVersion.execute();
    }

    @Override
    public void onSuccess(final Update update) {
        Update currentVersion = InstalledVersion.get(this);
        final Context context = this;
        if (update == null || currentVersion == null) {
            return;
        }
        if (UtilsLib.isUpdateAvailable(currentVersion, update)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update is available");
            builder.setMessage("The new version of app available!");
            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    UtilsLib.goToUpdate(context, update.getAppAPK());
                }
            });
            builder.show();
        }

    }

    @Override
    public void onFailure(PlayMarketVersion.CheckUpdateError checkUpdateError) {
        Toast.makeText(this, "Can\'t check app version", Toast.LENGTH_LONG).show();
    }
}

