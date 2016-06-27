package com.maikeapp.maikewatch.util;

import android.util.Log;

import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by JLJ on 2016/5/10.
 */
public class NetWorkUtil {
    /**
     * 上传照片
     * @return
     * @throws Exception
     */
    public static String getResultFromUrlConnectionWithPhoto(
            String urlconn, String jsonargs,
            String fileName, String LoginName, File mFile) throws Exception {
        String result = null;
        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        String BOUNDARY = "|"; // request头和上传文件内容分隔符
        DataInputStream in1 =null;
        try {
            url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            // 设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            urlConnection.setRequestProperty("fileName", fileName);
            urlConnection.setRequestProperty("vertifyCode", LoginName);
            urlConnection.connect();
            out = new DataOutputStream(urlConnection.getOutputStream());
            in1 = new DataInputStream(new FileInputStream(mFile));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in1.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in1.close();
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", urlconn + ",status = "+statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);
            Log.e("上传图片的返回数据", "yzp_" + result);
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("数据错误", "yzp_" + result);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in1 != null) {
                in1.close();
            }
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }


    /**
     * 以post方式发送url请求
     */
    public static String getResultFromUrlConnection(String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        byte[] data = null;


        try {
            data = jsonargs.getBytes();
            url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("sVerifyCode", sVerifyCode);
            urlConnection.connect();
            out = urlConnection.getOutputStream();
            out.write(data);
            out.flush();

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new ServiceException("网络不给力，请稍后重试");
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 以get方式发送url请求
     */
    public static String getResultFromUrlConnectionWithGet(String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        String _urlconn = urlconn;
        if (jsonargs != null && !jsonargs.equals("")) {
            _urlconn = urlconn + "?jsonString=" + jsonargs;
        }

        try {
            url = new URL(_urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("sVerifyCode", sVerifyCode);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", _urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new ServiceException("网络不给力，请稍后重试");
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("查询出错");
            }

        } finally {

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public static String getStrFromInputSteam(InputStream in) throws Exception {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        //最好在将字节流转换为字符流的时候 进行转码
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = bf.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    private static Map<String, String> getResultFromUrlConnectionWithGetReturnSessionId(
            String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        Map<String, String> resultMap = new HashMap<String, String>();
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        String _urlconn = urlconn;
        if (jsonargs != null && !jsonargs.equals("")) {
            _urlconn = urlconn + "?jsonString=" + jsonargs;
        }

        try {
            url = new URL(_urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("sVerifyCode", sVerifyCode);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", _urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }

            String sessionId = getCookieValue(urlConnection);
            resultMap.put("sessionId", sessionId);

            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);
            resultMap.put("result", result);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new ServiceException("网络不给力，请稍后重试");
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("查询出错");
            }

        } finally {

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return resultMap;
    }

    public static String getCookieValue(HttpURLConnection urlConnection) {
        String cookieskey = "Set-Cookie";
        Map<String, List<String>> maps = urlConnection.getHeaderFields();
        List<String> coolist = maps.get(cookieskey);
        Iterator<String> it = coolist.iterator();
        StringBuffer sbu = new StringBuffer();
        //	    sbu.append("eos_style_cookie=default; ");
        while (it.hasNext()) {
            String _cookie = it.next();
            if (_cookie.contains("JSESSIONID")) {
                sbu.append(_cookie.subSequence(0, _cookie.indexOf(";") + 1));
                break;
            }
            //	    	sbu.append(_cookie);
        }
        //	    sbu.append("appWapLogin=true;");
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "sessionid=", "is " + sbu.toString());
        return sbu.toString();
    }
}
