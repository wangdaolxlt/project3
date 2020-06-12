package com.mall.user.services;

import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.converter.MemberConverter;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/12 8:55
 */
@Service
@Component
public class IMemberServiceImpl implements IMemberService {

    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberConverter memberConverter;

    /**
     * 根据用户id查询用户会员信息
     *
     * @param request
     * @return
     */
    @Override
    public QueryMemberResponse queryMemberById(QueryMemberRequest request) {
        QueryMemberResponse response = new QueryMemberResponse();
        request.requestCheck();
        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("id", request.getUserId());
        List<Member> memberList = memberMapper.selectByExample(memberExample);
        if (CollectionUtils.isEmpty(memberList)) {
            // 用户不存在
            response.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
            response.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
            return response;
        }

        Member memberRecord = memberList.get(0);
        response = memberConverter.member2Res(memberRecord);
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return response;
    }

    /**
     * 修改用户头像
     *
     * @param request
     * @return
     */
    @Override
    public HeadImageResponse updateHeadImage(HeadImageRequest request) {
        return null;
    }

    /**
     * 更新信息
     *
     * @param request
     * @return
     */
    @Override
    public UpdateMemberResponse updateMember(UpdateMemberRequest request) {
        return null;
    }
}
