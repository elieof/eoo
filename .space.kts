/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, refer to https://www.jetbrains.com/help/space/automation.html
*/

job("Build and run tests") {
   container("openjdk:11") {
        env["USERNAME"] = Params("github_username")
        env["PASSWORD"] = Secrets("github_token")
        env["MAVEN_USERNAME"] = Params("ossrh_username")
        env["MAVEN_PASSWORD"] = Secrets("ossrh_password")
        env["SIGNING_KEY"] = Params("ossrh_signing_key_1") + Params("ossrh_signing_key_2") + Params("ossrh_signing_key_3") +
       		Params("ossrh_signing_key_4") + Params("ossrh_signing_key_5") + Params("ossrh_signing_key_6") +
       		Params("ossrh_signing_key_7") + Params("ossrh_signing_key_8")
        env["SIGNING_PASSWORD"] = Secrets("ossrh_signing_password")
       kotlinScript { api ->
           api.gradlew("build publish")
       }
   }
}
