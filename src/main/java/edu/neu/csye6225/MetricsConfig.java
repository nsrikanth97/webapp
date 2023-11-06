package edu.neu.csye6225;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
public class MetricsConfig {
    private final MeterRegistry meterRegistry;


    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @ModelAttribute
    public void beforeAnyControllerMethod(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Counter.builder("api.calls")
                .tag("uri", uri)
                .register(meterRegistry)
                .increment();
    }
}
