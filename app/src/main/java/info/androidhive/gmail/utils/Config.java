package info.androidhive.gmail.utils;


public class Config {

    public static final String BASE_URL = "http://192.168.1.6:8000/";

    public static final String URL_ADD="http://169.254.57.23/CRUD/addUser.php";
    public static final String URL_GET_ALL = "http://169.254.57.23/CRUD/getAllUsers.php";
    public static final String URL_GET_USER = "http://169.254.57.23/CRUD/getUser.php?id=";
    public static final String URL_UPDATE_USER = "http://169.254.57.23/CRUD/updateUser.php";
    public static final String URL_DELETE_USER = "http://169.254.57.23/CRUD/deleteUser.php?id=";
    public static final String URL_LOGIN = BASE_URL+"authentificate/";
    public static final String URL_GET_ID = BASE_URL+"authentificate.json";
    public static final String URL_GetConduite = "http://169.254.57.23/CRUD/getConduite.php?id=";
    public static final String URL_GetCode = "http://169.254.57.23/CRUD/getCode.php?id=";
    public static final String URL_GetPlanning = "http://169.254.57.23/CRUD/getPlanning.php?moniteur=";
    public static final String URL_ADD_RENDEZ_VOUS = "http://169.254.57.23/CRUD/ajouterRendez-vous.php";







    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASS = "password";


    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PASS = "password";


    public static final String USER_ID = "user_id";
}
