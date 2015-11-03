package project.mingina.shopperdrop;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.AgeRange;

import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    String gender;

    ImageView mProfileImage;
    TextView mUsernameText, mEmailText,mUserAge, mUserGender,mAppName1,mAppName,mTextOr;
    GoogleApiClient mGoogleApiClient;
    Button mBtRegister,mBtContinue;
    LinearLayout mSignInLayout, mProfileLayout;
    SignInButton mBtGooglePlus;
    ProgressBar mProgressBar;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameText = (TextView)findViewById(R.id.user_name);
        mEmailText = (TextView)findViewById(R.id.user_email);
        mUserAge = (TextView)findViewById(R.id.user_age);
        mUserGender = (TextView)findViewById(R.id.user_gender);
        mAppName1 = (TextView)findViewById(R.id.textViewAppName1);
        mAppName = (TextView)findViewById(R.id.textViewAppName);
        mTextOr = (TextView)findViewById(R.id.textViewOr1);
        mTypeface = Typeface.createFromAsset(this.getAssets(),"fonts/Lobster.ttf");
        mAppName1.setTypeface(mTypeface);
        mAppName.setTypeface(mTypeface);
        mTextOr.setTypeface(mTypeface);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProfileImage = (ImageView)findViewById(R.id.user_profile_pic);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        mBtGooglePlus = (SignInButton)findViewById(R.id.signInButton);
        mBtRegister = (Button)findViewById(R.id.btRegister);
        mBtContinue = (Button)findViewById(R.id.btContinue);
        Intent intent = new Intent(RegisterActivity.this, LocationActivity.class);
        startActivity(intent);
        mBtGooglePlus.setOnClickListener(this);

        mSignInLayout = (LinearLayout)findViewById(R.id.sign_in_layout);
        mProfileLayout = (LinearLayout)findViewById(R.id.user_info_layout);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        signedInUser = false;
        Toast.makeText(this, "G+ Connected", Toast.LENGTH_LONG).show();
        getProfileInformation();
        mProgressBar.setVisibility(View.GONE);

    }
    private void updateProfile(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInLayout.setVisibility(View.GONE);
            mProfileLayout.setVisibility(View.VISIBLE);

        } else {
            mSignInLayout.setVisibility(View.VISIBLE);
            mProfileLayout.setVisibility(View.GONE);
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String AgeRange = String.valueOf(currentPerson.getAgeRange());
                int personGender = currentPerson.getGender();
                if(personGender == 0)
                {
                    gender ="Male";
                }
                if (personGender == 1)
                {
                    gender = "Female";

                }
                if (personGender == 2)
                {
                    gender = "Other";
                }
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


                mUsernameText.setText(personName);
                mEmailText.setText(email);
                mUserAge.setText(AgeRange);
                mUserGender.setText(gender);

                new LoadProfileImage(mProfileImage).execute(personPhotoUrl);

                // update profile frame with new info about Google Account
                // profile
                updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateProfile(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                googlePlusLogin();
                break;
        }

    }
    public void signIn(View v) {
        googlePlusLogin();

    }

    public void logout(View v) {
        googlePlusLogout();
        updateUIWidgets();
    }

    private void googlePlusLogin() {
        if (!mGoogleApiClient.isConnecting()) {
            signedInUser = true;
            resolveSignInError();
        }
    }
    private void updateUIWidgets() {
        if (mGoogleApiClient.isConnecting()) {
            mProgressBar.setVisibility(ProgressBar.GONE);

        } else {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);

        }

    }
    private void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateProfile(false);
        }
    }
    // download Google Account profile image, to complete profile
    private class LoadProfileImage extends AsyncTask {
        ImageView downloadedImage;

        public LoadProfileImage(ImageView image) {
            this.downloadedImage = image;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            downloadedImage.setImageBitmap(result);
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;

            if (signedInUser) {
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (responseCode == RESULT_OK) {
                    signedInUser = false;

                }
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }
}
