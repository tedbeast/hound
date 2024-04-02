package org.revature.service;

import org.revature.exception.EnvException;
import org.revature.exception.LabWebException;
import org.revature.exception.LabZipException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * the general service class for managing all lab related file manipulation
 */
public class LabService {

    EnvService envService;
    LabWebService labWebService;
    String incomingZipFileName;
    String outgoingZipFileName;
    String workspacePath;
    String apiUrl;
    String labName;
    String productKey;

    /**
     * constructor for creating a lab processor specific to a certain lab name
     */
    public LabService(EnvService envService, LabWebService labWebService) throws EnvException {
        this.envService = envService;
        this.labWebService = labWebService;
        this.incomingZipFileName = "in.zip";
        this.outgoingZipFileName = "out.zip";
        this.workspacePath = "./config/workspace/";
        this.apiUrl = envService.getApiUrl();
        this.labName = envService.getLab();
        this.productKey = envService.getProductKey();
    }

    /**
     * the main flow for pulling a lab from the api
     */
    public void loadLab() throws LabZipException, LabWebException, EnvException {

        getZip(apiUrl, labName, productKey);
        unzipFile();
        clearZipFile(incomingZipFileName);
    }

    public void clearZipFile(String zip){
        File zipFile = new File(workspacePath+zip);
        if(zipFile != null && zipFile.exists()) {
            zipFile.delete();
        }
    }

    /**
     * convert input stream from web into a zip file
     * @throws URISyntaxException
     * @throws IOException
     */
    public void getZip(String apiUrl, String labName, String productKey) throws LabWebException, LabZipException {
        try (FileOutputStream out = new FileOutputStream(workspacePath+incomingZipFileName)) {
            labWebService.getSavedZip(apiUrl, labName, productKey).transferTo(out);
        }catch(IOException e){
            throw new LabZipException("There was some issue loading the lab contents into a zipfile. Caused by: \n"+e);
        }
    }
    /**
     * the zip file already exists, now unzip all contents
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void unzipFile() throws LabZipException {
        try {
            String fileZip = workspacePath + incomingZipFileName;
            File destDir = new File(workspacePath);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new LabZipException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new LabZipException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }catch (IOException e){
            throw new LabZipException("Unspecified error while unzipping lab file.");
        }
    }
    /**
     * unpacks subdirectories of the zip file
     */
    public File newFile(File destinationDir, ZipEntry zipEntry) throws LabZipException {
        try {
            File destFile = new File(destinationDir, zipEntry.getName());

            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = destFile.getCanonicalPath();

            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new LabZipException("Entry is outside of the target dir: " + zipEntry.getName());
            }
            return destFile;
        }catch (IOException e){
            throw new LabZipException("Unspecified issue with creating files in unzipping process.");
        }
    }
    public void saveLab(){

    }
    public void resetLab(){

    }
    /**
     * TODO: pack all of the current lab contents, excluding whitelisted files, into a zip and send the zip
     * over to API
     */
    public void sendSaved(String apiURL, String labName, String productKey)throws LabZipException, LabWebException {
        File zip = new File(workspacePath+outgoingZipFileName);
        try{
            zip = pack();
            byte[] zipBytes = Files.readAllBytes(Paths.get(workspacePath+outgoingZipFileName));
            labWebService.sendSavedZip(apiURL, labName, productKey, zipBytes);
        }catch(IOException e){
            throw new LabZipException("There was some issue reading bytes of packed zipfile.");
        }finally{
            clearZipFile(outgoingZipFileName);
        }
    }
    /**
     * package the current workspace to zip, excluding whitelist files
     * @throws IOException
     */
    public File pack() throws LabZipException {
        try {
            Path p = Files.createFile(Paths.get(workspacePath+outgoingZipFileName));
            ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
                Path pp = Paths.get(workspacePath);
                Files.walk(pp)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        });
                File zip = new File(workspacePath+outgoingZipFileName);
                return zip;
        }catch (IOException e){
            throw new LabZipException("Issue packing directory contents");
        }
    }
}