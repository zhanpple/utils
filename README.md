ANDROID_UTILS
======

# 使用
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


    dependencies {
            implementation 'com.github.zhanpple:utils:-SNAPSHOT'
    }

```

* <H3>implementation 'com.github.zhanpple:utils:-SNAPSHOT'</H3>

## 打印类名 方法 行号的[logger源码](https://github.com/zhanpple/utils/tree/master/utils/src/main/java/com/zmp/utils/Logger.java)
### 用法
```java

	Logger.i("onCreate");
	// 控制台- >  I/Logger: com.zmp.androidutils.MainActivity->>onCreate->>14:onCreate
	Logger.v("onCreate");
	// 控制台- >  V/Logger: com.zmp.androidutils.MainActivity->>onCreate->>15:onCreate
	Logger.d("onCreate");
	// 控制台- >  D/Logger: com.zmp.androidutils.MainActivity->>onCreate->>16:onCreate
	Logger.e("onCreate");
	// 控制台- >  E/Logger: com.zmp.androidutils.MainActivity->>onCreate->>17:onCreate
	Logger.w("onCreate");
	// 控制台- >  W/Logger: com.zmp.androidutils.MainActivity->>onCreate->>18:onCreate
```

## 可在子线程Show单例[ToastTools源码](https://github.com/zhanpple/utils/tree/master/utils/src/main/java/com/zmp/utils/ToastTools.java)
### 用法
```java
	 ToastTools.getDefault().init(getApplicationContext());

	 ToastTools.getDefault().show("I am King of you!");
```

## 发送有邮件工具类 [SendEmailUtils源码](https://github.com/zhanpple/utils/tree/master/utils/src/main/java/com/zmp/utils/SendEmailUtils.java)
### 用法
```java

	String username = "xxx.xxx@xx.xx";
	String password = "xxxxxxxxxxx";
	//初始化用户名密码(使用账户需开通SMTP服务)
	SendEmailUtils.getInstance().init(username, password, new SendEmailUtils.ISendResult() {
			@Override
			public void onSuccess() {
					Logger.d("ISendResult:onSuccess");
			}

			@Override
			public void onFail(String ex) {
					Logger.d("ISendResult:onFail:" + ex);
			}
	});
	//添加收件人
	SendEmailUtils.getInstance().addToUser(toUsername1);
	SendEmailUtils.getInstance().addToUser(toUsername2);
	......
	......
	SendEmailUtils.getInstance().addToUser(toUsername);

	//发送邮件
	SendEmailUtils.getInstance().sendEmail(title,content);
	//发送带附件的邮件
	SendEmailUtils.getInstance().sendEmail(title,content,filePath,emailFileName);

```

## OkHttp工具类 [OkHttpUtils源码](https://github.com/zhanpple/utils/tree/master/utils/src/main/java/com/zmp/utils/okhttp/OkHttpUtils.java)
### 用法
```java
	//字符串
	String url0 = "https://raw.githubusercontent.com/zhanpple/androidUtils/master/testFile/color.json";
	String tag0 = url0;
	OkHttpUtils.getInstance().get(url0, tag0, new OkHttpUtils.MyCallBack<String>() {
			@Override
			public void onResponse(String string) {
					ToastTools.getDefault().show("colorBean:" +string);
			}

			@Override
			public Class<String> getClazz() {
					return String.class;
			}

			@Override
			public void onFailure(String s) {
					Logger.e(s);
					ToastTools.getDefault().show("onFailure:" + s);
			}
	});

	//jsonBean
	String url = "https://raw.githubusercontent.com/zhanpple/androidUtils/master/testFile/color.json";
	String tag = url;
	OkHttpUtils.getInstance().get(url, tag, new OkHttpUtils.MyCallBack<ColorBean>() {
			@Override
			public void onResponse(ColorBean colorBean) {
					ToastTools.getDefault().show("colorBean:" + colorBean.getColor());
			}

			@Override
			public Class<ColorBean> getClazz() {
					return ColorBean.class;
			}

			@Override
			public void onFailure(String s) {
					Logger.e(s);
					ToastTools.getDefault().show("onFailure:" + s);
			}
	});


	//BitMap
	String url2 = " https://raw.githubusercontent.com/zhanpple/androidUtils/master/testFile/ic_launcher.png";
	String tag2 = url;
	OkHttpUtils.getInstance().get(url2, tag2, new OkHttpUtils.MyCallBack<Bitmap>() {
			@Override
			public void onResponse(final Bitmap bitmap) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
									imageView.setImageBitmap(bitmap);
							}
					});
			}

			@Override
			public Class<Bitmap> getClazz() {
					return Bitmap.class;
			}

			@Override
			public void onFailure(String s) {
					Logger.e(s);
					ToastTools.getDefault().show("onFailure:" + s);
			}
	});

	//扩展
	 ProgressDownloader progressDownloader = new ProgressDownloader(url3, file, new IProgressCallback() {
                        @Override
                        public void onLoading(float current, float total) {
                                float percent;
                                if (total == 0) {
                                        percent = 100;
                                }
                                else {
                                        percent = current * 100.0F / total;
                                }
                                Log.e("TAG", "current:" + current);
                                Log.e("TAG", "total:" + total);
                                String format = String.format("下载进度%1$.2f%2$s".toLowerCase(), percent, "%");
                        }

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailed(String message) {

                        }

                        @Override
                        public void onSave(long startsPoint, int len) {
                                Log.e("MapTaskActivity", "onSave" + (startsPoint + len));
                        }
                });
				//下载
				long startsPoint = 0;
                progressDownloader.download(startsPoint);
				//暂停
                progressDownloader.pause();

```

## 二维码生成,扫描,识别工具类[QRCodeTools](https://github.com/zhanpple/utils/tree/master/utils/src/main/java/com/zmp/utils/qr/QRCodeTools.java)
### 用法
```java
        //识别
        QRCodeTools.analyzeBitmap(Bitmap mBitmap, AnalyzeCallback analyzeCallback);
        //生成
        QRCodeTools.createImage(String text, int w, int h, Bitmap logo);
        //扫描
        startActivityForResult(new Intent(MainActivity.this, QRScanActivity.class), 101);

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             super.onActivityResult(requestCode, resultCode, data);
             if (resultCode == RESULT_OK && requestCode == 101) {
                 //识别结果
                 String codeResult = data.getStringExtra(QRScanActivity.CODE_RESULT);
             }
        }
```

### 使用库
```java

    compileOnly 'com.squareup.okhttp3:okhttp:3.13.1'
    implementation 'com.sun.mail:android-mail:1.6.0'
    implementation 'com.alibaba:fastjson:1.2.56'
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
    // https://mvnrepository.com/artifact/com.google.zxing/core
    api 'com.google.zxing:core:3.3.3'

```
## 有任何疑问或建议可随时联系邮箱: zhanpples@qq.com
