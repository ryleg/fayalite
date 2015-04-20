package org.fayalite.aws

/**
 * Created by ryle on 11/29/2014.
 */

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


object AWS {

  def getKeys = {
    Source.fromFile("/Users/ryle/rootkey.csv")
      .mkString.split("\r\n").map{_.split("=").tail.mkString("=")} match {
      case Array(x,y) => (x,y)}
  }

  val (access, secret) = getKeys

  val credentials =  new BasicAWSCredentials(access, secret) // new DefaultAWSCredentialsProviderChain()
  val cred = new StaticCredentialsProvider(credentials)

  val supported = Region.getRegion(Regions.US_EAST_1).isServiceSupported(ServiceAbbreviations.EC2)
  println(supported)

  val clientConfig = new ClientConfiguration()

  val ec2 =  Region.getRegion(Regions.US_EAST_1).createClient(
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

    val requestRequest = new RequestSpotInstancesRequest();

    // Request 1 x t1.micro instance with a bid price of $0.03.
    requestRequest.setSpotPrice("0.03");
    requestRequest.setInstanceCount(Integer.valueOf(1));

    // Setup the specifications of the launch. This includes the
    // instance type (e.g. t1.micro) and the latest Amazon Linux
    // AMI id available. Note, you should always use the latest
    // Amazon Linux AMI id or another of your choosing.
    val launchSpecification = new LaunchSpecification();
    launchSpecification.setImageId("ami-8c1fece5");
    launchSpecification.setInstanceType("t1.micro");

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
