package com.muyuan.member.service.impl;

import com.muyuan.common.repo.jdbc.crud.SqlBuilder;
import com.muyuan.common.util.EncryptUtil;
import com.muyuan.common.util.IdUtil;
import com.muyuan.common.util.JWTUtil;
import com.muyuan.member.base.jwt.JWTManager;
import com.muyuan.member.dto.AccountLoginDTO;
import com.muyuan.member.dto.RegisterDTO;
import com.muyuan.member.model.User;
import com.muyuan.member.repo.dao.UserMapper;
import com.muyuan.member.service.LoginService;
import com.muyuan.member.vo.AccountLoginVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JWTManager jwtManager;

    @Override
    public Optional<AccountLoginVo> accountLogin(AccountLoginDTO loginInfo) {
        AccountLoginVo loginVo = new AccountLoginVo();
        User account = userMapper.selectFirst(new SqlBuilder(User.class)
                .eq("account", loginInfo.getAccount())
                .eq("type",loginInfo.getType())
                .build());

        if (account == null) {
            return Optional.empty();
        }

        final String password = EncryptUtil.SHA1(loginInfo.getPassword() + account.getSalt(), account.getEncryptKey());

        if (!account.getPassword().equals(password)) {
            return Optional.empty();
        }
        Map claims = new HashMap();
        claims.put("account",account.getAccount());
        DateTime time = DateTime.now();
        time.plus(30*60*60*1000);
        String jwt = jwtManager.createJwt(time.toDate(), claims);
        loginVo.setToken(jwt);
        loginVo.setExpireTime(time.toDate());
        return Optional.of(loginVo);
    }


}