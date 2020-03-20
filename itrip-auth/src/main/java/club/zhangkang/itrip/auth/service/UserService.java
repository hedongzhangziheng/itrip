package club.zhangkang.itrip.auth.service;

import club.zhangkang.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser login(String userCode, String password) throws Exception;
    ItripUser findByUserCode(String userCode) throws Exception;

    void createUserByPhone(ItripUser user) throws Exception;
    boolean validatePhone(String phoneNum,String code) throws Exception;

    void createUserByMail(ItripUser user) throws Exception;
    boolean validateMail(String mail,String code) throws Exception;
}
