package com.example.swaggerdemo.controller;

import io.swagger.annotations.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "HelloController", description = "Hello相关的Rest接口")
@RestController
public class HelloController {

    @ApiOperation(value="hello world", notes="test: 仅1有正确返回")
    @ApiImplicitParams({  // 用在方法上，包含一组参数说明
            // 给方法入参增加说明。 可以多个参数 ，重复配置
            @ApiImplicitParam(name = "name", value = "用户名称", required = false, dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {   // 用于表示一组响应
            // 用在@ApiResponses中，一般用于表达一个错误的响应信息
            @ApiResponse(code = 1, message = "Successful — 请求已完成"),
            @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求"),
            @ApiResponse(code = 401, message = "未授权客户机访问数据"),
            @ApiResponse(code = 500, message = "服务器不能完成请求")
    })
    @RequestMapping("hello")
    public String hello(){
        return "hello world";
    }


    @ApiImplicitParam(name = "student", value = "普通用户", required = true, dataType = "Student")
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public String saveStudent(@RequestBody @Valid Student student, BindingResult bindingResult){

        //在这里，我们判断参数是否通过校验
        if (bindingResult.hasErrors()) {
            // 默认是 普通模式 ： 会校验完所有的属性
            for (ObjectError error : bindingResult.getAllErrors()) {
                System.out.println(error.getDefaultMessage());
            }

            String message = bindingResult.getFieldError().getDefaultMessage();
            //自定义的返回，并将错误信息返回
            return message;
        }

        return "Success";
    }
}
