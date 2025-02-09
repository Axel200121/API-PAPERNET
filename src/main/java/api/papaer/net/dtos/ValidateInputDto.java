package api.papaer.net.dtos;

import java.io.Serializable;

public class ValidateInputDto implements Serializable {

    private String inputValidated;
    private String inputValidatedMessage;

    public String getInputValidated() {
        return inputValidated;
    }

    public void setInputValidated(String inputValidated) {
        this.inputValidated = inputValidated;
    }

    public String getInputValidatedMessage() {
        return inputValidatedMessage;
    }

    public void setInputValidatedMessage(String inputValidatedMessage) {
        this.inputValidatedMessage = inputValidatedMessage;
    }
}
