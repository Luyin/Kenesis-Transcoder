package com.kenesis.transcoder.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Luyin on 2017-04-30.
 */
public class SqlMapSessionFactory {

    public static SqlSessionFactory ssf;

    static{

        String resource = "configuration.xml";
        InputStream inputStream = null;

        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ssf = new SqlSessionFactoryBuilder().build(inputStream);

    }

    public static SqlSessionFactory getSqlSessionFactory(){
        return ssf;
    }
}