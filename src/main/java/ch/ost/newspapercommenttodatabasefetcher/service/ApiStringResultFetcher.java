package ch.ost.newspapercommenttodatabasefetcher.service;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotFetchFromApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.Charset;

@Service
/**
 * Creates HTTPS Requests
 */
public class ApiStringResultFetcher {

    @Value("${settings.20min.api.charset}")
    String charset = "UTF-8";

    /**
     * Sends a HTTP-Get Requests to a given endpoint and converts the result into a string
     *
     * @param endpoint the api-endpoint for the get request
     * @return the api-response as a string
     * @throws CannotFetchFromApiException if the api cannot be accessed
     */
    public String queryApiForJsonResponse(URI endpoint) throws CannotFetchFromApiException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(new StringHttpMessageConverter(Charset.forName(charset)));
        try {
            return restTemplate.getForObject(endpoint, String.class);
        } catch (Exception e) {
            throw new CannotFetchFromApiException(e);
        }
    }

}
