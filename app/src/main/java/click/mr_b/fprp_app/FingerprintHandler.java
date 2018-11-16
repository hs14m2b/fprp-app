package click.mr_b.fprp_app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private CancellationSignal cancellationSignal;
    private Context context;
    private Activity activity;
    private AuthenticationListener listener;
    private int failCount = 0;
    static int MAX_FAILS = 5;

    public FingerprintHandler(Context mContext, AuthenticationListener listener) {
        context = mContext;
        activity = (Activity) mContext;
        this.listener = listener;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        failCount = 0;
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        Log.d(this.getClass().getSimpleName(), "Started Authentication");
        //Toast.makeText(context, "Started Authentication" , Toast.LENGTH_LONG).show();
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        //Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getSimpleName(), "Authentication error\n" + errString);
        // cancel the activity
        //activity.setResult(Activity.RESULT_CANCELED);
        //activity.finish();
        listener.authenticationFailed();
    }

    @Override

    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        Log.d(this.getClass().getSimpleName(), "Authentication failed");
        //Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        failCount++;
        if (failCount >= MAX_FAILS)
        {
            // cancel the activity
            //activity.setResult(Activity.RESULT_CANCELED);
            //activity.finish();
            listener.authenticationFailed();
        }
    }

    @Override
    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.d(this.getClass().getSimpleName(), "Authentication help\n" + helpString);
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Log.d(this.getClass().getSimpleName(), "Authentication Success!");
        FingerprintManager.CryptoObject cryptoObject =  result.getCryptoObject();
        //Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        //Intent returnIntent = new Intent();
        //returnIntent.putExtra("result", "0");
        //returnIntent.putExtra("serializableResult", result);
        //activity.setResult(Activity.RESULT_OK,returnIntent);
        //activity.finish();
        listener.authenticationSucceeded(cryptoObject);
    }

}