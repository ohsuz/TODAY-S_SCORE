package com.example.ohjeom.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.AccountSummary;
import com.example.ohjeom.models.Payment;

import java.util.ArrayList;


public class PaymentService extends Service {
    public static String TAG = "PaymentService";
    public static int total;
    public static ArrayList<Payment> payments;
    private int amount = 0;
    private int money;
    private String usage;
    private boolean next = false;
    private Payment payment;
    private AccountSummary accountSummary;

    public PaymentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent clsIntent = new Intent(PaymentService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(PaymentService.this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);
            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            clsBuilder = new NotificationCompat.Builder(this);
        }

        // QQQ: notification 에 보여줄 타이틀, 내용을 수정한다.
        clsBuilder.setSmallIcon(R.drawable.icon_school)
                .setContentTitle("오늘의 점수").setContentText("Service is running...")
                .setContentIntent(pendingIntent);

        // foreground 서비스로 실행한다.
        startForeground(1, clsBuilder.build());

        total = 0;
        payments = new ArrayList<Payment>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //처음 실행 시
        if(startId==1) {
            Log.d(TAG,"시작");
            money = intent.getIntExtra("money",0);
            return super.onStartCommand(intent, flags, startId);
        }
        else {
            String message = intent.getStringExtra("contents");
            String[] array = message.split(" ");

            for (String s : array) {
                Log.d(TAG, s);
                if (s.contains("원") && amount == 0) {
                    amount = Integer.parseInt(s.replace(",", "").replace("원", ""));
                    total += amount;
                    Log.d(TAG + " 현재까지 총합:", String.valueOf(total));
                }
                if (next) {
                    usage = s;
                    next = false;
                    break;
                }
                if (s.contains(":")) {
                    next = true;
                }
            }

            payment = new Payment(usage, amount);
            payments.add(payment);
            amount = 0;

            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"종료");

        /*
        서버로 점수 보내기 및 총 사용량 & 사용내역 보내기 : score,accountSummary 보내면 됨
         */
        int score = 0;
        accountSummary = new AccountSummary(total, payments);

        Log.d("합계:", String.valueOf(total));
        for (Payment p : accountSummary.getDetails()) {
            Log.d(TAG + "사용내역 : ", p.getUsage() + p.getAmount() + "원");
        }

        if (total < money)
            score = 100;
        else
            score = 0;
        Log.d(TAG, String.valueOf(score));

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}