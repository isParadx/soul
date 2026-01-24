package com.paradx.soul.pojo.vo;

public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;

    // getter 和 setter 方法
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}