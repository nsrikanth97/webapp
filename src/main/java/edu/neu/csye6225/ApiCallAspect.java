package edu.neu.csye6225;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ApiCallAspect {

    @Bean
    public StatsDClient statsDCClient(){
        return new NonBlockingStatsDClient("web-application", "statsd-host", 8125);
    }

//    private final MeterRegistry meterRegistry;
//
//    public ApiCallAspect(MeterRegistry meterRegistry) {
//        this.meterRegistry = meterRegistry;
//    }
//
//    @Pointcut()
//    public void controller() {
//        log.info("controller() + srikanth");
//    }
//
//    @Pointcut()
//    protected void allMethods() {
//        log.info("allMethods() + srikanth");
//    }
//
//    @AfterReturning("controller() && allMethods()")
//    public void afterApiCall() {
//        log.info("afterApiCall() + srikanth");
//        Counter counter = Counter.builder("api.calls")
//                .description("Number of API calls")
//                .register(meterRegistry);
//        counter.increment();
//    }
}
