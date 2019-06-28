package br.com.senac.heroapp.webservice;

public class Api {
    private static final String ROOT_URL = "https://saitamaonep.000webhostapp.com/heroapp/v1/Api.php?apicall=";

    public static final String URL_CREATE_HEROAPP = ROOT_URL + "createheroapp";
    public static final String URL_READ_HEROAPP = ROOT_URL + "getheroapp";
    public static final String URL_UPDATE_HEROAPP = ROOT_URL + "updateheroapp";
    public static final String URL_DELETE_HEROAPP = ROOT_URL + "deleteheroapp&id=";

}
