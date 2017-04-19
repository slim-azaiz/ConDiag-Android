package info.androidhive.gmail.network;

import info.androidhive.gmail.model.Diagnostic;

public class JSONResponse {
    private Diagnostic[] diagnostics;
    private Diagnostic[] memory;
    private Diagnostic[] conditional;
    private Diagnostic[] network;
    private Diagnostic[] software;
    private Diagnostic[] loader;
    private Diagnostic[] identification;
    private Diagnostic[] sysInfo;
    private Diagnostic[] realTime;



    public Diagnostic[] getDiagnostics() {return diagnostics;}
    public Diagnostic[] getMemory() {return memory;}
    public Diagnostic[] getConditionalAccess() {return conditional;}
    public Diagnostic[] getNetwork() {return network;}
    public Diagnostic[] getSoftware() {return software;}
    public Diagnostic[] getLoader() {return loader;}
    public Diagnostic[] getIdentification() {return identification;}
    public Diagnostic[] getSysInfo() {return sysInfo;}
    public Diagnostic[] getRealTime() {return realTime;}
}

