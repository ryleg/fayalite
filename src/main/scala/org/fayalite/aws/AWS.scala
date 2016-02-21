package org.fayalite.aws

import fa._

import java.util

import com.amazonaws.ClientConfiguration
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.regions.{Region, ServiceAbbreviations, Regions}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.{LaunchSpecification, RequestSpotInstancesRequest, RunInstancesRequest}
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.rds.{AmazonRDSClient, AmazonRDS}

import scala.collection.JavaConversions._
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth._

import scala.io.Source
import scala.util.Try



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
    Source.fromFile("rootkey.csv")
      .mkString.split("\r\n").map{_.split("=").tail.mkString("=")} match {
      case Array(x,y) => (x,y)}
  }

  // Working credentials
  val (access, secret) = getKeys
  val credentials =  new BasicAWSCredentials(access, secret) // new DefaultAWSCredentialsProviderChain()
  val cred = new StaticCredentialsProvider(credentials)

  /**
    * Quick and dirty check
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

  def main(args: Array[String]) {
   testDescribeInstances()
  //  getKeys
  }

  def spot() = {

    val requestRequest = new RequestSpotInstancesRequest()
    requestRequest.setSpotPrice("0.10");
    requestRequest.setInstanceCount(Integer.valueOf(1))
    val launchSpecification = new LaunchSpecification()
    launchSpecification.setImageId(AppLauncher.ubuntu1404HVM)
    launchSpecification.setInstanceType("c4.large")

    // Add the security group to the request.
    val securityGroups = new util.ArrayList[String]()
    securityGroups.add("super-permissive")
    launchSpecification.setSecurityGroups(securityGroups)

    // Add the launch specifications to the request.
    requestRequest.setLaunchSpecification(launchSpecification)

    // Call the RequestSpotInstance API.
    val requestResult = ec2.requestSpotInstances(requestRequest)


  }

}
