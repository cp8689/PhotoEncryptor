package com.example.helloworld;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

public class Fileparser {

    String pathname;
    //this hash table is used to organize the data in sections: name|hash|Salt
    Hashtable<String, EncryptedFileInfo> fileInfos;
    //BufferedReader br = new BufferedReader(new FileReader(file));

    public Fileparser(String pathname) throws FileNotFoundException {
        this.pathname = pathname;
        fileInfos = new Hashtable<>();
    }

    public void parse() throws IOException {
        File file = new File(this.pathname);
        handleLineByLine(file, Charset.forName("UTF8"));
    }

    //This parses an entire line in password.txt in the format: name|hash|salt
    private void handleLineByLine(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             BufferedReader buffer = new BufferedReader(reader)) {
             String line;
             String[] infos;
             //
             while ((line=buffer.readLine()) != null) {
                 infos = line.split(" ");
                 if (infos.length != 3) continue;
                 EncryptedFileInfo info = new EncryptedFileInfo(infos[0], infos[1], infos[2]);
                 fileInfos.put(info.fileName, info);
             }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////


    public boolean checkforfile(String name) throws FileNotFoundException { //returns true if the name of the file is found - meaning there is a hashed password for that image already
        return fileInfos.contains(name);
    }


    public boolean hasHash(String name) throws FileNotFoundException { //returns true if the name of the file is found - meaning there is a hashed password for that image already
        return fileInfos.contains(name);
    }


    public void openFile()throws FileNotFoundException{
        File file = new File(pathname);
        Scanner x = new Scanner(file);
        x = new Scanner(new File(pathname));

    }

//reads the
    public String readName(Scanner x)throws FileNotFoundException{
        String a = x.next();
        return a;
    }

// old way - didn't work
//    public String readHash(String name)throws FileNotFoundException{
//        File file = new File(pathname);
//        Scanner x = new Scanner(file);
//        x.findInLine(name);
//
//        return x.next();
//    }
//
//
//    public String readSalt(String name)throws FileNotFoundException{
//        File file = new File(pathname);
//        Scanner x = new Scanner(file);
//        x.findInLine(readHash(name));
//        return x.next();
//    }

    // this initiates a class for the fileparser class to use
    private static class EncryptedFileInfo {
        String fileName;
        String hash;
        String salt;

        public EncryptedFileInfo(String fileName, String hash, String salt) {
            this.fileName = fileName;
            this.hash = hash;
            this.salt = salt;
        }
    }
    //this parses text so that I can grab the hash/salt from the name of the file, which acts as an ID.
    public String getHash(String fileName) {
        EncryptedFileInfo info = fileInfos.get(fileName);

        if (info != null) {
            return info.hash;
        } else {
            return null;
        }
    }

    public String getSalt(String fileName) {
        EncryptedFileInfo info = fileInfos.get(fileName);

        if (info != null) {
            return info.salt;
        } else {
            return null;
        }
    }
}
