package itu.GreenField.dto;

import java.util.*;

public class CommandeBackFilterDto {
    Integer lineNumber;
    List<Integer> clientId;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<Integer> getClientId() {
        return clientId;
    }

    public void setClientId(List<Integer> clientId) {
        this.clientId = clientId;
    }
}
