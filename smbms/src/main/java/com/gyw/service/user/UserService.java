package com.gyw.service.user;

import com.gyw.pojo.Role;
import com.gyw.pojo.User;
import jdk.nashorn.internal.ir.IdentNode;

import java.util.List;

public interface UserService {
    //用户登陆
    public User login(String userCode,String password);

    //修改密码
    public boolean updatePwd(int id,String password);

    //查询记录字
    public int getUserCount(String username,int userRole);

    //查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserROle, int currentPageNo, int pageSize);

    //添加用户
    public boolean add(User user);

    //删除用户
    public boolean deleteUserById(Integer delId);

    //修改用户
    public boolean modify(User user);

    //查找用户
    public User getUserById(String id);

}
