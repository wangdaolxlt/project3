package com.mall.order.biz.convert;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.result.AbstractResponse;
import com.mall.order.biz.context.CreateSeckillOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.dto.CreateOrderRequest;


/**
 * @PackgeName: com.mall.order.biz.convert
 * @ClassName: CreateSeckillOrderConvert
 * @Author: Li Haiquan
 * Date: 2020/6/17 16:53
 * project name: cs-mall
 */
public class CreateSeckillOrderConvert implements TransConvert{
    @Override
    public TransHandlerContext convertRequest2Ctx(AbstractRequest req, TransHandlerContext context) {
        return null;
    }

    @Override
    public AbstractResponse convertCtx2Respond(TransHandlerContext ctx) {
        return null;
    }
}
