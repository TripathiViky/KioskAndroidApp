package com.mgc.kioskandroidapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private WebView webView = null;
    public static final int REQUEST_PERMISSION = 123;

    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.v("App", "Build Version Greater than or equal to M: " + Build.VERSION_CODES.M);
//            checkDrawOverlayPermission();
//        } else {
//            Log.v("App", "OS Version Less than M");
//            //No need for Permission as less then M OS.
//        }
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

//        preventStatusBarExpansion(this);

//        Space space = (Space) findViewById(R.id.top_corner_space);
//        space.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("Space clicked");
//            }
//        });

        if (!DetectConnection.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
        } else {

            webView = (WebView) findViewById(R.id.webview);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            WebViewController webViewController = new WebViewController(this);
            webView.setWebViewClient(webViewController);

            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl("https://vkd-stage.kloudspot.com/0a0a0a000132/index.html?fs9htdbq=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwYTowYTowYTowMDowMTozMiIsImF1dGgiOiJST0xFX0FDQ09VTlRfVVNFUiIsImV4cCI6MTY2ODE1MTM4Mn0.AAn471DneTfqLBkUq8D3W_-eW_4CXjOKfC_sHho0lH5vjnnTaQmnQyyf4nYXbW_XQpQzz2gTn7aKzIrys-9gkA");
            //webView.loadUrl("https://vkd-stage.kloudspot.com/0a0a0a0000a5/index.html?fs9htdbq=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwYTowYTowYTowMDowMDphNSIsImF1dGgiOiJST0xFX0FDQ09VTlRfVVNFUiIsImV4cCI6MTY1NTMwMzM0OX0.sv09AfxN1Q18oedFo8asHynJHzQmJihGwYPfhBNcEpoZN1i8dK-Wbu1e1vLUacVLDbcl-5l0aYyoD6Cmp9FqTg");
            webView.setOnTouchListener(null);
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request.grant(request.getResources());
                    }
                }

            });

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(context);

        manager.addView(view, localLayoutParams);
    }

    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    public final static int REQUEST_CODE = 15;
    /////////////NEW
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        Log.v("App", "Package Name: " + getApplicationContext().getPackageName());

        // Check if we already  have permission to draw over other apps
        if (!Settings.canDrawOverlays(this)) {
            Log.v("App", "Requesting Permission" + Settings.canDrawOverlays(this));
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getApplicationContext().getPackageName()));
            // request permission via start activity for result
            startActivityForResult(intent, REQUEST_CODE); //It will call onActivityResult Function After you press Yes/No and go Back after giving permission
        } else {
            Log.v("App", "We already have permission for it.");
            // disablePullNotificationTouch();
            // Do your stuff, we got permission captain
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("App", "OnActivity Result.");
        //check if received result code
        //  is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // Permission Granted by Overlay
                    // Do your Stuff
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permissionRequestCode, String[] permissions, int[] grantResults) {
        if (permissionRequestCode != REQUEST_PERMISSION) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //PermHandling();
        } else {
            // Ask the user to grant the permission
        }
    }

    public void RequestPermission(Activity thisActivity, String Permission, int Code) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Permission) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Permission)) {} else {
                ActivityCompat.requestPermissions(thisActivity,
                        new String[] {
                                Permission
                        },
                        Code);
            }
        }
    }

}
