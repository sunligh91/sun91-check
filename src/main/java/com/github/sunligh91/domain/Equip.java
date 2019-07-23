package com.github.sunligh91.domain;

public class Equip {
    private String equipName;
    private String command;

    public Equip(String equipName, String command) {
        this.equipName = equipName;
        this.command = command;
    }

    public Equip() {
    }

    public String getEquipName() {
        return equipName;
    }

    public void setEquipName(String equipName) {
        this.equipName = equipName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
