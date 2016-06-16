package com.pedro.easyyt.base;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.pedro.easyyt.constants.Constants;
import java.util.Arrays;

/**
 * Created by pedro on 4/05/16.
 */
public class EasyYTActivity extends Activity {

  private final String TAG = EasyYTActivity.class.toString();

  private GoogleAccountCredential credential;
  private SharedPreferences sp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sp = PreferenceManager
            .getDefaultSharedPreferences(this);
    acquireCredencial();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    MultiDex.install(this); //fix error class not found exception
  }

  private void acquireCredencial(){
    credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(Constants.SCOPES));
    credential.setBackOff(new ExponentialBackOff());
  }

  protected void chooseAccount(){
    String name = sp.getString("accountName", null);
    if(name == null){
      startActivityForResult(credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER);
    }
    else{
      loadAccount();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode){
      case Constants.REQUEST_ACCOUNT_PICKER:
        String account = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
        credential.setSelectedAccountName(account);
        saveAccount(account);
      case Constants.REQUEST_AUTHORIZATION:
        if(resultCode != Activity.RESULT_OK){
          chooseAccount();
        }
      default:
    }
  }

  private void saveAccount(String name) {
    sp.edit().putString("accountName", name).apply();
    Log.d(TAG, name + " saved");
  }

  private void loadAccount() {
    credential.setSelectedAccountName(sp.getString("accountName", null));
    invalidateOptionsMenu();
    Log.d(TAG, sp.getString("accountName", null) + " loaded");
  }

  protected void changeAccount(){
    startActivityForResult(credential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER);
  }

  protected GoogleAccountCredential getCredential(){
    return credential;
  }
}