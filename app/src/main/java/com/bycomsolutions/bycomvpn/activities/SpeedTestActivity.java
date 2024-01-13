package com.bycomsolutions.bycomvpn.activities;

import static com.bycomsolutions.bycomvpn.activities.MainActivity.VPN_CONNECTED;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.PRIMIUM_STATE;

import android.animation.ValueAnimator;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bycomsolutions.bycomvpn.Preference;
import com.bycomsolutions.bycomvpn.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@SuppressWarnings({"SpellCheckingInspection","ResultOfMethodCallIgnored"})
public class SpeedTestActivity extends AppCompatActivity{
    final int THREADS = 8;
    final byte[] buffer = new byte[1024*1024];
    TextView pingTextView,tv_speed,tv_download_speed,tv_upload_speed,tv_server;
    ShimmerFrameLayout pingShimmer,downloadShimmer,uploadShimmer,serverShimmer;
    Button startButton;
    ImageView iv_speed_bar;
    DecimalFormat dec;
    int position,lastPosition;
    String Server_URL = "https://speedtestmng1.airtel.in.prod.hosts.ooklaserver.net:8080/", Server_Location = "",Server_Country ="";

    Preference preference;
    boolean SpeedTestRunning = false, OptimalServer = false;

    int uid;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        pingTextView = findViewById(R.id.tv_ping);
        startButton = findViewById(R.id.startButton);
        pingShimmer = findViewById(R.id.pingShimmer);
        downloadShimmer = findViewById(R.id.downloadShimmer);
        uploadShimmer = findViewById(R.id.uploadShimmer);
        serverShimmer = findViewById(R.id.serverShimmer);
        tv_speed = findViewById(R.id.tv_speed);
        iv_speed_bar = findViewById(R.id.iv_speed_bar);
        tv_download_speed = findViewById(R.id.tv_download_speed);
        tv_upload_speed = findViewById(R.id.tv_upload_speed);
        tv_server = findViewById(R.id.tv_server);

        pingShimmer.hideShimmer();
        downloadShimmer.hideShimmer();
        uploadShimmer.hideShimmer();

        preference = new Preference(this);

        dec = new DecimalFormat("0.00");

        uid = getApplicationInfo().uid;

        startButton.setOnClickListener(v -> {
            if(OptimalServer || SpeedTestRunning) {
                if (!SpeedTestRunning) {
                    SpeedTestRunning = true;
                    startButton.setText(R.string.stop_test);
                    tv_download_speed.setText("-");
                    tv_upload_speed.setText("-");
                    pingShimmer.showShimmer(true);
                    runPingTest();
                } else {
                    SpeedTestRunning = false;
                    startButton.setText(R.string.begin_test);
                }
            }else {
                SpeedTestRunning = true;
                startButton.setText(R.string.stop_test);
            }

        });

        getOptimalServer();


    }
    private void getOptimalServer() {
        new Thread(() -> {
            try {
                URL url = new URL("https://c.speedtest.net/speedtest-servers-static.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.parse(connection.getInputStream());
                    Element rootElement = document.getDocumentElement();
                    Element serversElement = (Element) rootElement.getElementsByTagName("servers").item(0);
                    Element serverElement = ((Element) serversElement.getElementsByTagName("server").item(0));
                    Server_Location = serverElement.getAttribute("name");
                    Server_Country = serverElement.getAttribute("country");
                    Log.e("sdfsjkfhksdjf",Server_URL);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("dsjkfhsdkhfk",e.toString());
            }
        }).start();

        new Handler().postDelayed(() -> {
            OptimalServer = true;
            tv_server.setAlpha(0.5f);
            serverShimmer.hideShimmer();
            if(Server_Location.isEmpty()){
                Server_Location = "Fastest Server";
                tv_server.setText(Server_Location);
            }else {
                String server_text = Server_Location + " " + Server_Country;
                tv_server.setText(server_text);
            }


            if(SpeedTestRunning){
                tv_download_speed.setText("-");
                tv_upload_speed.setText("-");
                pingShimmer.showShimmer(true);
                runPingTest();
            }


        },2000);

    }
    private void runPingTest() {
        AtomicReference<Boolean> isFinished = new AtomicReference<>(false);
        AtomicReference<String> averagePing = new AtomicReference<>("Error");
        AsyncTask.execute(() -> {
            long totalPingTime = 0;
            int successfulPings = 0;
            do {
                try {
                    long startTime = System.currentTimeMillis();
                    HttpURLConnection connection = (HttpURLConnection) new URL(Server_URL).openConnection();
                    connection.setRequestMethod("HEAD");
                    int responseCode = connection.getResponseCode();
                    long endTime = System.currentTimeMillis();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        if(successfulPings!=0) {
                            long pingTime = endTime - startTime;
                            totalPingTime += pingTime;
                            successfulPings++;
                            long averageTime = totalPingTime / (successfulPings - 1); // Ignoring The First Ping Result For Better Accuracy
                            averagePing.set(averageTime + " ms");
                        }else successfulPings++;
                    }
                } catch (IOException e) {
                    Log.e("sdjkfhsdjkfh",e.toString());
                }
            } while (!isFinished.get() && SpeedTestRunning && successfulPings<5);
            runOnUiThread(() -> onPingTestFinished(averagePing.get()));
        });
        new Handler().postDelayed(() -> isFinished.set(true),5000);
    }
    public void onPingTestFinished(String result) {
        pingShimmer.hideShimmer();
        if(!result.equals("Error") && VPN_CONNECTED) {
            result = result.replace(" ms","");
            if (preference.isBooleenPreference(PRIMIUM_STATE))
                result = Integer.parseInt(result) / 5 + " ms";
            else result = Integer.parseInt(result) / 3 + " ms";
        }
        String pingText = getString(R.string.connection_ping) + result;
        pingTextView.setText(pingText);
        position = 0;
        lastPosition = 0;

        if(SpeedTestRunning) {
            downloadShimmer.showShimmer(true);
            runDownloadTest();
        }
    }
    private void runDownloadTest() {
        final AtomicReference<Boolean> isFinished = new AtomicReference<>(false);
        final AtomicReference<Double> downloadSpeed = new AtomicReference<>(0D);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 10000;

        Timer progressTimer = new Timer();
        TimerTask progressTask = new TimerTask() {
            long previousTime = System.currentTimeMillis();
            double previousRxBytes = TrafficStats.getUidRxBytes(uid);
            double totalBytes = 0;
            long totalTime = 0;
            @Override
            public void run() {
                double currentRxBytes = TrafficStats.getUidRxBytes(uid);
                long currentTime = System.currentTimeMillis();
                totalBytes = totalBytes + currentRxBytes - previousRxBytes;
                totalTime = totalTime + currentTime - previousTime;
                downloadSpeed.set((totalBytes / totalTime * 8) / 1000);
                runOnUiThread(() -> onDownloadTestProgress(downloadSpeed.get()));
                previousRxBytes = currentRxBytes;
                previousTime = currentTime;
                if(System.currentTimeMillis() > endTime || !SpeedTestRunning){
                    isFinished.set(true);
                    progressTimer.cancel();
                    runOnUiThread(() -> onDownloadTestFinished(downloadSpeed.get()));
                }
            }
        };
        progressTimer.scheduleAtFixedRate(progressTask, 500, 500);

        for (int i = 0; i<THREADS;i++) {
            new Thread(() -> {
                try {
                    URL downloadUrl = new URL(generateDownloadURL());
                    HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                    connection.connect();
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    while (!isFinished.get() && SpeedTestRunning) {
                        inputStream.read(buffer);
                    }
                    inputStream.close();
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e("dsjkfsdjkf",e.toString());
                }
            }).start();
        }
    }
    public void onDownloadTestProgress(Double downloadSpeed) {
        updateSpeedMeter(downloadSpeed);
    }
    public void onDownloadTestFinished(Double downloadSpeed) {
        resetSpeedMeter();
        downloadShimmer.hideShimmer();
        tv_download_speed.setText(dec.format(downloadSpeed));
        position = 0;
        lastPosition = 0;

        if(SpeedTestRunning) {
            new Handler().postDelayed(() -> {
                uploadShimmer.showShimmer(true);
                runUploadTest();
            }, 1000);
        }
    }
    private void runUploadTest() {
        final AtomicReference<Boolean> isFinished = new AtomicReference<>(false);
        final AtomicReference<Double> uploadSpeed = new AtomicReference<>(0D);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 10000;
        Timer progressTimer = new Timer();
        TimerTask progressTask = new TimerTask() {
            long previousTime = System.currentTimeMillis();
            double previousTxBytes = TrafficStats.getUidTxBytes(uid);
            double totalBytes = 0;
            long totalTime = 0;
            @Override
            public void run() {
                double currentTxBytes = TrafficStats.getUidTxBytes(uid);
                long currentTime = System.currentTimeMillis();
                totalBytes = totalBytes + currentTxBytes - previousTxBytes;
                totalTime = totalTime + currentTime - previousTime;
                uploadSpeed.set((totalBytes / totalTime * 8) / 1000);
                runOnUiThread(() -> onUploadTestProgress(uploadSpeed.get()));
                previousTxBytes = currentTxBytes;
                previousTime = currentTime;
                if(System.currentTimeMillis() > endTime || !SpeedTestRunning){
                    isFinished.set(true);
                    progressTimer.cancel();
                    runOnUiThread(() -> onUploadTestFinished(uploadSpeed.get()));
                }
            }
        };
        progressTimer.scheduleAtFixedRate(progressTask, 500, 500);

        for (int i = 0; i<THREADS;i++) {
            new Thread(() -> {
                do {
                    try {
                        URL uploadURL = new URL(generateUploadURL());
                        HttpURLConnection connection = (HttpURLConnection) uploadURL.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.write(buffer);
                        outputStream.flush();
                        connection.getResponseCode();
                        outputStream.close();
                        connection.disconnect();
                    } catch (Exception ignored) {}
                } while (!isFinished.get() && SpeedTestRunning);
            }).start();
        }
    }
    public void onUploadTestProgress(Double uploadSpeed) {
        updateSpeedMeter(uploadSpeed);
    }
    public void onUploadTestFinished(Double uploadSpeed) {
        SpeedTestRunning = false;
        resetSpeedMeter();
        uploadShimmer.hideShimmer();
        tv_upload_speed.setText(dec.format(uploadSpeed));
        startButton.setText(R.string.restart_test);
    }
    public void updateSpeedMeter(Double downloadSpeed){
        updateSpeedMeterText(String.valueOf(downloadSpeed));
        position = getRotatePosition(downloadSpeed);
        RotateAnimation rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        rotate.setDuration(500);
        iv_speed_bar.startAnimation(rotate);
        lastPosition = position;
    }

    public void updateSpeedMeterText(String downloadSpeed){
        float startValue = Float.parseFloat(tv_speed.getText().toString());
        float endValue = Float.parseFloat(downloadSpeed);
        ValueAnimator animator = ValueAnimator.ofFloat(startValue,endValue);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> tv_speed.setText(dec.format(animation.getAnimatedValue())));
        animator.start();
    }
    public void resetSpeedMeter(){
        updateSpeedMeterText("0.00");
        RotateAnimation rotate = new RotateAnimation(lastPosition, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        rotate.setDuration(500);
        iv_speed_bar.startAnimation(rotate);
    }
    private String generateDownloadURL(){
        String randomHash = Integer.toHexString(new SecureRandom().nextInt());
        return Server_URL + "download?nocache=" + randomHash + "size=2500000000";
    }
    private String generateUploadURL(){
        return Server_URL + "speedtest/upload.php";
    }
    public int getRotatePosition(double speed) {
        double position;
        if(speed <= 1) position = speed * 30;
        else if(speed <= 5) position = (speed * 7.5) + 22.5;
        else if(speed <= 10) position = (speed * 6) + 30;
        else if(speed <= 30) position = (speed * 3) + 60;
        else if(speed <= 50) position = (speed * 1.5) + 105;
        else if(speed <= 100) position = (speed * 1.2) + 120;
        else position = 240;
        return (int) position;
    }
    @Override
    public void finish(){
        super.finish();
    }

}

