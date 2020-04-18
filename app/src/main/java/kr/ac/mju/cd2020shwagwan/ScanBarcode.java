package kr.ac.mju.cd2020shwagwan;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

public class ScanBarcode extends AppCompatActivity {

    private IntentIntegrator scanBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        //바코드 초기 설정 및 가로 세로 화면 설정
        scanBarcode = new IntentIntegrator(this);
        scanBarcode.setOrientationLocked(false);
        scanBarcode.initiateScan();

    }

    //바코드 인식이 잘 되었는 지 확인하는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "ScanBar.java Cancelled", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                //바코드 정보를 HomeFragment에 넘겨주는 부분
                Intent returnIntent = getIntent();
                returnIntent.putExtra("barcode",result.getContents());
                setResult(0,returnIntent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}