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

/**
 * Created by chasechocolate.
 */
public class GistFile {
    private String name;
    private String content;

    public GistFile(String name, String content){
        this.name = name;
        this.content = content;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return content;
    }

    public BasicDBObject serialize(){
        BasicDBObject object = new BasicDBObject();

        object.append("content", content);

        return object;
    }
}