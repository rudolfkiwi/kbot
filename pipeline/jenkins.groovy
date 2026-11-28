pipeline {
    agent any
    
    parameters {
        choice(
            name: 'OS',
            choices: ['linux', 'darwin', 'windows'],
            description: 'Target operating system'
        )
        choice(
            name: 'ARCH',
            choices: ['amd64', 'arm64'],
            description: 'Target architecture'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip running tests'
        )
        booleanParam(
            name: 'SKIP_LINT',
            defaultValue: false,
            description: 'Skip running linter'
        )
    }

    environment {
        TARGETOS = "${params.OS}"
        TARGETARCH = "${params.ARCH}"
    }

    stages {
        stage("lint") {
            when {
                expression { !params.SKIP_LINT }
            }
            steps {
                echo 'LINT EXECUTION STARTED'
                sh 'make lint'
            }
        }
      
        stage("test") {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                echo 'TEST EXECUTION STARTED'
                sh 'make test'
            }
        }

        stage("build") {
            steps {
                echo 'BUILD EXECUTION STARTED'
                sh 'make build'
            }
        }

        stage("image") {
            when {
                expression { params.OS == 'linux' }
            }
            steps {
                script {
                    echo 'BUILD EXECUTION STARTED'
                    sh 'make image'
                }
            }
        }

        stage("push") {
            when {
                expression { params.OS == 'linux' }
            }
            steps {
                script {
                    docker.withRegistry( '', 'dockerhub' ) {
                    sh 'make push'
                    }
                }
            }
        }
    }
}
