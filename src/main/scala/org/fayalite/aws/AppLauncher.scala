package org.fayalite.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.elasticloadbalancing.model.{Listener, CreateLoadBalancerRequest}
import com.amazonaws.services.opsworks.model.ElasticLoadBalancer

import scala.collection.JavaConversions
import JavaConversions._

/*
Remember when creating the VPC you must create a gateway and setup the routes.
 */

/**
 * In theory, something that could help you launch machines on AWS.
 */
object AppLauncher {

  val ubuntu1404HVM = "ami-06116566"
  val defaultRegion = Regions.US_WEST_1
  val pubNet = "subnet-ff295988"
  val appSg = "sg-a58bcbc1"

  def main(args: Array[String]) {


    val elbr = new CreateLoadBalancerRequest()
    elbr.setLoadBalancerName("app-elb")
    elbr.setSecurityGroups(List(appSg))
    elbr.setSubnets(List(pubNet))

    val listeners = List(new Listener().withInstancePort(80).withLoadBalancerPort(80)
    .withInstanceProtocol("HTTP").withProtocol("HTTP"))
    elbr.setListeners(listeners)
    AWS.elb.createLoadBalancer(elbr)


  }
  def run() = {
    val rir = new RunInstancesRequest()
    rir.setImageId(ubuntu1404HVM)
    rir.setEbsOptimized(false)
    rir.setInstanceType("t2.micro")
    rir.setSubnetId(pubNet)
    rir.setSecurityGroupIds(List(appSg))
    rir.setKeyName("fayalite")
    rir.setMinCount(1)
    //rir.setNetworkInterfaces()
    rir.setMaxCount(1)
    //rir.setPlacement()
    AWS.ec2.runInstances(rir)


  }

}
