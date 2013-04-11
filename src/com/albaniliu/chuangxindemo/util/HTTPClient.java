package com.albaniliu.chuangxindemo.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class HTTPClient {
	public static int CONNECT_TIME_OUT = 5000;
	public static int READ_TIME_OUT = 30000;
	private HttpURLConnection mHttpConnection = null;
	private static String TAG = "HTTPClient";
	
	public static String URL_INDEX;
	public static String COVER_INDEX_PREFIX;
	
	public static String URL_ABOUTPAGE;
	
	static {
		URL_INDEX = "http://184.105.176.95/app/DemoCenter/Api/albumlist.vdi";
		COVER_INDEX_PREFIX = "http://184.105.176.95";
	}
	
	public static URLConnection getJSONHttpConnection(String strURL)
			throws SocketTimeoutException, MalformedURLException, IOException {
		Log.v(TAG, "getHttpConnection");
		URL url = new URL(strURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
		httpURLConnection.setReadTimeout(READ_TIME_OUT);
		httpURLConnection.setAllowUserInteraction(false);
		httpURLConnection.setInstanceFollowRedirects(true);
		httpURLConnection.setRequestMethod("GET");
		Log.v(TAG, "connection OK");
		return httpURLConnection;
	}
	
	public static URLConnection getHttpConnection(String strURL)
			throws SocketTimeoutException, MalformedURLException, IOException {
		Log.v(TAG, "getHttpConnection");
		URL url = new URL(strURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
		httpURLConnection.setReadTimeout(READ_TIME_OUT);
		httpURLConnection.setRequestMethod("GET");
		Log.v(TAG, "connection OK");
		return httpURLConnection;
	}
	
	/**
	 * 根据Url得到InputStream 先查找本地是否存储有从该Url取得的JSON如果存在直接读取，
	 * 如果不存在从Url取得JOSN文件并存储到指定位置
	 * 
	 * @param url
	 * @returnhou
	 * @throws SocketTimeoutException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONArray getJSONArrayFromUrl(String url)
			throws SocketTimeoutException, MalformedURLException, IOException,
			JSONException {
		InputStream inputStream = null;
			URLConnection urlConn_sourceRecommend = getJSONHttpConnection(url);
			urlConn_sourceRecommend.connect();
			inputStream = urlConn_sourceRecommend.getInputStream();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] temp = new byte[1024];
		int count = 0;
		while ((count = inputStream.read(temp)) >= 0) {
			bao.write(temp, 0, count);
		}
		String result = new String(bao.toByteArray());
		return new JSONArray(result);
	}
	
	public static boolean getStreamFromUrl(String url, String path)
            throws SocketTimeoutException, MalformedURLException, IOException,
            JSONException {
        // 注释掉从文件取结果，这个功能没有要求
        InputStream inputStream = null;
            URLConnection urlConn_sourceRecommend = getJSONHttpConnection(url);
            urlConn_sourceRecommend.connect();
            inputStream = urlConn_sourceRecommend.getInputStream();
        BufferedOutputStream bao = new BufferedOutputStream(new FileOutputStream(path));
        byte[] temp = new byte[1024];
        int count = 0;
        while ((count = inputStream.read(temp)) >= 0) {
            bao.write(temp, 0, count);
        }
        bao.close();
        return true;
    }

	protected InputStream getStreamFromNetwork(URI imageUri) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpRequest = new HttpGet(imageUri.toString());
        HttpResponse response = httpClient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        return bufHttpEntity.getContent();
    }
}
