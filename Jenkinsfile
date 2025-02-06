pipeline {
	agent any

	triggers {
		githubPush()
	}

	stages {
		stage('Checkout') {
			steps {
				git branch: 'main', credentialsId: 'github-credentials', url: 'https://github.com/your-username/your-repository.git'
			}
		}

		stage('Build') {
			steps {
				sh './gradlew clean buildJar'
			}
		}

		stage('Docker Build') {
			steps {
				sh './gradlew dockerBuild'
			}
		}

		stage('Push to DockerHub') {
			steps {
				withDockerRegistry([credentialsId: 'docker-hub-credentials', url: '']) {
					sh 'docker tag my-app:1.0.0 username/my-app:latest'
					sh 'docker push username/my-app:latest'
				}
			}
		}

		stage('Deploy to Kubernetes') {
			steps {
				sh 'kubectl apply -f k8s/deployment.yml'
			}
		}
	}
}
