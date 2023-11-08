package edu.neu.csye6225.annotations;

import edu.neu.csye6225.dto.MetricTag;
import edu.neu.csye6225.services.MetricPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {

    private final MetricPublisher metricPublisher;

    @Autowired
    public RequestInterceptor(MetricPublisher metricPublisher){
        this.metricPublisher = metricPublisher;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURL = request.getRequestURL().toString();
        log.info(requestURL);
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();


            List<MetricTag> metricTags = new ArrayList<>();
            metricTags.add(new MetricTag("Type", "Counter"));
            metricTags.add(new MetricTag("ClassName", controllerName));
            metricTags.add(new MetricTag("MethodType", request.getMethod()));

            metricPublisher.putMetricData("web-app", methodName, 1.0, metricTags );
        }
    }
}
