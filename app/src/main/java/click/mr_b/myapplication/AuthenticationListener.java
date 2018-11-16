package click.mr_b.myapplication;

import android.hardware.fingerprint.FingerprintManager;

interface AuthenticationListener {
    public void authenticationSucceeded(FingerprintManager.CryptoObject cryptoObject);
    public void authenticationFailed();
    public void authenticationCancelled();
}
