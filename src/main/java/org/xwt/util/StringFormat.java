/*
 * Copyright 2008 Saad Khawaja 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License. 
 */

package org.xwt.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringFormat {

    private static final Log logger = LogFactory.getLog(StringFormat.class);
    
    private String schema;
    private StringReader schemaReader;
    private List replacers;
    
    public StringFormat(String schema) {
        this.schema = schema;
        this.schemaReader = new StringReader(this.schema);
        replacers = new ArrayList();
        initReplacers();
    }
    
    private void initReplacers(){
        
        try {
            int read=-1;
            while((read = this.schemaReader.read()) != -1){
                String str = Character.toString((char)read);
                if(!"#".equals(str) && !replacers.contains(str)){
                    replacers.add(str);
                }
            }
            this.schemaReader.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public String format(String rawValue){
        StringBuffer formattedValue = new StringBuffer();
        
        StringReader rvReader = new StringReader(rawValue);
               
        int read=-1;
        try {
            while((read = schemaReader.read()) != -1){
                char c = (char)read;
                logger.debug(""+c);
                if(c == '#'){
                    char tmp = (char)rvReader.read();
                    logger.debug(""+tmp);
                    formattedValue.append(tmp);
                } else {
                    formattedValue.append(c);
                }
            }
            this.schemaReader.reset();
        } catch (IOException e) {
            throw new StringFormatException(e);
        }
        
        if(formattedValue.length() != this.schema.length()){
            throw new StringFormatException("Failed to format rawValue="+rawValue+" to schema="+this.schema+":output="+formattedValue.toString());
        }
        return formattedValue.toString();
    }
    
    public String unformat(String formattedValue){
        
        String unFormattedValue = formattedValue;
        
        Iterator repIterators = this.replacers.iterator();
        while (repIterators.hasNext()) {
            String element = (String) repIterators.next();
            unFormattedValue = unFormattedValue.replaceAll(element,"");
        } 
        return unFormattedValue;
    }
    
}
