package com.wanna.springioc.config;

import com.wanna.springioc.CustomerAnnotation.Autowired;
import com.wanna.springioc.CustomerAnnotation.Component;
import com.wanna.springioc.CustomerAnnotation.Qualifier;
import com.wanna.springioc.CustomerAnnotation.Value;
import com.wanna.springioc.util.BeanDefinition;
import com.wanna.springioc.util.MyTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationConfigSpringContext {
    public Map<String, Object> IOC = new HashMap<>();  //IOC容器

    public AnnotationConfigSpringContext(String pack) {
        Set<BeanDefinition> beanDefinitions = findComponentBeanDefinitions(pack);  //获取BeanDefinition集合
        createObject(beanDefinitions);  //创建对象
        autowireObject(beanDefinitions);  //处理自动注入的情况
    }

    public void autowireObject(Set<BeanDefinition> beanDefinitions) {
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator(); //获取迭代器
        while (iterator.hasNext()) {
            BeanDefinition next = iterator.next(); //获取迭代的元素
            Class clazz = next.getBeanClass();//获取Class
            Field[] declaredFields = clazz.getDeclaredFields(); //获取所有的字段的值
            for (Field field :
                    declaredFields) {  //遍历每个字段
                Autowired annotation = field.getAnnotation(Autowired.class);//尝试获取Autowired注解
                if (annotation != null) {  //如果该字段上存在了@Autowired注解
                    try {
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);  //尝试去获取@Qualifier注解
                        String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase()
                                + field.getName().substring(1);  //获取Set方法的名称
                        Method method = clazz.getMethod(setMethodName, field.getType()); //获取方法
                        Object object = IOC.get(next.getBeanName());   //这个参数是外层的Bean
                        if (qualifier == null) {  //如果字段上没有写@Qualifier，使用按类型ByType注入
                            for (String key :
                                    IOC.keySet()) {  //遍历每一个Key
                                //类名相同这个判断条件后续可以改！
                                if (field.getType() == IOC.get(key).getClass()) {  //如果类名相同，那么就将其注入
                                    Object bean = IOC.get(key);
                                    method.invoke(object, bean);  //Invoke，将内层的Bean赋值给外层的Bean的属性
                                    break;
                                }
                            }
                        } else {//如果字段上有写@Qualifier，使用按名ByName注入
                            String beanName = qualifier.value();  //获取@Qualifier的Value值
                            Object bean = IOC.get(beanName);  //从IOC容器中获取beanName，即要注入的内容，内层的Bean
                            method.invoke(object, bean);  //Invoke，将内层的Bean赋值给外层的Bean的属性
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {  //如果该字段上没有@Autowired注解

                }
            }

        }
    }

    public void createObject(Set<BeanDefinition> beanDefinitions) {  //把扫描进来的类创建对象
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator(); //获取迭代器对象
        while (iterator.hasNext()) {  //迭代
            BeanDefinition beanDefinition = iterator.next();  //获取迭代的元素
            Class clazz = beanDefinition.getBeanClass();  //获取类
            String beanName = beanDefinition.getBeanName();  //获取BeanName
            try {
                Object newInstance = clazz.getConstructor().newInstance();  //根据clazz直接创建一个对象
                Field[] declaredFields = clazz.getDeclaredFields(); //获取已经定义的字段
                for (Field field :
                        declaredFields) {  //每一个字段
                    Value annotation = field.getAnnotation(Value.class); //尝试去获取Value注解
                    if (annotation != null) {  //如果不为空，即字段上有写@Value
                        String value = annotation.value();  //获取value的值
                        //下面是将值注入到对应的字段
                        String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase()
                                + field.getName().substring(1); //获取方法名
                        Method method = clazz.getMethod(setMethodName, field.getType()); //获取set方法
                        //method.invoke去调用这个方法
                        //根据字段的类型去设置
                        switch (field.getType().getName()) {
                            case "java.lang.String":
                                method.invoke(newInstance, value);
                                break;
                            case "java.lang.Integer":
                                method.invoke(newInstance, Integer.parseInt(value));
                                break;
                            case "java.lang.Float":
                                method.invoke(newInstance, Float.parseFloat(value));
                                break;
                            case "java.lang.Double":
                                method.invoke(newInstance, Double.parseDouble(value));
                                break;
                        }
                    }
                    IOC.put(beanName, newInstance);  //放入IOC容器当中
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    //根据包找到有Component注解的类
    public Set<BeanDefinition> findComponentBeanDefinitions(String pack) {
        Set<Class<?>> classes = MyTools.getClasses(pack);  //这个方法是用来根据包名获取包下的所有类
        Iterator<Class<?>> iterator = classes.iterator();  //获取迭代器
        Set<BeanDefinition> beanDefinitions = new HashSet<>();  //存放已经有@Component容器的缓冲区
        while (iterator.hasNext()) {
            Class<?> next = iterator.next();
            Component annotation = next.getAnnotation(Component.class);
            if (annotation != null) {  //如果Component这个注解不为空
                String value = annotation.value();  //获取Value的值
                if (value == null) {  //如果用户没有指定Bean的名字，使用类名首字母小写
                    String className = next.getName();  //获取类名
                    String packageName = next.getPackage().getName() + ".";  //包名末尾加上点
                    String beanName = className.replaceAll(packageName, ""); //将包名部分用空字符替换
                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1); //将首字母小写
                    beanDefinitions.add(new BeanDefinition(beanName, next));
                } else { //如果用户指定了BeanName
                    beanDefinitions.add(new BeanDefinition(value, next));
                }

            }
        }
        return beanDefinitions;
    }

    public Object getBean(String beanName) {
        return IOC.get(beanName);  //通过BeanName从容器中取出来这样一个Bean
    }
}
