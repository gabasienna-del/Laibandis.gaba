package com.laibandis.core;
interface IShield {
    void setToken(String token);
    String request(String base, String path, String jsonBody);
    void callPhone(String phone);
    String health();   // новый метод
}
