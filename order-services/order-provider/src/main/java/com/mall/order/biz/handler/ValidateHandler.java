package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.user.IAddressService;
import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.QueryMemberRequest;
import com.mall.user.dto.QueryMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  ciggar
 * create-date: 2019/8/1-下午4:47
 *
 */
@Slf4j
@Component
public class ValidateHandler extends AbstractTransHandler {

    @Reference(check = false)
    private IMemberService memberService;
    @Reference
    private IAddressService iAddressService;
    /**
     * 验证用户合法性
     * @return
     */

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
/*        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        QueryMemberRequest request = new QueryMemberRequest();
        request.setUserId(createOrderContext.getUserId());

        QueryMemberResponse response = memberService.queryMemberById(request);
        if(response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            String username = response.getUsername();
            if(! username.equals(createOrderContext.getUserName())){
                throw new BizException(response.getCode(), response.getMsg());
            }

            createOrderContext.setBuyerNickName(username);
        }*/
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        QueryMemberRequest request = new QueryMemberRequest();
        request.setUserId(createOrderContext.getUserId());
        QueryMemberResponse response = memberService.queryMemberById(request);
        String username = response.getUsername();
        createOrderContext.setBuyerNickName(username);
        return true;
    }
}
