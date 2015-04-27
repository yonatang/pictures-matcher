package idc.storyalbum.matcher.conf;

import idc.storyalbum.matcher.pipeline.albumsearch.AlbumSearchFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yonatan on 27/4/2015.
 */
@Configuration
public class AlbumSearchConfig {
    @Bean
    ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(AlbumSearchFactory.class);
        return bean;
    }

    @Bean
    AlbumSearchFactory albumSearchFactory() {
        return (AlbumSearchFactory) serviceLocatorFactoryBean().getObject();
    }
}
