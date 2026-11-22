# Servidor Java

Projeto independente que hospedará a API e o intermediário próprio. Requer JDK 17 ou superior, sem dependências externas neste esqueleto.

```text
src/votacao/
  api/          ServidorHttp e os três objetos expostos pela API
  servico/      Intermediario: eleições, assinaturas e distribuição de eventos
  modelo/       Entidades e eventos
```

ServidorHttp utilizará com.sun.net.httpserver.HttpServer, a biblioteca HTTP do JDK.

VotacoesApi, AssinaturasApi e ComunicadosApi receberão requisições HTTP, converterão JSON e invocarão o mesmo Intermediario. Esse objeto organizará os eventos conforme as assinaturas. A consulta de eventos será feita pelos clientes via HTTP, sem callback para endereços de clientes.

Compile em PowerShell, dentro de servidor/:

```powershell
New-Item -ItemType Directory -Force build
javac -encoding UTF-8 --add-modules jdk.httpserver -d build (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
```

A entrada prevista é votacao.api.ServidorHttp. Ainda não inicia um servidor funcional. A compilação valida apenas o esqueleto; os métodos da API e do intermediário não estão implementados.
