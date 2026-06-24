# =====================================================================
#  Script de teste do PetFriends
#  Dispara os eventos e mostra os logs visuais (emojis) de volta.
#
#  IMPORTANTE: suba os servicos ANTES (so 1 vez):
#     docker compose up -d --build
#  Depois rode este script quantas vezes quiser:
#     .\testar.ps1            -> testa os dois
#     .\testar.ps1 almox      -> testa so o Almoxarifado
#     .\testar.ps1 transp     -> testa so o Transporte
# =====================================================================

param(
    [string]$alvo = "ambos"
)

function Disparar($nome, $url, $servicoDocker) {
    Write-Host ""
    Write-Host "==================================================" -ForegroundColor Cyan
    Write-Host " 📤 Disparando teste: $nome" -ForegroundColor Cyan
    Write-Host "==================================================" -ForegroundColor Cyan

    try {
        $resposta = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 10
        Write-Host " Resposta HTTP: $resposta" -ForegroundColor Green
    }
    catch {
        Write-Host " ❌ Nao consegui chamar $url" -ForegroundColor Red
        Write-Host "    Os servicos estao rodando? (docker compose up -d --build)" -ForegroundColor Yellow
        return
    }

    # da um tempinho pro evento ser processado de forma assincrona
    Start-Sleep -Seconds 2

    Write-Host ""
    Write-Host " 📜 Ultimos logs visuais do '$servicoDocker':" -ForegroundColor Magenta
    Write-Host "--------------------------------------------------"
    docker logs --tail 15 $servicoDocker
    Write-Host "--------------------------------------------------"
}

if ($alvo -eq "ambos" -or $alvo -eq "almox") {
    Disparar "Pedido Confirmado -> Almoxarifado" `
             "http://localhost:8081/teste/confirmar-pedido" `
             "petfriends-almoxarifado"
}

if ($alvo -eq "ambos" -or $alvo -eq "transp") {
    Disparar "Pedido Despachado -> Transporte" `
             "http://localhost:8082/teste/despachar-pedido" `
             "petfriends-transporte"
}

Write-Host ""
Write-Host "✅ Teste finalizado!" -ForegroundColor Green
