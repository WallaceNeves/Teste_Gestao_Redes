import paramiko
import datetime

# Definir as informações do roteador
routerIP = '192.168.100.1'
routerUsername = 'root'
routerPassword = 'admin'  # Substitua pela senha correta

# Criar uma conexão SSH com o roteador
ssh_client = paramiko.SSHClient()
ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

try:
    ssh_client.connect(routerIP, username=routerUsername, password=routerPassword)

    # Executar o comando para obter o backup da configuração
    stdin, stdout, stderr = ssh_client.exec_command('show running-config')

    # Ler o resultado do comando
    output = stdout.read().decode()

    # Data e hora para nomear o arquivo de backup
    data_hora = datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
    nome_arquivo = f'backup_router_{data_hora}.cfg'

    # Salvar o backup em um arquivo local
    with open(nome_arquivo, 'w') as arquivo:
        arquivo.write(output)

    print(f'Backup do roteador salvo em {nome_arquivo}')
except paramiko.AuthenticationException:
    print('Falha na autenticação. Verifique as credenciais.')
except paramiko.SSHException as e:
    print(f'Erro SSH: {e}')
finally:
    ssh_client.close()
