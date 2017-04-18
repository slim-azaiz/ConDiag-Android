package info.androidhive.gmail.control_diagnostic.control;

/**
 * Created by armin on 10/01/15.
 */
public class Command {

    private int frequency;
    private int irCode;

    public Command(int frequency, int irCommand) {
        this.frequency = frequency;
        this.irCode = irCommand;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getIrCode() {
        return irCode;
    }

    public void setIrCode(int irCode) {
        this.irCode = irCode;
    }
}
