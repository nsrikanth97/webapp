package edu.neu.csye6225.services;


import edu.neu.csye6225.dto.MetricTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricPublisher {

    private CloudWatchAsyncClient cloudWatchAsyncClient;


    @Autowired
    public MetricPublisher(CloudWatchAsyncClient cloudWatchAsyncClient) {
        super();
        this.cloudWatchAsyncClient = cloudWatchAsyncClient;
    }

    public void putMetricData(final String namespace,
                              String metricName,
                              Double dataPoint,
                              List<MetricTag> metricTags) {

        try{
            List<Dimension> dimensions = metricTags
                    .stream()
                    .map((metricTag)-> Dimension
                            .builder()
                            .name(metricTag.getName())
                            .value(metricTag.getValue())
                            .build()).toList();
            String time = ZonedDateTime
                    .now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);
            Instant instant = Instant.parse(time);

            MetricDatum datum = MetricDatum
                    .builder()
                    .metricName(metricName)
                    .unit(StandardUnit.NONE)
                    .value(dataPoint)
                    .timestamp(instant)
                    .dimensions(dimensions)
                    .build();

            PutMetricDataRequest request =
                    PutMetricDataRequest
                            .builder()
                            .namespace(namespace)
                            .metricData(datum)
                            .build();
            cloudWatchAsyncClient.putMetricData(request);
        }catch (CloudWatchException e){
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}
