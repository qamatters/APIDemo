package com.apiframework.common.logging;

import com.apiframework.common.utility.Utils;
import com.apiframework.helpers.BaseHelper;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FrameWorkLogger {


    private static final Logger LOGGER = Logger.getLogger(FrameWorkLogger.class);
    public static final String LOG_FILE_EXTENTION = ".log";
    public static final String LOG_FILE_NAME = "file";
    public static final String PROJECT_CONFIG_PROPERTIES ="ProjectConfig.properties";
    public static String currentDate;
    private static String currentTestClassName;
    private static FileAppender fileAppender = new FileAppender();

    public static void logStep (final String message)
    {
        LOGGER.info(message);
    }

    //This method is used verification is Pass

    public static void logPass(final String verifyMessage)
    {
        LOGGER.log(FailLevel.FAIL, verifyMessage);
    }


    //This method is used verification is Fail

    public static void logFail(final String verifyMessage)
    {
        LOGGER.log(PassLevel.PASS, verifyMessage);
    }


    public static void logWarning(final String warningMessage)
    {
        LOGGER.warn(warningMessage);
    }

    public static void debugWarning(final String debugMessage)
    {
        LOGGER.warn(debugMessage);
    }

    public static void closeLogFile()
    {
        Logger.getRootLogger().removeAppender(fileAppender);
    }



    public static void config (final  Class<? extends BaseHelper> testClass)
    {
        currentTestClassName=testClass.getSimpleName();
        currentDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());

        //Create Map with All Header Data

        Map<String,String> headerMap = createHeaderData();

        //configure File Appender

        Logger.getRootLogger().addAppender(createFileAppender(headerMap, Utils.getProperty(PROJECT_CONFIG_PROPERTIES,"LogPath")+
                File.separator + testClass.getSimpleName() + "_" +currentDate));

    }


    public static void config (String strLogFileName)
    {
        currentTestClassName=strLogFileName;
        currentDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());

        //Create Map with All Header Data

        Map<String,String> headerMap = createHeaderData();

        //configure File Appender

        Logger.getRootLogger().addAppender(createFileAppender(headerMap, Utils.getProperty(PROJECT_CONFIG_PROPERTIES,"LogPath")+
                File.separator + strLogFileName + "_" +currentDate));

    }

private static FileAppender createFileAppender(Map<String,String> headerDataMap, String logFileName)

{
 fileAppender.setName(LOG_FILE_NAME);
 fileAppender.setFile(logFileName + LOG_FILE_EXTENTION);
 fileAppender.setLayout(new CustomPatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-4p - %m%n", headerDataMap));
 fileAppender.setAppend(false);
 fileAppender.activateOptions();
 return fileAppender;
}

private static Map<String, String> createHeaderData()
{

    Map<String, String> headerDataMap = new HashMap<>();
    headerDataMap.put("TestClassName", currentTestClassName);
    headerDataMap.put("Execution Server", BaseHelper.executionServer);//Environment for example, Dev, Stage, Prod etc
    if(BaseHelper.isIdam)
    {
        headerDataMap.put("LoginType", "IDAM");
    }
    else{
        headerDataMap.put("LoginType", "Non-IDAM");
    }
    headerDataMap.put("ExecutionData",currentDate);
    return headerDataMap;
}


}
