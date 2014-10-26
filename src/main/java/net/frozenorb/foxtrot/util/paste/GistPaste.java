/***********************************************************************
 * Copyright (c) 2014 chasechocolate (Chase King).
 *
 * All rights reserved.
 *
 * This file belongs to HuddleMC. Files in this project may not be
 * distributed to others outside of the organization without consent
 * of the original author(s) prior (see below). Use of plugins are
 * available to anyone officially within the organization. 
 *
 * Authors/Contributors:
 * - chasechocolate (Chase King)
 ***********************************************************************/

package net.frozenorb.foxtrot.util.paste;

import com.mongodb.BasicDBObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chasechocolate.
 */
public class GistPaste {
    private static final String BASE_URL = "https://api.github.com/gists";

    private String name;
    private String desc;

    private List<GistFile> files;

    public GistPaste(String name, String desc){
        this.name = name;
        this.desc = desc;
        this.files = new ArrayList<GistFile>();
    }

    public GistPaste(String name){
        this(name, "");
    }

    public String getName(){
        return name;
    }

    public String getDesc(){
        return desc;
    }

    public List<GistFile> getFiles(){
        return files;
    }

    public void addFile(GistFile file){
        files.add(file);
    }

    private String serialize(){
        BasicDBObject object = new BasicDBObject();

        object.append("description", desc);
        object.append("public", Boolean.valueOf(false));

        BasicDBObject filesObj = new BasicDBObject();

        for(GistFile file : files){
            filesObj.append(file.getName(), file.serialize());
        }

        object.put("files", filesObj);
        return object.toString();
    }

    public String paste() throws ParseException {
        String response = post(BASE_URL, serialize());
        JSONObject json = (JSONObject) new JSONParser().parse(response);

        return (String) json.get("html_url");
    }

    private String post(String url, String body){
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Retrieve response
            BufferedOutputStream writer = new BufferedOutputStream(connection.getOutputStream());
            byte[] bytes = body.getBytes();

            //prog.setMax(bytes.length);
            //prog.setMessage(UPLOAD_MESSAGE);

            for(int i = 0; i < bytes.length; i++){
                writer.write(bytes[i]);
                //prog.setProgress(i);
            }

            writer.flush();
            //prog.setProgress(0);
            //prog.setMax(connection.getContentLengthLong());
            //prog.setMessage(DOWNLOAD_MESSAGE);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            char[] buff = new char[512];

            while(true){
                int len = br.read(buff, 0, buff.length);

                //prog.incProgress(len);

                if(len == -1){
                    break;
                }

                sb.append(buff, 0, len);
            }

            br.close();

            //prog.finish(sb.toString());
            return sb.toString();
        } catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }
}