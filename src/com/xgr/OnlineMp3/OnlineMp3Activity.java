package com.xgr.OnlineMp3;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class OnlineMp3Activity extends Activity {
    private TextView textView;
    private ImageButton startButton, pauseButton, resetButton, stopButton;
    private MediaPlayer mediaPlayer;
    private boolean isReleased = false;
    private boolean isPaused = false;
    private static final String TAG = "onlinemp3";

    private String currentFilePath = "";
    private String currentTempFilePath = "";
    private String strVideoURL = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mediaPlayer = new MediaPlayer();

        strVideoURL = "http://10.0.2.2:8080/tomcatpro/a1.mp3";
        strVideoURL = "http://quku.cn010w.com/qkca1116sp/upload_quku3/20071019162018303.mp3";
        strVideoURL = "http://quku.cn010w.com/qkca1116sp/upload_quku/2007810143148496.mp3";
        strVideoURL = "http://quku.cn010w.com/qkca1116sp/upload_quku/200781013143398.mp3";
        textView = (TextView) findViewById(R.id.textView);
        //设置透明 度
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        startButton = (ImageButton) findViewById(R.id.startButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        resetButton = (ImageButton) findViewById(R.id.resetButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);

        startButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                playVideo(strVideoURL);
                textView.setText("正在播放" + "\n" + strVideoURL);
            }

        });

        resetButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isReleased == false) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(0);
                        textView.setText("reset");
                    }
                }
            }

        });
        pauseButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mediaPlayer != null) {
                    if (isReleased == false) {
                        if (isPaused == false) {
                            mediaPlayer.pause();
                            isPaused = true;
                            textView.setText("pause");
                        } else if (isPaused == true) {
                            mediaPlayer.start();
                            isPaused = false;
                            textView.setText("start");
                        }
                    }
                }
            }

        });

        stopButton.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    if (mediaPlayer != null) {
                        if (isReleased == false) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            isReleased = true;
                            delFile(currentFilePath);
                            textView.setText("stop");
                        }
                    }
                } catch (Exception e) {
                    textView.setText(e.toString());
                    e.printStackTrace();
                }
            }

        });
    }

    private void playVideo(final String strPath) {
        // TODO Auto-generated method stub
        try {
            //if(currentFilePath!=null&&mediaPlayer!=null){
            mediaPlayer.setDataSource(strPath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //return;
            //}else{
            //	Toast.makeText(getApplicationContext(), "fileNotFound", Toast.LENGTH_LONG).show();
            //	}
            //	currentFilePath=strPath;
            //   mediaPlayer=new MediaPlayer();
            //mediaPlayer.setAudioStreamType(2);

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "Error on Listener,what:" + what + "extra:" + extra);
                    return false;
                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                @Override
                public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
                    // TODO Auto-generated method stub
                    //Log.i(TAG,"Update:"+Integer.toString(percent)+"%");
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "mediaPlayer Listener completed");
                    mediaPlayer.seekTo(0);
                }

            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer arg0) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "prepared Listener");
                }
            });

            Runnable r = new Runnable() {
                public void run() {
                    try {
						setDateSource(strPath);
                        mediaPlayer.prepare();
                        Log.i(TAG, "Duration:" + mediaPlayer.getDuration());
                        mediaPlayer.start();
                        isReleased = false;
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            };
//            new Thread(r).start();


        } catch (Exception e) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();

            }
            e.printStackTrace();
        }
    }

    private void setDateSource(String strPath) throws Exception {
        if (!URLUtil.isNetworkUrl(strPath)) {
            mediaPlayer.setDataSource(strPath);
        } else {
            if (isReleased == false) {
                URL myUrl = new URL(strPath);
                URLConnection conn = myUrl.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                if (is == null) {
                    throw new RuntimeException("stream is null");
                }
                File myTempFile = File.createTempFile("yinyue", "." + getFileExtendsion(strPath));
                currentFilePath = myTempFile.getAbsolutePath();
                if (currentTempFilePath != null) {
                    Log.i(TAG, "currentTempFilepath");
                    System.out.println(currentFilePath);
                }
                FileOutputStream fos = new FileOutputStream(myTempFile);
                byte[] buf = new byte[128];
                do {
                    int numread = is.read(buf);
                    if (numread <= 0) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (true);
                mediaPlayer.setDataSource(currentFilePath);
                try {
                    is.close();
                } catch (Exception e) {
                    Log.e(TAG, "error:" + e.getMessage(), e);
                }
            }
        }
    }

    private String getFileExtendsion(String strFileName) {
        File myFile = new File(strFileName);
        String strFileExtendsion = myFile.getName();
        strFileExtendsion = (strFileExtendsion.substring(strFileExtendsion.lastIndexOf(".") + 1)).toLowerCase();
        if (strFileExtendsion == "") {
            strFileExtendsion = "dat";
        }
        return strFileExtendsion;
    }

    private void delFile(String strFileName) {
        File myFile = new File(strFileName);
        if (myFile.exists()) {
            myFile.delete();
        }
    }


}