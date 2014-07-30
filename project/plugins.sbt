resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "sean8223 Releases" at "https://github.com/sean8223/repository/raw/master/releases"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.2")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.0.0")

// jooq plugins
addSbtPlugin("sean8223" %% "jooq-sbt-plugin" % "1.4")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")