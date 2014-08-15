name := """jooq"""

version := "1.0"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.31" % "jooq"

scalaVersion := "2.10.2"

seq(jooqSettings: _*)

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  //cache,
  //ws,
  "mysql" % "mysql-connector-java" % "5.1.31",
  "org.jooq" % "jooq" % "3.4.0",
  "org.jooq" % "jooq-meta" % "3.4.0",
  "org.jooq" % "jooq-scala" % "3.4.0",
  "org.jooq" % "jooq-codegen" % "3.4.0",
  "javax.persistence" % "persistence-api" % "1.0.2",
  "javax.validation" % "validation-api" % "1.1.0.Final",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.3"
) //map {_.withSources().withJavadoc()}


jooqVersion := "3.4.0"

jooqOptions := Seq(
  "jdbc.driver" -> "com.mysql.jdbc.Driver",
  "jdbc.url" -> "jdbc:mysql://dbserver:3306/test",
  "jdbc.user" -> "ebadmin",
  "jdbc.password" -> "111111",
  "generator.database.name" -> "org.jooq.util.mysql.MySQLDatabase",
  "generator.database.inputSchema" -> "test",
  "generator.target.packageName" -> "imadz.model.gen",
  "generator.generate.pojos" -> "true",
  "generator.generate.daos" -> "true",
  "generator.generate.jpaAnnotations" -> "true",
  "generator.generate.validationAnnotations" -> "true",
  "generator.generate.generatedAnnotation" -> "true"
)

// Uncomment to use Akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.3"

libraryDependencies += "net.imadz" %% "perf-test-lib" % "1.0"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.6"

libraryDependencies += "log4j" % "log4j" % "1.2.13"

