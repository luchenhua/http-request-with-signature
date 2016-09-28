import com.google.common.base.Joiner;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * HTTP模拟请求。
 */
public final class HttpUtil {

    /**
     * 处理get请求。（参数在Body中）
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     */
    public String get(String url, Map<String, Object> params) throws IOException {

        // 实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 处理参数
        if (params != null && !params.isEmpty()) {
            String appendParams = Joiner.on("&").withKeyValueSeparator("=").join(params);
            url += "?" + appendParams;
        }
        // 实例化get方法
        HttpGet httpget = new HttpGet(url);
        // 请求结果
        CloseableHttpResponse response;
        String content;
        // 执行get方法
        response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == 200) {
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            content = String.valueOf(response.getStatusLine().getStatusCode());
        }

        return content;
    }

    /**
     * 处理get请求。（参数在Header中）
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     */
    public String getRequestHeader(String url, Map<String, Object> params) throws IOException {

        // 实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化get方法
        HttpGet httpget = new HttpGet(url);
        // 处理参数
        params.forEach((key, value) -> httpget.addHeader(key, value.toString()));
        // 请求结果
        CloseableHttpResponse response;
        String content;

        // 执行get方法
        response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == 200) {
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            content = String.valueOf(response.getStatusLine().getStatusCode());
        }

        return content;
    }

    /**
     * 处理get请求。（取得stream，结果是zip文件）
     *
     * @param url    请求路径
     * @param params 参数
     * @return zip文件解压以后的文件名
     */
    public String getZipStream(String url, Map<String, Object> params) throws IOException {

        // 实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化get方法
        HttpGet httpget = new HttpGet(url);
        // 处理参数
        params.forEach((key, value) -> httpget.addHeader(key, value.toString()));
        // 请求结果
        CloseableHttpResponse response;
        String fileName = "";

        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;

        // 执行get方法
        response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            zipInputStream = new ZipInputStream(entity.getContent());
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                fileName = zipEntry.getName();
                System.out.println("Unzipping " + fileName);
                fileOutputStream = new FileOutputStream(fileName);
                for (int i = zipInputStream.read(); i != -1; i = zipInputStream.read()) {
                    fileOutputStream.write(i);
                }
                zipInputStream.closeEntry();
                fileOutputStream.close();
            }
            zipInputStream.close();
        }

        closeQuietly(fileOutputStream);
        closeQuietly(zipInputStream);

        return fileName;
    }

    /**
     * 处理post请求（JSON）
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     */
    public String postJSON(String url, Map<String, Object> params) throws IOException {

        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);
        // 处理参数
        String entity = new JSONObject(params).toString();

        // 结果
        CloseableHttpResponse response;
        String content;

        // 提交的参数
        StringEntity input = new StringEntity(entity);
        input.setContentType("application/json");

        // 将参数给post方法
        httpPost.setEntity(input);

        // 执行post方法
        response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            content = String.valueOf(response.getStatusLine().getStatusCode());
        }

        return content;
    }

    /**
     * 处理post请求（JSON）
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     */
    public String postSimple(String url, Map<String, Object> params) throws IOException {

        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);
        // 处理参数
        List<NameValuePair> nvps = new ArrayList<>();
        Set<String> keySet = params.keySet();
        nvps.addAll(
                keySet.stream()
                        .map(key -> new BasicNameValuePair(key, params.get(key).toString()))
                        .collect(Collectors.toList()));
        // 结果
        CloseableHttpResponse response;
        String content;

        //提交的参数
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
        //将参数给post方法
        httpPost.setEntity(uefEntity);
        // 执行post方法
        response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            content = String.valueOf(response.getStatusLine().getStatusCode());
        }

        return content;
    }

    private void closeQuietly(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }
}
