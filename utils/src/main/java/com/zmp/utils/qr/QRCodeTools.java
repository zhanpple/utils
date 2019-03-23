package com.zmp.utils.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by aaron on 16/7/27.
 * 二维码扫描工具类
 */
public class QRCodeTools {

        public static final String RESULT_TYPE = "result_type";

        public static final String RESULT_STRING = "result_string";

        public static final int RESULT_SUCCESS = 1;

        public static final int RESULT_FAILED = 2;

        public static final String LAYOUT_ID = "layout_id";


        /**
         * 解析二维码图片工具类.
         *
         * @param analyzeCallback analyzeCallback
         */
        public static void analyzeBitmap(String path, AnalyzeCallback analyzeCallback) {

                //首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 先获取原大小
                BitmapFactory.decodeFile(path, options);
                options.inJustDecodeBounds = false; // 获取新的大小

                int sampleSize = (int) (options.outHeight / (float) 400);

                if (sampleSize <= 0) {
                        sampleSize = 1;
                }
                options.inSampleSize = sampleSize;
                Bitmap mBitmap = BitmapFactory.decodeFile(path, options);

                MultiFormatReader multiFormatReader = new MultiFormatReader();

                // 解码的参数
                Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
                // 可以解析的编码类型
                Vector<BarcodeFormat> decodeFormats = new Vector<>();
                if (decodeFormats.isEmpty()) {
                        decodeFormats = new Vector<>();
                        // 这里设置可扫描的类型，我这里选择了都支持
                        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
                }
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                // 设置继续的字符编码格式为UTF8
                hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                // 设置解析配置参数
                multiFormatReader.setHints(hints);

                // 开始对图像资源解码
                Result rawResult = null;
                try {
                        rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(mBitmap))));
                }
                catch (Exception e) {
                        e.printStackTrace();
                }

                if (rawResult != null) {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult, sampleSize);
                        }
                }
                else {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeFailed();
                        }
                }
        }


        /**
         * 解析二维码图片工具类.
         *
         * @param analyzeCallback analyzeCallback
         */
        public static void analyzeYuvData(byte[] data,int dataWidth,int dataHeight, AnalyzeCallback analyzeCallback) {

                MultiFormatReader multiFormatReader = new MultiFormatReader();

                // 解码的参数
                Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
                // 可以解析的编码类型
                Vector<BarcodeFormat> decodeFormats = new Vector<>();
                if (decodeFormats.isEmpty()) {
                        decodeFormats = new Vector<>();
                        // 这里设置可扫描的类型，我这里选择了都支持
                        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
                }
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                // 设置继续的字符编码格式为UTF8
                hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                // 设置解析配置参数
                multiFormatReader.setHints(hints);

                // 开始对图像资源解码
                Result rawResult = null;
                try {
                        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, dataWidth, dataHeight, 0, 0, dataWidth, dataHeight, true);
                        rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                }
                catch (Exception e) {
                        e.printStackTrace();
                }

                if (rawResult != null) {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeSuccess(null, rawResult, 0);
                        }
                }
                else {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeFailed();
                        }
                }
        }

        /**
         * 解析二维码图片工具类.
         *
         * @param analyzeCallback analyzeCallback
         */
        public static void analyzeBitmap(Bitmap mBitmap, AnalyzeCallback analyzeCallback) {

                MultiFormatReader multiFormatReader = new MultiFormatReader();

                // 解码的参数
                Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
                // 可以解析的编码类型
                Vector<BarcodeFormat> decodeFormats = new Vector<>();
                if (decodeFormats.isEmpty()) {
                        decodeFormats = new Vector<>();
                        // 这里设置可扫描的类型，我这里选择了都支持
                        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
                }
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                // 设置继续的字符编码格式为UTF8
                hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                // 设置解析配置参数
                multiFormatReader.setHints(hints);

                // 开始对图像资源解码
                Result rawResult = null;
                try {
                        rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(mBitmap))));
                }
                catch (Exception e) {
                        e.printStackTrace();
                }

                if (rawResult != null) {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult, 1);
                        }
                }
                else {
                        if (analyzeCallback != null) {
                                analyzeCallback.onAnalyzeFailed();
                        }
                }
        }

        /**
         * 生成二维码图片
         *
         * @param text text
         * @param w    w
         * @param h    h
         * @param logo logo
         * @return return
         */
        public static Bitmap createImage(String text, int w, int h, Bitmap logo) {
                if (TextUtils.isEmpty(text)) {
                        return null;
                }
                try {
                        Bitmap scaleLogo = getScaleLogo(logo, w, h);

                        int offsetX = w / 2;
                        int offsetY = h / 2;

                        int scaleWidth = 0;
                        int scaleHeight = 0;
                        if (scaleLogo != null) {
                                scaleWidth = scaleLogo.getWidth();
                                scaleHeight = scaleLogo.getHeight();
                                offsetX = (w - scaleWidth) / 2;
                                offsetY = (h - scaleHeight) / 2;
                        }
                        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
                        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                        //容错级别
                        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                        //设置空白边距的宽度
                        hints.put(EncodeHintType.MARGIN, 0);
                        BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
                        int[] pixels = new int[w * h];
                        for (int y = 0; y < h; y++) {
                                for (int x = 0; x < w; x++) {
                                        if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                                                int pixel;
                                                if (scaleLogo == null) {
                                                        pixel = 0;
                                                }
                                                else {
                                                        pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                                                }
                                                if (pixel == 0) {
                                                        if (bitMatrix.get(x, y)) {
                                                                pixel = 0xff000000;
                                                        }
                                                        else {
                                                                pixel = 0xffffffff;
                                                        }
                                                }
                                                pixels[y * w + x] = pixel;
                                        }
                                        else {
                                                if (bitMatrix.get(x, y)) {
                                                        pixels[y * w + x] = 0xff000000;
                                                }
                                                else {
                                                        pixels[y * w + x] = 0xffffffff;
                                                }
                                        }
                                }
                        }
                        Bitmap bitmap = Bitmap.createBitmap(w, h,
                                                            Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
                        return bitmap;
                }
                catch (WriterException e) {
                        e.printStackTrace();
                }
                return null;
        }

        private static final int[] colors = {Color.RED, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.GREEN, Color.BLUE};

        private static int getRandomColor() {
                return colors[(int) (Math.random() * colors.length)];
        }

        private static int getColorByXX(int x, int y, int w, int h) {
                int i = x - w / 2;
                int i2 = y - h / 2;
                int i1 = ((i * i + i2 * i2) * (colors.length) - 1) / (w * w / 4 + h * h / 4);
                return colors[i1];
        }

        private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
                if (logo == null) {
                        return null;
                }
                Matrix matrix = new Matrix();
                float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
                matrix.postScale(scaleFactor, scaleFactor);
                return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        }


        /**
         * 生成二维码 要转换的地址或字符串,可以是中文.
         *
         * @param url    url
         * @param width  width
         * @param height height
         * @return return
         */
        public static Bitmap createQRImage(String url, final int width, final int height) {
                try {
                        // 判断URL合法性
                        if (TextUtils.isEmpty(url)) {
                                return null;
                        }
                        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
                        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                        // 图像数据转换，使用了矩阵转换
                        BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                                                                        BarcodeFormat.QR_CODE, width, height, hints);
                        int[] pixels = new int[width * height];
                        // 下面这里按照二维码的算法，逐个生成二维码的图片，
                        // 两个for循环是图片横列扫描的结果
                        for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                        if (bitMatrix.get(x, y)) {
                                                pixels[y * width + x] = 0xff000000;
                                        }
                                        else {
                                                pixels[y * width + x] = 0xffffffff;
                                        }
                                }
                        }
                        // 生成二维码图片的格式，使用ARGB_8888
                        Bitmap bitmap = Bitmap.createBitmap(width, height,
                                                            Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                        return bitmap;
                }
                catch (WriterException e) {
                        e.printStackTrace();
                }
                return null;
        }

        /**
         * 生成条形码
         *
         * @param context       context
         * @param contents      需要生成的内容
         * @param desiredWidth  生成条形码的宽带
         * @param desiredHeight 生成条形码的高度
         * @param displayCode   是否在条形码下方显示内容
         * @return return
         */
        public static Bitmap createBarcode(Context context, String contents,
                                           int desiredWidth, int desiredHeight, boolean displayCode) {
                Bitmap resultBitmap;

                // 图片两端所保留的空白的宽度

                int marginW = 20;
                // 条形码的编码类型

                BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

                if (displayCode) {
                        Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                                                              desiredWidth, desiredHeight);
                        Bitmap codeBitmap = createCodeBitmap(contents, desiredWidth + 2
                                                                                      * marginW, desiredHeight, context);
                        resultBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                                0, desiredHeight));
                }
                else {
                        resultBitmap = encodeAsBitmap(contents, barcodeFormat,
                                                      desiredWidth, desiredHeight);
                }

                return resultBitmap;
        }

        /**
         * 生成条形码的Bitmap
         *
         * @param contents      需要生成的内容
         * @param format        编码格式
         * @param desiredWidth  desiredWidth
         * @param desiredHeight desiredHeight
         * @return return
         */
        private static Bitmap encodeAsBitmap(String contents,
                                             BarcodeFormat format, int desiredWidth, int desiredHeight) {
                final int WHITE = 0xFFFFFFFF;
                final int BLACK = 0xFF000000;

                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix result;
                try {
                        result = writer.encode(contents, format, desiredWidth,
                                               desiredHeight, null);
                }
                catch (WriterException e) {
                        e.printStackTrace();
                        return null;
                }

                int width = result.getWidth();
                int height = result.getHeight();
                int[] pixels = new int[width * height];
                // All are 0, or black, by default
                for (int y = 0; y < height; y++) {
                        int offset = y * width;
                        for (int x = 0; x < width; x++) {
                                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                        }
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height,
                                                    Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                return bitmap;
        }

        /**
         * 生成显示编码的Bitmap
         *
         * @param contents contents
         * @param width    width
         * @param height   height
         * @param context  context
         * @return return return
         */
        private static Bitmap createCodeBitmap(String contents, int width,
                                               int height, Context context) {
                TextView tv = new TextView(context);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(layoutParams);
                tv.setText(contents);
                tv.setHeight(height);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setWidth(width);
                tv.setDrawingCacheEnabled(true);
                tv.setTextColor(Color.BLACK);
                tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                           View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

                return tv.getDrawingCache();
        }

        /**
         * 将两个Bitmap合并成一个
         *
         * @param first     first
         * @param second    second
         * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
         * @return return
         */
        private static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                            PointF fromPoint) {
                if (first == null || second == null || fromPoint == null) {
                        return null;
                }
                int marginW = 20;
                Bitmap newBitmap = Bitmap.createBitmap(
                        first.getWidth() + second.getWidth() + marginW,
                        first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(newBitmap);
                cv.drawBitmap(first, marginW, 0, null);
                cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
                cv.save();
                cv.restore();

                return newBitmap;
        }


        /**
         * 解析二维码结果
         */
        public interface AnalyzeCallback {

                void onAnalyzeSuccess(Bitmap mBitmap, Result result, int sampleSize);

                void onAnalyzeFailed();
        }


}
