package org.fayalite.aws

import com.amazonaws.services.ec2.model._
import com.amazonaws.services.rds.model.{Tag, CreateDBInstanceRequest}

import scala.collection.JavaConversions
import JavaConversions._

object RDS {

  def getDB() = {
    AWS.rds.describeDBInstances().getDBInstances.foreach{
      q => println(q.getDBName)
    }
  }

  def main(args: Array[String]) {
//    ensureSubnet()
    makeDB()
    getDB()
  }

  val vpcCidr = "10.0.0.0/16"

  def nameTag(name: String) = {
    val t = new com.amazonaws.services.ec2.model.Tag().withKey("Name").withValue(name)
    t
  }

  val pubSubCidr = "10.0.1.0/24"
  def ensureSubnet() = {
/*    val cvr = new CreateVpcRequest()
    cvr.setCidrBlock(vpcCidr)
    cvr.setInstanceTenancy("default")

    val vpc = AWS.ec2.createVpc(cvr)
    vpc.setVpc(vpc.getVpc.withTags(List(nameTag("fayalite-vpc"))))    */

    val vpcId = AWS.ec2.describeVpcs().getVpcs.filter{_.getCidrBlock == vpcCidr}.head.getVpcId
    //val vpid = vpc.getVpc.getVpcId
/*
    val csr = new CreateSubnetRequest()
    csr.setAvailabilityZone("us-east-1c")
    csr.setVpcId(vpcId)
    csr.setCidrBlock(pubSubCidr)
    val subnet = AWS.ec2.createSubnet(csr)*/

    val subnetId = AWS.ec2.describeSubnets().getSubnets.filter{_.getCidrBlock == pubSubCidr
    }.head.getSubnetId
/*
    val csg = new CreateSecurityGroupRequest().withVpcId(vpcId)
    csg.setDescription("fayalite database should only have port open to other groups")
    csg.setGroupName("fayalite-db")
    AWS.ec2.createSecurityGroup(csg)
    */
/*val csg = new CreateSecurityGroupRequest().withVpcId(vpcId)
    csg.setDescription("fayalite app open to world.")
    csg.setGroupName("fayalite-app")
   val appSgId =  AWS.ec2.createSecurityGroup(csg).getGroupId
    println("appsgid " + appSgId)*/

    val appSgId = AWS.ec2.describeSecurityGroups().getSecurityGroups
      .filter{_.getGroupName == "fayalite-app"}.head.getGroupId

    val dbSgId = AWS.ec2.describeSecurityGroups().getSecurityGroups
      .filter{_.getGroupName == "fayalite-db"}.head.getGroupId
    println(appSgId + " " + dbSgId)

    val all = "0.0.0.0/0"
    val yo = {
      val ipPermission =  new IpPermission()
      val ipr = ipPermission.withIpRanges(all).withIpProtocol("tcp").withFromPort(22)
        .withToPort(22)
      val ipr2 = ipPermission.withIpRanges(all).withIpProtocol("tcp").withFromPort(80)
        .withToPort(80)
      val ipr3 = ipPermission.withIpRanges(vpcCidr).withIpProtocol("tcp").withFromPort(0)
        .withToPort(60000)
      val authorizeSecurityGroupIngressRequest =
        new AuthorizeSecurityGroupIngressRequest()
     //     .with
          .withSourceSecurityGroupOwnerId(dbSgId)
          .withGroupId(appSgId)//.withSourceSecurityGroupName(dbSgId)
      AWS.ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest)
      //        .withIpPermissions(ipr, ipr2, ipr3)
    }

    val ipPermission =  new IpPermission()
    val ipr = ipPermission.withIpRanges(vpcCidr).withIpProtocol("tcp").withFromPort(0)
      .withToPort(60000)
    val authorizeSecurityGroupIngressRequest =
      new AuthorizeSecurityGroupIngressRequest().withGroupName(dbSgId)
      .withIpPermissions(ipPermission).withSourceSecurityGroupName(appSgId)

    AWS.ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest)


  }
    def makeDB() = { // dbname fayalite
  val dbr = new CreateDBInstanceRequest()
  dbr.setAllocatedStorage(5)
  dbr.setAvailabilityZone("us-east-1c")
  dbr.setBackupRetentionPeriod(1)
  dbr.setDBInstanceClass("db.t1.micro")
  dbr.setDBName("fayalite")
  dbr.setDBInstanceIdentifier("fayalite-db")
  dbr.setEngine("MySQL")
  dbr.setMasterUsername("master")
  dbr.setVpcSecurityGroupIds(List("app-db-vpc"))
  dbr.setMasterUserPassword("fayalitedbmasterpassword")
  dbr.setPubliclyAccessible(false)
  dbr.setMultiAZ(false)
  dbr.setRequestCredentials(AWS.credentials)
  dbr.setDBSubnetGroupName("fayalite-subnet")

  val t = new Tag()
  t.setKey("Name") ;t.setValue("fayalite-db-tag")
  dbr.setTags(List(t))

  AWS.rds.createDBInstance(dbr)

    }

}
