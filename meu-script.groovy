pipeline {
    agent any

    stages {
        stage('Configuração do Ambiente') {
            steps {
                // Etapa de preparação do ambiente, como instalação de dependências, se necessário.
            }
        }

        stage('Backup do Roteador') {
            steps {
                script {
                    // Definir as informações do roteador
                    def routerIP = 'IP_DO_ROTEADOR'
                    def routerUsername = 'USUARIO_DO_ROTEADOR'
                    def routerPassword = 'SENHA_DO_ROTEADOR'

                    // Comando para executar o backup no roteador via SSH
                    def backupCommand = "sshpass -p '${routerPassword}' ssh ${routerUsername}@${routerIP} 'show running-config' > backup_router_$(date +%Y%m%d%H%M%S).cfg"

                    // Executar o comando de backup no roteador
                    sh returnStatus: true, script: backupCommand
                }
            }
        }

        stage('Notificação') {
            steps {
                // Etapa para notificar sobre o resultado do backup ou lidar com erros
                script {
                    def subject
                    def body

                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        subject = "Notificação de Falha - Pipeline de Backup do Roteador"
                        body = "O pipeline falhou. Verifique o log para detalhes."
                    }
                    success {
                        subject = "Notificação de Sucesso - Pipeline de Backup do Roteador"
                        body = "O pipeline foi executado com sucesso."

                        // Arquivar o backup gerado para armazená-lo no Jenkins
                        archiveArtifacts artifacts: 'backup_router*.cfg', fingerprint: true
                    }

                    emailext body: body,
                            subject: subject,
                            to: 'seu-email@exemplo.com'
                }
            }
        }

        stage('Enviar para o GitHub') {
            steps {
                // Fazendo o commit do arquivo de backup e enviando para o GitHub
                sh 'git config --global user.email "jenkins@example.com"'
                sh 'git config --global user.name "Jenkins"'
                sh 'git add backup_router*.cfg'
                sh 'git commit -m "Atualizar backup do roteador [Jenkins Build]"'
                sh 'git push origin master'
            }
        }
    }

    post {
        always {
            // Etapa opcional para executar ações sempre após o pipeline, como limpar recursos temporários.
        }
    }
}
