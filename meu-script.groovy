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
                    def routerIP = '192.168.100.1'
                    def routerUsername = 'root'
                    def routerPassword = 'admin'

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
                            to: 'wallaceseles00@gmail.com'
                }
            }
        }


    post {
        always {
            // Etapa opcional para executar ações sempre após o pipeline, como limpar recursos temporários.
        }
    }
}
