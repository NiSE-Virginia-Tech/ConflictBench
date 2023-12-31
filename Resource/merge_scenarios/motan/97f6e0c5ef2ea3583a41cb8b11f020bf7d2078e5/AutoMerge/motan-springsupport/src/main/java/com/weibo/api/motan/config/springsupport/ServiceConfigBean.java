package com.weibo.api.motan.config.springsupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.BasicServiceInterfaceConfig;
import com.weibo.api.motan.config.ConfigUtil;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.exception.MotanErrorMsgConstant;
import com.weibo.api.motan.exception.MotanFrameworkException;
import com.weibo.api.motan.util.CollectionUtil;
import com.weibo.api.motan.util.MathUtil;
import com.weibo.api.motan.util.MotanFrameworkUtil;

public class ServiceConfigBean<T extends java.lang.Object> extends ServiceConfig<T> implements BeanPostProcessor, BeanFactoryAware, InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent> {
  private static final long serialVersionUID = -7247592395983804440L;

  private transient BeanFactory beanFactory;

  @Override public void destroy() throws Exception {
    unexport();
  }

  @Override public void afterPropertiesSet() throws Exception {
    checkAndConfigBasicConfig();
    checkAndConfigExport();
    checkAndConfigRegistry();
  }

  @Override public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override public void onApplicationEvent(ContextRefreshedEvent event) {
    if (!getExported().get()) {
      export();
    }
  }

  /**
     * 检查并配置basicConfig
     */
  private void checkAndConfigBasicConfig() {
    if (getBasicServiceConfig() == null) {
      if (MotanNamespaceHandler.basicServiceConfigDefineNames.size() == 0) {
        if (beanFactory instanceof ListableBeanFactory) {
          ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
          String[] basicServiceConfigNames = listableBeanFactory.getBeanNamesForType(BasicServiceInterfaceConfig.class);
          MotanNamespaceHandler.basicServiceConfigDefineNames.addAll(Arrays.asList(basicServiceConfigNames));
        }
      }
      for (String name : MotanNamespaceHandler.basicServiceConfigDefineNames) {
        BasicServiceInterfaceConfig biConfig = beanFactory.getBean(name, BasicServiceInterfaceConfig.class);
        if (biConfig == null) {
          continue;
        }
        if (MotanNamespaceHandler.basicServiceConfigDefineNames.size() == 1) {
          setBasicServiceConfig(biConfig);
        } else {
          if (biConfig.isDefault() != null && biConfig.isDefault().booleanValue()) {
            setBasicServiceConfig(biConfig);
          }
        }
      }
    }
  }

  /**
     * 检查是否已经装配export，如果没有则到basicConfig查找
     */
  private void checkAndConfigExport() {
    if (StringUtils.isBlank(getExport()) && getBasicServiceConfig() != null && !StringUtils.isBlank(getBasicServiceConfig().getExport())) {
      setExport(getBasicServiceConfig().getExport());
      if (getBasicServiceConfig().getProtocols() != null) {
        setProtocols(new ArrayList<ProtocolConfig>(getBasicServiceConfig().getProtocols()));
      }
    }
    if (CollectionUtil.isEmpty(getProtocols()) && StringUtils.isNotEmpty(getExport())) {
      Map<String, Integer> exportMap = ConfigUtil.parseExport(export);
      if (!exportMap.isEmpty()) {
        List<ProtocolConfig> protos = new ArrayList<ProtocolConfig>();
        for (String p : exportMap.keySet()) {
          ProtocolConfig proto = beanFactory.getBean(p, ProtocolConfig.class);
          if (proto == null) {
            if (MotanConstants.PROTOCOL_MOTAN.equals(p)) {
              proto = MotanFrameworkUtil.getDefaultProtocolConfig();
            } else {
              throw new MotanFrameworkException(String.format("cann\'t find %s ProtocolConfig bean! export:%s", p, export), MotanErrorMsgConstant.FRAMEWORK_INIT_ERROR);
            }
          }
          protos.add(proto);
        }
        setProtocols(protos);
      }
    }
    if (StringUtils.isEmpty(getExport()) || CollectionUtil.isEmpty(getProtocols())) {
      throw new MotanFrameworkException(String.format("%s ServiceConfig must config right export value!", getInterface().getName()), MotanErrorMsgConstant.FRAMEWORK_INIT_ERROR);
    }
  }

  /**
     * 检查并配置registry
     */
  private void checkAndConfigRegistry() {
    if (CollectionUtil.isEmpty(getRegistries()) && getBasicServiceConfig() != null && !CollectionUtil.isEmpty(getBasicServiceConfig().getRegistries())) {
      setRegistries(getBasicServiceConfig().getRegistries());
    }
    if (CollectionUtil.isEmpty(getRegistries())) {
      for (String name : MotanNamespaceHandler.registryDefineNames) {
        RegistryConfig rc = beanFactory.getBean(name, RegistryConfig.class);
        if (rc == null) {
          continue;
        }
        if (MotanNamespaceHandler.registryDefineNames.size() == 1) {
          setRegistry(rc);
        } else {
          if (rc.isDefault() != null && rc.isDefault().booleanValue()) {
            setRegistry(rc);
          }
        }
      }
    }
    if (CollectionUtil.isEmpty(getRegistries())) {
      setRegistry(MotanFrameworkUtil.getDefaultRegistryConfig());
    }
  }
}