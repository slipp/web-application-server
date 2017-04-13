package model;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public interface ApiInterface {
  void sendResponse(DataOutputStream dos) throws IOException;
}
