import sys
from netmiko import ConnectHandler

# Recebendo os argumentos passados pela pipeline
router_ip = sys.argv[1]
username = sys.argv[2]
password = sys.argv[3]

# Configurando o roteador
router = {
    "device_type": "cisco_ios",
    "ip": router_ip,
    "username": username,
    "password": password,
}

# Conectar ao roteador e salvar as configurações
try:
    with ConnectHandler(**router) as net_connect:
        output = net_connect.send_command("show running-config")
        with open("router_config.txt", "w") as f:
            f.write(output)
    print("Configurações salvas com sucesso.")
except Exception as e:
    print("Erro ao salvar as configurações:", str(e))
