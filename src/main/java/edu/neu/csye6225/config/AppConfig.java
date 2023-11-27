package edu.neu.csye6225.config;


import edu.neu.csye6225.annotations.RequestInterceptor;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.sns.SnsClient;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AppConfig  {


    @Value("${REGION}")
    String region;

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        AwsCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
                .profileName("root")
                .build();
        return CloudWatchAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        AwsCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
                .profileName("root")
                .build();
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public MeterRegistry getMeterRegistry() {
        CloudWatchConfig cloudWatchConfig = setupCloudWatchConfig();

        CloudWatchMeterRegistry cloudWatchMeterRegistry =
                new CloudWatchMeterRegistry(
                        cloudWatchConfig,
                        Clock.SYSTEM,
                        cloudWatchAsyncClient());

        return cloudWatchMeterRegistry;
    }

    private CloudWatchConfig setupCloudWatchConfig() {
        return new CloudWatchConfig() {

            private Map<String, String> configuration = Map.of(
                    "cloudwatch.namespace", "web-app",
                    "cloudwatch.step", Duration.ofMinutes(1).toString());

            @Override
            public String get(@NotNull String key) {
                return configuration.get(key);
            }
        };
    }

}
