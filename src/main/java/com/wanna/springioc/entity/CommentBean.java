package com.wanna.springioc.entity;

import com.wanna.springioc.CustomerAnnotation.Component;
import com.wanna.springioc.CustomerAnnotation.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component("commentBean")
public class CommentBean {
    @Value("22")
    public Integer id;
    @Value("hello world")
    public String content;
}
