package com.wanna.springioc.entity;

import com.wanna.springioc.CustomerAnnotation.Autowired;
import com.wanna.springioc.CustomerAnnotation.Component;
import com.wanna.springioc.CustomerAnnotation.Qualifier;
import com.wanna.springioc.CustomerAnnotation.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component(value = "userbean")
public class UserBean {
    @Value(value = "12")
    private Integer id;
    @Value(value = "zhangsan")
    private String name;
    @Value(value = "23.5")
    private Double age;

    @Autowired
    @Qualifier("commentBean")
    private CommentBean commentBean;
}
