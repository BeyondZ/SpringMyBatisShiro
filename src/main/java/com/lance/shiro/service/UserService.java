package com.lance.shiro.service;

import com.lance.shiro.entity.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface UserService {
    // 通过用户名及密码核查用户登录
    IUser login(String username, String password);

    //增加用户
    int register(IUser user);

    //根据用户名查询
    IUser findByUserName(String user);

    /**
     * 获取资源集合
     *
     * @param account
     * @return
     */
    Set<String> findPermissions(String account);

    /**
     * 通过role获取用户列表
     * @param role
     * @return
     */
    ArrayList<IUser> findAllByRoles(List<String> role);

    /**
     * 删除用户
     * @param id
     */
    int deleteAllByIds(List<String> id);

    /**
     * 修改用户
     * @param user
     */
    IUser update(IUser user);

}
