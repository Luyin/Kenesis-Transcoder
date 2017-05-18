package com.kenesis.transcoder.service;

import com.kenesis.transcoder.domain.EncodeVO;
import com.kenesis.transcoder.persistence.EncodeDAO;

/**
 * Created by Luyin on 2017-04-30.
 */
public class EncodeService {

    private static EncodeService service;

    private EncodeDAO dao = EncodeDAO.getInstance();

    public static EncodeService getInstance(){
        if(service == null){
            service = new EncodeService();
        }
        return service;
    }

    public void requestEncode(EncodeVO vo) {
        dao.insertEncode(vo);
    }

    public void cancleEncode(int fileid) {
        dao.deleteEncode(fileid);
    }

    public EncodeVO statusEncode(int fileid) {
        return dao.readEncode(fileid);
    }

    public void updateEncode(EncodeVO vo) { dao.updateEncode(vo); }
}
