package org.fayalite.aws

import java.util

import com.amazonaws.services.ec2.model._

import scala.util.Try

/**
  * Shortcuts for spot instance related AWS stuff.
  */
object Spot {
  
  import AWS.ec2
  import scala.collection.JavaConversions._

  /**
    * Easy to read debug information on a spot instance
    * request formatted easily
    * @param sir: Spot Instance Request, see AWS JDK for details
    *           or explore type below.
    * @return : Report string with useful information on spot request
    */
  def prettify(sir: SpotInstanceRequest) = {
    s"${sir.getType} @ ${sir.getSpotPrice} state: ${sir.getState} | " +
      s"message: ${sir.getStatus.getMessage}"
  }

  /**
    * Periodically dump info on active spot requests.
    */
  def checkSpotRequests(): Unit = {
    getSpotRequests.map{prettify}.foreach{println}
  }

  /**
    * Scala friendly getter for all spot requests. 
    * NOT instances, just requests FOR instances (i.e. a 
    * request that has or hasn't been fulfilled.)
    * @return : All spot instance requests, check their
    *         state for usages. (Active, fulfilled, cancelled, etc.)
    */
  def getSpotRequests = {
    ec2.describeSpotInstanceRequests().getSpotInstanceRequests.toSeq
  }


  def launchTestServer() {
    awaitSpotFulfillment()
    resetElasticIP
    println("Done")
  }

  def main(args: Array[String]): Unit = {
  }

  def resetElasticIP = {
    val ar = new AssociateAddressRequest(
      AWS.getRunningInstances.head.getInstanceId,"52.8.59.72")
    ec2.associateAddress(ar)
  }

  def awaitSpotFulfillment() = {
    var done = false
    while (!done) {
      val anyDone = getSpotRequests.exists{
        _.getState == "active"
      } && AWS.getRunningInstances.nonEmpty
      println("Any done? " + anyDone)
      done = anyDone
      getSpotRequests.foreach{
        z => println(z.getState)
      }
      Thread.sleep(10*1000)
    }

  }


  def ensureSG: Unit = Try {
    val sgs = ec2.describeSecurityGroups().getSecurityGroups
    if (!sgs.contains("fayalite")) {
      println("Creating security group")
      val csg = new CreateSecurityGroupRequest(
        "fayalite", "na")
      csg.withVpcId("vpc-a9757ccb")
      ec2.createSecurityGroup(csg)
      val ipp = new IpPermission()
      ipp.setFromPort(22)
      ipp.setToPort(22)
      ipp.setIpProtocol("TCP")
      val ac = new AuthorizeSecurityGroupIngressRequest("fayalite", List(ipp))
      ec2.authorizeSecurityGroupIngress(ac)
    }
  }

  def spot() = {

    val requestRequest = new RequestSpotInstancesRequest()
    requestRequest.setSpotPrice("0.10")
    requestRequest.setInstanceCount(Integer.valueOf(1))
    val launchSpecification = new LaunchSpecification()
    launchSpecification.setImageId(AppLauncher.ubuntu1404HVM)
    launchSpecification.setInstanceType("c4.xlarge")
    launchSpecification.setKeyName("fa")

    // Add the security group to the request.
    val securityGroups = new util.ArrayList[String]()

    val vpc = ec2.describeVpcs().getVpcs.map{
      q =>
        q.getVpcId
    }
    println("VPC ids " + vpc.toList)

    ensureSG

    securityGroups.add("fayalite")
    launchSpecification.setSecurityGroups(securityGroups)

    // Add the launch specifications to the request.
    requestRequest.setLaunchSpecification(launchSpecification)

    // Call the RequestSpotInstance API.
    val requestResult = ec2.requestSpotInstances(requestRequest)

  }

}
