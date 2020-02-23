package com.example.order.service;

import com.example.common.dto.AccountDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.enums.RspStatusEnum;
import com.example.common.response.ObjectResponse;
import com.example.common.service.AccountService;
import com.example.common.service.OrderService;
import com.example.order.entity.Order;
import com.example.order.mapper.OrderMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Service
public class OrderServiceImp implements OrderService {

    @Reference(version = "1.0.0")
    AccountService accountService;

    @Autowired
    OrderMapper orderMapper;

    @Override
    public ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO) {
        ObjectResponse<OrderDTO> response = new ObjectResponse<>();
        //扣减用户账户
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUserId(orderDTO.getUserId());
        accountDTO.setAmount(orderDTO.getOrderAmount());
        ObjectResponse objectResponse = accountService.decreaseAccount(accountDTO);

        //生成订单号
        orderDTO.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        //生成订单
        Order tOrder = new Order();
        BeanUtils.copyProperties(orderDTO,tOrder);
        tOrder.setCount(orderDTO.getOrderCount());
        tOrder.setAmount(orderDTO.getOrderAmount().doubleValue());
        try {
            orderMapper.createOrder(tOrder);
        } catch (Exception e) {
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        if (objectResponse.getStatus() != 200) {
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        response.setStatus(RspStatusEnum.SUCCESS.getCode());
        response.setMessage(RspStatusEnum.SUCCESS.getMessage());
        return response;
    }
}