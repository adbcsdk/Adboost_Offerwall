package com.adbc.sdk.offerwall.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adbc.sdk.adboost.v2.AdbcReward;
import com.adbc.sdk.adboost.v2.OfferwallBuilder;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class MainActivity extends AppCompatActivity {

    private String appUserId = "someUser13";
    private String[] permissions;

    private OfferwallBuilder builder;

    private EditText titleText;
    private EditText editText;
    private TextView initText;
    private Button initBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.title);
        editText = findViewById(R.id.user_id);
        initText = findViewById(R.id.text_init);
        initBtn = findViewById(R.id.btn_init);

        permissions = new String[] { Manifest.permission.GET_ACCOUNTS };

        if(!hasPermission(permissions[0])) {
            getPermission();
        } else {
            initOfferwall();
        }

        initBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = editText.getText().toString();

                if(TextUtils.isEmpty(userId)) {
                    Toast.makeText(MainActivity.this, "app_uid 값을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    appUserId = userId;
                    initOfferwall();
                }
            }
        });

        (findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(builder != null) {

                    String title = titleText.getText().toString();
                    if(TextUtils.isEmpty(title))
                        title = "Adboost Offerwall";

                    builder.setTitle(title); // 오퍼월 화면 타이틀영역 제목 ( 미 입력 시 '무료 충전소' )
                    builder.setReferrer(appUserId); // 광고 참여 시 포스트백으로 전달 될 리퍼러 ( 매체 개별 활용값 )
                    builder.showOfferwall(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initOfferwall();
        }
    }

    private void initOfferwall() {

        /**
         * @params
         *   - Context context
         *   - String appCode ( 발급받은 매체 코드 )
         *   - String userId ( 유저 구분을 위한 ID 값 - 매체 임의 생성 )
         *   - OnAdbcRewardListener initListener
         * */
        AdbcReward.init(getApplicationContext(), "Lms9yVfJWn", appUserId, new AdbcReward.OnAdbcRewardListener() {
            @Override
            public void onResult(boolean result, String msg, OfferwallBuilder offerwallBuilder) {

                if(result) {
                    initText.setText("SDK가 초기화 되었습니다.");
                    builder = offerwallBuilder;
                } else {
                    Log.e("tag", msg);
                    initText.setText("SDK가 초기화 되지 않았습니다.");
                }
            }
        });
    }

    /** 권한 확인 */
    private boolean hasPermission(String permission) {
        return (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /** 권한 요청 */
    private void getPermission() {
        ActivityCompat.requestPermissions(this, permissions, 10);
    }

    /** 암호화 된 유저 ID 생성 예제 */
    private String encId() {

        //byte[] bytes = (appUserId + ("adid")).getBytes();
        byte[] bytes = appUserId.getBytes();

        Checksum crc = new CRC32();
        crc.update(bytes, 0, bytes.length);
        return String.format("%08x", crc.getValue());
    }
}