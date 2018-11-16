package click.mr_b.myapplication;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleObserver;

import click.mr_b.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LifecycleObserver, ClickListener, AuthenticationListener{

    private TextView mTextMessage;
    private ConstraintLayout plansView;
    private RecyclerView recyclerView;
    private FloatingActionButton FAB;
    private BottomNavigationView navigation;
    private WebView mFprpWebView;
    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    protected static final String KEY_NAME = "fprpEncryptionKey";
    private Cipher cipher;
    private Cipher enccipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private TextView textView2;
    private ImageView imageView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private ActivityMainBinding binding;
    FingerprintHandler helper;
    private boolean authenticated = false;
    private boolean previouslyauthenticated = false;
    private PlanViewModel mPlanViewModel;
    List<Plan> mPlans;
    public static final int AUTH_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_PLAN_ACTIVITY_REQUEST_CODE = 2;
    private int previousMenuItem = 0;
    private int currentMenuItem = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            previousMenuItem = currentMenuItem;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentMenuItem = 0;
                    showHome();
                    return true;
                case R.id.navigation_dashboard:
                    currentMenuItem = 1;
                    showPlans();
                    return true;
                case R.id.navigation_shop:
                    currentMenuItem = 2;
                    if (showShop()) {
                        return true;
                    } else
                    {
                        return false;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mTextMessage = binding.textViewHome;
        textView2 = binding.textViewInstructions;
        imageView = binding.imageFPRPFront;
        recyclerView = binding.recyclerview;
        FAB = binding.floatingActionButton;
        navigation = binding.navigation;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFprpWebView = binding.fprpWebview;
        mFprpWebView.loadUrl(getResources().getString(R.string.shopUrl));

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewPlanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, NEW_PLAN_ACTIVITY_REQUEST_CODE);
            }
        });
        //obeserve the lifecycle events
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

   }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        Log.d(this.getClass().getSimpleName(), "App in background");
        authenticated = false;
        showHome();
        navigation.getMenu().getItem(0).setChecked(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onAppForegrounded() {
        Log.d(this.getClass().getSimpleName(), "App in foreground");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AUTH_ACTIVITY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if (result.equals("0")) {
                    //mTextMessage.setText("You have successfully unlocked your plans! " + result);
                    authenticated = true;
                    showPlans();
                }
                if (result.equals("2"))
                {
                    //password was reset - delete all plans
                    authenticated = false;
                    mPlanViewModel.deleteAll();
                    Log.d("MainActivity", "All plans deleted");
                    //restart authentication
                    showPlans();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                //mTextMessage.setText("You need to unlock your plans");
                authenticated=false;
                showHome();
            }
        }
        if (requestCode == NEW_PLAN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String sID = data.getStringExtra(NewPlanActivity.EXTRA_REPLY_ID);
                Plan plan = new Plan(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_NAME));
                try{
                    if (!sID.equals("")) {
                        plan.setId(Integer.parseInt(sID));
                    }
                }
                catch (Exception ex)
                {
                    //do nothing - the ID was not a number - no idea why...
                }
                plan.setQuestion1(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q1));
                plan.setQuestion2(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q2));
                plan.setQuestion3(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q3));
                plan.setQuestion4(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q4));
                plan.setQuestion5(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q5));
                plan.setQuestion6(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_Q6));
                plan.setPoint1(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_RP1));
                plan.setPoint2(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_RP2));
                plan.setPoint3(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_RP3));
                plan.setPoint4(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_RP4));
                plan.setPoint5(data.getStringExtra(NewPlanActivity.EXTRA_REPLY_RP5));
                mPlanViewModel.insert(plan);
            } else {
                Log.d("MainActivity", "Plan not saved as empty or not changed");
            }
        }

    }//onActivityResult

    //hide any active rescue plans
    private void handleOnPause()
    {
        //authenticated = false;
    }

    //hide plans and shop
    private void showHome()
    {
        mTextMessage.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        mFprpWebView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        FAB.setVisibility(View.INVISIBLE);
    }

    //hide plans and home
    private boolean showShop()
    {
        if (checkInternetConnection(this)) {
            mTextMessage.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            mFprpWebView.loadUrl(getResources().getString(R.string.shopUrl));
            mFprpWebView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            FAB.setVisibility(View.INVISIBLE);
            return true;
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please connect to the internet to access the shop", Toast.LENGTH_LONG).show();
            navigation.getMenu().getItem(previousMenuItem).setChecked(true);
            currentMenuItem = previousMenuItem;
            return false;
        }
    }
    //show rescue plans (via authentication if needed)
    private void showPlans()
    {
        if (!authenticated)
        {
            boolean useFingerprint = false;
            //invoke the fingerprint authentication to show the info
            //fingerprint stuff
            // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
            // or higher before executing any fingerprint-related code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Get an instance of KeyguardManager and FingerprintManager//
                keyguardManager =
                        (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                fingerprintManager =
                        (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                textView = (TextView) findViewById(R.id.textView_home);

                //Check whether the device has a fingerprint sensor//
                if (!fingerprintManager.isHardwareDetected()) {
                    // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                    //textView.setText("Your device doesn't support fingerprint authentication");
                }
                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    // If your app doesn't have this permission, then display the following text//
                    Toast.makeText(MainActivity.this, "Please enable the fingerprint permission to unlock your plans using fingerprints", Toast.LENGTH_LONG).show();

                }
                //Check that the user has registered at least one fingerprint//
                else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // If the user hasn’t configured any fingerprints, then display the following message//
                    Toast.makeText(MainActivity.this, "No fingerprint configured. Please register at least one fingerprint in your device's Settings to unlock your plans using fingerprints", Toast.LENGTH_LONG).show();
                }
                //Check that the lockscreen is secured//
                else if (!keyguardManager.isKeyguardSecure()) {
                    // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                    Toast.makeText(MainActivity.this, "Please enable lockscreen security in your device's Settings to unlock your plans using fingerprints", Toast.LENGTH_LONG).show();
                } else {

                    useFingerprint = true;

                }
            }

            if (useFingerprint)
            {
                //show the fingerprint handler screen
                Intent intent = new Intent(this, FingerprintDialog.class);
                startActivityForResult(intent, AUTH_ACTIVITY_REQUEST_CODE);
            }
            else
            {
                //show the password handler screen
                Intent intent = new Intent(this, PasswordDialog.class);
                startActivityForResult(intent, AUTH_ACTIVITY_REQUEST_CODE);
            }
        }
        else
        {
            initialisePlanView();
            mTextMessage.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            mFprpWebView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            FAB.setVisibility(View.VISIBLE);
            if (!navigation.getMenu().getItem(1).isChecked()){
                navigation.getMenu().getItem(1).setChecked(true);
            }
        }

    }

/*
    private void showFP()
    {
        mTextMessage.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        mFprpWebView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        FAB.setVisibility(View.INVISIBLE);
        fpView.setVisibility(View.VISIBLE);
        try {
            generateKey();
            Log.d("Show FP", "Generated Key");
        } catch (Exception e) {
            Log.e("Show FP", "Failed to generate key");
            e.printStackTrace();
        }

        if (initCipher()) {
            //If the cipher is initialized successfully, then create a CryptoObject instance//
            //cryptoObject = new FingerprintManager.CryptoObject(cipher);
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
            // for starting the authentication process (via the startAuth method) and processing the authentication process events//
            helper = new FingerprintHandler(this, this);
            //helper.startAuth(fingerprintManager, cryptoObject);
            helper.startAuth(fingerprintManager, null);
            Toast.makeText(this, "Started Authentication", Toast.LENGTH_LONG).show();
        }
    }
*/

/*
    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws Exception {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(KEY_NAME)) {
                Log.d("generateKey", "Key not found in keystore so generate new key");
                //Generate the key//
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                //keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

                //Initialize an empty KeyStore//
                keyStore.load(null);

                //Initialize the KeyGenerator//
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        //Set randomized encryption required to false to enable iv to be specified
                        .setRandomizedEncryptionRequired(false)
                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        //.setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                //KeyGenParameterSpec.Builder(KEY_NAME,
                //KeyProperties.PURPOSE_ENCRYPT |
                //        KeyProperties.PURPOSE_DECRYPT)
                //.setDigests(KeyProperties.DIGEST_SHA256,
                //        KeyProperties.DIGEST_SHA512)
                //.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                //.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                //.setUserAuthenticationRequired(true)
                //.build());

                //Generate the key//
                keyGenerator.generateKey();
            }
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new Exception(exc);
        }
    }
    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            String providerName= "AndroidKeyStoreBCWorkaround";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
                providerName = "AndroidOpenSSL";
            }
//            cipher = Cipher.getInstance(
//                    KeyProperties.KEY_ALGORITHM_RSA + "/"
//                            + KeyProperties.BLOCK_MODE_ECB + "/"
//                            + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP,providerName); // or "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
//            Log.d("initCipher", "Initialised cipher");
//            enccipher = Cipher.getInstance(
//                    KeyProperties.KEY_ALGORITHM_RSA + "/"
//                            + KeyProperties.BLOCK_MODE_ECB + "/"
//                            + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP,providerName); // or "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
//            Log.d("initCipher", "Initialised enccipher");
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            enccipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            return true;
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            Log.e("initCipher", "Failed to get Cipher " + e.getMessage());
            return false;
            //throw new RuntimeException("Failed to get Cipher", e);
        }

    }
*/

    @Override
    public void onClick(View view, final int position) {
        //Values are passing to activity & to fragment as well
        Log.d("ClickListener", "Single Click on position        :"+position);
        LinearLayout ll_details = view.findViewById(R.id.plan_details);
        if (ll_details.getVisibility() == View.VISIBLE)
        {
            ll_details.setVisibility(View.INVISIBLE);
            ll_details.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        }
        else
        {
            ll_details.setVisibility(View.VISIBLE);
            ll_details.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onLongClick(View view, int planId) {
        Log.d("ClickListener", "Long click on plan :"+planId);
        //Toast.makeText(MainActivity.this, "Caught event for click on plan :"+planId, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, NewPlanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("planId", planId);
        //use the model to get the plan information and add it all to the bundle
        Plan planToView = mPlanViewModel.getPlanById(planId);
        bundle.putString(NewPlanActivity.EXTRA_REPLY_ID, planToView.getId()+"");
        bundle.putString(NewPlanActivity.EXTRA_REPLY_NAME, planToView.getPlanName());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q1, planToView.getQuestion1());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q2, planToView.getQuestion2());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q3, planToView.getQuestion3());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q4, planToView.getQuestion4());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q5, planToView.getQuestion5());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_Q6, planToView.getQuestion6());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_RP1, planToView.getPoint1());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_RP2, planToView.getPoint2());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_RP3, planToView.getPoint3());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_RP4, planToView.getPoint4());
        bundle.putString(NewPlanActivity.EXTRA_REPLY_RP5, planToView.getPoint5());
        intent.putExtras(bundle);
        startActivityForResult(intent, NEW_PLAN_ACTIVITY_REQUEST_CODE);
    }

    private int mPlanToDelete = -1;
    @Override
    public void onSwipeRight(View view, int planId){
        Log.d("ClickListener", "Swipe Right on plan :"+planId);
        mPlanToDelete = planId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want delete this plan?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DialogListener", "Clicked Yes:" + which);
                if (mPlanToDelete > -1) {
                    Plan planToDelete = new Plan();
                    planToDelete.setId(mPlanToDelete);
                    mPlanViewModel.delete(planToDelete);
                    mPlanToDelete = -1;
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DialogListener", "Clicked No:" + which);
                mPlanToDelete = -1;
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSwipeLeft(View view, int planId){
        Log.d("ClickListener", "Swipe Left on plan :"+planId);
    }

    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }

    @Override
    public void authenticationSucceeded(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(this, "Authentication Successful!!!", Toast.LENGTH_LONG).show();
        authenticated = true;
        //this.cryptoObject = cryptoObject;
        showPlans();
    }
    private void initialisePlanView()
    {
        if (!previouslyauthenticated) {
            final PlanListAdapter adapter = new PlanListAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mPlanViewModel = ViewModelProviders.of(this).get(PlanViewModel.class);
            mPlanViewModel.getAllPlans().observe(this, new Observer<List<Plan>>() {
                @Override
                public void onChanged(@Nullable final List<Plan> plans) {
                    // Update the cached copy of the words in the adapter.
                    // mPlans = plans;
                    adapter.setPlans(plans);
                }
            });

            recyclerView.addOnItemTouchListener(new PlanItemTouchListener(this, recyclerView, mPlanViewModel, this));
            previouslyauthenticated = true;
        }
    }

    @Override
    public void authenticationFailed() {
        authenticated = false;
    }

    @Override
    public void authenticationCancelled() {
        authenticated = false;
    }
}
