package org.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "config")
public class Setting {
    private String prefix;
    private String agent;
    private String webinterfaceLogin;
    private String webinterfacePassword;
    private String webinterface;
    private List<HashMap<String, String>> site;

}
