package com.shuangpin.rich.zbor;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.ui.activity.mine.PaymentActivity;
import com.shuangpin.rich.util.DeviceUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.zbor.camber.CameraManager;
import com.shuangpin.rich.zbor.decoding.CaptureActivityHandler;
import com.shuangpin.rich.zbor.decoding.InactivityTimer;
import com.shuangpin.rich.zbor.util.DecoderLocalFile;
import com.shuangpin.rich.zbor.view.ViewfinderView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class MipcaActivityCapture extends BaseActivity implements SurfaceHolder.Callback {

    public static final int CHOOSE_PICTURE = 1003;
    private static final String TAG = "MipcaActivityCapture";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private boolean isOpen = false;
    private String storeUrl;
    private Context mContext = MipcaActivityCapture.this;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipca_capture);
        StatusBarUtil.setStatusBar(this, R.color.theme_color);
        setTitleBar(SHOW_NOTHING);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
//        storeUrl = getIntent().getStringExtra("Shop_url");
    }


    @Override
    protected void onResume() {
        super.onResume();
        continuePreview();
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    /*
     * 有时候直接调用此方法会出现bug：多次扫描过后返回到上一个界面再次进入的时候会出现黑屏
     * 解决方案：判断handler是否为空，不为空再调用CaptureActivityHandler的restartPreviewAndDecode()方法！
     */
    private void continuePreview() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface && handler != null) {
            initCamera(surfaceHolder);
            handler.restartPreviewAndDecode();
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

    }

    public void btn(View view) {
        switch (view.getId()) {
            case R.id.top_mask:
                if (!isOpen) {
                    CameraManager.get().enableFlash();
                    isOpen = true;
                } else {  // 关灯
                    CameraManager.get().disableFlash();
                    isOpen = false;
                }
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.top_openpicture:
                getPicture();

                break;
            default:
                break;
        }
    }

    /**
     * 调用系统相册
     */
    private void getPicture() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(final Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //https://pick.shuangpinkeji.com/shop/qrcode-money?shopId=1
        LogUtilsxp.e2(TAG, "resultString" + resultString);//
        if (resultString.equals("")) {
            Toast.makeText(MipcaActivityCapture.this, "扫描失败", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            String returnResult = "abundant.xjkrfx.net/shop/qrcode-money";
            if (resultString.contains(returnResult)) {
                resultString = resultString.substring(resultString.lastIndexOf("=") + 1);
                LogUtilsxp.e2(TAG, "resultStringid" + resultString);//
                final String id = resultString;
                Intent intent = new Intent();
                intent.setClass(mContext, PaymentActivity.class);
                intent.putExtra("title", "支付");
                intent.putExtra("id", id);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(MipcaActivityCapture.this, " 该二维码非本公司出品  ", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException | RuntimeException ioe) {
            //SPUtil.writeCameraState("close", mContext);
            if (DeviceUtils.checkCameraDevice(mContext)) {
                ToastUtils.showToast(mContext, "无拍照授权!请在手机管家打开摄像头权限");

            } else {
                ToastUtils.showToast(mContext, "此设备无摄像头！");
            }
            finish();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
                        originalUri);
                if (photo != null) {
                    // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                    Bitmap smallBitmap = zoomBitmap(photo,
                            photo.getWidth() / 2, photo.getHeight() / 2);
                    // 释放原始图片占用的内存，防止out of memory异常发生
                    photo.recycle();
                    String bitmappath = saveFile(smallBitmap, setImageName());
                    DecoderLocalFile decoder = new DecoderLocalFile(bitmappath);
                    String phone = decoder.handleQRCodeFormPhoto(
                            MipcaActivityCapture.this, DecoderLocalFile.loadBitmap(bitmappath));
                    if ("-1".equals(phone)) {
                        Toast.makeText(MipcaActivityCapture.this, "图片中无二维码信息", Toast.LENGTH_LONG).show();
                    } else {
                        String returnResult = "shop/qrcode/shopid";
                        if (phone.contains(returnResult)) {
                            phone = phone.substring(phone.lastIndexOf(":") + 1);
                            final String id = phone;
                            Intent intent = new Intent();
                            intent.setClass(mContext, PaymentActivity.class);
                            intent.putExtra("title", "付款码");
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MipcaActivityCapture.this, " 该二维码非本公司出品  ", Toast.LENGTH_LONG).show();
                            this.finish();
                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final static String ALBUM_PATH = Environment
            .getExternalStorageDirectory() + File.separator + "fengci/";

    /*
     * 保存文件
     */
    public static String saveFile(Bitmap bm, String fileName)
            throws IOException {
        String path;
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        path = myCaptureFile.getAbsolutePath();
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return path;
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 设置文件名称
     *
     * @return
     */
    public static String setImageName() {
        String str;
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);// 获取当前时间，进一步转化为字符串
        date = new Date();
        str = format.format(date);
        return str + ".jpg";
    }
}
