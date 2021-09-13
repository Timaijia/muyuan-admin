package com.muyuan.member.domain.repo;

import com.muyuan.member.domain.model.User;

import java.util.Map;

public interface UserRepo {

    User find(int userNo);

    User selectFirst(Map params);

    void insert(User dataObject);
}