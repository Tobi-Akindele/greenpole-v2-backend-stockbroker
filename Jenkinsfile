pipeline {
    agent any 
    stages {
        
        stage('This is Docker Build') {
            environment {
                    BUILD_NUMBER = "${env.BUILD_NUMBER}"
                }
            steps {
                script {
                    sshagent(['jenkins']) {
                        sh '''
                            if sshpass -p admin002  sudo docker build -t greenpole.azurecr.io/greenpole-stockbroker:latest -f Dockerfile . ; then
                                echo "Docker Build Successful";

                            else
                                echo "We are in error";
                                echo "Docker Build Unsuccessful"; exit 1;
                            fi
                            '''
                    }
                }
            }
        }
        stage ('Integration Testing'){
            environment {
                    BUILD_NUMBER = "${env.BUILD_NUMBER}"
                }
            steps {
                script {
                    sshagent(['jenkins']) {
                        containerID = sh (
                                        script: "sshpass -p admin002  sudo docker run -d --name greenpole-stockbroker -e SPRING_DATASOURCE_URL=jdbc:postgresql://104.40.226.227:5432/greenpole -e SPRING_DATASOURCE_PASSWORD=abi_step1ola -e SPRING_DATASOURCE_USERNAME=abiola002 -p 8189:8086 greenpole.azurecr.io/greenpole-stockbroker:latest", 
                                    returnStdout: true
                                    ).trim()
                        
                            if  ( !containerID ){

                                echo "We are in error";
                                echo "Container ID is ==> ${containerID}"
                                sh "sshpass -p admin002  sudo docker rm ${containerID}"
                                echo "Docker Build Unsuccessful"; exit 1;
                              }  
                            else{     
                                        echo "Container ID is ==> ${containerID}"
                                        //sh "sshpass -p admin002  sudo docker cp ${containerID}:/TestResults/test_results.xml test_results.xml"
                                        sh "sshpass -p admin002  sudo docker stop ${containerID}"
                                        sh "sshpass -p admin002  sudo docker rm ${containerID}"
                            }
                            
                    }
                }
            }
        }
        stage ('Docker push to ACR'){
            environment {
                    BUILD_NUMBER = "${env.BUILD_NUMBER}"
                }
            steps {
                script {
                    sshagent(['jenkins']) {
                                        sh 'sshpass -p admin002  sudo docker login greenpole.azurecr.io'
                                        push_output = sh (
                                                        script: "sshpass -p admin002  sudo docker push  greenpole.azurecr.io/greenpole-stockbroker:latest", 
                                                    returnStdout: true
                                                    ).trim()
                                        echo "This is push output ==> ${push_output}"
                    }
                }
            }
        }

        stage ('Jenkins run kubernetes'){
            steps{
                sh 'chmod a+x jenkins/test.sh'
                sh './jenkins/test.sh'
            }
        }
        
    }
    post {
        // always {
            
        // }
        success {
            script {
                if (currentBuild.currentResult == 'SUCCESS') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT', 
                        replyTo: '$DEFAULT_REPLYTO',
                        to: 'backend-developer@africaprudential.com'
                }
            }
        }
        failure {
            script {
                if (currentBuild.currentResult == 'FAILURE') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        replyTo: '$DEFAULT_REPLYTO',
                        to: 'backend-developer@africaprudential.com'
                }
            }
        }
        unstable {
            script {
                if (currentBuild.currentResult == 'UNSTABLE') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        replyTo: '$DEFAULT_REPLYTO',
                        to: 'backend-developer@africaprudential.com'
                }
            }
        }
        changed {
            script {
                if (currentBuild.currentResult == 'FAILURE') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        replyTo: '$DEFAULT_REPLYTO',
                        to: 'backend-developer@africaprudential.com'
                }
            }
        }
    }
}
