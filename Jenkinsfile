
pipeline {
    agent any

    environment {
        dockerImage_students = ""
        STUDENT_REGISTRY = "737971166371.dkr.ecr.us-east-1.amazonaws.com/studentmanagement"
        PROFILE = 'deploy'
        AWS_REGION = 'us-east-1'
        REGISTRY_CREDENTIALS = 'AWS-Access'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    parameters {
        string (name: 'LB_DOMAIN_NAME', defaultValue: 'a9fdea6df74814f8790f5fcb5f62f00a-434091433.us-east-1.elb.amazonaws.com', description: "Domain Name for the Ingress LoadBalancer.")
    }

    stages {
       stage('Set Environment Variable'){
            steps {
                script {
                    env.LB_DOMAIN_NAME = "${params.LB_DOMAIN_NAME}"

                }
            }
        }
        stage('Checkout & Environment Prep'){
            steps{
                script {
                    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: 'xterm']){
                        withCredentials([
                            [ $class: 'AmazonWebServicesCredentialsBinding',
                                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                                credentialsId: 'AWS-Access',

                            ]])
                        {
                            try {
                                    echo "Setting Up Jump Instance."
                                    sh ("""
                                            
                                            aws configure --profile ${PROFILE} set aws_access_key_id ${AWS_ACCESS_KEY_ID}
                                            aws configure --profile ${PROFILE} set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
                                            aws configure --profile ${PROFILE} set region ${AWS_REGION}
                                            export AWS_PROFILE=${PROFILE}
                                    """)
                            } catch (ex) {
                                echo 'Err: Build Failed with Error: ' + ex.toString()
                                currentBuild.result = "UNSTABLE"
                            }
                        }
                        
                    }
                }
            }
        }
        stage ('Docker Build'){
            parallel {
                
                stage("Build book Application"){
                    steps {
                        script {
                            
                            dockerImage_students =  docker.build("${STUDENT_REGISTRY}" + ":${env.BUILD_NUMBER}")
                            
                        }
                    }
                }
                
            }
        }
        
        stage("Push Book Application"){
                    steps {
                        script {
                            docker.withRegistry("https://" + "${STUDENTS_REGISTRY}", "ecr:us-east-1:" + "${REGISTRY_CREDENTIALS}") {
                                dockerImage_students.push()
                            }
                        }
                    }
        }
                
            
        

        stage('Deploy Image to Kubernetes'){
            steps {
                script{
                    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: 'xterm']){
                        withCredentials([
                            [ $class: 'AmazonWebServicesCredentialsBinding',
                                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                                credentialsId: 'AWS-Access',
                            ]]) {
                                dir("eks_cicd"){
                                    sh("""
                                    
                                    export STUDENT_REGISTRY=$STUDENT_REGISTRY
                                    
                                    export IMAGE_TAG=${env.BUILD_NUMBER}
                                    export DOMAIN_NAME=$LB_DOMAIN_NAME
                                    kubectl apply -f deployment.yaml
                                    envsubst < ./service.yaml | kubectl apply -f -
                                    envsubst < ./ingress.yaml | kubectl apply -f -
                                    
                                    """)
                                }
                            }
                    }
                }
            }
        } 
    }
}
