package com.example.ohjeom.etc;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.ohjeom.models.Template;
import com.example.ohjeom.services.PaymentService;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;
import static java.util.Arrays.asList;

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG ="SmsReceiver";
    private int count = 1;
    private String[] phoneNumber = {"01029698752"}; // 결제문자 전화번호으로 목록 채워줘야함.

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"문자 수신 받기 시작");

        //SMS_RECEIVED에 대한 액션일때 실행
        if(intent.getAction().equals(SMS_RECEIVED)&&isServiceRunning("com.example.ohjeom.services.PaymentService",context)) {
            Log.d(TAG, "onReceiver() 호출");
            Log.d(TAG, String.valueOf(count));

            //Bundle을 이용해서 메세지 내용을 가져옴
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);

            //메시지가 있을 경우 내용을 로그로 출력해 봄
            if (messages.length > 0 && asList(phoneNumber).contains(messages[0].getOriginatingAddress()) && messages[0].getMessageBody().toString().contains("원")) {
                //메시지의 내용을 가져오기
                String sender = messages[0].getOriginatingAddress();
                String contents = messages[0].getMessageBody().toString();
                Date receivedDate = new Date(messages[0].getTimestampMillis());

                //로그 출력
                Log.d(TAG, "Sender:" + sender);
                Log.d(TAG, "contents:" + contents);
                Log.d(TAG, "receivedDate:" + receivedDate);

                //액티비티로 메시지의 내용을 전달해줌
                sendToService(context, sender, contents, receivedDate);
            }
        }
        else
            Log.d("소비 측정","X");
    }

    //액티비티로 메시지의 내용 전달
    private void sendToService(Context context,String sender,String contents, Date receivedDate) {
        Intent intent = new Intent(context, PaymentService.class);

        //Flag 설정
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //메시지의 내용을 Extra에 넣어줌
        intent.putExtra("count",count);
        intent.putExtra("sender",sender);
        intent.putExtra("contents",contents);
        intent.putExtra("receivedDate",format.format(receivedDate));

        context.startService(intent);
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }

    //서비스 작동 여부 체크
    public boolean isServiceRunning(String serviceName,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}