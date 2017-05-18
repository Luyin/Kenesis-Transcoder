package com.kenesis.transcoder.persistence;

import com.kenesis.transcoder.domain.UserVO;
import com.kenesis.transcoder.mybatis.SqlMapSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Created by Luyin on 2017-05-01.
 */
public class UserDAO {
    private final String  namespace = "com.kenesis.mappers.UserMapper";

    private static UserDAO dao;

    public static UserDAO getInstance()
    {
        if(dao == null)
        {
            dao = new UserDAO();
        }

        return dao;
    }

    SqlSessionFactory factory = SqlMapSessionFactory.getSqlSessionFactory();

    public void insertUser(UserVO vo){
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.insert(namespace + ".insertUser", vo);
        sqlSession.close();
    }

    public void deleteUser(String userid) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.delete(namespace + ".deleteUser", userid);
        sqlSession.close();
    }

    public void updateUser(UserVO vo) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.update(namespace + ".updateUser", vo);
        sqlSession.close();
    }

    public UserVO readUser(String userid) {
        SqlSession sqlSession = factory.openSession();
        UserVO vo = sqlSession.selectOne(namespace + ".readUser", userid);
        sqlSession.close();
        return vo;
    }
}
