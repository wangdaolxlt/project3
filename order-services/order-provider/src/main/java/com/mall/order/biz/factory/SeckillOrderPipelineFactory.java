package com.mall.order.biz.factory;

import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.CreateSeckillOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.biz.convert.TransConvert;
import com.mall.order.biz.handler.TransPipeline;

/**
 * @PackgeName: com.mall.order.biz.factory
 * @ClassName: SeckillOrderPipelineFactory
 * @Author: Li Haiquan
 * Date: 2020/6/17 16:16
 * project name: cs-mall
 */
public class SeckillOrderPipelineFactory extends AbstranctTransPipelineFactory{
    @Override
    protected TransHandlerContext createContext() {
        CreateSeckillOrderContext createSeckillOrderContext = new CreateSeckillOrderContext();
        return createSeckillOrderContext;
    }

    @Override
    protected void doBuild(TransPipeline pipeline) {
        pipeline.addLast();
    }

    @Override
    protected TransConvert createConvert() {
        return null;
    }

}
