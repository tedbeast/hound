package org.revature.service;

import org.revature.exception.ArgumentException;
import org.revature.exception.LabWebException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * utility class containing static methods for sending http requests to api
 */
public class LabWebService {

    HttpClient httpClient;
    public LabWebService(HttpClient httpClient){
        this.httpClient = httpClient;
    }
    /**
     * retrieve the current users zip file for a specific lab
     */
    public InputStream getSavedZip(String apiURL, String labName, String productKey) throws LabWebException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/saved/"+labName))
                    .GET()
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {

            throw new LabWebException("There was some error retrieving your lab from the cloud.");
        }
//        going to try to make this an httpresponse later so that we can do better error handling
        InputStream is = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body).join();

        return is;
    }
    /**
     * TODO send the saved lab zip to the api
     */
    public void sendSavedZip(String apiURL, String labName, String productKey, byte[] zip) throws LabWebException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/lab-save-request/"+labName))
                    .POST(BodyPublishers.ofByteArray(zip))
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {
            throw new LabWebException("There was some error saving your lab to the cloud.");
        }
    }
    /**
     * TODO send a request to reset the users lab to the api
     * @throws ArgumentException
     */
    public void sendResetRequest(String apiURL, String labName, String productKey) throws LabWebException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/lab-reset-request/"+labName))
                    .POST(null)
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {
            throw new LabWebException("There was some error resetting your lab on the cloud.");
        }
    }
}