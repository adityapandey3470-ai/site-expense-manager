package com.aditya.siteexpensemanager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class SystemSettingsDefaultsProperties {
    private BigDecimal foodRatePerPerson = new BigDecimal("330");
    private int payoutCycleDays = 2;
}