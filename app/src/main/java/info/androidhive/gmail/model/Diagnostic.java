package info.androidhive.gmail.model;


import info.androidhive.gmail.R;



public class Diagnostic {
    private String parameter;
    private String value;


    public Diagnostic() {
    }
    public Diagnostic( String parameter, String value  ) {
        this.parameter = parameter;
        this.value = value;

    }




    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
