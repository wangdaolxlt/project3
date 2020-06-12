package com.mall.user.services;

import com.mall.user.bootstrap.UserProviderApplicationTests;
import com.mall.user.dto.QueryMemberRequest;
import com.mall.user.dto.QueryMemberResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/12 9:04
 */
public class IMemberServiceImplTest extends UserProviderApplicationTests {

    @Autowired
    IMemberServiceImpl iMemberService;

    @Test
    public void queryMemberById() {
        QueryMemberRequest queryMemberRequest = new QueryMemberRequest();
        queryMemberRequest.setUserId(71L);
        QueryMemberResponse response = iMemberService.queryMemberById(queryMemberRequest);
        response.toString();
    }
}