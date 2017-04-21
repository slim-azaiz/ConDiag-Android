package info.androidhive.gmail.control_diagnostic.control;

/**
 * Created by armin on 10/01/15.
 */
public class Command {

    private int frequency;
    private String irCode;

    public Command(int frequency, String irCode) {
        this.frequency = frequency;
        this.irCode = irCode;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getIrCode() {
        return irCode;
    }

    public void setIrCode(String irCode) {
        this.irCode = irCode;
    }
}
