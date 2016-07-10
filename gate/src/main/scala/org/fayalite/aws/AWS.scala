package org.fayalite.aws

import java.util

import com.amazonaws._
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Region, Regions, ServiceAbbreviations}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.rds.{AmazonRDS, AmazonRDSClient}

import scala.collection.JavaConversions
import scala.collection.JavaConversions._
import com.amazonaws.auth.{DefaultAWSCredentialsProviderChain, EnvironmentVariableCredentialsProvider}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth._
import com.amazonaws.event.{ProgressEvent, ProgressEventType}

import scala.io.Source
import scala.util.{Random, Try}
import JavaConversions._


/**
 * Main interaction gateway for AWS. Sort of works? Maybe?
 */
object AWS {

  /**
    * Some BS for getting around AWS SDK key detection failures
    *
    * @return : Access -> Secret
    */
  def getKeys = {
    Source.fromFile(".rootkey.csv")
      .mkString.split("\r\n").map{_.split("=").tail.mkString("=")} match {
      case Array(x,y) => (x,y)}
  }

  // Working credentials
  val (access, secret) = getKeys
  val credentials =  new BasicAWSCredentials(access, secret) // new DefaultAWSCredentialsProviderChain()
  val cred = new StaticCredentialsProvider(credentials)

  /**
    * Quick and dirty check
    *
    * @return : EC2 should be online
    */
  def verifyAuthenticated = {
    val supported = Region.getRegion(Regions.US_WEST_1).isServiceSupported(ServiceAbbreviations.EC2)
    supported
  }

  val clientConfig = new ClientConfiguration()

  val ec2 =  Region.getRegion(Regions.US_WEST_1).createClient(
    classOf[AmazonEC2Client], cred, clientConfig)

  val s3 = new AmazonS3Client(credentials)

  val rds = new AmazonRDSClient(credentials)

  val elb = new AmazonElasticLoadBalancingClient(credentials)


  def testDescribeInstances() = {
    val inst = ec2.describeInstances()
    inst.getReservations.foreach{
      _.getInstances.foreach{
        i => println(i.getLaunchTime)
      }
    }
    ServerManager.requestServerInfo()
  }

  def instances = ec2.describeInstances().getReservations.toList.flatMap{
    _.getInstances
  }
  def instanceIds = instances.map{_.getInstanceId}

  def destroyInstances = {
    val tr = new TerminateInstancesRequest(instanceIds)
    ec2.terminateInstances(tr)
  }

  def main(args: Array[String]) {
   //testDescribeInstances()
  //checkSpotRequests()
   // destroyInstances
  //  getKeys
    spot()
    launchTestServer
   // destroyInstances
  }

  def launchTestServer: Unit = {
    awaitSpotFulfillment()
    resetElasticIP
    println("Done")
  }

  def resetElasticIP = {
    val ar = new AssociateAddressRequest(
      getRunningInstances.head.getInstanceId,"52.8.59.72")
    ec2.associateAddress(ar)
  }

  def getRunningInstances = {
    ec2.describeInstances()
      .getReservations
      .flatMap {
        _.getInstances
      }
      .filter {
        _.getState.getName == "running"
      }.toList
  }

  def checkSpotRequests(): Unit = {
    getSpotRequests.foreach {
      q =>
        println(
          q.getType + " @ " +
          q.getSpotPrice + " " +
          q.getState + " " +
          q.getStatus.getMessage
        )
    }
  }

  def getSpotRequests = {
    ec2.describeSpotInstanceRequests().getSpotInstanceRequests.toSeq
  }

  def awaitSpotFulfillment() = {
    var done = false
    while (!done) {
      val anyDone = getSpotRequests.exists{
        _.getState == "active"
      } && getRunningInstances.nonEmpty
      println("Any done? " + anyDone)
      done = anyDone
      getSpotRequests.foreach{
        z => println(z.getState)
      }
      Thread.sleep(10*1000)
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
}
