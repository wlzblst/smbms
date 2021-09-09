package com.gyw.service.user;

import com.gyw.dao.BaseDao;
import com.gyw.dao.user.UserDao;
import com.gyw.dao.user.UserDaoImpl;
import com.gyw.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {

    //业务层都会调用dao层
    private UserDao userDao;
    //五参构造-懒汉加载
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;
        System.out.println("进入login-service");

        try{
            connection = BaseDao.getConnection();
            System.out.println("获得数据库连接");
            System.out.println(connection);
            user = userDao.getLoginUser(connection,userCode,password);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    public boolean updatePwd(int id, String password) {
        Connection connection = null;
        boolean flag = false;
        connection = BaseDao.getConnection();
        try {
            if(userDao.update(connection,id,password)>0){
                flag = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection,username,userRole);
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection,null,null);
        }
        return count;
    }

    public List<User> getUserList(String queryUserName, int queryUserROle, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection,queryUserName,queryUserROle,currentPageNo,pageSize);
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection,null,null);
        }
        return userList;

    }

    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;
        try{
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false); //开启JDBC事务
            int updateRows = userDao.add(connection,user);
            connection.commit();
            if(updateRows > 0){
                flag = true;
                System.out.println("add success");
            }else{
                System.out.println("add failed");
            }
        }catch (Exception e){
            e.printStackTrace();
        } try{
            System.out.println("----rollback----");
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    public boolean deleteUserById(Integer delId) {
        Connection connection = null;
        boolean flag = false;
        try{
            connection = BaseDao.getConnection();
            if(userDao.deleteUserById(connection,delId)>0)
                flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    public boolean modify(User user) {
        Connection connection = null;
        boolean flag = false;
        try{
            connection = BaseDao.getConnection();
            if(userDao.modify(connection,user)>0){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override
    public User getUserById(String id) {
        User user = null;
        Connection connection = null;
        try{
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
            System.out.println("servvice");
            System.out.println(user);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }
}
