package com.kenesis.transcoder;

import com.kenesis.transcoder.rabbitmq.QueueListener;

import java.io.File;

import static java.lang.System.getProperty;

/**
 * Created by Luyin on 2017-05-01.
 */
public class Main {
    public static void main(String[] args) throws Exception
    {
        final String host = "luyin.iptime.org";
        final String vhost = "kenesisHost";
        final int port = 9002;
        final String user = "kenesisService";

        final String password = "kenesis";
        String encode_queue = "encode";
        String delete_queue = "delete";

        QueueListener ql = new QueueListener();
        String uri = "amqp://"+user+":"+password+"@"+host+":"+port +"/"+ vhost;
        ql.invoke(uri, encode_queue, 1);
        ql.invoke(uri, delete_queue, 1);
    }
}
