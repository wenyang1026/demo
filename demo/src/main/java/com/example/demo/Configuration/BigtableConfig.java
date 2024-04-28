package com.example.demo.Configuration;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BigtableConfig {

    @Bean
    public BigtableDataClient bigtableDataClient() throws IOException {
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "shared-539";

        // 创建Bigtable的配置设置
        BigtableDataSettings dataSettings = BigtableDataSettings.newBuilder()
                .setProjectId(projectId)
                .setInstanceId(instanceId)
                .build();

        // 创建Bigtable客户端
        return BigtableDataClient.create(dataSettings);
    }
}