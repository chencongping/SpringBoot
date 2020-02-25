package com.example.business.service;

import com.example.common.DefaultException;
import com.example.common.dto.BusinessDTO;
import com.example.common.dto.CommodityDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.enums.RspStatusEnum;
import com.example.common.response.ObjectResponse;
import com.example.common.service.OrderService;
import com.example.common.service.StorageService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImp implements BusinessService {
    @Reference(version = "0.0.1")
    private StorageService storageService;

    @Reference(version = "0.0.1")
    private OrderService orderService;

    private boolean flag;

    /**
     * 处理业务逻辑
     * @Param:
     * @Return:
     */
    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-gts-seata-example")
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {

        System.out.println("开始全局事务，XID = " + RootContext.getXID());

        ObjectResponse<Object> objectResponse = new ObjectResponse<>();
        //1、扣减库存
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setCount(businessDTO.getCount());
        ObjectResponse storageResponse = storageService.decreaseStorage(commodityDTO);

        //2、创建订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderCount(businessDTO.getCount());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderService.createOrder(orderDTO);

        //打开注释测试事务发生异常后，全局回滚功能
//        if (!flag) {
//            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
//        }

        if (storageResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }

        objectResponse.setStatus(RspStatusEnum.SUCCESS.getCode());
        objectResponse.setMessage(RspStatusEnum.SUCCESS.getMessage());
        objectResponse.setData(response.getData());
        return objectResponse;
    }

}