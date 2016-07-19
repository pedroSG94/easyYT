#easyYT

Library for streaming youtube with ffmpeg

easYT is a wrapper of YouTube Data API v3 for stream to YouTube with ffmpeg (3.0.1)

#Installation

- Download the project and put it on your root project

- In your app build.gradle:

```gradle
compile project(':easyYT')

```
  - In your settings.gradle

```gradle
include ':easyYT'
```
- Get YouTube Data API OAuth 2.0 key for your project here:
  https://console.developers.google.com.
  You don't need use your key anywhere. You may need to wait some minutes for let Google process the key)

#Code example

```java
public class MainActivity extends EasyYTActivity implements EasyYTCallback, Button.OnClickListener {

    private EasyYTView easyYTView;
    private Button button;
    private EasyStream easyStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        chooseAccount();

        easyYTView = (EasyYTView) findViewById(R.id.recordvideo);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        easyStream = StreamBuilder.getInstance()
                .setCredential(getCredential())
                .setEastYTCallback(this)
                .setResolution(Resolution.R_480P)
                .setSurfaceView(easyYTView)
                .setState(StreamState.PRIVATE)
                .build();
    }

    @Override
    public void onClick(View view) {
        if(!easyStream.isStreaming()) easyStream.startStream();
        else easyStream.stopStream();
    }

    @Override
    public void streamingStarted() {
        button.setText("Stop");
    }

    @Override
    public void streamingStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText("Start");
            }
        });
    }

    @Override
    public void createEventSuccess(StreamDataInfo streamDataInfo, String endPoint) {

    }

    @Override
    public void startEventSuccess() {

    }

    @Override
    public void endEventSuccess() {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onErrorStartActivityForResult(UserRecoverableAuthIOException e) {
        startActivityForResult(e);
    }

    @Override
    public void onErrorStartActivityForResult2(IllegalArgumentException e) {
        startActivityForResult2(e);
    }
}
```
