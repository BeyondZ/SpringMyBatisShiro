package com.lance.shiro.config;

import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.google.common.collect.Maps;

@Configuration
public class ShiroConfig {
	
	/**
	 * FilterRegistrationBean
	 * @return
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter")); 
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*"); 
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistration;
	}
	
	/**
	 * @see org.apache.shiro.spring.web.ShiroFilterFactoryBean
	 * @return
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(){
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(securityManager());
		bean.setLoginUrl("/a");
//		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//		shiroFilterFactoryBean.setLoginUrl(loginUrl);
//		// 登录成功后要跳转的连接
		bean.setSuccessUrl("/b");
//		shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
		bean.setUnauthorizedUrl("/c");
		
		Map<String, Filter>filters = Maps.newHashMap();
		filters.put("perms", urlPermissionsFilter());
		filters.put("anon", new AnonymousFilter());
		bean.setFilters(filters);
		
		Map<String, String> chains = Maps.newHashMap();
		chains.put("/**","anon");
//		chains.put("/index**","anon");
		chains.put("/login", "anon");
		chains.put("/echo", "anon"); //add for echo connection
		chains.put("/unauthorized", "anon");
		chains.put("/logout", "logout");
		chains.put("/base/**", "anon");
		chains.put("/css/**", "anon");
		chains.put("/layer/**", "anon");
		chains.put("/common/**","anon");  //common only allow login
//		chains.put("/**", "authc,perms");
		bean.setFilterChainDefinitionMap(chains);
		return bean;
	}
	
	/**
	 * @see org.apache.shiro.mgt.SecurityManager
	 * @return
	 */
	@Bean(name="securityManager")
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(userRealm());
		manager.setCacheManager(redisCacheManager());
		manager.setSessionManager(defaultWebSessionManager());
		return manager;
	}
	
	/**
	 * @see DefaultWebSessionManager
	 * @return
	 */
	@Bean(name="sessionManager")
	public DefaultWebSessionManager defaultWebSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setCacheManager(redisCacheManager());
		sessionManager.setGlobalSessionTimeout(1800000);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setDeleteInvalidSessions(true);
		return sessionManager;
	}
	
	/**
	 * @see UserRealm--->AuthorizingRealm
	 * @return
	 */
	@Bean
	@DependsOn(value={"lifecycleBeanPostProcessor", "shrioRedisCacheManager"})
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		userRealm.setCacheManager(redisCacheManager());
//		userRealm.setCachingEnabled(false);
//		userRealm.setAuthenticationCachingEnabled(false);
		userRealm.setAuthorizationCachingEnabled(false);
		return userRealm;
	}
	
	@Bean
	public URLPermissionsFilter urlPermissionsFilter() {
		return new URLPermissionsFilter();
	}
	
	@Bean(name="shrioRedisCacheManager")
	@DependsOn(value="redisTemplate")
	public ShrioRedisCacheManager redisCacheManager() {
		ShrioRedisCacheManager cacheManager = new ShrioRedisCacheManager(redisTemplate());
		return cacheManager;
	}
	
	@Bean(name="redisTemplate")
	public RedisTemplate<byte[], Object> redisTemplate() {
	    RedisTemplate<byte[], Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory());
	    return template;
	}
	
	@Bean
	public JedisConnectionFactory connectionFactory(){
		JedisConnectionFactory conn = new JedisConnectionFactory();
		conn.setDatabase(3);
		conn.setHostName("127.0.0.1");
//		conn.setPassword("123456");
		conn.setPort(6379);
		conn.setTimeout(3000);
		return conn;
	}
	
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
}