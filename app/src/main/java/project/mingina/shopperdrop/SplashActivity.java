package project.mingina.shopperdrop;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import android.os.Handler;
import java.util.logging.LogRecord;

public class SplashActivity extends AppCompatActivity {

    public final static int SPLASH_SCREEN_TIMEOUT = 4000;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

      Typeface mTypeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Lobster.ttf");
        TextView mTextView = (TextView) findViewById(R.id.textViewSplash);
        mTextView.setTypeface(mTypeFace);

                                new Handler().postDelayed(new Runnable(){
                                        @Override
                                      public void run() {
                                            Intent mIntent = new Intent(SplashActivity.this,RegisterActivity.class);
                                            SplashActivity.this.startActivity(mIntent);
                                            SplashActivity.this.finish();
                                      }
                                  },SPLASH_SCREEN_TIMEOUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
}
