package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 
 * @author Ansel
 *
 */
@EnableScheduling
@SpringBootApplication
public class HlApplication  extends SpringBootServletInitializer{
	

	public static void main(String[] args) {
		SpringApplication.run(HlApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HlApplication.class);
    }

//	@Bean
//	public HttpMessageConverters fastJsonHttpMessageConverters() {
//		// 1.需要定义一个Convert转换消息的对象
//		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//		// 2.添加fastjson的配置信息，比如是否要格式化返回的json数据
//		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//		// 3.在convert中添加配置信息
//		fastConverter.setFastJsonConfig(fastJsonConfig);
//
//		HttpMessageConverter<?> converter = fastConverter;
//		return new HttpMessageConverters(converter);
//	}
}
