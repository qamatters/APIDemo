package com.apiframework.helpers;

import com.apiframework.common.enums.HeaderParameers;
import com.apiframework.common.enums.MultiDeviceRepositoryParameters;
import com.apiframework.common.logging.FrameWorkLogger;
import com.apiframework.common.utility.Utils;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.config.HttpClientConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.response.ValidatableResponse;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

public class BaseHelper {

   public static String executionServer; //It will take environment Name
   public static Map<String,String> apiUrls; // It will include the URL's
   public static boolean isIdam; //This is login Type
   public static Map<String, String> serverConfig;
   public static String testName;
   public static String accessToken;
   public static String userId;
   public static boolean userIsLoggedIn;
    private static String strUserDeviceKey;
    private static Map<String,Map<String,String>> multiUserRepositoryMap =null;
    private static Map<String,String> headerMap=null;
    private String strLoggerFileNameForLogin= "Login";
    private HashMap<String, String> repositoryMap;
    public String globalAPIName = null;

    /**
     *
     * Base Test Script Constructor
     */


public BaseHelper()

{
    serverConfig = Utils.getServerConfigMap();
    executionServer = serverConfig != null ? serverConfig.get("environment") : null;
    isIdam = Boolean.parseBoolean(serverConfig != null ? serverConfig.get("isIDAM") : null);
    apiUrls = Utils.getPropertyMap(executionServer);
    initializeLogger();
    // Reading variables from jenkin/System Variables

       String strEnvValue = null;
       strEnvValue=System.getenv("environment");

       String strEmail= null;
       strEmail=System.getenv("Email");

       String strPassword= null;
       strPassword=System.getenv("Password");

       String strDeviceKey= null;
       strDeviceKey=System.getenv("devicekey");


       String strBuildNumber= null;
       strBuildNumber=System.getenv("BUILD_NUMBER");

       //set serviceConfig with all key value pairs from projectconfig file
    FrameWorkLogger.logStep("this is first time logger check");

      serverConfig = Utils.getServerConfigMap();

      //Adding jenkins variable/values into serverconfig map

      if(strEnvValue!=null)
      {
          serverConfig.put("environment", strEnvValue);
          //Similarly for otheer variables
      }

      executionServer= serverConfig!=null?serverConfig.get("environment"):null;

      apiUrls=Utils.getPropertyMap(executionServer);



   }


//   protected Map<String, String> createHeader()
//   {
//       headerMap= initializeHeader();
//       return headerMap;
//   }
   /*
* Method to Initialize RestAssuredLogger
 */

public void initializeLogger()
{
    try
    {
        FrameWorkLogger.config(getClass());

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
}

    public void initializeLogger(String strLogFileName)
    {
        try
        {
            FrameWorkLogger.config(strLogFileName);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    protected static void getBaseURL()
    {
        String envValue = serverConfig.get("environment");
        FrameWorkLogger.logStep("env value is " +envValue);
        if(envValue.equalsIgnoreCase("DEV"))
        {
            apiUrls=Utils.getPropertyMap(executionServer);

        } else if (envValue.equalsIgnoreCase("PROD")) {

            //TODO
        }

    }


//     @BeforeSuite (alwaysRun = true)
//    public void preConditionManager()
//      {
//
//         initializeLogger(strLoggerFileNameForLogin);
//         File loginFail = new File("./trmpLoginFailed.txt");
//         if(loginFail.exists())
//         {
//             loginFail.delete();
//         }
//
//         repositoryMap= new HashMap<String, String>();
//         multiUserRepositoryMap= new HashMap<String, Map<String,String>>() ;
//         if (strUserDeviceKey!=null)
//         {
//             defaultLogin(serverConfig.get(strUserDeviceKey));
//         }
//          else
//         {
//             defaultLogin(serverConfig.get("deviceKey"));
//         }
//
//      }

    private void defaultLogin(String strDeviceId) {
    FrameWorkLogger.logStep("DefaultLogin started");
    userIsLoggedIn =false;
  //  headerMap =initializeHeader();
    //Remove from Header for this API

     headerMap.remove(HeaderParameers.XUSERID.getValue())  ;
     headerMap.remove(HeaderParameers.AUTHORIZATION.getValue())  ;
     //headerMap.remove(HeaderParameers.XDEVICEKEY.getValue())  ;
        // setIdamJsonBody(strDeviceId);  //RemoveIDAM

    }

    private void setIdamJsonBody(String strDeviceId) {
    setUserData(strDeviceId);
    updateJsonIDAM();
    getLoginURL();

    }

    private void updateJsonIDAM() {
    }

    private void getLoginURL() {
    }

    private void setUserData(String strDeviceId) {

    String path =null;
    if(serverConfig.get("deviceType").equalsIgnoreCase("W"))
    {
     path=System.getProperty("user.dir")+ "/resources/loginTestData/loginBodyForWeb.js";
    }
      else
    {
          path=System.getProperty("user.dir")+ "/resources/loginTestData/loginBody.js";
    }

    }











    //Overloading with DeviceKey and AuthToken

    protected static Map<String, String> initializeHeader(Map<String,String> deviceInfoMap)
    {
        headerMap = new HashMap<>();
        if(deviceInfoMap!=null)
        {
            headerMap.put(HeaderParameers.XUSERID.getValue(),deviceInfoMap.get(MultiDeviceRepositoryParameters.USERID.getValue()));
            headerMap.put(HeaderParameers.AUTHORIZATION.getValue(),deviceInfoMap.get(MultiDeviceRepositoryParameters.AUTHTOKEN.getValue()))  ;

            //             put DeviceKey and other details
        }
        headerMap.put(HeaderParameers.CONTENTTYPE.getValue(),"application/json");
        headerMap.put(HeaderParameers.ACCEPLANGUAGE.getValue(),"en");
        // Put other details

      //  addXapiHeaderKey(headerMap);

        return headerMap;
    }

//    private static void addXapiHeaderKey(Map<String, String> headerMap) {
//
//        String withXAPIKey = apiUrls.get("withXapiKey");
//        if(withXAPIKey.equalsIgnoreCase("Yes"))
//        {
//            String xApiKeyValue =apiUrls.get("xApiKeyValue");
//            headerMap.put(HeaderParameers.XAPIKEY.getValue(),xApiKeyValue)   ;
//        }
//
//    }


//    //Default InitializeHeader
//
//    protected static Map<String, String> initializeHeader()
//    {
//        Map <String, String> headerMapLocal = null;
//        try
//        {
//            if (strUserDeviceKey!=null)     {
//                headerMapLocal= multiUserRepositoryMap.get(serverConfig.get(strUserDeviceKey)) ;
//            }      else
//            {
//                headerMapLocal=multiUserRepositoryMap.get(serverConfig.get("deviceKey"));
//            }
//
//        }
//        catch (Exception e)
//        {
//            headerMapLocal=null;
//        }
//        initializeHeader(headerMapLocal);
//        return headerMap;
//
//    }


    /**

     *Validatable Response for Get API
     * @author Deepak Mathpal
     * Call the Get API and return the response
     *
     */


    public ValidatableResponse triggerGetAPI (String apiURL, Map<String, String>headerMap)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).urlEncodingEnabled(false)
                .headers(tempHeader).log().all().when().get(apiURL).then();
        response.log().all();
        return response;
    }


    public ValidatableResponse triggerGetAPI (String apiURL)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);

        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).urlEncodingEnabled(false)
                .headers(tempHeader).log().all().when().get(apiURL).then();
        response.log().all();
        return response;
    }


    public ValidatableResponse triggerGetAPIWithoutHeaer (String apiURL)
    {

        //Remove if something from this header
        //Add if something from this header
        FrameWorkLogger.logStep(apiURL);

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).urlEncodingEnabled(false)
                .log().all().when().get(apiURL).then();
        response.log().all();
        return response;
    }



    public ValidatableResponse triggerGetAPIWithCookies (String apiURL, Map<String, String>headerMap, String cookies)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().cookie("tsk_"+cookies,cookies).
                config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).urlEncodingEnabled(false)
                .headers(tempHeader).log().all().when().get(apiURL).then();
        response.log().all();
        return response;
    }




    public ValidatableResponse triggerPostAPI (String apiURL, Map<String, String>headerMap, String jsonBody)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames()).encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(jsonBody).headers(tempHeader).log().all().when().post(apiURL).then();
        response.log().all();

        return response;
    }

    //Post API with if you have to send cookies also

    public ValidatableResponse triggerPostAPIWithCookies (String apiURL, Map<String, String>headerMap, String jsonBody, String cookies)
    {
        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().cookie("tsk_" + cookies, cookies).config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames()).encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(jsonBody).headers(tempHeader).log().all().when().post(apiURL).then();
        response.log().all();

        return response;
    }

    //Post API if you have mutipart File requirement


    public ValidatableResponse triggerPostAPIWithMultipat (String apiURL, Map<String, String>headerMap, String jsonBody,File file)
    {
        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).multiPart("metadata", jsonBody,"application/json").multiPart(file).headers(tempHeader)
                .log().all().when().post(apiURL).then();
        response.log().all();

        return response;
    }



    /**

     *Validatable Response for Put API
     * @author Deepak Mathpal
     * Call the GpUTet API and return the response
     */


    public ValidatableResponse triggerPutAPI (String apiURL, Map<String, String>headerMap)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames()))
                .headers(tempHeader).log().all().when().put(apiURL).then();
        response.log().all();
        return response;
    }


    public ValidatableResponse triggerPutAPI (String apiURL, Map<String, String>headerMap, String jsonBody)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).body(jsonBody)
                .headers(tempHeader).log().all().when().put(apiURL).then();
        response.log().all();
        return response;
    }


    public ValidatableResponse triggerPutAPI (String apiURL)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().
                config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                        setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                        sslConfig(new SSLConfig().allowAllHostnames()))
                .headers(tempHeader).log().all().when().put(apiURL).then();
        response.log().all();
        return response;
    }



    /**

     *Validatable Response for Delete API
     * @author Deepak Mathpal
     * Call the Delete API and return the response
     */


    public ValidatableResponse triggerDeleteAPI (String apiURL, Map<String, String>headerMap, boolean urlEncodingFlag)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames())).urlEncodingEnabled(urlEncodingFlag)
                .headers(tempHeader).log().all().when().delete(apiURL).then();
        response.log().all();
        return response;
    }


    public ValidatableResponse triggerDeleteAPI (String apiURL, Map<String, String>headerMap)
    {

        Map<String, String> tempHeader = new HashMap<>(headerMap);
        //Remove if something from this header
        //Add if something from this header

        ValidatableResponse response = given().config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig().
                setParam(CoreConnectionPNames.CONNECTION_TIMEOUT,120000).setParam(CoreConnectionPNames.SO_TIMEOUT,120000)).
                sslConfig(new SSLConfig().allowAllHostnames()))
                .headers(tempHeader).log().all().when().delete(apiURL).then();
        response.log().all();
        return response;
    }
}
