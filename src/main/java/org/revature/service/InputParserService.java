package org.revature.service;

import org.revature.exception.ArgumentException;
import org.revature.exception.EnvException;
import org.revature.exception.LabWebException;
import org.revature.exception.LabZipException;

/**
 * class manages cli input to send appropriate calls of service class methods
 */
public class InputParserService {

    LabService labService;

    public InputParserService(LabService labService){
        this.labService = labService;
    }

    /**
     * main switch for taking in cli command
     */
    public void parseCommand(String[] command) {
        try{
            if(command[0].equals("open")){
                labService.loadLab();
            }else if(command[0].equals("save")){
                labService.saveLab();
            }else if(command[0].equals("reset")){
                labService.resetLab();
            }else{
                throw new ArgumentException("no such command found");
            }
        }catch (LabZipException e){
            e.printStackTrace();
        }catch (LabWebException e){
            e.printStackTrace();
        }catch(EnvException e){
            e.printStackTrace();
        }catch (ArgumentException e){
            e.printStackTrace();
        }
    }
}