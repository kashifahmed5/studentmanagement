pipeline {
    agent any

    environment {
        dockerImage_books = ""
        BOOK_REGISTRY = "737971166371.dkr.ecr.us-east-1.amazonaws.com/studentmanagment"
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

   

    stages {
       
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
                            
                            dockerImage_books =  docker.build("${BOOK_REGISTRY}" + ":${env.BUILD_NUMBER}")
                            
                        }
                    }
                }
                
            }
        }
        
        stage("Push Book Application"){
                    steps {
                        script {
                            docker.withRegistry("https://" + "${BOOK_REGISTRY}", "ecr:us-east-1:" + "${REGISTRY_CREDENTIALS}") {
                                dockerImage_books.push()
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
                                dir("k8s"){
                                    sh("""
                                    
                                    export BOOK_REGISTRY=$BOOK_REGISTRY
                                    
                                    export IMAGE_TAG=${env.BUILD_NUMBER}
                                    kubectl apply -f namespace.yaml
                                    envsubst < ./deployment.yaml | kubectl apply -f -
                                    envsubst < ./service.yaml | kubectl apply -f -
                                    envsubst < ./ingress.yaml | kubectl apply -f -
                                    sleep 60
                                    kubectl get svc -n demo
                                    """)
                                }
                            }
                    }
                }
            }
        } 
    }
}
