package com.apiframework.common.logging;

import org.apache.log4j.PatternLayout;

import java.util.Map;

public class CustomPatternLayout extends PatternLayout{

    private Map<String,String> headerData;
    public CustomPatternLayout(final String pattern, final Map<String,String> headerData)
    {
        super(pattern);
        this.headerData =headerData;
    }
    @Override
    public String getHeader()
    {

        StringBuilder builder = new StringBuilder();

        for(String key:headerData.keySet())
        {
            builder.append(key).append(":");
            int i = key.length()<35?35-key.length():0;
            char character = (char) i;
            builder.append(character=='\0'?' ':character);
            builder.append(headerData.get(key));
            builder.append(System.getProperty("Line.seperator"));
        }
         builder.append(System.getProperty("Line.seperator"));
        return builder.toString();
    }

}
