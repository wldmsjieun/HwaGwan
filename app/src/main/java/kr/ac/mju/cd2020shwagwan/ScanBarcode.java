package kr.ac.mju.cd2020shwagwan;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanBarcode extends AppCompatActivity {

    private IntentIntegrator scanBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

//        new IntentIntegrator(this).initiateScan();
        scanBarcode = new IntentIntegrator(this);
        scanBarcode.setOrientationLocked(false);
        scanBarcode.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}