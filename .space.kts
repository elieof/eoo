/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, refer to https://www.jetbrains.com/help/space/automation.html
*/

job("Build and run tests") {
   container("openjdk:11") {
        env["USERNAME"] = Secrets("github_username")
        env["PASSWORD"] = Secrets("github_token")
       kotlinScript { api ->
           api.gradlew("build publishAllPublicationsToGitHubPackagesRepository")
       }
   }
}
