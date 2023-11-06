import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApiCallAspect {

    private final MeterRegistry meterRegistry;

    public ApiCallAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
        log.info("controller() + srikanth");
    }

    @Pointcut("execution(* *.*(..))")
    protected void allMethods() {
        log.info("allMethods() + srikanth");
    }

    @AfterReturning("controller() && allMethods()")
    public void afterApiCall() {
        log.info("afterApiCall() + srikanth");
        Counter counter = Counter.builder("api.calls")
                .description("Number of API calls")
                .register(meterRegistry);
        counter.increment();
    }
}
