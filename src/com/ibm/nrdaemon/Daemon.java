package com.ibm.nrdaemon;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Cloud on 06/11/2015.
 */
public class Daemon {



    public static void main(String [] args) throws IOException {

        FetchProperties fetch = new FetchProperties();

        MakeRequest request = new MakeRequest();
        request.makeApplicationRESTRequest();

    }

}
