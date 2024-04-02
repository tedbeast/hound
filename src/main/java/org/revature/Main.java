package org.revature;

import org.revature.exception.ArgumentException;
import org.revature.exception.EnvException;
import org.revature.service.EnvService;
import org.revature.service.InputParserService;
import org.revature.service.LabService;
import org.revature.service.LabWebService;

import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) throws EnvException {
        HttpClient httpClient = HttpClient.newHttpClient();
        EnvService envService = new EnvService();
        LabWebService labWebService = new LabWebService(httpClient);
        LabService labService = new LabService(envService, labWebService);
        InputParserService inputParserService = new InputParserService(labService);

        inputParserService.parseCommand(args);
    }
}