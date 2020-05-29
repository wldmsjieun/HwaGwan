package kr.ac.mju.cd2020shwagwan;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanBarcode extends AppCompatActivity {

    private IntentIntegrator sbIntentIntegrator;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        //바코드 초기 설정 및 가로 세로 화면 설정
        sbIntentIntegrator = new IntentIntegrator(this);
        sbIntentIntegrator.setOrientationLocked(false);
        sbIntentIntegrator.initiateScan();

    }

    //바코드 인식이 잘 되었는 지 확인하는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult sbInetentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Intent sbIntent = getIntent();
        if(sbInetentResult != null) {
            if(sbInetentResult.getContents() != null) {//성공시
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //바코드 정보를 HomeFragment에 넘겨주는 부분
                sbIntent.putExtra("barcode",sbInetentResult.getContents());
                setResult(0,sbIntent);
                finish();
            } else {//실패시
 //                Toast.makeText(this, "ScanBar.java Cancelled", Toast.LENGTH_SHORT).show();
                //바코드 인식 실패 정보를 HomeFragment에 넘겨주는 부분
                sbIntent.putExtra("barcode",1);
                setResult(1,sbIntent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}