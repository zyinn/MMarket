package com.sumscope.optimus.moneymarket.gatewayinvoke;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by qikai.yu on 2016/4/28.
 */
@Component
public class GatewayHTTPService {

	@Value("${external.httpReadTimeout}")
	private int readTimeOut;

    public String sendHttpRequest(String url, String param){
        return post(url,param, null);
    }

	public String sendHttpRequest(String url, String param , Map<String, String> headers){
		return post(url,param,headers);
	}

	private String post(String uri, String request, Map<String, String> headers) {
		try {
			HttpURLConnection connection = getConnection(uri);
			if (headers != null) {
				headers.entrySet().forEach(it->
					connection.setRequestProperty(it.getKey(),it.getValue())
				);
			}
			connection.setReadTimeout(readTimeOut);
			connection.connect();
			BufferedOutputStream outputStream = new BufferedOutputStream(new DataOutputStream(connection.getOutputStream()));
			outputStream.write(request.getBytes());
			outputStream.flush();
			outputStream.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			connection.disconnect();
			return sb.toString();
		} catch (IOException e) {
			LogManager.error(new BusinessRuntimeException(BusinessRuntimeExceptionType.OTHER,e));
		}
		return null;
	}

	private HttpURLConnection getConnection(String uri) throws IOException {
        HttpURLConnection connection;
        URL url = new URL(uri);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

}
