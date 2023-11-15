package edu.neu.csye6225.controller;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import org.springframework.web.bind.annotation.GetMapping;
import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.services.ec2.model.*;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;


@RestController("/instance")
public class InstanceController {

    @GetMapping
    public String getInstanceId() {
        String instanceId = EC2MetadataUtils.getInstanceId();
        String privateAddress = EC2MetadataUtils.getInstanceInfo().getPrivateIp();
        AmazonEC2 client = AmazonEC2ClientBuilder.defaultClient();
        String publicAddress = client.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId))
                .getReservations().stream()
                .map(Reservation::getInstances)
                .flatMap(List::stream)
                .findFirst()
                .map(Instance::getPublicIpAddress)
                .orElse(null);
        {
            return "Instance ID: " + instanceId + "\n" +
                    "Private IP: " + privateAddress + "\n" +
                    "Public IP: " + publicAddress;
        }
    }


}
