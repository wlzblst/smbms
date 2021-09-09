package com.gyw.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.gyw.pojo.Role;
import com.gyw.pojo.User;
import com.gyw.service.role.RoleServiceImpl;
import com.gyw.service.user.UserService;
import com.gyw.service.user.UserServiceImpl;
import com.gyw.tools.PageSupport;
import com.gyw.util.Constants;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method!=null && method.equals("savepwd")){
            this.updatePwd(req,resp);
        }else if(method!=null && method.equals("pwdmodify")){
            this.pwdModify(req,resp);
        }else if (method!=null && method.equals("query")){
            this.query(req,resp);
        }else if (method!=null && method.equals("add")){
            this.add(req,resp);
        }else if(method!=null && method.equals("deluser")){
            this.delUser(req,resp);
        }else if(method!=null && method.equals("modify")){
            this.getUserById(req,resp,"usermodify.jsp");
        }else if(method!=null && method.equals("view")){
            this.getUserById(req,resp,"userview.jsp");
        }else if (method!=null && method.equals("modifyexe")){
            this.modify(req,resp);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //拿ID
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;

        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if(flag){
                req.setAttribute("message","修改密码成功，请重新登陆");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{
                req.setAttribute("message","密码修改失败");
            }
        }else {
            req.setAttribute("message","新密码错误");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }

    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(o == null){
            resultMap.put("result","sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");
        }else{
            String userPassword = ((User)o).getPassword();
            if(oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else{
                resultMap.put("result","false");
            }
        }
        try{
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void query(HttpServletRequest req,HttpServletResponse resp){

        //从前端获取数据
        String queryUserName =  req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        int pageSize = 5;
        int currentPageNo = 1;

        if(queryUserName == null){
            queryUserName = "";
        }
        if (temp!=null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if(pageIndex != null){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        int totalCount = userService.getUserCount(queryUserName,queryUserRole);

        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        if(currentPageNo<1){
            currentPageNo = 1;
        }else if(currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }

        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);
        System.out.println(userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        System.out.println(roleList);

        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        System.out.println("add()----------------");
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setPassword(userPassword);
        user.setAddress(address);
        try{
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e){
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }
    }

    public void delUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        String id = req.getParameter("uid");
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e){
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult","notexist");
        }else{
            UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult","true");
            }else{
                resultMap.put("delResult","false");
            }
        }
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();

    }

    public void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        System.out.println("modify()----------------");
        String id = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setAddress(address);
        try{
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e){
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyDate(new Date());
        user.setModifyBy((((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId()));

        UserService userService = new UserServiceImpl();
        if(userService.modify(user)){
            resp.sendRedirect(req.getContextPath()+"jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        }
    }

    public void  getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException,IOException{
        String id = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            System.out.println("servlet");
            System.out.println(user);
            req.setAttribute("user",user);
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }
}
