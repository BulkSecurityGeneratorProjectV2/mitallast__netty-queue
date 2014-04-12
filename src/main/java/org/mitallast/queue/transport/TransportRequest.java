package org.mitallast.queue.transport;

import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.mitallast.queue.rest.RestRequest;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportRequest implements RestRequest {

    public static final String ENCODING = "UTF-8";
    public static final Charset charset = Charset.forName(ENCODING);

    public static final String METHOD_TUNNEL = "_method";
    private static final String DEFAULT_PROTOCOL = "http";
    private FullHttpRequest httpRequest;
    private HttpMethod httpMethod;
    private HttpHeaders httpHeaders;

    private Map<String, String> paramMap;
    private String queryPath;

    public TransportRequest(FullHttpRequest request) {
        this.httpRequest = request;
        this.httpMethod = request.getMethod();
        this.httpHeaders = request.headers();
        this.parseQueryString();
        determineEffectiveHttpMethod();
    }

    public FullHttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public String param(String param) {
        return paramMap.get(param);
    }

    public boolean hasParam(String param) {
        return paramMap.containsKey(param);
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public boolean isMethodGet() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isMethodDelete() {
        return httpMethod.equals(HttpMethod.DELETE);
    }

    public boolean isMethodPost() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public boolean isMethodPut() {
        return httpMethod.equals(HttpMethod.PUT);
    }

    public boolean isMethodHead() {
        return httpMethod.equals(HttpMethod.HEAD);
    }

    public boolean isMethodOptions() {
        return httpMethod.equals(HttpMethod.OPTIONS);
    }

    public InputStream getInputStream() {
        return new ByteBufInputStream(httpRequest.content());
    }

    public String getBody() {
        return httpRequest.content().toString(charset);
    }

    public String getProtocol() {
        return DEFAULT_PROTOCOL;
    }

    public String getHost() {
        return httpHeaders.get(HttpHeaders.Names.HOST);
    }

    public String getQueryPath() {
        return queryPath;
    }

    public String getBaseUrl() {
        return getProtocol() + "://" + getHost();
    }

    public String getUrl() {
        return getBaseUrl() + getQueryPath();
    }

    private void determineEffectiveHttpMethod() {
        if (!HttpMethod.POST.equals(httpRequest.getMethod())) {
            return;
        }

        String methodString = httpHeaders.get(METHOD_TUNNEL);

        if (HttpMethod.PUT.name().equalsIgnoreCase(methodString) || HttpMethod.DELETE.name().equalsIgnoreCase(methodString)) {
            httpMethod = HttpMethod.valueOf(methodString.toUpperCase());
        }
    }

    private void parseQueryString() {
        String uri = httpRequest.getUri();
        if (!uri.contains("?")) {
            paramMap = new HashMap<>();
            queryPath = uri;
            return;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();
        queryPath = decoder.path();

        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        paramMap = new HashMap<>(parameters.size());

        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue().get(0));

            for (String value : entry.getValue()) {
                try {
                    httpHeaders.add(entry.getKey(), URLDecoder.decode(value, ENCODING));
                } catch (Exception e) {
                    httpHeaders.add(entry.getKey(), value);
                }
            }
        }
    }
}
