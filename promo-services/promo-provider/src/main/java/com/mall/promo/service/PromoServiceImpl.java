package com.mall.promo.service;

import com.mall.order.OrderCoreService;
import com.mall.promo.PromoService;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.constants.PromoRetCode;
import com.mall.promo.converter.PromoConverter;
import com.mall.promo.dal.entitys.PromoItem;
import com.mall.promo.dal.entitys.PromoSession;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dal.persistence.PromoSessionMapper;
import com.mall.promo.dto.*;
import com.mall.promo.mq.PromoMqProducer;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @PackgeName: com.mall.promo.service
 * @ClassName: PromoServiceImpl
 * @Author: Li Haiquan
 * Date: 2020/6/16 21:33
 * project name: cs-mall
 */
@Service
@Component
@Slf4j
public class PromoServiceImpl implements PromoService{
    @Autowired
    private PromoItemMapper promoItemMapper;
    @Autowired
    private PromoSessionMapper promoSessionMapper;

    @Reference(timeout = 3000, check = false)
    private OrderCoreService orderCoreService;
    @Reference(timeout = 3000, check = false)
    private IProductService productService;
    @Autowired
    private PromoConverter promoConverter;

    @Autowired
    private PromoMqProducer promoMqProducer;
    @Autowired
    private CacheManager cacheManager;

    /**
     * 获取秒杀商品列表接口
     * @param request
     * @return
     */
    @Override
    public SeckillListResponse getSeckillList(SeckillListRequest request) {
        SeckillListResponse response = new SeckillListResponse();
        //通过session和yyyymmdd确定唯一的秒杀场次
        Integer sessionId = request.getSessionId();
        String yyyymmdd = request.getYyyymmdd();
        Example promoSessionExample = new Example(PromoSession.class);
        promoSessionExample.createCriteria().andEqualTo("sessionId",sessionId).andEqualTo("yyyymmdd",yyyymmdd);
        List<PromoSession> promoSessionList = promoSessionMapper.selectByExample(promoSessionExample);
        if(promoSessionList.size() == 0){
            response.setCode(PromoRetCode.DB_EXCEPTION.getCode());
            response.setMsg(PromoRetCode.DB_EXCEPTION.getMessage());
            return response;
        }
        //通过秒杀场次的id去秒杀商品关联表查商品数据
        Integer psId = promoSessionList.get(0).getId();
        Example promoItemExample = new Example(PromoItem.class);
        promoItemExample.createCriteria().andEqualTo("psId", psId);
        List<PromoItem> promoItems = promoItemMapper.selectByExample(promoItemExample);
        ArrayList<SeckillProductDTO> seckillProductList = new ArrayList<>();
        for (PromoItem promoItem : promoItems) {
            SeckillProductDTO productDTO = new SeckillProductDTO();
            Integer itemId = promoItem.getItemId();
            ProductDetailResponse productDetailResp = productService.getProductDetails(itemId.longValue());
            ProductDetailDto productDetailDto = productDetailResp.getProductDetailDto();

            productDTO.setId(promoItem.getItemId().longValue());
            productDTO.setPicUrl(productDetailDto.getProductImageBig());
            productDTO.setInventory(promoItem.getItemStock());
            productDTO.setPrice(productDetailDto.getSalePrice());
            productDTO.setProductName(productDetailDto.getProductName());
            productDTO.setSeckillPrice(promoItem.getSeckillPrice());

            seckillProductList.add(productDTO);
        }
        response.setCode(PromoRetCode.SUCCESS.getCode());
        response.setMsg(PromoRetCode.SUCCESS.getMessage());
        response.setPsId(psId);
        response.setSessionId(sessionId);
        response.setProductList(seckillProductList);
        return response;
    }

    /**
     *  获取秒杀商品详情接口
     * @param request
     * @return
     */
    @Override
    public PromoProductDetailResponse getPromoProductDetail(PromoProductDetailRequest request) {
        PromoProductDetailResponse response = new PromoProductDetailResponse();
        Long psId = request.getPsId();
        Long productId = request.getProductId();
        Example promoItemExample = new Example(PromoItem.class);
        promoItemExample.createCriteria().andEqualTo("psId", psId).andEqualTo("itemId", productId);
        //通过psId和itemId，唯一确定一条记录，取出秒杀价
        List<PromoItem> promoItemList = promoItemMapper.selectByExample(promoItemExample);
        if(promoItemList.size() == 0){
            response.setCode(PromoRetCode.DB_EXCEPTION.getCode());
            response.setMsg(PromoRetCode.DB_EXCEPTION.getMessage());
            return response;
        }
        BigDecimal promoPrice = promoItemList.get(0).getSeckillPrice();

        //取出商品详情
        ProductDetailResponse productDetailResp = productService.getProductDetails(productId);
        ProductDetailDto productDetailDto = productDetailResp.getProductDetailDto();
        PromoProductDetailDTO promoProductDetailDTO = promoConverter.shopping2promo(productDetailDto);
        promoProductDetailDTO.setPromoPrice(promoPrice);

        response.setCode(PromoRetCode.SUCCESS.getCode());
        response.setMsg(PromoRetCode.SUCCESS.getMessage());
        response.setPromoProductDetailDTO(promoProductDetailDTO);
        return response;
    }

    /**
     * 秒杀下单
     * @param request
     * @return
     */
    @Override
    public SeckillOrderResponse createSeckillOrder(SeckillOrderRequest request) {
        SeckillOrderResponse response = new SeckillOrderResponse();

        /**
         * 先扣减库存
         */
        Long psId = request.getPsId();
        Long productId = request.getProductId();
        Example promoItemExample = new Example(PromoItem.class);
        promoItemExample.createCriteria().andEqualTo("psId", psId).andEqualTo("itemId", productId);
        //通过psId和itemId，唯一确定一条记录
        List<PromoItem> promoItemList = promoItemMapper.selectByExample(promoItemExample);
        if(promoItemList.size() == 0){
            response.setCode(PromoRetCode.DB_EXCEPTION.getCode());
            response.setMsg(PromoRetCode.DB_EXCEPTION.getMessage());
            return response;
        }
        PromoItem promoItem = promoItemList.get(0);
        Integer itemStock = promoItem.getItemStock();
        if(itemStock < 1){
            response.setCode(PromoRetCode.SYSTEM_TIMEOUT.getCode());
            response.setMsg("库存不足");
            return response;
        }

        Integer inventory = itemStock - 1;

        promoItemMapper.updatePromoItemStock(promoItem.getId());


        /**
         * 调用order服务插入订单和邮寄信息表
         */
        request.setSeckillPrice(promoItem.getSeckillPrice());
        response = orderCoreService.createSeckillOrder(request);

        response.setInventory(inventory);
        response.setProductId(productId);
        return response;
    }

    /**
     * 事务型秒杀下单
     * @param request
     * @return
     */
    @Override
    public SeckillOrderResponse createSeckillOrderInTransaction(SeckillOrderRequest request) {
        SeckillOrderResponse response = new SeckillOrderResponse();
        String stockKey = "stock_" + request.getPsId() + "_" + request.getProductId();
        String stockValue  = cacheManager.checkCache(stockKey);
        if("empty".equals(stockValue)){
            response.setCode(PromoRetCode.PROMO_ITEM_STOCK_NOT_ENOUGH.getCode());
            response.setMsg(PromoRetCode.PROMO_ITEM_STOCK_NOT_ENOUGH.getMessage());
            return response;
        }

        PromoItem promoItem = promoItemMapper.findPromoItemStockForUpdate(request.getPsId(), request.getProductId());
        request.setSeckillPrice(promoItem.getSeckillPrice());

        Boolean ret = promoMqProducer.sendOrderMessageIntransaction(request);
        if (ret) {
            response.setInventory(promoItem.getItemStock() - 1);
            response.setProductId(request.getProductId());
            response.setCode(PromoRetCode.SUCCESS.getCode());
            response.setMsg(PromoRetCode.SUCCESS.getMessage());
            return response;
        }else {
            response.setCode(PromoRetCode.PROMO_FAIL.getCode());
            response.setMsg(PromoRetCode.PROMO_FAIL.getMessage());
            return response;
        }
    }
}
