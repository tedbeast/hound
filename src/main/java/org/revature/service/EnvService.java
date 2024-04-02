package org.revature.service;

import org.revature.exception.EnvException;

/**
 * utility class for managing the labs.properties file
 */
public class EnvService {

    public EnvService(){

    }

    public String getLab() throws EnvException {
        String env = System.getenv("lab");
        if(env == null || env.length()<2 ){
            throw new EnvException("lab env variable missing");
        }
        return env;
    }

    public String getProductKey() throws EnvException {
        String env = System.getenv("product_key");
        if(env == null || env.length()<2 ){
            throw new EnvException("product_key env variable missing");
        }
        return env;
    }

    public String getApiUrl() throws EnvException {
        String env = System.getenv("api_url");
        if(env == null || env.length()<2 ){
            throw new EnvException("api_url env variable missing");
        }
        return env;
    }

}
