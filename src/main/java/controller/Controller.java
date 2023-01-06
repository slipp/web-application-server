package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Controller {
    public  void get(String path, BufferedReader in, DataOutputStream out) throws IOException;

    public void post(String path, BufferedReader in, DataOutputStream out) throws IOException;

}
