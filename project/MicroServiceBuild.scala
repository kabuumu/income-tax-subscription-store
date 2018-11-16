import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "income-tax-subscription-store"

  override lazy val appDependencies: Seq[ModuleID] = compile ++ test() ++ integrationTest()

  private val domainVersion = "5.2.0"
  private val bootstrapVersion = "0.30.0"
  private val simpleReactiveMongoVersion = "7.3.0-play-26"
  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.5"
  private val pegdownVersion = "1.6.0"
  private val mockitoVersion = "2.7.17"
  private val scalaTestPlusVersion = "3.1.2"

  private val wiremockVersion = "2.19.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "simple-reactivemongo" % simpleReactiveMongoVersion
  )

  def test(scope: String = "test,it") = Seq(
    "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
    "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
    "org.pegdown" % "pegdown" % pegdownVersion % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.mockito" % "mockito-core" % mockitoVersion % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope
  )

  def integrationTest(scope: String = "it") = Seq(
    "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope
  )

}
