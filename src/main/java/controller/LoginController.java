package controller;

import util.Header;
import util.HttpResponseUtils;
import util.HttpStatusCode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController implements Controller{
    @Override
    public void get(String path, BufferedReader in, DataOutputStream out) throws IOException {
        HttpResponseUtils.responseHeader(out, HttpStatusCode.NOT_FOUND, new ArrayList<>());
    }

    @Override
    public void post(String path, BufferedReader in, DataOutputStream out) throws IOException {

    }
}
