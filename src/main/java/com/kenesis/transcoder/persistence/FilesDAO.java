package com.kenesis.transcoder.persistence;

import com.kenesis.transcoder.domain.FilesVO;
import com.kenesis.transcoder.mybatis.SqlMapSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luyin on 2017-05-01.
 */
public class FilesDAO {
    private final String  namespace = "com.kenesis.mappers.FilesMapper";

    private static FilesDAO dao;

    public static FilesDAO getInstance()
    {
        if(dao == null)
        {
            dao = new FilesDAO();
        }
        return dao;
    }

    SqlSessionFactory factory = SqlMapSessionFactory.getSqlSessionFactory();

    public List<FilesVO> readFiles(FilesVO vo) {
        SqlSession sqlSession = factory.openSession();
        List<FilesVO> outputs = sqlSession.selectList(namespace + ".readFiles", vo);
        sqlSession.close();
        return outputs;
    }

    public void insertFiles(FilesVO vo) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.insert(namespace + ".insertFiles", vo);
        sqlSession.close();
    }

    public FilesVO readFile(String userid, String location) {
        SqlSession sqlSession = factory.openSession();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("userid", userid);
        paramMap.put("location", location);
        FilesVO vo = sqlSession.selectOne(namespace + ".readFile", paramMap);
        sqlSession.close();
        return vo;
    }

    public void deleteFile(int fileid) {
        SqlSession sqlSession = factory.openSession(true);
        sqlSession.delete(namespace + ".deleteFile", fileid);
        sqlSession.close();
    }
}
