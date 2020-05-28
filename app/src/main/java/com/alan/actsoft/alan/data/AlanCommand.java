package com.alan.actsoft.alan.data;
/**
 * This object is used while processing the data of alan callback event's command.
 */
public class AlanCommand{
    private String command;
    private String value;
    private String error;

    public AlanCommand(String cmd, String cValue){
        this.command = cmd;
        this.value = cValue;
    }

    public AlanCommand(String er){
        this.error = er;
    }

    public boolean hasError(){
        return this.error != null ? true : false;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
