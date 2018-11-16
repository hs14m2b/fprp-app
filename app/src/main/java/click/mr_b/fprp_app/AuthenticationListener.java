package click.mr_b.fprp_app;

import android.hardware.fingerprint.FingerprintManager;

interface AuthenticationListener {
    public void authenticationSucceeded(FingerprintManager.CryptoObject cryptoObject);
    public void authenticationFailed();
    public void authenticationCancelled();
}
