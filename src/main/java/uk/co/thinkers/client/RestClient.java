package uk.co.thinkers.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;
import java.util.*;

/**
 * Client used to consume remote rest service.
 *
 * @param <T> requestObject
 * @param <U> responseObject
 */
@Singleton
public class RestClient<T, U> {
    private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);
    private static final String APPLICATION = "application";
    private static final String JSON = "json";
    private RestTemplate template;

    public RestClient() {
        template = new RestTemplate();

        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

        List<MediaType> mediaTypes = new LinkedList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypes);

        List<HttpMessageConverter<?>> converters = template.getMessageConverters();
        converters.add(converter);
        template.setMessageConverters(converters);
    }

    /**
     * Retrieve JSON object from an endpoint.
     *
     * @param url               endpoint URL
     * @param request           populated request to be sent to rest service
     * @param responseType      type of response returned from rest service
     * @param additionalHeaders additional headers to pass with the request
     * @return response returned from rest service
     */
    public U postForObject(String url, T request, Class<U> responseType, Map<String, List<String>> additionalHeaders) {
        return postForObject(url, request, responseType, null, null, additionalHeaders);
    }

    /**
     * Retrieve JSON object from an endpoint.
     *
     * @param url          endpoint URL
     * @param request      populated request to be sent to rest service
     * @param responseType type of response returned from rest service
     * @return response returned from rest service
     */
    public U postForObject(String url, T request, Class<U> responseType) {
        return postForObject(url, request, responseType, null, null);
    }

    /**
     * Retrieve JSON object from an endpoint, passing in a session ID as cookie.
     *
     * @param url           endpoint URL
     * @param request       populated request to be sent to rest service
     * @param responseType  type of response returned from rest service
     * @param sessionIDName name of the Hybris session ID
     * @param sessionID     the session ID to send to Hybris
     * @return response returned from rest service
     */
    public U postForObject(String url, T request, Class<U> responseType, String sessionIDName, String sessionID) {
        return postForObject(url, request, responseType, sessionIDName, sessionID, null);
    }

    /**
     * Retrieve JSON object from an endpoint, passing in a session ID as cookie.
     *
     * @param url               endpoint URL
     * @param request           populated request to be sent to rest service
     * @param responseType      type of response returned from rest service
     * @param sessionIDName     name of the Hybris session ID
     * @param sessionID         the session ID to send to Hybris
     * @param additionalHeaders additional headers to pass with the request
     * @return response returned from rest service
     */
    public U postForObject(String url, T request, Class<U> responseType, String sessionIDName, String sessionID, Map<String, List<String>> additionalHeaders) {
        if (url == null) {
            throw new IllegalArgumentException("end point url map is null, please set");
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        if ((additionalHeaders == null || (additionalHeaders != null && !additionalHeaders.containsKey("Cookie")))
                && sessionIDName != null && sessionID != null) {
            httpHeaders.add("Cookie", sessionIDName + "=" + sessionID);
        }
        if (additionalHeaders != null) {
            for (Map.Entry<String, List<String>> entry : additionalHeaders.entrySet()) {
                httpHeaders.putAll(Collections.singletonMap(entry.getKey(), entry.getValue()));
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<String>(requestBody, httpHeaders);
            ResponseEntity<U> response = template.exchange(url, HttpMethod.POST, entity, responseType);

            return response.getBody();
        } catch (Exception e) {
            LOG.error("Error while posting for object at url {}: {}", url, e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve JSON object from an endpoint.
     *
     * @param url               endpoint URL
     * @param responseType      type of response returned from rest service
     * @param additionalHeaders additional headers to pass with the request
     * @return response returned from rest service
     */
    public U getForObject(String url, Class<U> responseType, Map<String, List<String>> additionalHeaders) {
        return getForObject(url, responseType, null, null, null, additionalHeaders);
    }

    /**
     * Retrieve JSON object from an endpoint.
     *
     * @param url          endpoint URL
     * @param responseType type of response returned from rest service
     * @return response returned from rest service
     */
    public U getForObject(String url, Class<U> responseType) {
        return getForObject(url, responseType, null, null);
    }

    /**
     * Retrieve JSON object from an endpoint, passing in a session ID as cookie.
     *
     * @param url           endpoint URL
     * @param responseType  type of response returned from rest service
     * @param sessionIDName name of the Hybris session ID
     * @param sessionID     the session ID to send to Hybris
     * @return response returned from rest service
     */
    public U getForObject(String url, Class<U> responseType, String sessionIDName, String sessionID) {
        return getForObject(url, responseType, sessionIDName, sessionID, Collections.<String, Object>emptyMap());
    }

    /**
     * Retrieve JSON object from an endpoint, passing in a session ID as cookie.
     *
     * @param url           endpoint URL
     * @param responseType  type of response returned from rest service
     * @param sessionIDName name of the Hybris session ID
     * @param sessionID     the session ID to send to Hybris
     * @param urlParameters map of parameters to send in url
     * @return response returned from rest service
     */
    public U getForObject(String url, Class<U> responseType, String sessionIDName, String sessionID, Map<String, ?> urlParameters) {
        return getForObject(url, responseType, sessionIDName, sessionID, urlParameters, null);
    }

    /**
     * Retrieve JSON object from an endpoint, passing in a session ID as cookie.
     *
     * @param url           endpoint URL
     * @param responseType  type of response returned from rest service
     * @param sessionIDName name of the Hybris session ID
     * @param sessionID     the session ID to send to Hybris
     * @param urlParameters map of parameters to send in url
     * @param extraHeaders  additional headers to pass with the request
     * @return response returned from rest service
     */
    public U getForObject(String url, Class<U> responseType, String sessionIDName, String sessionID,
                          Map<String, ?> urlParameters, Map<String, List<String>> extraHeaders) {
        if (url == null) {
            throw new IllegalArgumentException("end point url map is null, please set");
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        if ((extraHeaders == null || (extraHeaders != null && !extraHeaders.containsKey("Cookie")))
                && sessionIDName != null && sessionID != null) {
            httpHeaders.add("Cookie", sessionIDName + "=" + sessionID);
        }
        if (extraHeaders != null) {
            for (Map.Entry<String, List<String>> entry : extraHeaders.entrySet()) {
                httpHeaders.putAll(Collections.singletonMap(entry.getKey(), entry.getValue()));
            }
        }

        if (urlParameters == null) {
            urlParameters = new HashMap<String, String>(0);
        }

        try {
            HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
            ResponseEntity<U> response = template.exchange(url, HttpMethod.GET, entity, responseType, urlParameters);

            return response.getBody();
        } catch (Exception e) {
            LOG.error("Error while getting object at url {}: {}", url, e.getMessage());
            return null;
        }
    }

}

