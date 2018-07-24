import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "income-tax-subscription-store"

  override lazy val appDependencies: Seq[ModuleID] = compile ++ test() ++ integrationTest()

  private val domainVersion = "5.2.0"
  private val bootstrapVersion = "1.7.0"
  private val reactiveMongoVersion = "6.2.0"

  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.1"
  private val pegdownVersion = "1.6.0"
  private val mockitoVersion = "2.7.17"
  private val scalaTestPlusVersion = "2.0.0"

  private val wiremockVersion = "2.5.1"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "play-reactivemongo" % reactiveMongoVersion
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
