package com.gyw.dao.user;

import com.gyw.dao.BaseDao;
import com.gyw.pojo.Role;
import com.gyw.pojo.User;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    public int update(Connection connection, int id, String password) throws SQLException {
        PreparedStatement pstm = null;
        int execute = 0;

        if(connection!=null){
            String sql = "UPDATE smbms_user SET userPassword = ? where id = ?";
            Object parms[] = {password,id};
            execute = BaseDao.updateData(connection, sql, parms,pstm);
            BaseDao.closeResource(connection,pstm,null);
        }
        return execute;

    }

    public User getLoginUser(Connection connection, String userCode , String password) {
        System.out.println("进入login-dao");

        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if(connection != null){
            String sql = "SELECT * FROM smbms_user where userCode=? and userPassword=?";
            Object[] params = {userCode,password};
            try {
                rs = BaseDao.searchData(connection,sql,pstm,rs,params);
                if(rs.next()){
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserCode(rs.getString("userCode"));
                    user.setUserName(rs.getString("userName"));
                    user.setPassword(rs.getString("userPassword"));
                    user.setGender(rs.getInt("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setUserRole(rs.getInt("userRole"));
                    user.setCreatedBy(rs.getInt("createdBy"));
                    user.setCreationDate(rs.getTimestamp("creationDate"));
                    user.setModifyBy(rs.getInt("modifyBy"));
                    user.setModifyDate(rs.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(connection,pstm,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        System.out.println(user);
        return user;
    }

    public int getUserCount(Connection connection, String userName, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet re = null;
        int count = 0;

        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS count from smbms_user u, smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" AND u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole>0){
                sql.append(" AND u.userRole like ?");
                list.add(userRole);
            }
            Object[] params = list.toArray();
            re = BaseDao.searchData(connection,sql.toString(),pstm,re,params);

            if(re.next()){
                count = re.getInt("count");
            }
            BaseDao.closeResource(connection,pstm,re);
        }
        return count;
    }

    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet re = null;
        List<User> userList = new ArrayList<User>();
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT u.*,r.roleName AS userRoleName from smbms_user u, smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" AND u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole>0){
                sql.append(" AND u.userRole like ?");
                list.add(userRole);
            }
            sql.append(" ORDER BY creationDate DESC LIMIT ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
            re = BaseDao.searchData(connection,sql.toString(),pstm,re,params);

            while(re.next()){
                User user = new User();
                user.setId(re.getInt("id"));
                user.setUserCode(re.getString("userCode"));
                user.setUserName(re.getString("userName"));
                user.setGender(re.getInt("gender"));
                user.setBirthday(re.getDate("birthday"));
                user.setPhone(re.getString("phone"));
                user.setAddress(re.getString("address"));
                user.setUserRole(re.getInt("userRole"));
                user.setUserRoleName(re.getString("userRoleName"));
                userList.add(user);
            }
            BaseDao.closeResource(connection,pstm,re);
        }
        return userList;
    }

    public int add(Connection connection, User user) throws Exception {

        PreparedStatement pstm = null;
        int updateRows = 0;
        if(connection != null){
            String sql = "INSERT INTO smbms_user (userCode,userName,userPassword)" +
                    "userRole,gender,birthday,phone,address,creationDate,createBy)" +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {
              user.getUserCode(),user.getUserName(),user.getPassword(),user.getUserRole(),
              user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getCreationDate(),user.getCreatedBy()
            };
            updateRows = BaseDao.updateData(connection,sql,params,pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return updateRows;
    }

    public int deleteUserById(Connection connection, Integer delId) throws Exception {
        PreparedStatement pstm = null;
        int flag = 0;
        if(connection != null){
            String sql = "DELETE FROM smbms_user WHERE id = ?";
            Object[] params = {delId};
            flag = BaseDao.updateData(connection,sql,params,pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }

    public int modify(Connection connection, User user) throws Exception {
        PreparedStatement pstm = null;
        int flag = 0;
        if(connection!=null){
            String sql = "UPDATE smbms_user set userName=?," +
            "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? WHERE id=?";
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),user.getPhone(),
            user.getAddress(),user.getUserRole(),user.getModifyBy(),user.getModifyDate(),user.getId()};
            flag = BaseDao.updateData(connection ,sql ,params ,pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }

    @Override
    public User getUserById(Connection connection, String id) throws Exception {
        User user = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(connection!=null){
            String sql = "SELECT u.*,r.roleName FROM smbms_user u,smbms_role r WHERE u.id=? AND u.userRole=r.id";
            Object[] params = {id};
            rs = BaseDao.searchData(connection, sql, pstm, rs, params);
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        System.out.println("dao");
        System.out.println(user);
        return user;
    }
}
