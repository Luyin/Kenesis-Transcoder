package com.kenesis.transcoder.service;

import com.kenesis.transcoder.domain.UserVO;
import com.kenesis.transcoder.persistence.UserDAO;

/**
 * Created by Luyin on 2017-05-01.
 */
public class UserService {
    private static UserService service;

    private UserDAO dao = UserDAO.getInstance();

    public static UserService getInstance(){
        if(service == null){
            service = new UserService();
        }
        return service;
    }

    public void update(UserVO vo) {
        dao.updateUser(vo);
    }

    public UserVO read(String userid) {
        return (UserVO) dao.readUser(userid);
    }

    public void signup(UserVO vo) {
        dao.insertUser(vo);
    }

    public void signout(String userid) {
        dao.deleteUser(userid);
    }
}
