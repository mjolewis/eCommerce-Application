pipeline {
    agent any
    triggers {
        pollSCM '* * * * *' // 5 stars means poll the scm every minute
    }
    tools {
        maven 'Maven 3.6.3'
    }
    options {
        skipStagesAfterUnstable()
    }
    stages { // Continuous Integration phase
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Build') { // The Continuous Delivery phase
            steps {
                    sh 'mvn -B -Dmaven.clean.skip=true -DskipTests package'
            }
            post { // If the maven build succeeded, archive the jar file
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
        stage('Deploy') { // The Continuous Deployment phase
            steps {
                echo "TODO DEPLOY TO AWS"
                //sh 'mvn -DskipTests deploy'
            }
        }
    }
   post {
        always {
            echo 'Test, Build, and Deploy stages have finished'
        }
        success {
            echo 'SUCCESS: eCommerce-Pipeline completed successfully'
        }
        failure {
            echo 'FAILURE: eCommerce-Pipeline failed'
        }
        unstable {
            echo 'This will run only if the run was marked as unstable'
        }
        changed {
            echo 'This will run only if the state of the Pipeline has changed'
            echo 'For example, if the Pipeline was previously failing but is now successful'
        }
   }
}