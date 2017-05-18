package com.kenesis.transcoder.persistence;

import com.kenesis.transcoder.domain.EncodeVO;
import com.kenesis.transcoder.mybatis.SqlMapSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class EncodeDAO {
    private final String namespace = "com.kenesis.mappers.EncodeMapper";

    private static EncodeDAO dao;

    public static EncodeDAO getInstance() {
        if(dao == null)
        {
            dao = new EncodeDAO();
        }

        return dao;
    }

    SqlSessionFactory factory = SqlMapSessionFactory.getSqlSessionFactory();

    public void insertEncode(EncodeVO vo) {
        SqlSession sqlSession = factory.openSession();
        sqlSession.insert(namespace + ".insertEncode", vo);
        sqlSession.close();
    }


    public EncodeVO readEncode(int fileid) {
        SqlSession sqlSession = factory.openSession();
        EncodeVO vo = sqlSession.selectOne(namespace + ".readEncode", fileid);
        sqlSession.close();
        return vo;
    }


    public void deleteEncode(int fileid) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.delete(namespace + ".deleteEncode", fileid);
        sqlSession.close();
    }

    public void updateEncode(EncodeVO vo) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.update(namespace + ".updateEncode", vo);
        sqlSession.close();
    }
}
