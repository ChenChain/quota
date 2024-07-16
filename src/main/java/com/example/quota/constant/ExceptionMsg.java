package com.example.quota.constant;

public class ExceptionMsg {
    public static int Internal_Error = 1000;
    public static String Internal_Error_Msg = "internal err, please contact the admin";

    public static int Parameter_Invalid = 2001;
    public static String Parameter_Invalid_Msg = "the parameter is invalid, please check and try again";

    public static int Quota_Avail_Has_Used = 2002;
    public static String Quota_Avail_Has_Used_Msg = "the available quota has been used";

    public static int Quota_Avail_Not_Enough = 2003;
    public static String Quota_Avail_Not_Enough_Msg = "the available quota is not enough";


    public static int Quota_Has_Existed = 2004;
    public static String Quota_Has_Existed_Msg = "user has the type quota";

    public static int Quota_Not_Qualifying= 2005;
    public static String Quota_Not_Qualifying_Msg= "the qualifying quota does not exist";

}
