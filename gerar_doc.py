#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import base64, os
from PIL import Image
import io

SCREENSHOTS_DIR = r"D:\MonitorFan\screenshots"
OUTPUT = r"D:\MonitorFan\documentacao.html"

def img_b64(name, max_width=240, quality=72):
    path = os.path.join(SCREENSHOTS_DIR, f"{name}.png")
    if not os.path.exists(path):
        return None
    img = Image.open(path)
    ratio = max_width / img.width
    new_h = int(img.height * ratio)
    img = img.resize((max_width, new_h), Image.LANCZOS)
    if img.mode in ("RGBA", "P"):
        img = img.convert("RGB")
    buf = io.BytesIO()
    img.save(buf, format="JPEG", quality=quality, optimize=True)
    b64 = base64.b64encode(buf.getvalue()).decode()
    size_kb = len(buf.getvalue()) // 1024
    print(f"  {name}: {size_kb}KB")
    return f"data:image/jpeg;base64,{b64}"

def screen_img(name):
    src = img_b64(name)
    if src:
        return f'<div class="screen-img-wrap"><img src="{src}" class="screen-img" alt="{name}"></div>'
    return '<div class="screen-img-wrap"><div class="screen-placeholder">Screenshot pendente</div></div>'

print("Gerando imagens...")
screens = {
    "login": screen_img("login"),
    "cadastro": screen_img("cadastro"),
    "esqueceu_senha": screen_img("esqueceu_senha"),
    "home": screen_img("home"),
    "feed": screen_img("feed"),
    "detalhe_duvida": screen_img("detalhe_duvida"),
    "nova_duvida": screen_img("nova_duvida"),
    "perfil": screen_img("perfil"),
    "admin_usuarios": screen_img("admin_usuarios"),
    "admin_monitorias": screen_img("admin_monitorias"),
}

html = f"""<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Documentação — MonitorFan</title>
<style>
* {{ margin:0; padding:0; box-sizing:border-box; }}
body {{ font-family:'Segoe UI',Arial,sans-serif; background:#f4f6fb; color:#1e2a3a; font-size:14px; }}
.cover {{ background:linear-gradient(135deg,#1a2540 0%,#1e3a6e 100%); color:white; padding:80px 60px; min-height:100vh; display:flex; flex-direction:column; justify-content:center; page-break-after:always; }}
.cover-logo {{ font-size:56px; font-weight:900; margin-bottom:12px; }}
.cover-logo span {{ color:#f17535; }}
.cover-subtitle {{ font-size:20px; color:#8fa3c0; margin-bottom:48px; }}
.cover-desc {{ font-size:16px; line-height:1.8; color:#c5d4e8; max-width:600px; margin-bottom:60px; }}
.cover-meta {{ font-size:13px; color:#6a85a8; border-top:1px solid #2e4a72; padding-top:20px; }}
.cover-meta span {{ margin-right:32px; }}
.page {{ padding:48px 60px; max-width:960px; margin:0 auto; }}
h1 {{ font-size:28px; font-weight:800; color:#1a2540; border-bottom:3px solid #f17535; padding-bottom:10px; margin-bottom:28px; margin-top:48px; }}
h1:first-child {{ margin-top:0; }}
h2 {{ font-size:20px; font-weight:700; color:#1e3a6e; margin:32px 0 14px; }}
h3 {{ font-size:15px; font-weight:700; color:#f17535; margin:22px 0 8px; text-transform:uppercase; letter-spacing:.5px; }}
p {{ line-height:1.7; margin-bottom:10px; color:#374558; }}
.badge {{ display:inline-block; padding:3px 10px; border-radius:20px; font-size:11px; font-weight:700; text-transform:uppercase; }}
.badge-admin {{ background:#fde8d8; color:#c04a00; }}
.badge-monitor {{ background:#dce8ff; color:#1e3a6e; }}
.badge-professor {{ background:#fff3d8; color:#8a5c00; }}
.badge-aluno {{ background:#e8f5e9; color:#2e6b3e; }}
.screen-card {{ background:white; border-radius:12px; border:1px solid #dde5f0; padding:24px 28px; margin-bottom:20px; box-shadow:0 2px 8px rgba(0,0,0,.04); page-break-inside:avoid; display:flex; gap:24px; align-items:flex-start; }}
.screen-body {{ flex:1; min-width:0; }}
.screen-header {{ display:flex; align-items:center; gap:14px; margin-bottom:14px; }}
.screen-icon {{ width:44px; height:44px; background:linear-gradient(135deg,#f17535,#1e3a6e); border-radius:10px; display:flex; align-items:center; justify-content:center; color:white; font-size:20px; flex-shrink:0; }}
.screen-title {{ font-size:18px; font-weight:700; color:#1a2540; }}
.screen-file {{ font-size:11px; color:#8fa3c0; font-family:monospace; }}
.screen-desc {{ color:#5a6a7e; line-height:1.7; margin-bottom:14px; }}
.screen-access {{ display:flex; gap:6px; flex-wrap:wrap; margin-bottom:10px; }}
.screen-img-wrap {{ flex-shrink:0; }}
.screen-img {{ width:160px; border-radius:16px; border:2px solid #2e4a72; box-shadow:0 8px 24px rgba(0,0,0,.18); display:block; }}
.screen-placeholder {{ width:160px; height:200px; background:#1a2540; border-radius:16px; display:flex; align-items:center; justify-content:center; color:#8fa3c0; font-size:11px; text-align:center; padding:10px; }}
.feature-list {{ list-style:none; padding:0; }}
.feature-list li {{ padding:6px 0 6px 22px; position:relative; color:#374558; border-bottom:1px solid #f0f4fa; font-size:13px; }}
.feature-list li:last-child {{ border-bottom:none; }}
.feature-list li::before {{ content:"→"; position:absolute; left:0; color:#f17535; font-weight:700; }}
.collection-card {{ background:white; border-radius:12px; border:1px solid #dde5f0; padding:24px 28px; margin-bottom:20px; box-shadow:0 2px 8px rgba(0,0,0,.04); page-break-inside:avoid; }}
.collection-name {{ font-size:16px; font-weight:700; color:#1a2540; font-family:monospace; background:#f0f4fa; padding:4px 10px; border-radius:6px; display:inline-block; margin-bottom:14px; }}
table {{ width:100%; border-collapse:collapse; margin-top:10px; }}
th {{ background:#1a2540; color:white; padding:10px 14px; text-align:left; font-size:12px; text-transform:uppercase; letter-spacing:.5px; }}
td {{ padding:10px 14px; border-bottom:1px solid #edf0f7; font-size:13px; color:#374558; }}
tr:last-child td {{ border-bottom:none; }}
tr:nth-child(even) td {{ background:#f8fafc; }}
.type-string {{ color:#2e7d32; font-family:monospace; }}
.type-number {{ color:#1565c0; font-family:monospace; }}
.type-boolean {{ color:#6a1b9a; font-family:monospace; }}
.url-card {{ background:#1a2540; border-radius:10px; padding:18px 22px; margin-bottom:14px; page-break-inside:avoid; }}
.url-method {{ display:inline-block; background:#2e7d32; color:white; font-size:11px; font-weight:700; padding:2px 8px; border-radius:4px; font-family:monospace; margin-bottom:8px; }}
.url-path {{ font-family:monospace; font-size:13px; color:#7ec8e3; word-break:break-all; margin-bottom:6px; }}
.url-desc {{ font-size:12px; color:#8fa3c0; }}
.url-section {{ margin-bottom:28px; }}
.url-section-title {{ font-size:14px; font-weight:700; color:#f17535; margin-bottom:12px; text-transform:uppercase; letter-spacing:1px; }}
.arch-block {{ display:grid; grid-template-columns:1fr 1fr; gap:16px; margin:20px 0; }}
.arch-item {{ background:white; border-radius:10px; border:1px solid #dde5f0; padding:18px 20px; }}
.arch-item-title {{ font-size:13px; font-weight:700; color:#1e3a6e; margin-bottom:8px; }}
.arch-item-desc {{ font-size:12px; color:#5a6a7e; line-height:1.6; }}
.info-box {{ background:#e8f0fe; border-left:4px solid #1e3a6e; border-radius:0 8px 8px 0; padding:14px 18px; margin:16px 0; }}
.info-box p {{ color:#1e3a6e; margin:0; font-size:13px; }}
.page-break {{ page-break-before:always; }}
@media print {{
  body {{ background:white; }}
  .cover {{ min-height:auto; padding:60px 48px; }}
  .page {{ padding:32px 48px; }}
  .screen-img {{ width:130px; }}
}}
</style>
</head>
<body>

<div class="cover">
  <div class="cover-logo">Monitor<span>Fan</span></div>
  <div class="cover-subtitle">Plataforma de Monitorias Acadêmicas</div>
  <div class="cover-desc">
    O MonitorFan é um aplicativo Android desenvolvido com Jetpack Compose que conecta alunos, monitores e professores de uma instituição de ensino. A plataforma centraliza monitorias disponíveis por curso, oferece um feed de dúvidas com interação entre alunos e monitores/professores, e disponibiliza gestão administrativa de usuários e horários.
  </div>
  <div class="cover-meta">
    <span>📱 Android — Jetpack Compose</span>
    <span>☁️ Firebase Firestore + Auth</span>
    <span>🌐 Project ID: monitorfan-66f16</span>
    <span>📅 Junho 2026</span>
  </div>
</div>

<div class="page">
  <h1>1. Visão Geral</h1>
  <p>O <strong>MonitorFan</strong> é um app mobile para Android que digitaliza o processo de monitorias em instituições de ensino superior. Alunos podem consultar horários de monitoria e tirar dúvidas diretamente com monitores e professores do seu curso. A plataforma organiza o conteúdo por curso, garantindo que cada usuário veja apenas o que é relevante para ele.</p>

  <h2>Tecnologias</h2>
  <div class="arch-block">
    <div class="arch-item">
      <div class="arch-item-title">Interface</div>
      <div class="arch-item-desc">Jetpack Compose com Material 3. Navegação por HorizontalPager com BottomNavigationBar. Tema escuro com gradiente azul e laranja.</div>
    </div>
    <div class="arch-item">
      <div class="arch-item-title">Autenticação</div>
      <div class="arch-item-desc">Firebase Authentication com Email/Senha. Suporte a redefinição de senha por e-mail e alteração de senha autenticada.</div>
    </div>
    <div class="arch-item">
      <div class="arch-item-title">Banco de Dados</div>
      <div class="arch-item-desc">Firebase Firestore com listeners em tempo real. Dados sincronizados automaticamente entre dispositivos sem necessidade de refresh manual.</div>
    </div>
    <div class="arch-item">
      <div class="arch-item-title">Arquitetura</div>
      <div class="arch-item-desc">MVVM com ViewModels por tela. Repositório singleton em memória (Repositorio.kt) alimentado por flows do Firestore com retry automático.</div>
    </div>
  </div>

  <h2>Cargos e Permissões</h2>
  <table>
    <tr><th>Cargo</th><th>Criar Dúvida</th><th>Responder Dúvida</th><th>Ver Monitorias</th><th>Gerenciar</th></tr>
    <tr><td><span class="badge badge-aluno">Aluno</span></td><td>✅ Sim</td><td>Reply a monitor/prof</td><td>✅ Sim</td><td>❌ Não</td></tr>
    <tr><td><span class="badge badge-monitor">Monitor</span></td><td>❌ Não</td><td>✅ Sim</td><td>✅ Sim</td><td>❌ Não</td></tr>
    <tr><td><span class="badge badge-professor">Professor</span></td><td>❌ Não</td><td>✅ Sim</td><td>✅ Sim</td><td>❌ Não</td></tr>
    <tr><td><span class="badge badge-admin">Admin</span></td><td>✅ Sim</td><td>✅ Sim</td><td>✅ Sim</td><td>✅ Sim</td></tr>
  </table>
  <div class="info-box"><p>Todo usuário recém-cadastrado recebe o cargo <strong>Aluno</strong> por padrão. A promoção é feita pelo Administrador na tela de gerenciamento de usuários.</p></div>
</div>

<div class="page page-break">
  <h1>2. Telas do Aplicativo</h1>

  <div class="screen-card">
    {screens["login"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">🔑</div><div><div class="screen-title">Login</div><div class="screen-file">Login.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Todos</span></div>
      <p class="screen-desc">Tela de entrada do aplicativo. Autentica via Firebase Auth com e-mail e senha.</p>
      <ul class="feature-list">
        <li>Campo de e-mail e senha com validação</li>
        <li>Exibição/ocultação da senha</li>
        <li>Mensagem de erro em credenciais inválidas</li>
        <li>Link para recuperação de senha</li>
        <li>Link para criação de nova conta</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["cadastro"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">📝</div><div><div class="screen-title">Criar Conta</div><div class="screen-file">Cadastro.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Público</span></div>
      <p class="screen-desc">Registro de novos usuários. Cria conta no Firebase Auth e salva perfil no Firestore com cargo Aluno.</p>
      <ul class="feature-list">
        <li>Campos: nome, curso (dropdown), matrícula, e-mail, senha e confirmação</li>
        <li>Validação completa antes de enviar</li>
        <li>Senha mínima de 6 caracteres</li>
        <li>Cargo atribuído automaticamente como Aluno</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["esqueceu_senha"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">🔒</div><div><div class="screen-title">Redefinir Senha</div><div class="screen-file">EsqueceuSenha.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Público</span></div>
      <p class="screen-desc">Recuperação de acesso. Envia link de redefinição de senha por e-mail via Firebase.</p>
      <ul class="feature-list">
        <li>Campo de e-mail com validação</li>
        <li>Envio de e-mail pelo Firebase Auth</li>
        <li>Confirmação visual de sucesso</li>
        <li>Botão de voltar ao login</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["home"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">🏠</div><div><div class="screen-title">Início — Monitorias</div><div class="screen-file">Home.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Aluno</span><span class="badge badge-monitor">Monitor</span><span class="badge badge-professor">Professor</span></div>
      <p class="screen-desc">Tela principal com todas as monitorias do curso do usuário logado, com filtros e busca.</p>
      <ul class="feature-list">
        <li>Saudação personalizada com o nome do usuário</li>
        <li>Busca por disciplina ou nome do monitor</li>
        <li>Filtros por disciplina (chips)</li>
        <li>Cards de monitoria com nome, cargo, disciplina, dia e horário</li>
        <li>Cards de professores destacados com borda laranja</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["feed"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">💬</div><div><div class="screen-title">Feed de Dúvidas</div><div class="screen-file">FeedDuvidas.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Aluno</span><span class="badge badge-monitor">Monitor</span><span class="badge badge-professor">Professor</span></div>
      <p class="screen-desc">Feed com todas as dúvidas do curso. Monitor/professor não veem o botão de nova dúvida.</p>
      <ul class="feature-list">
        <li>Lista de dúvidas em ordem cronológica</li>
        <li>Busca por título, disciplina ou descrição</li>
        <li>Filtros por disciplina</li>
        <li>Badge "Respondida por monitor/professor"</li>
        <li>Botão + visível apenas para Aluno e Admin</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["detalhe_duvida"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">🔍</div><div><div class="screen-title">Detalhe da Dúvida</div><div class="screen-file">DetalheDuvida.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Todos</span></div>
      <p class="screen-desc">Visualização completa de uma dúvida com respostas. Monitor/professor respondem; aluno pode fazer reply.</p>
      <ul class="feature-list">
        <li>Card da dúvida com título e descrição completa</li>
        <li>Respostas com destaque para monitor/professor</li>
        <li>Campo de resposta para monitor, professor e autor</li>
        <li>Botão "Responder" (reply) para o aluno</li>
        <li>Edição e exclusão por autor e admin</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["nova_duvida"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">✏️</div><div><div class="screen-title">Nova Dúvida</div><div class="screen-file">NovaDuvida.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Aluno</span><span class="badge badge-admin">Admin</span></div>
      <p class="screen-desc">Formulário para publicar nova dúvida. Visível apenas para usuários do mesmo curso.</p>
      <ul class="feature-list">
        <li>Campo de título</li>
        <li>Dropdown de disciplinas do curso</li>
        <li>Campo de descrição detalhada</li>
        <li>Card com dica para melhores respostas</li>
        <li>Botão desabilitado até preencher todos os campos</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["perfil"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">👤</div><div><div class="screen-title">Meu Perfil</div><div class="screen-file">Perfil.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-aluno">Todos</span></div>
      <p class="screen-desc">Gerenciamento da conta do usuário logado com edição de perfil, foto, senha e exclusão de conta.</p>
      <ul class="feature-list">
        <li>Avatar, nome, cargo e curso</li>
        <li>Edição: nome, curso, matrícula e e-mail</li>
        <li>Seleção de foto da galeria</li>
        <li>Dialog de alteração de senha</li>
        <li>Exclusão de conta com confirmação por senha</li>
        <li>Logout</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["admin_usuarios"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">👥</div><div><div class="screen-title">Gerenciar Usuários</div><div class="screen-file">AdminUsuarios.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-admin">Admin</span></div>
      <p class="screen-desc">Lista todos os usuários cadastrados e permite alterar cargos em tempo real.</p>
      <ul class="feature-list">
        <li>Lista de usuários com nome, e-mail e cargo atual</li>
        <li>Alteração de cargo por dropdown</li>
        <li>Mudanças refletem em todos os dispositivos imediatamente</li>
      </ul>
    </div>
  </div>

  <div class="screen-card">
    {screens["admin_monitorias"]}
    <div class="screen-body">
      <div class="screen-header"><div class="screen-icon">📅</div><div><div class="screen-title">Gerenciar Monitorias</div><div class="screen-file">AdminMonitorias.kt</div></div></div>
      <div class="screen-access"><span class="badge badge-admin">Admin</span></div>
      <p class="screen-desc">Criação, edição e exclusão de monitorias. Associa monitor/professor a disciplina com horário e sala.</p>
      <ul class="feature-list">
        <li>Lista de monitorias cadastradas</li>
        <li>Formulário com responsável, disciplina, curso, dia, horário e sala</li>
        <li>Edição e exclusão inline</li>
      </ul>
    </div>
  </div>
</div>

<div class="page page-break">
  <h1>3. Banco de Dados — Firestore</h1>

  <div class="collection-card">
    <div class="collection-name">/usuarios</div>
    <p>Perfil de cada usuário. O ID do documento é o UID do Firebase Authentication.</p>
    <table>
      <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
      <tr><td>nome</td><td><span class="type-string">string</span></td><td>Nome completo</td></tr>
      <tr><td>email</td><td><span class="type-string">string</span></td><td>E-mail em letras minúsculas</td></tr>
      <tr><td>curso</td><td><span class="type-string">string</span></td><td>Nome do curso</td></tr>
      <tr><td>matricula</td><td><span class="type-string">string</span></td><td>Número de matrícula</td></tr>
      <tr><td>cargo</td><td><span class="type-string">string</span></td><td>USUARIO | MONITOR | PROFESSOR | ADMIN</td></tr>
      <tr><td>fotoUri</td><td><span class="type-string">string</span> | null</td><td>URI local da foto de perfil</td></tr>
    </table>
  </div>

  <div class="collection-card">
    <div class="collection-name">/monitorias</div>
    <p>Horários de monitoria cadastrados pelo administrador.</p>
    <table>
      <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
      <tr><td>monitorId</td><td><span class="type-string">string</span></td><td>UID do monitor ou professor</td></tr>
      <tr><td>disciplina</td><td><span class="type-string">string</span></td><td>Nome da disciplina</td></tr>
      <tr><td>curso</td><td><span class="type-string">string</span></td><td>Curso da monitoria</td></tr>
      <tr><td>diaSemana</td><td><span class="type-string">string</span></td><td>Ex: "Segunda-feira"</td></tr>
      <tr><td>horario</td><td><span class="type-string">string</span></td><td>Ex: "14:00 às 16:00"</td></tr>
      <tr><td>sala</td><td><span class="type-string">string</span></td><td>Localização física</td></tr>
    </table>
  </div>

  <div class="collection-card">
    <div class="collection-name">/duvidas</div>
    <p>Dúvidas publicadas pelos alunos. Cada dúvida possui uma subcoleção <code>respostas</code>.</p>
    <table>
      <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
      <tr><td>autorId</td><td><span class="type-string">string</span></td><td>UID do aluno autor</td></tr>
      <tr><td>curso</td><td><span class="type-string">string</span></td><td>Curso da dúvida</td></tr>
      <tr><td>disciplina</td><td><span class="type-string">string</span></td><td>Disciplina relacionada</td></tr>
      <tr><td>titulo</td><td><span class="type-string">string</span></td><td>Título resumido</td></tr>
      <tr><td>descricao</td><td><span class="type-string">string</span></td><td>Descrição detalhada</td></tr>
      <tr><td>criadaEm</td><td><span class="type-string">string</span></td><td>Formato "dd/MM/yyyy HH:mm" (Brasília)</td></tr>
      <tr><td>respostasCount</td><td><span class="type-number">number</span></td><td>Contador de respostas</td></tr>
      <tr><td>respondidaPorMonitor</td><td><span class="type-boolean">boolean</span></td><td>true se monitor/professor respondeu</td></tr>
    </table>
    <h3 style="margin-top:20px">Subcoleção: /duvidas/{{id}}/respostas</h3>
    <table>
      <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
      <tr><td>autorId</td><td><span class="type-string">string</span></td><td>UID de quem respondeu</td></tr>
      <tr><td>texto</td><td><span class="type-string">string</span></td><td>Conteúdo da resposta</td></tr>
      <tr><td>criadaEm</td><td><span class="type-string">string</span></td><td>Formato "dd/MM/yyyy HH:mm"</td></tr>
    </table>
  </div>
</div>

<div class="page page-break">
  <h1>4. REST API — Firestore</h1>
  <div class="info-box"><p><strong>Base:</strong> https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents</p></div>

  <div class="url-section">
    <div class="url-section-title">Usuários</div>
    <div class="url-card"><div class="url-method">GET</div><div class="url-path">https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents/usuarios</div><div class="url-desc">Lista todos os usuários</div></div>
    <div class="url-card"><div class="url-method">GET</div><div class="url-path">https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents/usuarios/{{UID}}</div><div class="url-desc">Perfil de um usuário específico</div></div>
  </div>
  <div class="url-section">
    <div class="url-section-title">Monitorias</div>
    <div class="url-card"><div class="url-method">GET</div><div class="url-path">https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents/monitorias</div><div class="url-desc">Lista todas as monitorias</div></div>
  </div>
  <div class="url-section">
    <div class="url-section-title">Dúvidas e Respostas</div>
    <div class="url-card"><div class="url-method">GET</div><div class="url-path">https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents/duvidas</div><div class="url-desc">Lista todas as dúvidas</div></div>
    <div class="url-card"><div class="url-method">GET</div><div class="url-path">https://firestore.googleapis.com/v1/projects/monitorfan-66f16/databases/(default)/documents/duvidas/{{ID}}/respostas</div><div class="url-desc">Respostas de uma dúvida específica</div></div>
  </div>
  <div class="info-box"><p>⚠️ O JSON retornado envolve cada valor com o tipo: {{"stringValue":"..."}}, {{"integerValue":"..."}}, {{"booleanValue":true}}.</p></div>
</div>

</body>
</html>"""

with open(OUTPUT, 'w', encoding='utf-8') as f:
    f.write(html)

size_kb = os.path.getsize(OUTPUT) // 1024
print(f"\nArquivo gerado: {OUTPUT}")
print(f"Tamanho: {size_kb}KB")
