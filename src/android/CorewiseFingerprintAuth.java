package android_serialport_api;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;




public class CorewiseFingerprintAuth extends CordovaPlugin{


    //protected MyApplication application;
    private HandlerThread handlerThread;
    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;
    private CallbackContext callbackContext = null;



    @Override
    public boolean execute(String action,  JSONArray args, CallbackContext cbCtx) throws JSONException {

        this.callbackContext = cbCtx;
         if (action.equals("register")) {
            cordova.getThreadPool().execute(new Runnable()  {
                public void run() {
                    asyncFingerprint.register2();
                }
            });
        } else if (action.equals("validate")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    asyncFingerprint.validate2();
                }
            });
        }else {
            return false;
        }
      


        return true;

    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        handlerThread = new HandlerThread("handlerThread",android.os.Process.THREAD_PRIORITY_DEFAULT);
//      handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        initData();
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AsyncFingerprint.SHOW_PROGRESSDIALOG:
                    cancelProgressDialog();
                    showProgressDialog((String) msg.obj);
                    break;

                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancelProgressDialog();
                    if(msg.obj != null){
                        Integer id = (Integer) msg.obj;
                        callbackContext.success(id);
                    }else{
                        callbackContext.error("Successfully registered your fingerprint, but failed to assign it an internal id please try to register again");
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancelProgressDialog();
                    callbackContext.error("Registering your fingerprint failed, please try again");
                    break;

                case AsyncFingerprint.VALIDATE_RESULT2:
                    cancelProgressDialog();
                    Integer r = (Integer) msg.obj;
                    if(r != -1) {
                        callbackContext.success(r);
                    }
                    else{
                        callbackContext.error("Successfully verified your fingerprint, but failed to get its internal id please try to validate again");
                    }
                    break;

                default:
                    break;
            }
        }

    };

    private void initData() {

        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);


        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
    }




    private void showProgressDialog(String msg) {
        progressDialog = new ProgressDialog(webView.getContext());
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    asyncFingerprint.setStop(true);
                }
                return false;
            }
        });
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }


    @Override
    public void onDestroy() {
        cancelProgressDialog();
        super.onDestroy();
    }



    @Override
    public void onStop() {
        super.onStop();
        Log.i("whw", "onStop");
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if(!SerialPortManager.getInstance().isOpen()){
            SerialPortManager.getInstance().openSerialPort();
        }
        Log.i("whw", "onResume="+SerialPortManager.getInstance().isOpen());

        Log.i("whw", "onResume");
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if(SerialPortManager.getInstance().isOpen()){
            SerialPortManager.getInstance().closeSerialPort();
        }
        handlerThread = null;
        cancelProgressDialog();
        asyncFingerprint.setStop(true);
        Log.i("whw", "onPause");
    }



}
