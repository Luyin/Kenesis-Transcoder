package com.kenesis.transcoder.service;

import com.kenesis.transcoder.domain.FilesVO;
import com.kenesis.transcoder.persistence.FilesDAO;

import java.util.List;

/**
 * Created by Luyin on 2017-05-01.
 */
public class FilesService {
    private static FilesService service;

    private FilesDAO dao = FilesDAO.getInstance();

    public static FilesService getInstance(){
        if(service == null){
            service = new FilesService();
        }
        return service;
    }

    public List<FilesVO> viewDirectory(FilesVO vo) {
        return dao.readFiles(vo);
    }

    public void AddFiles(FilesVO vo) {
        dao.insertFiles(vo);
    }

    public FilesVO readFile(String userid, String location) {
        return dao.readFile(userid, location);
    }

    public void deleteFile(int fileid) { dao.deleteFile(fileid); }
}
