package com.wanna.springioc.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BeanDefinition {
    String beanName;  //BeanName
    Class beanClass;  //BeanClass
}
