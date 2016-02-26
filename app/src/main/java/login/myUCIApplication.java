package login;

import android.app.Application;
import android.util.Log;

import com.firebase.client.Firebase;

public class myUCIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("login", "before set context");
        Firebase.setAndroidContext(this);
        Log.d("login", "after set context");

    }
}
